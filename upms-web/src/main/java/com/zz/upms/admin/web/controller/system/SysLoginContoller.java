package com.zz.upms.admin.web.controller.system;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.zz.upms.base.common.protocol.Response;
import com.zz.upms.base.domain.system.PmUser;
import com.zz.upms.base.service.system.AdminUserService;
import com.zz.upms.admin.web.controller.base.BaseController;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-06-29 17:07
 * @desc SysLoginContoller
 * ************************************
 */
@Controller
public class SysLoginContoller extends BaseController {
    @Autowired
    private Producer producer;
    @Autowired
    private AdminUserService userService;

    /**
     * 验证码
     *
     * @param response
     * @throws IOException
     */
    @RequestMapping("captcha")
    public void captcha(HttpServletResponse response)throws IOException {
        response.setHeader("Cache-Control", "no-store, no-cache");
        response.setContentType("image/jpeg");

        //生成文字验证码
        String text = producer.createText();
        //生成图片验证码
        BufferedImage image = producer.createImage(text);
        //保存到shiro session
        saveSessionAttr(Constants.KAPTCHA_SESSION_KEY, text);

        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, "jpg", out);
    }

    /**
     * 登录
     */
    @ResponseBody
    @RequestMapping(value = "/sys/login", method = RequestMethod.POST)
    public Response login(String username, String password, String captcha) {
        /*Object kaptcha = getSessionAttr(Constants.KAPTCHA_SESSION_KEY);

        if(kaptcha == null) {
            return Response.error("验证码已过期");
        }
        if(!captcha.equalsIgnoreCase((String) kaptcha)){
            return Response.error("验证码不正确");
        }*/

        try{
            Subject subject = SecurityUtils.getSubject();
            UsernamePasswordToken token = new UsernamePasswordToken(username, password, false);
            subject.login(token);
        }catch (UnknownAccountException e) {
            return Response.error(e.getMessage());
        }catch (IncorrectCredentialsException e) {
            return Response.error("账号或密码不正确");
        }catch (LockedAccountException e) {
            return Response.error("账号已被锁定,请联系管理员");
        }catch (AuthenticationException e) {
            return Response.error("账户验证失败");
        }

        // 更新上线时间
        PmUser loginUser = new PmUser();
        loginUser.setId(getCurUser().id);
        loginUser.setLastTime(new Date());

        userService.updateById(loginUser);

        return Response.success();
    }

    /**
     * 退出
     */
    /*@RequestMapping(value = "logout", method = RequestMethod.GET)
    public String logout() {
        SecurityUtils.getSubject().logout();
        return "redirect:login.html";
    }*/
}
