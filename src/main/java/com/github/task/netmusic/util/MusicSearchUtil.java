package com.task.netmusic.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.system.util.HttpUtils;
import org.apache.http.HttpResponse;

import java.util.HashMap;
import java.util.Map;

public class MusicSearchUtil {
    private static final String cookie = "";
    private static final String url = "https://music.163.com/weapi/song/enhance/player/url/v1?csrf_token=";
    private static final String searchUrl = "http://music.163.com/api/search/get/web";
    private static final String limit = "10";
    private static final String imageUrl = "https://api.vvhan.com/api/music";

    public static void main(String[] args) {
//5238992
//        String playUrl = getPlayUrl("5238992");
//        System.out.println(playUrl);
        Map<String, String> map = MusicSearchUtil.searchMusicReturnResultBot("让风告诉你");
        String id = map.get("id");
        System.out.println(id);
    }

    /**
     * 根据id匹配曲图
     * @param musicId
     * @return
     */
    public static Map<String,String> getImageAndSoOnById(String musicId) {
        Map<String,String> map = new HashMap<>();
        Map<String, String> querys = new HashMap<>();
        querys.put("id", musicId);
        querys.put("type", "song");
        querys.put("media", "netease");
        try {
            HttpResponse httpResponse = HttpUtils.doGet(imageUrl, null, HttpUtils.getHeaders(), querys);
            JSONObject json = HttpUtils.getJson(httpResponse);
            map.put("cover",json.getString("cover"));
            map.put("name",json.getString("name"));
            map.put("mp3url",json.getString("mp3url"));
            map.put("song_id",json.getString("song_id"));
            map.put("author",json.getString("author"));
            return map;
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * 输出排版好的搜索结果
     * @param musicName
     * @return
     */
    public static String searchMusicReturnResult(String musicName) {
        JSONObject jsonObject = searchMusic(musicName, limit);
        JSONArray songs = jsonObject.getJSONObject("result").getJSONArray("songs");
        StringBuilder stringBuilder = new StringBuilder("-------网易云解析-------\n");
        for (int i = 0; i < songs.size(); i++) {
            JSONObject song = songs.getJSONObject(i);
            String songName = song.getString("name") +"_"+ song.getJSONObject("album").getString("name");
            stringBuilder.append(i+1).append(". ").append(songName);
            JSONArray alias = song.getJSONObject("album").getJSONArray("alia");
            if (alias != null && alias.size() > 0){
                stringBuilder.append("(").append(alias.get(0)).append(")");
            }
            JSONArray artists = song.getJSONArray("artists");
            stringBuilder.append(" - ");
            for (int i1 = 0; i1 < artists.size(); i1++) {
                stringBuilder.append(artists.getJSONObject(i1).getString("name"));
                if (i1 != artists.size()-1){
                    stringBuilder.append("|");
                }
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    /**
     * 输出排版好的搜索结果和一个歌曲id数组
     * @param musicName
     * @return
     */
    public static Map<String,String> searchMusicReturnResultBot(String musicName) {
        Map<String,String> map = new HashMap<>();
        JSONObject jsonObject = searchMusic(musicName, limit);
        JSONArray songs = jsonObject.getJSONObject("result").getJSONArray("songs");
        JSONArray infos = new JSONArray();
        StringBuilder stringBuilder = new StringBuilder("-------网易云解析-------\n");
        for (int i = 0; i < songs.size(); i++) {
            JSONObject song = songs.getJSONObject(i);
            JSONObject info = new JSONObject();
            info.put("id",song.getString("id"));
            String songName = song.getString("name") +"_"+ song.getJSONObject("album").getString("name");
            stringBuilder.append(i+1).append(". ").append(songName);
            JSONArray alias = song.getJSONObject("album").getJSONArray("alia");
            if (alias != null && alias.size() > 0){
                stringBuilder.append("(").append(alias.get(0)).append(")");
                info.put("content",alias.get(0));
            }
            JSONArray artists = song.getJSONArray("artists");
            String title = songName + " - ";
            stringBuilder.append(" - ");
            for (int i1 = 0; i1 < artists.size(); i1++) {
                stringBuilder.append(artists.getJSONObject(i1).getString("name"));
                title = title + artists.getJSONObject(i1).getString("name");
                if (i1 != artists.size()-1){
                    stringBuilder.append("|");
                    title = title + "|";
                }
            }
            info.put("title",title);
            infos.set(i,info);
            stringBuilder.append("\n");
        }
        map.put("str",stringBuilder.toString());
        map.put("infos", infos.toJSONString());
        return map;
    }

    /**
     * 搜索音乐
     * @param musicName
     * @param limit
     * @return
     */
    public static JSONObject searchMusic(String musicName, String limit) {
        Map<String, String> querys = new HashMap<>();
        querys.put("s", musicName);
        querys.put("type", "1");
//        querys.put("offset","0");
        querys.put("total", "true");
        querys.put("limit", limit);
        try {
            HttpResponse httpResponse = HttpUtils.doGet(searchUrl, null, HttpUtils.getHeaders(), querys);
            return HttpUtils.getJson(httpResponse);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    /**
     * 根据id获取播放链接
     * @param musicId
     * @return
     */
    public static String getPlayUrl(String musicId) {
        JSONObject connect = NeteaseMusicUtil.getMusicJsonByMusicId(musicId, cookie);
        if (connect == null) {
            return null;
        }
        JSONArray data = connect.getJSONArray("data");
        JSONObject jsonObject = data.getJSONObject(0);
        return jsonObject.getString("url");
    }

}
