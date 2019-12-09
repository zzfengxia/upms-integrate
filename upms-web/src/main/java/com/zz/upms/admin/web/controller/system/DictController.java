package com.zz.upms.admin.web.controller.system;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.baomidou.mybatisplus.plugins.Page;
import com.zz.upms.base.common.protocol.PageParam;
import com.zz.upms.base.common.protocol.PageResponse;
import com.zz.upms.base.common.protocol.Response;
import com.zz.upms.base.entity.system.Dict;
import com.zz.upms.base.service.system.DictService;
import com.zz.upms.admin.web.controller.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-07-31 14:35
 * @desc DictController
 * ************************************
 */
@RequestMapping("/dict")
@RestController
public class DictController extends BaseController {
    @Autowired
    private DictService dictService;
    @Value("${my.host}")
    private String myHost;
    @NacosValue(value = "${my.rabbit}", autoRefreshed = true)
    private String rabbitUser;      // 使用@Value引用配置中心的参数不会自动刷新
    @Value("${RABBIT_MQ_PASSWORD}")
    private String rabbitPass;      // 使用@Value引用配置中心的参数不会自动刷新
    @NacosValue(value = "${DATASOURCE_HOST}", autoRefreshed = true)
    private String dbHost;

    @RequestMapping("/list")
    public PageResponse<?> list(PageParam param) {
        Page<Dict> result = dictService.queryPage(param);

        System.out.println("myHost:" + myHost);
        System.out.println("rabbitUser:" + rabbitUser);
        System.out.println("rabbitPass:" + rabbitPass);
        System.out.println("dbHost:" + dbHost);

        return wrapperPageResult(result);
    }

    @RequestMapping("/delete")
    public Response<?> delete(@RequestBody Long[] ids) {
        dictService.deleteDict(ids);

        return Response.success();
    }

    @RequestMapping("/info/{id}")
    public Response<?> info(@PathVariable("id") Long id) {
        Dict dcit = dictService.selectById(id);

        return Response.success(dcit);
    }

    @RequestMapping("/save/create")
    public Response<?> create(@Valid @RequestBody Dict dict) {
        // 查询是否已存在该字典项
        Dict old = dictService.checkDict(dict.getDictType(), dict.getDictKey());
        if(old != null) {
            return Response.error("字典项已存在,请勿重复添加");
        }

        dictService.createDict(dict);

        return Response.success();
    }

    @RequestMapping("/save/update")
    public Response<?> update(@RequestBody Dict dict) {
        dictService.updateDict(dict);

        return Response.success();
    }

    /**
     * 获取所有字典类型
     *
     * @return
     */
    @RequestMapping("/allType")
    public Response<?> allType() {
        List<String> dictTypes = dictService.findAllType();

        return Response.success(dictTypes);
    }
}
