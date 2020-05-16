package com.gwidgets.mongotest;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@NoArgsConstructor
public class Person
{
    String id;

    String name;

    public Person(String name) {
        this.name = name;
    }
}
