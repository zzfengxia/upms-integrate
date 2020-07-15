package ${packageName}.base.service.${moduleName};

import ${packageName}.base.service.base.BaseService;
import ${packageName}.base.common.protocol.PageParam;
import ${packageName}.base.dao.${moduleName}.${classInfo.className}Dao;
import ${packageName}.base.entity.${moduleName}.${classInfo.className};
import com.baomidou.mybatisplus.core.mapper.EntityWrapper;
import com.baomidou.mybatisplus.core.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;
import com.alibaba.fastjson.JSONObject;

/**
 * ************************************
 * create by Code-Generator-${version!'0.0.1'}
 * @author ${author!'Francis.zz'}<#if (email)??>-${email}</#if>
 * @date   ${.now?string("yyyy-MM-dd HH:mm:ss")}
 * @desc   ${classInfo.className}
 * ************************************
 */
@Service
public class ${classInfo.className}Service extends BaseService<${classInfo.className + "Dao"}, ${classInfo.className}> {

    /**
     * 分页查询
     *
     * @param param
     * @return
     */
    public Page<${classInfo.className}> queryPage(PageParam<JSONObject> param) {
        // TODO 自定义关键字搜索
        String searchText = param.getSearch();
        Wrapper<${classInfo.className}> wrapper = new EntityWrapper<${classInfo.className}>();
        // 排序
        String sortCol = param.getSort();
        sortCol = StringUtils.isEmpty(sortCol) ? <#if pkField??>"${pkField.fieldName}"<#else> ""</#if>: sortCol;
        param.setSort(sortCol);

        return queryWithPage(param, wrapper);
    }
}