package ${packageName}.admin.web.controller.${moduleName};

import ${packageName}.base.common.protocol.PageParam;
import ${packageName}.base.common.protocol.PageResponse;
import ${packageName}.base.common.protocol.Response;
import ${packageName}.base.entity.${moduleName}.${classInfo.className};
import ${packageName}.base.service.${moduleName}.${classInfo.className}Service;
import ${packageName}.admin.web.controller.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import java.util.Arrays;
import com.baomidou.mybatisplus.plugins.Page;
import com.alibaba.fastjson.JSONObject;

/**
 * ************************************
 * create by Code-Generator-${version!'0.0.1'}
 * @author ${author!'Francis.zz'}<#if (email)??>-${email}</#if>
 * @date   ${.now?string("yyyy-MM-dd HH:mm:ss")}
 * @desc   ${classInfo.className}
 * ************************************
 */
@RequestMapping("${moduleName}/${classInfo.className?uncap_first}")
@RestController
public class ${classInfo.className}Controller extends BaseController {
    @Autowired
    private ${classInfo.className}Service ${classInfo.className?uncap_first}Service;

    /**
     * 分页列表
     */
    @RequestMapping("/list")
    public PageResponse<?> list(@RequestBody PageParam<JSONObject> param) {
        Page<${classInfo.className}> result = ${classInfo.className?uncap_first}Service.queryPage(param);

        return wrapperPageResult(result);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{${pkField.fieldName}}")
    public Response<?> info(@PathVariable("${pkField.fieldName}") ${pkField.fieldType} ${pkField.fieldName}) {
        ${classInfo.className} ${classInfo.className?uncap_first} = ${classInfo.className?uncap_first}Service.selectById(${pkField.fieldName});

        return Response.success(${classInfo.className?uncap_first});
    }

    /**
     * 新增
     */
    @RequestMapping("/save/create")
    public Response<?> create(@Validated @RequestBody ${classInfo.className} ${classInfo.className?uncap_first}, BindingResult validResult) {
        if(validResult.hasErrors()){
            for (FieldError fieldError : validResult.getFieldErrors()) {
                return Response.error(fieldError.getDefaultMessage());
            }
        }
        // TODO 校验数据是否冲突

        ${classInfo.className?uncap_first}Service.insert(${classInfo.className?uncap_first});

        return Response.success();
    }

    /**
     * 修改
     */
    @RequestMapping("/save/update")
    public Response<?> update(@RequestBody ${classInfo.className} ${classInfo.className?uncap_first}) {
        // TODO 增量更新操作
        ${classInfo.className?uncap_first}Service.updateById(${classInfo.className?uncap_first});

        return Response.success();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public Response<?> delete(@RequestBody ${pkField.fieldType}[] ${pkField.fieldName}s){
        // TODO 自定义删除操作
        ${classInfo.className?uncap_first}Service.deleteBatchIds(Arrays.asList(${pkField.fieldName}s));

        return Response.success();
    }

}