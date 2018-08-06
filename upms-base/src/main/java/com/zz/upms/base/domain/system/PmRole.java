package com.zz.upms.base.domain.system;

import com.baomidou.mybatisplus.annotations.TableField;
import com.zz.upms.base.domain.BaseEntity;
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