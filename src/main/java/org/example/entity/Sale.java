package org.example.entity;

import lombok.*;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString

public class Sale {
    private int id;
    private Instant creationDatetime;
    private Order order;
}
