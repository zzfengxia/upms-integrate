package com.zz.upms.base.entity.system;

import com.baomidou.mybatisplus.annotation.TableField;
import com.zz.upms.base.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class PmRole extends BaseEntity {
	private String rolename;		// 主键
	private String roleintro;		// 角色描述

	@TableField(exist = false)
	private List<Long> permIds;
}