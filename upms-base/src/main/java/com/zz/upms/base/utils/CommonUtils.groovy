package com.zz.upms.base.utils

import com.google.common.base.CaseFormat

import java.text.SimpleDateFormat

/**
 * ************************************
 * create by Intellij IDEA                             
 * @author Francis.zz
 * @date 2018-08-02 15:44
 * @desc CommonUtils
 * ************************************
 */
class CommonUtils {
    static String getFormatDateStr(String format = "yyyy-MM-dd HH:mm:ss", Date date = new Date()) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format)

        return simpleDateFormat.format(date)
    }

    /**
     * 驼峰命名转换
     *
     * @param oriStr
     * @param capitalizeFirstLetter 第一个字符是否大写，默认false
     * @return
     */
    static String camelCaseNameConversion(String oriStr, boolean capitalizeFirstLetter = false) {
        return CaseFormat.LOWER_UNDERSCORE.to(capitalizeFirstLetter ? CaseFormat.UPPER_CAMEL : CaseFormat.LOWER_CAMEL, oriStr)
    }
}
