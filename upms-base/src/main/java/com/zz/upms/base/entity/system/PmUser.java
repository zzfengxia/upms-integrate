package com.zz.upms.base.entity.system;

import com.baomidou.mybatisplus.annotations.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.zz.upms.base.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class PmUser extends BaseEntity {
    private String username;
    private String realname;
    private String password;
    private String salt;
    private String email;
    private String phone;
    private String jobuuid;

    private Long departmentId;
    private Date lastTime;
    private String status;         // 1:正常;0:删除
    private String homePage;       // 首页
    private String bgStyle;        // 背景风格

    @TableField(exist = false)
    private List<PmRole> roleList;
    @TableField(exist = false)
    private List<Long> roles;    // role id


    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String getPassword() {
        return password;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String getSalt() {
        return salt;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+08:00")
    public Date getLastTime() {
        return lastTime;
    }
}