package com.haoa193.creeper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenyong on 2016/1/31.
 */
public class DyGodsCreeper {

    private static final String BASEURL = "http://www.dy2018.com";
    private static final String SEARCHURI = "/e/search/index.php";

//    private int searchDeep = 5;
    private List<String> urlCacheList = new ArrayList<>();
    public static Document getDocument(String url) throws IOException {
        return Jsoup.connect(BASEURL + url).get();
    }
    public static Document getDocument(String url, String filmName) throws IOException {
        return Jsoup.connect(BASEURL + url)
                .data("keyboard", filmName)
                .data("classid", "0")
                .data("tempid", "1")
                .data("show", "title,smalltext")
                .ignoreContentType(true)
                .header("charset", "utf-8")
//                .header("charset", "gb2312")
                .followRedirects(false)
                .timeout(1000 * 60)
                .post();
    }

//    private int searchDeep = 2;
    private static int INIT_DEEP = 2;

    public List<String> getDownloadList(String url, int searchDeep) throws IOException {

        url = buildURL(url);

        if (urlCacheList.contains(url)) {
            return null;
        }

        if (--searchDeep < 0) {
            return null;
        }

        urlCacheList.add(url);

        Document document = getDocument(url);

       Elements elements = document.select("table td a");

        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            String href = element.attr("href");
            if (href.startsWith("ed2k://") || href.startsWith("ftp://")) {
                System.out.println(href);
            } else if (href.endsWith(".html") && urlCacheList.contains(url)) {
                if (searchDeep+1 == INIT_DEEP) {
                    String title = element.text();
                    System.out.println("####film title: " + title);
                }
                try {
                    getDownloadList(href, searchDeep);
//                    System.out.println(href);
                } catch (Exception e) {
                }
            }
        }

        Elements pageElements = document.select(".x > a");
//        System.out.println("page:" + pageElements.size());
        for (int j = 1; j < pageElements.size(); j++) {
            Element element = pageElements.get(j);
            String href = element.attr("href");
//            System.out.println(j+"j href:" + href);
            if (href.startsWith(url.substring(0, url.length()-".html".length()) + "-page-") && href.endsWith(".html")) {
                try {
                    getDownloadList(href, INIT_DEEP);
//                    System.out.println(href);
                } catch (Exception e) {
                }
            }
        }
        return null;
    }

    private String buildURL(String url) {
        if (url.startsWith(BASEURL)) {
            url = url.substring(url.indexOf(BASEURL) + BASEURL.length(), url.length());
        }
        if (!url.startsWith("/")) {
            url = "/" + url;
        }
        return url;
    }

    public static void main(String[] args) {

        String url = "http://www.dy2018.com/i/96328.html";
//        url = "http://www.dy2018.com/i/95670.html";//蜀山战纪1


        DyGodsCreeper creeper = new DyGodsCreeper();
        try {
//            INIT_DEEP = 2;
//            url = "http://www.dy2018.com/e/search/result/searchid-367076.html";//蜀山战纪1-5
//
//            INIT_DEEP = 1;
//            url = "http://www.dy2018.com/i/96051.html";//芈月传
//            creeper.getDownloadList(url, INIT_DEEP);

            INIT_DEEP = 1;
            String searchName = "速度与激情";
            creeper.searchFilms(searchName);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void searchFilms(String filmName) throws IOException {
        String url = buildURL(SEARCHURI);
        urlCacheList.add(url);

        Document document = getDocument(url, filmName);

//        String docHtml = document.toString();
//        System.out.println(docHtml);

        String title = document.getElementsByTag("title").text();
        System.out.println(title);
        if (title.equalsIgnoreCase("文档已移动")) {
//            System.out.println(2222);
            Elements elements = document.select("body a");
            String redirectURL = elements.first().attr("href");
            if (redirectURL.startsWith("result/searchid")) {
                redirectURL = "/e/search/" + redirectURL;
            }
            System.out.println(redirectURL);
            try {

                getDownloadList(redirectURL, INIT_DEEP);
//                    System.out.println(href);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

  /*  public List<String> getSearchFilmsList(String url, String filmName) throws IOException {

        url = buildURL(url);

        Document document = getDocument(url, filmName);

        Elements elements = document.select("table tbody td a");

        for (int i = 0; i < elements.size(); i++) {

            Element element = elements.get(i);

            String href = element.attr("href");

            if (href.startsWith("ed2k://") || href.startsWith("ftp://")) {
                System.out.println(href);
            } else if (href.startsWith("/i/") && urlCacheList.contains(url)) {
                String title = element.text();
                System.out.println("####film title: " + title);
                try {
                    getDownloadList(href, 2);
//                    System.out.println(href);

                } catch (Exception e) {

                    e.printStackTrace();
                }

            }

        }
        return null;
    }*/

}
