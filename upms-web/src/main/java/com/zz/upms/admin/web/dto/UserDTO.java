package com.zz.upms.admin.web.dto;

import com.zz.upms.base.domain.system.PmUser;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-08-01 18:21
 * @desc UserDTO
 * ************************************
 */
@Data
public class UserDTO {
    private Long id;
    private String username;
    private String realname;
    private String email;
    private String phone;
    private String jobuuid;

    private String status;		// 1:正常;0:删除
    private String homePage;	// 首页

    private List<Long> roles;	// role id

    public UserDTO(PmUser user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.realname = user.getRealname();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.jobuuid = user.getJobuuid();
        this.status = user.getStatus();
        this.homePage = user.getHomePage();
        if(user.getRoles() != null) {
            this.roles = new ArrayList<>();
            this.roles.addAll(user.getRoles());
        }
    }
}
