package com.zz.jmeter.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * ************************************
 * create by Intellij IDEA
 *
 * @author Francis.zz
 * @date   2022-06-10 10:37
 * ************************************
 */
public class JsonUtil {
    private static void parseJson2Map(Map<String, String> map, JsonObject jsonObject, String parentKey) {
        for (Map.Entry<String, JsonElement> object : jsonObject.entrySet()) {
            String key = object.getKey();
            JsonElement value = object.getValue();
            String fullkey = (null == parentKey || parentKey.trim().equals("")) ? key : key;// parentKey.trim() + "." + key;
            if (value.isJsonObject()) {
                parseJson2Map(map, value.getAsJsonObject(), fullkey);
            } else if (value.isJsonArray()) {
                JsonArray jsonArray = value.getAsJsonArray();
                for (JsonElement jsonElement1 : jsonArray) {
                    try {
                        jsonElement1.getAsJsonObject();
                    } catch (IllegalStateException ie) {
                        flatValue(map, fullkey, jsonElement1.getAsString());
                        continue;
                    }
                    parseJson2Map(map, jsonElement1.getAsJsonObject(), fullkey);
                }
            } else if (value.isJsonPrimitive()) {
                isJsonPrimitive(map, fullkey, value);
            }
        }
    }

    private static void isJsonPrimitive(Map<String, String> map, String fullkey, JsonElement value) {
        try {
            JsonElement element = new JsonParser().parse(value.getAsString());
            if (element.isJsonNull()) {
                flatValue(map, fullkey, value.getAsString());
            } else if (element.isJsonObject()) {
                parseJson2Map(map, element.getAsJsonObject(), fullkey);
            } else if (element.isJsonPrimitive()) {
                JsonPrimitive jsonPrimitive = element.getAsJsonPrimitive();

                if (jsonPrimitive.isNumber()) {
                    flatValue(map, fullkey, jsonPrimitive.getAsNumber());
                } else {
                    flatValue(map, fullkey, jsonPrimitive.getAsString());
                }
            } else if (element.isJsonArray()) {
                JsonArray jsonArray = element.getAsJsonArray();
                Iterator<JsonElement> iterator = jsonArray.iterator();
                while (iterator.hasNext()) {
                    parseJson2Map(map, iterator.next().getAsJsonObject(), fullkey);
                }
            }
        } catch (Exception e) {
            flatValue(map, fullkey, value.getAsString());
        }
    }

    private static void flatValue(Map<String, String> map, String key, Object value) {
        Object flag = map.get(key);
        if ((flag != null) && (!"".equals(flag))) {
            map.put(key, flag.toString().trim() + "," + value.toString());
        } else {
            map.put(key, value.toString());
        }
    }

    public static void parseJson2Map(Map<String, String> map, String json) {
        JsonElement jsonElement = new JsonParser().parse(json);
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            parseJson2Map(map, jsonObject, null);
        } else if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (JsonElement element : jsonArray) {
                parseJson2Map(map, element.getAsJsonObject(), null);
            }
        } else if (jsonElement.isJsonPrimitive()) {
            //log.info("json illegal: {}", json);
        } else if (jsonElement.isJsonNull()) {

        }
    }

    public static void main(String[] args) {
        String metadata = "{\"traceid\":\"name\",\"respStrategy\":0,\"extParams\":\"{\\\"type\\\": \\\"1\\\"}\"}";
        Map<String, String> result = new HashMap<>();
       parseJson2Map(result, metadata);

        System.out.println(result);
        System.out.println(result.get("type"));
    }
}
