package com.github.push.model.push;

import cn.hutool.json.JSONObject;
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

    public JSONObject execute() {
        if (content == null && embeds.isEmpty()) {
            throw new IllegalArgumentException("Set content or add at least one EmbedObject");
        }
        JSONObject json = new JSONObject();
        json.set("content", content);
        json.set("username", username);
        json.set("avatar_url", avatarUrl);
        json.set("tts", tts);
        if (!embeds.isEmpty()) {
            List<JSONObject> embedObjects = new ArrayList<>();
            for (EmbedObject embed : embeds) {
                JSONObject jsonEmbed = new JSONObject();
                jsonEmbed.set("title", embed.getTitle());
                jsonEmbed.set("description", embed.getDescription());
                jsonEmbed.set("url", embed.getUrl());
                if (embed.getColor() != null) {
                    Color color = embed.getColor();
                    int rgb = color.getRed();
                    rgb = (rgb << 8) + color.getGreen();
                    rgb = (rgb << 8) + color.getBlue();
                    jsonEmbed.set("color", rgb);
                }
                EmbedObject.Footer footer = embed.getFooter();
                EmbedObject.Image image = embed.getImage();
                EmbedObject.Thumbnail thumbnail = embed.getThumbnail();
                EmbedObject.Author author = embed.getAuthor();
                List<EmbedObject.Field> fields = embed.getFields();
                if (footer != null) {
                    JSONObject jsonFooter = new JSONObject();
                    jsonFooter.set("text", footer.getText());
                    jsonFooter.set("icon_url", footer.getIconUrl());
                    jsonEmbed.set("footer", jsonFooter);
                }
                if (image != null) {
                    JSONObject jsonImage = new JSONObject();
                    jsonImage.set("url", image.getUrl());
                    jsonEmbed.set("image", jsonImage);
                }
                if (thumbnail != null) {
                    JSONObject jsonThumbnail = new JSONObject();
                    jsonThumbnail.set("url", thumbnail.getUrl());
                    jsonEmbed.set("thumbnail", jsonThumbnail);
                }
                if (author != null) {
                    JSONObject jsonAuthor = new JSONObject();
                    jsonAuthor.set("name", author.getName());
                    jsonAuthor.set("url", author.getUrl());
                    jsonAuthor.set("icon_url", author.getIconUrl());
                    jsonEmbed.set("author", jsonAuthor);
                }
                List<JSONObject> jsonFields = new ArrayList<>();
                for (EmbedObject.Field field : fields) {
                    JSONObject jsonField = new JSONObject();
                    jsonField.set("name", field.getName());
                    jsonField.set("value", field.getValue());
                    jsonField.set("inline", field.isInline());
                    jsonFields.add(jsonField);
                }
                jsonEmbed.set("fields", jsonFields.toArray());
                embedObjects.add(jsonEmbed);
            }
            json.set("embeds", embedObjects.toArray());
        }
        return json;
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

        @Getter
        private record Image(String url) {
        }

        @Getter
        private record Author(String name, String url, String iconUrl) {
        }

        @Getter
        private record Field(String name, String value, boolean inline) {
        }

        @Getter
        private record Footer(String text, String iconUrl) {
        }

        @Getter
        private record Thumbnail(String url) {
        }
    }

}
