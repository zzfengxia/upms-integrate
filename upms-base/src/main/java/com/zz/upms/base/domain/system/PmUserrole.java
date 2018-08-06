package com.zz.upms.base.domain.system;


import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.zz.upms.base.domain.IDEntity;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@TableName("pm_user_role")
public class PmUserrole extends IDEntity {
	private Long userId;

	private Long roleId;

	@TableField(exist = false)
	private PmUser user;

	@TableField(exist = false)
	private PmRole role;
}