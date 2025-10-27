package com.library.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

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
