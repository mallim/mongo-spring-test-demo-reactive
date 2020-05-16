package com.gwidgets.mongotest;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Taxi
{
    @Id
    private String id;

    private String number;

    private int noOfSeats;

}
