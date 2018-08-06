package com.zz.upms.base.service.shiro;

import com.zz.upms.base.dao.system.*;
import com.zz.upms.base.domain.system.*;
import com.zz.upms.base.utils.DigestsUtis;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Created by Francis.zz on 2017/4/21.
 */
@Service
public class AccountService {
    @Autowired
    private PmUserDao userDao;
    @Autowired
    private MenuDao menuDao;

    public List<String> findAllPerm() {
        return menuDao.findAllNormalPerm();
    }

    public PmUser findByUsername(String userName) {
        return userDao.findByUsername(userName);
    }

    public List<String> findPermsByUserId(Long userId) {
        return menuDao.findPermsByUserId(userId);
    }

    /**
     * 为用户生成密文
     * @param user
     * @return
     */
    public PmUser encryptUser(PmUser user) {
        if(StringUtils.isEmpty(user.getJobuuid())) {
            user.setJobuuid(UUID.randomUUID().toString().replace("-", ""));
        }
        if(StringUtils.isEmpty(user.getSalt())) {
            user.setSalt(DigestsUtis.getSalt());
        }
        if(!StringUtils.isEmpty(user.getPassword())) {
            user.setPassword(DigestsUtis.encrypt(user.getPassword(), user.getSalt()));
        }

        return user;
    }

    public boolean checkPwd(PmUser curUser, String pwd) {
        String authPwd = DigestsUtis.encrypt(pwd, curUser.getSalt());

        return curUser.getPassword().equalsIgnoreCase(authPwd);
    }
}
