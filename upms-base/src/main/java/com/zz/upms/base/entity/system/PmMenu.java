package com.zz.upms.base.entity.system;

import com.baomidou.mybatisplus.annotation.TableField;
import com.zz.upms.base.entity.IDEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class PmMenu extends IDEntity {
	private String viewName;
	private String iconClass;		// 图标样式
	private short type;				// 功能类型0:目录;1:菜单;2:按钮
	private String url;				// url
	private String perm;			// 权限标识，多个权限使用";"隔开
	private String status;			// 状态 0:删除;1:启用
	private Long parentId;			// 父功能

	/**
	 * 是否为显式菜单/按钮，即是否在为角色添加权限的界面上展示该菜单，设置为0则仅作为权限标识
	 * 1:展示;0:不展示
	 */
	private boolean showFlag;

	@TableField(exist = false)
	private PmMenu parent;

	@TableField(exist = false)
	private List<PmMenu> childMenu; // 子功能

	private Integer idx;

	/**
	 * ztree属性
	 */
	@TableField(exist = false)
	private Boolean open;

	public PmMenu() {
	}

	public PmMenu(Long id) {
		 this.setId(id);
	}
}