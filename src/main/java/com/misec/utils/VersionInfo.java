package com.misec.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

/**
 * version info.
 *
 * @author Junzhou Liu
 * @since 2020/11/21 15:22
 */
@Log4j2
@Data
public class VersionInfo {
    private static String releaseVersion = "";
    private static String releaseDate = "";
    private static String projectRepo = "https://github.com/JunzhouLiu/BILIBILI-HELPER-PRE";
    private static String releaseInfo = "";

    public static void initInfo() {
        String release = LoadFileResource.loadJsonFromAsset("release.json");
        JsonObject jsonObject = new JsonParser().parse(release).getAsJsonObject();
        releaseVersion = jsonObject.get("tag_main").getAsString();
        releaseDate = jsonObject.get("release_date").getAsString();
        releaseInfo = LoadFileResource.loadJsonFromAsset("release.info");
    }

    public static void printVersionInfo() {
        initInfo();
        JsonObject jsonObject = HttpUtil.doGet("https://api.github.com/repos/JunzhouLiu/BILIBILI-HELPER-PRE/releases/latest");
        log.info("-----版本信息-----");
        log.info("当前版本: {}", releaseVersion);
        try {
            log.info("最新版本为: {}", jsonObject.get("tag_name").getAsString().replaceAll("V", ""));
            log.info("最新版本更新内容: {}", jsonObject.get("body").getAsString().replaceAll("\"", ""));
            log.info("最近更新时间: {}", jsonObject.get("created_at"));
            log.info("项目开源地址: {}", projectRepo);
        } catch (Exception e) {
            log.warn("网络问题，未请求到新版本", e);
        }

    }
}
