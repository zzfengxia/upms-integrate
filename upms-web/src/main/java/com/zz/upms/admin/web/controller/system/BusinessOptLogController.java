package com.zz.upms.admin.web.controller.system;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zz.upms.admin.web.controller.base.BaseController;
import com.zz.upms.base.annotation.RoutingWith;
import com.zz.upms.base.common.constans.Constants;
import com.zz.upms.base.common.protocol.PageParam;
import com.zz.upms.base.common.protocol.PageResponse;
import com.zz.upms.base.entity.platform.BusinessOptLog;
import com.zz.upms.base.entity.platform.BusinessOptLogVO;
import com.zz.upms.base.service.system.BusinessOptLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ************************************
 * create by Code-Generator-0.0.1
 * @author 郑天翔-tianxiang2017@gmail.com
 * @date   2019-02-14 10:29:05
 * @desc   BusinessOptLog
 * ************************************
 */
@RequestMapping("platform/businessOptLog")
@RestController
public class BusinessOptLogController extends BaseController {
    @Autowired
    private BusinessOptLogService businessOptLogService;
    /**
     * 分页列表
     */
    @RequestMapping("/list")
    @RoutingWith(Constants.DB_SOURCE_SHARDING)
    public PageResponse<?> list(@RequestBody PageParam<BusinessOptLogVO> param) {
        Page<BusinessOptLog> result = businessOptLogService.queryPage(param);
        
        
        return wrapperPageResult(result);
    }

}