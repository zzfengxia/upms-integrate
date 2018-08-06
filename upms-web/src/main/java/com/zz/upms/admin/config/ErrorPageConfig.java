package com.zz.upms.admin.config;

import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-07-11 14:07
 * @desc 自定义错误页,需要实现错误页Controller
 * ************************************
 */
@Component
public class ErrorPageConfig implements ErrorPageRegistrar {
    @Override
    public void registerErrorPages(ErrorPageRegistry registry) {
        ErrorPage[] errorPages = new ErrorPage[5];

        errorPages[0] = new ErrorPage(HttpStatus.NOT_FOUND, "/404");
        errorPages[1] = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/500");
        errorPages[2] = new ErrorPage(HttpStatus.FORBIDDEN, "/403");
        errorPages[3] = new ErrorPage(HttpStatus.UNAUTHORIZED, "/403");
        errorPages[4] = new ErrorPage(UnauthorizedException.class, "/403");

        registry.addErrorPages(errorPages);
    }
}
