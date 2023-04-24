package com.zz.upms.base.excel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.listener.ReadListener;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2023-04-24 14:22
 * @desc ParseExcel
 * ************************************
 */
@Slf4j
public class ParseExcel {
    public static void main(String[] args) throws IOException {
        InputStream is = Files.newInputStream(Paths.get("C:\\Users\\86153\\Desktop\\TaskFlow多轮意图.xlsx"));
        List<AiChatRobotIntentionRobotParseDTO> data = new ArrayList<>();
        ReadListener<AiChatRobotIntentionRobotParseDTO> listener = new AnalysisEventListener<AiChatRobotIntentionRobotParseDTO>() {
            @Override
            public void invoke(AiChatRobotIntentionRobotParseDTO row, AnalysisContext context) {
                // 处理Excel文件中的一行数据

                data.add(row);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                log.info("taskflow多轮对话意图文件解析完成，成功解析" + data.size() + "条数据");
            }
        };

        EasyExcel.read(is, AiChatRobotIntentionRobotParseDTO.class, listener).sheet().doRead();

        data.forEach(System.out::println);
    }
}
