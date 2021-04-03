package com.orchid.jsoup.util;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.map.HashedMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JdParseUtil {
    private static final Pattern SHOP_PATTERN=Pattern.compile("index-[0-9]*.html");

    public static Integer getProductCount(String source){
        Document doc = Jsoup.parse(source);
        Elements elements=doc.select("#J_bottomPage > .p-skip > em > b");
        if(elements.size()>0){
            int totalPage=Integer.parseInt(elements.first().text());
            return totalPage;
        }
        return 0;
    }

    /**
     * 获取京东空调数据
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
            map.put("price", element.select("div > div.p-price > strong > i").first().text());
            Element e=element.select("div > div.p-shop > span > a").first();
            if(e!=null){
                map.put("shopName", e.attr("title"));
                String shopHref=e.attr("href");
                Matcher matcher=SHOP_PATTERN.matcher(shopHref);
                if(matcher.find()){
                    String shopInfo=matcher.group();
                    map.put("shopId", shopInfo.substring(shopInfo.indexOf("-")+1, shopInfo.indexOf(".")));
                }
            }
            products.add(map);
        });
        return JSON.toJSONString(products);
    }


    public static void main(String[] args) throws IOException {
        File file=new File("D:/file1.txt");
        Document doc = Jsoup.parse(file, "UTF-8", "http://example.com/");

//        Elements elements=doc.select("#J_goodsList > ul > li");
//        List<Map<String,String>> products=new ArrayList<>();
//        elements.forEach(element -> {
//            Map<String,String> map=new HashMap<>();
//            map.put("id", element.attr("data-sku"));
//            map.put("name", element.select("div > div.p-name > a> em").first().text());
//            map.put("price", element.select("div > div.p-price > strong > i").first().text());
//            Element e=element.select("div > div.p-shop > span > a").first();
//            if(e!=null){
//                map.put("shopName", e.attr("title"));
//                String shopHref=e.attr("href");
//                Matcher matcher=SHOP_PATTERN.matcher(shopHref);
//                if(matcher.find()){
//                    String shopInfo=matcher.group();
//                    map.put("shopId", shopInfo.substring(shopInfo.indexOf("-")+1, shopInfo.indexOf(".")));
//                }
//            }
//            products.add(map);
//            System.out.println(JSON.toJSONString(map));
//        });
        Elements elements=doc.select("#J_bottomPage > .p-skip > em > b");
        int totalPage=0;
        if(elements.size()>0){
            totalPage=Integer.parseInt(elements.first().text());
        }
        System.out.println(totalPage);
    }

}
