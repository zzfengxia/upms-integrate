package com.zz.upms.base.domain.system;

import com.baomidou.mybatisplus.annotations.TableName;
import com.zz.upms.base.domain.IDEntity;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * Created by Francis.zz on 2017/6/5.
 * 数据字典
 * 通过dictHash和dictKey找到配置的值。与redis的hash结果相似
 */
@Getter
@Setter
@TableName("pm_dict")
public class Dict extends IDEntity {
    // 字典名称
    private String dictName;
    // 字典类型，缓存的 key
    @NotNull(message = "字典类型不能为空")
	private String dictType;
	// hash key
    @NotNull(message = "字典key不能为空")
    private String dictKey;
    // 字典值 hash value
    private String dictVal;
    // 排序
    private Integer dictOrd;
    private String remark;

    public String toKeyString() {
        return this.dictType + "." + this.dictKey;
    }
}
