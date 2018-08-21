package com.zz.generator.controller;

import com.zz.generator.annotation.RoutingWith;
import com.zz.generator.common.*;
import com.zz.generator.controller.dto.DataSourceDto;
import com.zz.generator.controller.dto.GeneratorConfig;
import com.zz.generator.service.GeneratorService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-08-10 17:41
 * @desc GeneratorController
 * ************************************
 */
@Controller
@RequestMapping("/generator")
class GeneratorController {
    @Autowired
    private GeneratorService generatorService;

    @RequestMapping("connectTest")
    @ResponseBody
    @RoutingWith(Constants.CUSTOMER_SOURCE_KEY)
    public Response<?> connectTest(@RequestBody DataSourceDto dsDto) {
        // 注册DataSource
        generatorService.registryDataSource(dsDto);

        boolean connFlag = generatorService.connectCheck();

        return connFlag ? Response.success("连接成功") : Response.error("连接失败");
    }

    @RequestMapping("getConfig")
    @ResponseBody
    public Response<?> getConfig() {
        GeneratorConfig config = GlobalConfig.isInit() ? GlobalConfig.get() : null;

        return Response.success(config);
    }

    @RequestMapping("config")
    @ResponseBody
    public Response<?> config(@RequestBody GeneratorConfig config) {
        DataSourceDto dataSourceDto = config.getDataSource();

        // 注册数据源
        generatorService.registryDataSource(dataSourceDto);
        // 保存参数
        GlobalConfig.instance(config);

        return Response.success();
    }

    @RequestMapping("checkConfig")
    @ResponseBody
    public Response<?> checkConfig() {
        return Response.success(GlobalConfig.isInit());
    }

    @RequestMapping("tableList")
    @ResponseBody
    @RoutingWith(Constants.CUSTOMER_SOURCE_KEY)
    public PageResponse<?> tableList(PageParam param) {
        PageResponse result = generatorService.queryTablePage(param);

        return result;
    }

    @RequestMapping("generate")
    @RoutingWith(Constants.CUSTOMER_SOURCE_KEY)
    public void generate(@RequestBody String[] tables, HttpServletResponse response) throws IOException {
        byte[] data = generatorService.generateCode(tables);

        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"generate-code.zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");

        IOUtils.write(data, response.getOutputStream());
    }
}