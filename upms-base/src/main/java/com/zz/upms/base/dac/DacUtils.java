package com.zz.upms.base.dac;

import java.util.Arrays;
import java.util.List;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2020-07-13 09:32
 * ************************************
 */
public class DacUtils {
    public static List<String> getDacGroup(String dacField) {
        return dacField != null ? Arrays.asList(dacField.split(",")) : null;
    }
}
