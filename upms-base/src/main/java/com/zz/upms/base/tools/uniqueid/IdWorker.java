package com.zz.upms.base.tools.uniqueid;

import com.zz.upms.base.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public abstract class IdWorker {
	/** 支持最多使用1089年 */
    protected int timeBits = 35;
    /** 支持65535次服务重启 */
    protected int workerBits = 16;
    /** 支持每秒4095个序号 */
    protected int seqBits = 12;

    /**
     * 基准时间，起始时间 2019-06-01
     */
    protected String epochStr = "2019-06-01";
    protected long epochSeconds = TimeUnit.MILLISECONDS.toSeconds(1559318400000L);

    /**
     * Stable fields after spring bean initializing
     */
    protected BitsAllocator bitsAllocator;
    protected long workerId;

    /**
     * Volatile fields caused by nextId()
     */
    protected long sequence = 0L;
    protected long lastSecond = -1L;

    /**
     * 必要要在使用前调用改初始化方法
     */
    protected void init() {
        bitsAllocator = new BitsAllocator(timeBits, workerBits, seqBits);
        workerId = getWorkerId(bitsAllocator.getMaxWorkerId());
    }

    public String parseUID(long uid) {
        long totalBits = BitsAllocator.TOTAL_BITS;
        long signBits = bitsAllocator.getSignBits();
        long timestampBits = bitsAllocator.getTimestampBits();
        long workerIdBits = bitsAllocator.getWorkerIdBits();
        long sequenceBits = bitsAllocator.getSequenceBits();

        // parse UID
        long sequence = (uid << (totalBits - sequenceBits)) >>> (totalBits - sequenceBits);
        long workerId = (uid << (timestampBits + signBits)) >>> (totalBits - workerIdBits);
        long deltaSeconds = uid >>> (workerIdBits + sequenceBits);

        Date thatTime = new Date(TimeUnit.SECONDS.toMillis(epochSeconds + deltaSeconds));
        String thatTimeStr = DateUtil.formatByDateTimePattern(thatTime);

        // format as string
        return String.format("{\"UID\":\"%d\",\"timestamp\":\"%s\",\"workerId\":\"%d\",\"sequence\":\"%d\"}",
                uid, thatTimeStr, workerId, sequence);
    }

    protected synchronized long nextId() {
        long currentSecond = getCurrentSecond();

        // Clock moved backwards, refuse to generate uid
        if (currentSecond < lastSecond) {
            long refusedSeconds = lastSecond - currentSecond;
            throw new IllegalArgumentException("Clock moved backwards. Refusing for " + refusedSeconds + " seconds");
        }

        // At the same second, increase sequence
        if (currentSecond == lastSecond) {
            sequence = (sequence + 1) & bitsAllocator.getMaxSequence();
            // Exceed the max sequence, we wait the next second to generate uid
            if (sequence == 0) {
                currentSecond = getNextSecond(lastSecond);
            }

            // At the different second, sequence restart from zero
        } else {
            sequence = 0L;
        }

        lastSecond = currentSecond;

        // Allocate bits for UID
        return bitsAllocator.assemble(currentSecond - epochSeconds, workerId, sequence);
    }

    /**
     * Get next millisecond
     */
    private long getNextSecond(long lastTimestamp) {
        long timestamp = getCurrentSecond();
        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentSecond();
        }

        return timestamp;
    }

    /**
     * Get current second
     */
    private long getCurrentSecond() {
        long currentSecond = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        if (currentSecond - epochSeconds > bitsAllocator.getMaxDeltaSeconds()) {
            throw new IllegalArgumentException("Timestamp bits is exhausted. Refusing UID generate. Now: " + currentSecond);
        }

        return currentSecond;
    }

    public void setTimeBits(int timeBits) {
        if (timeBits > 0) {
            this.timeBits = timeBits;
        }
    }

    public void setWorkerBits(int workerBits) {
        if (workerBits > 0) {
            this.workerBits = workerBits;
        }
    }

    public void setSeqBits(int seqBits) {
        if (seqBits > 0) {
            this.seqBits = seqBits;
        }
    }

    /**
     * 设置起始时间
     *
     * @param epochStr
     */
    public void setEpochStr(String epochStr) {
        if (StringUtils.isNotBlank(epochStr)) {
            this.epochStr = epochStr;
            this.epochSeconds = TimeUnit.MILLISECONDS.toSeconds(DateUtil.parseByDayPattern(epochStr).getTime());
        }
    }

    /**
     * workerId分配接口
     *
     * @param maxWorkerId workerId最大允许取值
     * @return
     */
    protected abstract long getWorkerId(long maxWorkerId);
}
