package com.zz.upms.base.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2023-04-20 17:23
 * @desc AiChatRobotIntentionRobotParseDTO
 * ************************************
 */
@Data
@ToString
public class AiChatRobotIntentionRobotParseDTO {
    @ExcelProperty(index = 0)
    private String intentionCode;
    @ExcelProperty(index = 1)
    private String aliasName;
    @ExcelProperty(index = 4, converter = BooleanConverter.class)
    private Boolean custIntentionStatus;
    @ExcelProperty(index = 5, converter = BooleanConverter.class)
    private Boolean toArtificialStatus;
    @ExcelProperty(index = 6, converter = BooleanConverter.class)
    private Boolean wakeupStatus;
}
