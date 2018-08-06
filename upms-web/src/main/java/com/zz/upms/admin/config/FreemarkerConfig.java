package com.zz.upms.admin.config;

import com.zz.upms.base.service.shiro.ShiroTag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-06-29 09:37
 * @desc FreemarkerConfig
 * ************************************
 */
@Configuration
public class FreemarkerConfig {
    @Bean
    public FreeMarkerConfigurer freeMarkerConfigurer(ShiroTag shiroTag){
        FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
        configurer.setTemplateLoaderPath("classpath:/templates/");
        Map<String, Object> variables = new HashMap<>(1);
        variables.put("shiro", shiroTag);
        configurer.setFreemarkerVariables(variables);

        Properties settings = new Properties();
        settings.setProperty("default_encoding", "utf-8");
        settings.setProperty("number_format", "0.##");
        configurer.setFreemarkerSettings(settings);

        return configurer;
    }
}
