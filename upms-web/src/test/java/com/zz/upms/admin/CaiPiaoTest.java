package com.zz.upms.admin;

import com.alibaba.fastjson.JSON;
import com.zz.upms.base.dao.caipiao.CaiPiaoHistoryDao;
import com.zz.upms.base.utils.CustomApacheHttpClient;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date 2018-11-27 11:03
 * @desc HttpTest
 * ************************************
 */
public class CaiPiaoTest {
    @Autowired
    private CaiPiaoHistoryDao caiPiaoHistoryDao;

    @Test
    public void testGetCaiPiaoData() throws Exception {
        CustomApacheHttpClient client = new CustomApacheHttpClient(5000);
        Map<String, String> cookies = new HashMap<>();
        cookies.put("UniqueID", "xfxbgZKQTNlC0laj1534566549004");
        cookies.put("Sites", "_21");
        cookies.put("_ga", "GA1.3.1038745649.1534566546");
        cookies.put("_gid", "GA1.3.1604362988.1534566546");
        cookies.put("21_vq", "15");

        Map<String, String> headers = new HashMap<>();
        headers.put("Accept-Encoding", "gzip, deflate");
        headers.put("Accept-Language", "zh-CN,zh;q=0.9");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
        headers.put("Accept", "application/json, text/javascript, */*; q=0.01");
        headers.put("Referer", "http://www.cwl.gov.cn/kjxx/ssq/kjgg/");
        headers.put("X-Requested-With", "XMLHttpRequest");
        headers.put("Connection", "keep-alive");

        // name=ssq&issueCount=&issueStart=&issueEnd=&dayStart=2013-10-24&dayEnd=2018-11-27&pageNo=8
        String data = client.doGet("http://www.cwl.gov.cn/cwl_admin/kjxx/findDrawNotice?name=ssq&dayStart=2013-10-24&dayEnd=2018-11-27&pageNo=9", cookies, headers, "utf-8");
        String result = JSON.parseObject(data).getString("result");

        List<Map> content = JSON.parseArray(result, Map.class);
        System.out.println(content.size());
        Map<String, Object> row = content.get(content.size() - 1);
        for(String k : row.keySet()) {
            System.out.println(k + ":" + row.get(k));
        }
    }
}
