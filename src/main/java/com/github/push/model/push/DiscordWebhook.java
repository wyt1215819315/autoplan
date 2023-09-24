package com.github.push.model.push;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.awt.*;
import java.util.List;
import java.util.*;

@RequiredArgsConstructor
@Data
public class DiscordWebhook {

    private String content;
    private String username;
    private String avatarUrl;
    private boolean tts;
    private List<EmbedObject> embeds = new ArrayList<>();

    public void addEmbed(EmbedObject embed) {
        this.embeds.add(embed);
    }

    public String execute() {
        if (content == null && embeds.isEmpty()) {
            throw new IllegalArgumentException("Set content or add at least one EmbedObject");
        }
        JSONObject json = new JSONObject();
        json.put("content", content);
        json.put("username", username);
        json.put("avatar_url", avatarUrl);
        json.put("tts", tts);
        if (!embeds.isEmpty()) {
            List<JSONObject> embedObjects = new ArrayList<>();
            for (EmbedObject embed : embeds) {
                JSONObject jsonEmbed = new JSONObject();
                jsonEmbed.put("title", embed.getTitle());
                jsonEmbed.put("description", embed.getDescription());
                jsonEmbed.put("url", embed.getUrl());

                if (embed.getColor() != null) {
                    Color color = embed.getColor();
                    int rgb = color.getRed();
                    rgb = (rgb << 8) + color.getGreen();
                    rgb = (rgb << 8) + color.getBlue();

                    jsonEmbed.put("color", rgb);
                }
                EmbedObject.Footer footer = embed.getFooter();
                EmbedObject.Image image = embed.getImage();
                EmbedObject.Thumbnail thumbnail = embed.getThumbnail();
                EmbedObject.Author author = embed.getAuthor();
                List<EmbedObject.Field> fields = embed.getFields();
                if (footer != null) {
                    JSONObject jsonFooter = new JSONObject();
                    jsonFooter.put("text", footer.getText());
                    jsonFooter.put("icon_url", footer.getIconUrl());
                    jsonEmbed.put("footer", jsonFooter);
                }
                if (image != null) {
                    JSONObject jsonImage = new JSONObject();
                    jsonImage.put("url", image.getUrl());
                    jsonEmbed.put("image", jsonImage);
                }
                if (thumbnail != null) {
                    JSONObject jsonThumbnail = new JSONObject();
                    jsonThumbnail.put("url", thumbnail.getUrl());
                    jsonEmbed.put("thumbnail", jsonThumbnail);
                }
                if (author != null) {
                    JSONObject jsonAuthor = new JSONObject();
                    jsonAuthor.put("name", author.getName());
                    jsonAuthor.put("url", author.getUrl());
                    jsonAuthor.put("icon_url", author.getIconUrl());
                    jsonEmbed.put("author", jsonAuthor);
                }
                List<JSONObject> jsonFields = new ArrayList<>();
                for (EmbedObject.Field field : fields) {
                    JSONObject jsonField = new JSONObject();
                    jsonField.put("name", field.getName());
                    jsonField.put("value", field.getValue());
                    jsonField.put("inline", field.isInline());
                    jsonFields.add(jsonField);
                }
                jsonEmbed.put("fields", jsonFields.toArray());
                embedObjects.add(jsonEmbed);
            }
            json.put("embeds", embedObjects.toArray());
        }
        return json.toJSONString();
    }

    @Data
    public static class EmbedObject {
        private final List<Field> fields = new ArrayList<>();
        private String title;
        private String description;
        private String url;
        private Color color;
        private Footer footer;
        private Thumbnail thumbnail;
        private Image image;
        private Author author;

        public EmbedObject setThumbnail(String url) {
            this.thumbnail = new Thumbnail(url);
            return this;
        }

        public EmbedObject setImage(String url) {
            this.image = new Image(url);
            return this;
        }

        public EmbedObject setFooter(String text, String icon) {
            this.footer = new Footer(text, icon);
            return this;
        }

        public EmbedObject setAuthor(String name, String url, String icon) {
            this.author = new Author(name, url, icon);
            return this;
        }

        public EmbedObject addField(String name, String value, boolean inline) {
            this.fields.add(new Field(name, value, inline));
            return this;
        }

        @AllArgsConstructor
        @Getter
        private static class Image {
            private final String url;
        }

        @AllArgsConstructor
        @Getter
        private static class Author {
            private final String name;
            private final String url;
            private final String iconUrl;
        }

        @AllArgsConstructor
        @Getter
        private static class Field {
            private final String name;
            private final String value;
            private final boolean inline;
        }

        @AllArgsConstructor
        @Getter
        private static class Footer {
            private final String text;
            private final String iconUrl;
        }

        @AllArgsConstructor
        @Getter
        private static class Thumbnail {
            private final String url;
        }
    }

}
