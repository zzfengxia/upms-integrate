package com.zz.upms.base.entity.system;

import com.baomidou.mybatisplus.annotations.TableName;
import com.zz.upms.base.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

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
public class ConfigParam extends BaseEntity {
    private String paramKey;
    private String paramValue;
    // 状态
    private Boolean status;
    private String remark;
}
