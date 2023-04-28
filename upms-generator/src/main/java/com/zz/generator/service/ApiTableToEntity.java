package com.zz.generator.service;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2023-04-27 22:56
 * @desc 开放平台API转成实体类/文档转实体类/表格转实体类
 * ************************************
 */
public class ApiTableToEntity {
    public static void main(String[] args) {
        // 参数
        /**
         * 1：跳过行数
         * 2：变量名索引
         * 3：类型索引
         * 4：备注索引，可以是个数组
         * 5: 数据行使用“|”切分的数量
         */
        List<Object> params = Lists.newArrayList(2, 1, 3,
                Lists.newArrayList(new Remark(0, null),
                        new Remark(2, "是否必填："),
                        new Remark(5, "描述："),
                        new Remark(3, "长度：")),
                6);

        int oriLineSize = 0;
        String fileName = "C:\\Users\\86153\\Desktop\\wechatapi.md";
        try(BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;

            List<List<String>> data = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                // 处理每一行数据
                if(!StringUtils.hasText(line)) {
                    continue;
                }
                ++oriLineSize;
                if(line.startsWith("|")) {
                    line = line.substring(1);
                }
                if(line.endsWith("|")) {
                    line = line.substring(0, line.length() - 1);
                }
                Integer maxSize = (Integer) params.get(4);
                String[] datas = line.split("\\|");
                if(datas.length != maxSize) {
                    System.out.println("解析失败的行：" + line);
                    continue;
                }
                data.add(Arrays.stream(datas).map(String::trim).collect(Collectors.toList()));
            }
            int parseSize = 0;
            int skitLine = (int) params.get(0);
            for (int i = 0; i < data.size(); i++) {
                if(i < skitLine) {
                    continue;
                }
                List<String> lineData = data.get(i);
                int typeIndex = (int) params.get(2);
                int varIndex = (int) params.get(1);
                String varName = dealVarName(lineData.get(varIndex));

                List<Remark> remarks = (List<Remark>) params.get(3);
                System.out.println("/**");
                for (Remark remark : remarks) {
                    String remarkStr = remark.assemblyMsg(lineData);
                    System.out.printf("* %s%n", remarkStr);
                }
                System.out.println("*/");
                System.out.printf("private %s %s;%n", toType(lineData.get(typeIndex)), varName);
                System.out.println();

                ++parseSize;
            }

            System.out.println("数据总行数：" + oriLineSize + ", 解析属性数量：" + parseSize);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Data
    @AllArgsConstructor
    private static class Remark {
        private int index;
        private String prefix;

        public String assemblyMsg(List<String> line) {
            synchronized (this) {
                if(line.size() < index) {
                    return null;
                }
                if(line.get(index) != null) {
                    return prefix != null ? prefix + line.get(index) : line.get(index);
                }
                return null;
            }
        }
    }

    private static class TypeConverter {
        public String toType(String ori) {
            try {
                // 先简单处理
                if(StringUtils.hasText(ori)) {
                    return ori.substring(0, 1).toUpperCase() + ori.substring(1);
                }
            } catch (Exception e) {
                // no op
            }
            return ori;
        }
    }

    private static String toType(String ori) {
        try {
            // 先简单处理，后面可以转成对应包装类型
            if(StringUtils.hasText(ori)) {
                ori = ori.substring(0, ori.indexOf('('));
                return ori.substring(0, 1).toUpperCase() + ori.substring(1);
            }
        } catch (Exception e) {
            // no op
        }
        return ori;
    }

    private static String dealVarName(String oriStr) {

        return oriStr;
    }
}
