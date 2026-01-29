package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class Order {
    private int id;
    private String reference;
    private Instant creationDatetime;
    private List<DishOrder> dishOrders;

    public Double getTotalAmountWithoutVAT() {
        throw new RuntimeException("Not implemented yet.");
    }

    public Double getTotalAmountWithVAT() {
        throw new RuntimeException("Not implemented yet.");
    }
}
