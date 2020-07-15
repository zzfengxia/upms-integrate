package com.zz.upms.admin.web.controller.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zz.upms.admin.web.controller.base.BaseController;
import com.zz.upms.base.common.protocol.PageParam;
import com.zz.upms.base.common.protocol.PageResponse;
import com.zz.upms.base.common.protocol.Response;
import com.zz.upms.base.entity.system.ConfigParam;
import com.zz.upms.base.service.system.ConfigParamService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Francis.zz on 2017/4/27.
 */
@RequestMapping("/system/config")
@RestController
public class ConfigParamController extends BaseController {
    @Autowired
    private ConfigParamService configParamService;

    @RequestMapping("/list")
    public PageResponse<?> list(PageParam param) {
        Page<ConfigParam> result = configParamService.queryPage(param);

        return wrapperPageResult(result);
    }

    @RequestMapping("/delete")
    public Response<?> delete(@RequestBody String[] ids) {
        configParamService.deleteParam(ids);

        return Response.success();
    }

    @RequestMapping("/info/{id}")
    public Response<?> info(@PathVariable("id") String id) {
        ConfigParam param = configParamService.getById(id);

        if(param == null) {
            return Response.error("参数不存在,请刷新重试");
        }

        return Response.success(param);
    }

    @RequestMapping("/save/create")
    public Response<?> create(@RequestBody ConfigParam param) {
        if(StringUtils.isEmpty(param.getParamKey())) {
            return Response.error("参数KEY不能为空");
        }
        ConfigParam cp = configParamService.getOne(new QueryWrapper<ConfigParam>().eq("param_key", param.getParamKey()));

        if(cp != null) {
            return Response.error("参数KEY已存在，请重新输入");
        }

        configParamService.createParam(param);

        return Response.success();
    }

    @RequestMapping("/save/update")
    public Response<?> update(@RequestBody ConfigParam param) {
        configParamService.updateParam(param);

        return Response.success();
    }
}
