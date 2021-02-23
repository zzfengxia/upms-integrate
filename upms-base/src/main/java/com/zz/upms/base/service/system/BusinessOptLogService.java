package com.zz.upms.base.service.system;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zz.upms.base.common.protocol.PageParam;
import com.zz.upms.base.dao.platform.BusinessOptLogDao;
import com.zz.upms.base.entity.platform.BusinessOptLog;
import com.zz.upms.base.entity.platform.BusinessOptLogVO;
import com.zz.upms.base.service.base.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ************************************
 * create by Code-Generator-0.0.1
 * @author 郑天翔-tianxiang2017@gmail.com
 * @date   2019-02-14 10:29:05
 * @desc   BusinessOptLog
 * ************************************
 */
@Service
public class BusinessOptLogService extends BaseService<BusinessOptLogDao, BusinessOptLog> {

    @Autowired
    private BusinessOptLogDao businessOptLogDao;

    /**
     * 分页查询
     *
     * @param param
     * @return
     */
    public Page<BusinessOptLog> queryPage(PageParam<BusinessOptLogVO> param) {
        QueryWrapper<BusinessOptLog> wrapper = new QueryWrapper<BusinessOptLog>();

        BusinessOptLogVO condition = param.getParams();
        // 排序
        String sortCol = param.getSort();
        sortCol = StringUtils.isEmpty(sortCol) ? "id": sortCol;

        if(condition != null) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            String cardCode = condition.getCardCode();
            String interfaceCode = condition.getCommandId();
            String seid = condition.getSeUid();
            
            String cardNo = condition.getCardNo();
            String sourceChnl = condition.getSourceChnl();
            String optCode = condition.getOptCode();
            Integer bussinessType = condition.getBusinessType();
            Date startTime = condition.getStartTime();
            Date endTime = condition.getEndTime();
    
            wrapper.eq(StringUtils.isNotEmpty(cardCode), "card_code", cardCode);
            if(startTime != null) {
                wrapper.and(w -> w.between("create_time", dateFormat.format(startTime), dateFormat.format(endTime)));
                sortCol = "create_time";
            }
    
            wrapper.eq(StringUtils.isNotEmpty(sourceChnl), "source_chnl", sourceChnl)
                    .eq(StringUtils.isNotEmpty(interfaceCode), "command_id", interfaceCode)
                    .eq(bussinessType != null, "business_type", bussinessType)
                    .eq(StringUtils.isNotEmpty(seid), "se_uid", seid)
                    .eq(StringUtils.isNotEmpty(cardNo), "card_no", cardNo);
            if("0".equals(optCode)) {
                wrapper.eq("opt_code", optCode);
            }else {
                wrapper.ne(StringUtils.isNotEmpty(optCode), "opt_code", 0);
            }
            
            wrapper.like(StringUtils.isNotEmpty(param.getSearch()), "opt_msg", param.getSearch());
        }

        param.setSort(sortCol);

        return queryWithPage(param, wrapper);
    }
}