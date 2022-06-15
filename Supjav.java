package com.github.catvod.spider;

import android.text.TextUtils;

import com.github.catvod.utils.Misc;
import com.github.catvod.utils.okhttp.OkHttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Supjav {

    private static final String siteUrl = "https://supjav.com/zh/";
    private static final String playUrl = "https://lk1.supremejav.com/";

    private HashMap<String, String> getHeaders() {
        return getHeaders(siteUrl);
    }

    private HashMap<String, String> getHeaders(String referer) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("User-Agent", Misc.UaWinChrome);
        headers.put("Referer", referer);
        return headers;
    }

    public String a(boolean filter) {
        try {
            JSONArray classes = new JSONArray();
            JSONArray videos = new JSONArray();
            Document doc = Jsoup.parse(OkHttpUtil.string(siteUrl, getHeaders()));
            for (Element element : doc.select("ul.nav > li > a")) {
                String href = element.attr("href");
                if (href.split("/").length < 5) continue;
                String typeName = element.text();
                JSONObject object = new JSONObject();
                object.put("type_id", href.replace(siteUrl, ""));
                object.put("type_name", typeName);
                classes.put(object);
            }
            for (Element element : doc.select("div.post")) {
                String src = element.select("img").attr("src");
                String data = element.select("img").attr("data-original");
                String url = element.select("a").attr("href");
                String name = element.select("a").attr("title");
                String img = TextUtils.isEmpty(data) ? src : data;
                String id = url.split("/")[4];
                JSONObject v = new JSONObject();
                v.put("vod_id", id);
                v.put("vod_name", name);
                v.put("vod_pic", img);
                videos.put(v);
            }
            JSONObject result = new JSONObject();
            result.put("class", classes);
            result.put("list", videos);
            return result.toString();
        } catch (JSONException e) {
            return "";
        }
    }

    public String aa(String tid, String pg, boolean filter, HashMap<String, String> extend) {
        try {
            int pageCount;
            JSONObject result = new JSONObject();
            JSONArray videos = new JSONArray();
            Document doc = Jsoup.parse(OkHttpUtil.string(siteUrl + tid + "/page/" + pg, getHeaders()));
            try {
                Elements elements = doc.select("div.pagination > ul > li > a");
                pageCount = Integer.parseInt(elements.get(elements.size() - 2).text());
            } catch (Exception e) {
                pageCount = 1;
            }
            for (Element element : doc.select("div.post")) {
                String img = element.select("img").attr("data-original");
                String url = element.select("a").attr("href");
                String name = element.select("a").attr("title");
                String id = url.split("/")[4];
                JSONObject v = new JSONObject();
                v.put("vod_id", id);
                v.put("vod_name", name);
                v.put("vod_pic", img);
                videos.put(v);
            }
            result.put("page", pg);
            result.put("pagecount", pageCount);
            result.put("limit", videos.length());
            result.put("total", videos.length() * pageCount);
            result.put("list", videos);
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String aaa(List<String> ids) {
        try {
            JSONObject result = new JSONObject();
            JSONArray videos = new JSONArray();
            JSONObject video = new JSONObject();
            Map<String, String> sites = new LinkedHashMap<>();
            Document doc = Jsoup.parse(OkHttpUtil.string(siteUrl.concat(ids.get(0)), getHeaders()));
            String name = doc.select("div.post-meta > img").attr("alt");
            String img = doc.select("div.post-meta > img").attr("src");
            String type = doc.select("p.cat > a").text();
            String director = "", actor = "";
            for (Element p : doc.select("div.cats > p")) {
                if (p.select("span").text().contains("Maker")) {
                    director = p.select("a").text();
                }
                if (p.select("span").text().contains("Cast")) {
                    actor = p.select("a").text();
                }
            }
            Elements sources = doc.select("a.btn-server");
            for (int i = 0; i < sources.size(); i++) {
                Element source = sources.get(i);
                String sourceName = source.text();
                if (!sourceName.equals("TV")) continue;
                String sourceUrl = source.attr("data-link");
                List<String> vodItems = new ArrayList<>();
                vodItems.add("播放" + "$" + sourceUrl);
                sites.put(sourceName, TextUtils.join("#", vodItems));
            }
            if (sites.size() > 0) {
                String vod_play_from = TextUtils.join("$$$", sites.keySet());
                String vod_play_url = TextUtils.join("$$$", sites.values());
                video.put("vod_play_from", vod_play_from);
                video.put("vod_play_url", vod_play_url);
            }
            video.put("vod_id", ids.get(0));
            video.put("vod_pic", img);
            video.put("type_name", type);
            video.put("vod_name", name);
            video.put("vod_actor", actor);
            video.put("vod_director", director);
            videos.put(video);
            result.put("list", videos);
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String aaaa(String key, boolean quick) {
        try {
            JSONArray videos = new JSONArray();
            Document doc = Jsoup.parse(OkHttpUtil.string(siteUrl.concat("?s=").concat(URLEncoder.encode(key)), getHeaders()));
            for (Element element : doc.select("div.post")) {
                String img = element.select("img").attr("data-original");
                String url = element.select("a").attr("href");
                String name = element.select("a").attr("title");
                String id = url.split("/")[4];
                JSONObject v = new JSONObject();
                v.put("vod_id", id);
                v.put("vod_name", name);
                v.put("vod_pic", img);
                videos.put(v);
            }
            JSONObject result = new JSONObject();
            result.put("list", videos);
            return result.toString();
        } catch (JSONException e) {
            return "";
        }
    }

    public String aaaaa(String flag, String id, List<String> vipFlags) {
        try {
            JSONObject result = new JSONObject();
            Map<String, List<String>> respHeaders = new HashMap<>();
            OkHttpUtil.stringNoRedirect(playUrl + "supjav.php?c=" + new StringBuilder(id).reverse(), getHeaders(playUrl), respHeaders);
            String redirect = OkHttpUtil.getRedirectLocation(respHeaders);
            if (flag.equals("TV")) parseTV(result, redirect);
            result.put("parse", "0");
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private void parseTV(JSONObject result, String redirect) throws Exception {
        String data = OkHttpUtil.string(redirect, getHeaders(playUrl));
        result.put("header", new JSONObject(getHeaders(redirect)).toString());
        for (String var : data.split("var")) {
            if (var.contains("urlPlay")) {
                result.put("url", var.split("'")[1]);
                break;
            }
        }
    }
}
