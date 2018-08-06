package com.zz.upms.base.domain.system;

import com.baomidou.mybatisplus.annotations.TableField;
import com.zz.upms.base.domain.IDEntity;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;


@Setter
@Getter
public class PmRoleMenu extends IDEntity {
	private Long roleId;

	private Long menuId;

	public PmRoleMenu() {
	}

	public PmRoleMenu(Long roleId, Long menuId) {
		this.roleId = roleId;
		this.menuId = menuId;
	}

	@TableField(exist = false)
	private PmRole role;
	@TableField(exist = false)
	private PmMenu permission;

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}