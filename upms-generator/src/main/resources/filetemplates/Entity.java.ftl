package ${packageName}.base.entity.${moduleName};

<#if enableLombok>
import lombok.Getter;
import lombok.Setter;
</#if>
import java.io.Serializable;
<#if hasDate>
import java.util.Date;
</#if>
<#if hasBigDecimal>
import java.math.BigDecimal;
</#if>
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;

/**
 * ************************************
 * create by Code-Generator-${version!'0.0.1'}
 * @author ${author!'Francis.zz'}<#if (email)??>-${email}</#if>
 * @date   ${.now?string("yyyy-MM-dd HH:mm:ss")}
 * @desc   ${classInfo.className}
 * ************************************
 */
<#if enableLombok>
@Getter
@Setter
</#if>
@TableName("${classInfo.tableName}")
public class ${classInfo.className} implements Serializable {
<#list fields as col>
	<#if (col.fieldDesc)??>
    <#if col.fieldDesc?length gt 0>
    /**
	 * ${col.fieldDesc}
     */
    </#if>
	</#if>
    <#if col.priFalg>
    @TableId
    </#if>
    private ${col.fieldType} ${col.fieldName};
</#list>

<#if !enableLombok>
	<#list fields as col>
    public void set${col.fieldName?cap_first}(${col.fieldType} ${col.fieldName}) {
        this.${col.fieldName} = ${col.fieldName};
    }
    public ${col.fieldType} get${col.fieldName?cap_first}() {
        return this.${col.fieldName};
    }
	</#list>
</#if>
}