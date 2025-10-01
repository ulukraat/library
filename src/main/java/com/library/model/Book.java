package com.library.model;

public class Book {
    private Long id;
    private Long authorId;
    private String title;
    private String description;
    private String image;



    public Book(String title, String description,Long authorId, String image) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.authorId = authorId;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public Book(){}
}
