package com.library.model;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    private Long id;
    private Long authorId;
    private String title;
    private String description;
    private String image;

    public Book(String title, String description, Long authorId, String image) {
        this.title = title;
        this.description = description;
        this.authorId = authorId;
        this.image = image;
    }

}
