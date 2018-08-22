package com.zz.generator.utils

import com.google.common.base.CaseFormat
import com.google.common.base.Strings
import com.zz.generator.controller.dto.GeneratorConfig
import com.zz.generator.model.ClassModel
import com.zz.generator.entity.ColumnModel
import com.zz.generator.model.FieldModel
import com.zz.generator.entity.TableModel
import com.zz.generator.exception.BizException
import com.zz.generator.exception.ErrorCode
import freemarker.template.Configuration
import freemarker.template.Template
import freemarker.template.TemplateExceptionHandler
import org.apache.commons.io.IOUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource

import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * ************************************
 * create by Intellij IDEA                             
 * @author Francis.zz
 * @date 2018-08-09 14:29
 * @desc 代码生成工具类
 * ************************************
 */
class GeneratorUtils {
    private final static Logger logger = LoggerFactory.getLogger(GeneratorUtils.class)

    /**
     * 模板文件与生成文件命名
     *
     * @param packageName
     * @param moduleName
     * @param className
     * @return
     */
    static Map<String, String> templatesAndFiles(String packageName, String moduleName, String className) {
        // 包目录
        String packagePath = "main" + File.separator + "java" + File.separator
        if (!Strings.isNullOrEmpty(packageName)) {
            packagePath += packageName.replace(".", File.separator) + File.separator
        }

        return [
                "Entity.java.ftl" : "${packagePath}base${File.separator}entity${File.separator + moduleName + File.separator + className}.java",
                "Dto.java.ftl" : "${packagePath}admin${File.separator}web${File.separator}dto${File.separator + moduleName + File.separator + className}Dto.java",
                "Dao.java.ftl" : "${packagePath}base${File.separator}dao${File.separator + moduleName + File.separator + className}Dao.java",
                "Dao.xml.ftl" : "${packagePath}base${File.separator}mapper${File.separator + moduleName + File.separator + className}Dao.xml",
                "Service.java.ftl" : "${packagePath}base${File.separator}service${File.separator + moduleName + File.separator + className}Service.java",
                "Controller.java.ftl" : "${packagePath}admin${File.separator}web${File.separator}controller${File.separator + moduleName + File.separator + className}Controller.java",

                "list.html.ftl" : "main${File.separator}resources${File.separator}templates${File.separator}" +
                        "views${File.separator + moduleName + File.separator + getClientFileName(className)}.html",

                "list.js.ftl" : "main${File.separator}resources${File.separator}statics${File.separator}" +
                        "js${File.separator + moduleName + File.separator + getClientFileName(className)}.js"
        ]
    }

    static String getClientFileName(String className) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, className)
    }

    /**
     * 数据库类型-java类型
     * todo 全限定名，更多类型，可定制
     *
     * @return
     */
    static Map<String, String> javaTypeResolver = [
            "tinyint"   : "Boolean",
            "smallint"  : "Integer",
            "mediumint" : "Integer",
            "int"       : "Integer",
            "integer"   : "Integer",
            "bigint"    : "Long",
            "float"     : "Float",
            "double"    : "Double",
            "decimal"   : "BigDecimal",
            "bit"       : "Boolean",
            "char"      : "String",
            "varchar"   : "String",
            "tinytext"  : "String",
            "text"      : "String",
            "mediumtext": "String",
            "longtext"  : "String",
            "date"      : "Date",
            "datetime"  : "Date",
            "timestamp" : "Date"
    ]

    static Map<String, Object> assembelDataModel(TableModel tableInfo, List<ColumnModel> columnInfos, GeneratorConfig config) {
        Map<String, Object> dataModel = [ : ]
        ClassModel cm = new ClassModel(tableInfo)
        // 类名转换
        cm.setClassName(getClassName(cm.getClassName(), config.isRemovePrefix()))

        List<FieldModel> fieldModels = new ArrayList<>()

        // 主键属性
        FieldModel pkModel = null
        // Date类型 todo 后续可优化
        boolean hasDate = false
        // BigDecimal类型
        boolean hasBigDecimal = false

        columnInfos.each { col ->
            FieldModel fm = new FieldModel(col)
            // 属性名转换
            fm.setFieldName(camelCaseNameConversion(fm.getFieldName(), false))
            // java类型转换
            String javaType = javaTypeResolver.get(fm.getFieldType())
            if(javaType == null) {
                throw new BizException(ErrorCode.TYPE_NOT_FOUND_EXCEPTION, fm.getFieldType())
            }
            fm.setFieldType(javaType)

            if(!hasDate && "Date" == javaType) {
                hasDate = true
            }
            if(!hasBigDecimal && "BigDecimal" == javaType) {
                hasBigDecimal = true
            }

            // 判断主键
            if(fm.isPriFalg()) {
                pkModel = fm
            }
            fieldModels.add(fm)
        }

        // 组装模板数据

        // 实体类信息
        dataModel.put("classInfo", cm)
        // 实体域信息
        dataModel.put("fields", fieldModels)
        // 前端文件命名
        dataModel.put("clientFileName", getClientFileName(cm.getClassName()))
        dataModel.put("pkField", pkModel)
        dataModel.put("hasDate", hasDate)
        dataModel.put("hasBigDecimal", hasBigDecimal)

        dataModel.put("enableLombok", config.isEnableLombok())
        dataModel.put("author", config.getAuthor())
        dataModel.put("email", config.getEmail())
        dataModel.put("packageName", config.getPackageName())
        dataModel.put("moduleName", config.getModuleName())

        return dataModel
    }

    static void gen(TableModel tableInfo, List<ColumnModel> columnInfos, GeneratorConfig config, ZipOutputStream zip) {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_28)
        // 获取模板文件所在目录
        Resource resource = new ClassPathResource("filetemplates")
        // freemarker加载目录文件
        cfg.setDirectoryForTemplateLoading(resource.getFile())

        cfg.setDefaultEncoding("UTF-8")
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER)

        // 组装模板数据
        Map<String, Object> root = assembelDataModel(tableInfo, columnInfos, config)

        Map<String, String> tempFiles = templatesAndFiles(config.getPackageName(), root.get("moduleName") as String,
                ((ClassModel) root.get("classInfo")).getClassName())

        tempFiles.each { tempName, fileName ->
            Template template = null
            try {
                template = cfg.getTemplate(tempName, "UTF-8")
            } catch(Exception e) {
                // no op
            }

            if(template != null) {
                // 生成文件
                StringWriter sw = new StringWriter()
                template.process(root, sw)

                try {
                    //添加到zip
                    zip.putNextEntry(new ZipEntry(fileName))
                    IOUtils.write(sw.toString(), zip, "utf-8")

                    sw.close()
                    zip.closeEntry()
                } catch (IOException e) {
                    logger.error("模板解析失败", e)
                    throw new BizException("模板解析失败，表名[%s]" + tableInfo.getTableName())
                }
            }
        }
    }

    /**
     * 驼峰命名转换
     *
     * @param oriStr
     * @param capitalizeFirstLetter 第一个字符是否大写
     * @return
     */
    static String camelCaseNameConversion(String oriStr, boolean capitalizeFirstLetter) {
        return CaseFormat.LOWER_UNDERSCORE.to(capitalizeFirstLetter ? CaseFormat.UPPER_CAMEL : CaseFormat.LOWER_CAMEL, oriStr)
    }

    /**
     * 类名转换
     *
     * @param oriStr
     * @param removePrefix 移除前缀
     * @return
     */
    static String getClassName(String oriStr, boolean removePrefix) {
        if(removePrefix && oriStr.indexOf("_") != -1) {
            oriStr = oriStr.substring(oriStr.indexOf("_"))
        }

        return camelCaseNameConversion(oriStr, true)
    }
}
