package com.example.githubreviewertool.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Repository {
    private String name;
    private String description;
    private String language;
}