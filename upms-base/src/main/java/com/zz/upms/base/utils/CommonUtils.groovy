package com.zz.upms.base.utils

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
}
