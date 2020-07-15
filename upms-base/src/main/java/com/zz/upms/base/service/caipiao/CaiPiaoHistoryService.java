package com.zz.upms.base.service.caipiao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zz.upms.base.common.protocol.PageParam;
import com.zz.upms.base.dao.caipiao.CaiPiaoHistoryDao;
import com.zz.upms.base.entity.caipiao.CaiPiaoHistory;
import com.zz.upms.base.service.base.BaseService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

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
        Wrapper<CaiPiaoHistory> wrapper = new QueryWrapper<CaiPiaoHistory>();
        // 排序
        String sortCol = param.getSort();
        sortCol = StringUtils.isEmpty(sortCol) ? "id": sortCol;
        param.setSort(sortCol);

        return queryWithPage(param, wrapper);
    }
}