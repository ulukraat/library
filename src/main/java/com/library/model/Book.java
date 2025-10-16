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
}
