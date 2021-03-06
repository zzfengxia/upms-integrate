package com.zz.upms.base.entity.system;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.zz.upms.base.entity.IDEntity;
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