package com.tmyx.backend.dto;

public class ArticleUpdateDTO {
    private String id;
    private String title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "articleUpdateDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                '}';
    }

}
