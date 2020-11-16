package com.orchid.jsoup.util;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.map.HashedMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdParseUtil {

    public static Integer getProductCount(String doc){
        return null;
    }

    /**
     * 获取京东家电数据
     * @param source
     * @return
     */
    public static String getJDProducts(String source){
        Document doc = Jsoup.parse(source);

        Elements elements=doc.select("#J_goodsList > ul > li");
        List<Map<String,String>> products=new ArrayList<>();
        elements.forEach(element -> {
            Map<String,String> map=new HashMap<>();
            map.put("id", element.attr("data-sku"));
            map.put("name", element.select("div > div.p-name > a> em").first().text());
            products.add(map);
            System.out.println(JSON.toJSONString(map));
        });
        return JSON.toJSONString(products);
    }
    public static void main(String[] args) throws IOException {
        File file=new File("/Users/biejunyang/Documents/file.html");
        Document doc = Jsoup.parse(file, "UTF-8", "http://example.com/");

        Elements elements=doc.select("#J_goodsList > ul > li");
        List<Map<String,String>> products=new ArrayList<>();
        elements.forEach(element -> {
            Map<String,String> map=new HashMap<>();
            map.put("id", element.attr("data-sku"));
            map.put("name", element.select("div > div.p-name > a> em").first().text());
            products.add(map);
            System.out.println(JSON.toJSONString(map));
        });

    }

}
