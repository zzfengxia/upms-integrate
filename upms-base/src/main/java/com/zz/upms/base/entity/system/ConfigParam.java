package com.zz.upms.base.entity.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.zz.upms.base.dac.DacField;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-08-08 10:16
 * @desc ConfigParam
 * ************************************
 */
@TableName("pm_config_params")
@Getter
@Setter
public class ConfigParam implements DacField {
    @TableId(type = IdType.INPUT)
    private String paramKey;
    private String paramValue;
    // 状态
    private Boolean status;
    private String remark;
    
    private String dataGroup;
    @Override
    public String getDacField() {
        return dataGroup;
    }
    
    /**
     * 创建时间
     */
    private Date cTime;
    /**
     * 更新时间
     */
    @TableField(update = "now()")
    private Date mTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    public Date getcTime() {
        return cTime;
    }
    
    public void setcTime(Date cTime) {
        this.cTime = cTime;
    }
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    public Date getmTime() {
        return mTime;
    }
    
    public void setmTime(Date mTime) {
        this.mTime = mTime;
    }
}
