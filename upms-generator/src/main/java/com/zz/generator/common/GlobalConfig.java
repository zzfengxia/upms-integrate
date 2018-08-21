package com.zz.generator.common;

import com.zz.generator.controller.dto.GeneratorConfig;

import java.util.HashMap;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-08-14 17:30
 * @desc GlobalConfig
 * ************************************
 */
public class GlobalConfig extends HashMap<String, Object> {
    private static boolean init = false;

    private static GeneratorConfig config;

    private GlobalConfig() {
    }

    public static void instance(GeneratorConfig cog) {
        config = cog;
        GlobalConfig.init = true;
    }

    public static GeneratorConfig get() {
        return GlobalConfig.init ? config : null;
    }

    public static boolean isInit() {
        return GlobalConfig.init;
    }
}
