package com.zz.upms.base.service.system;

import com.zz.upms.base.dao.system.SequenceDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Francis.zz on 2017/7/27.
 */
@Service("sequenceService")
public class SequenceService {
    @Autowired
    private SequenceDao sequenceDao;

    public Integer nextValue(String seqName) {
        return sequenceDao.nextValue(seqName);
    }
}
