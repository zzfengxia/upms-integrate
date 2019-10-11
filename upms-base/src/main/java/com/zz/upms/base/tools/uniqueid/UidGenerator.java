package com.zz.upms.base.tools.uniqueid;

import com.zz.upms.base.service.system.SequenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2019-06-05 16:57
 * @desc UID生成器，继承{@link IdWorker} 基于snowflake
 * ************************************
 */
@Component
public class UidGenerator extends IdWorker implements InitializingBean {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SequenceService sequenceService;

    /**
     * 使用sequence作为workerId，达到最大值时重置
     *
     * @param maxWorkerId 允许的最大workerId
     * @return
     */
    @Override
    protected long getWorkerId(long maxWorkerId) {
        long workerId = sequenceService.nextValue("WORKER_ID_NO");
        logger.info("current worker id of uid generate server is:{}", workerId);

        if(workerId > maxWorkerId) {
            throw new IllegalArgumentException("current worker id beyond limit of maxWorkerId: " + maxWorkerId);
        }
        return workerId;
    }

    public long getId() {
        return super.nextId();
    }

    public String getIdStr() {
        return Long.toString(super.nextId());
    }

    /**
     * <p>
     * 获取去掉"-" UUID
     * </p>
     */
    public synchronized String get32UUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.init();
    }
}
