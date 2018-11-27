package com.zz.upms.base.service.caipiao;

import com.zz.upms.base.service.base.BaseService;
import com.zz.upms.base.common.protocol.PageParam;
import com.zz.upms.base.dao.caipiao.CaiPiaoHistoryDao;
import com.zz.upms.base.entity.caipiao.CaiPiaoHistory;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

/**
 * ************************************
 * create by Code-Generator-0.0.1
 * @author Francis.zz
 * @date   2018-11-27 16:05:22
 * @desc   PiaoHistory
 * ************************************
 */
@Service
public class CaiPiaoHistoryService extends BaseService<CaiPiaoHistoryDao, CaiPiaoHistory> {

    /**
     * 分页查询
     *
     * @param param
     * @return
     */
    public Page<CaiPiaoHistory> queryPage(PageParam param) {
        String searchText = param.getSearch();
        Wrapper<CaiPiaoHistory> wrapper = new EntityWrapper<CaiPiaoHistory>();
        // 排序
        String sortCol = param.getSort();
        sortCol = StringUtils.isEmpty(sortCol) ? "id": sortCol;
        param.setSort(sortCol);

        return queryWithPage(param, wrapper);
    }
}