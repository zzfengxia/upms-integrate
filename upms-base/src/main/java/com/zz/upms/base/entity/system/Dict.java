package com.zz.upms.base.entity.system;

import com.baomidou.mybatisplus.annotation.TableName;
import com.zz.upms.base.annotation.EnableCache;
import com.zz.upms.base.entity.IDEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Francis.zz on 2017/6/5.
 * 数据字典
 * 通过dictHash和dictKey找到配置的值。与redis的hash结果相似
 */
@Getter
@Setter
@TableName("pm_dict")
@EnableCache
public class Dict extends IDEntity {
    // 字典名称
    private String dictName;
    // 字典类型，缓存的 key
	private String dictType;
	// hash key
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
