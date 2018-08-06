package com.zz.upms.base.domain;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-06-21 18:05
 * @desc IDEntity
 * ************************************
 */
public class IDEntity implements LongID {
    /**
     * 主键：Long
     */
    private Long id;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }
}
