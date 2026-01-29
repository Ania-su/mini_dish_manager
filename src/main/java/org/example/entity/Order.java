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

    public enum PaymentStatus {
        PAID, UNPAID
    }

    private int id;
    private String reference;
    private Instant creationDatetime;
    private List<DishOrder> dishOrders;
    private Sale sale;
    private PaymentStatus paymentStatus;

    public Double getTotalAmountWithoutVAT() {
        if (dishOrders == null || dishOrders.isEmpty()) {
            return 0.0;
        }

        double total = 0.0;
        for (DishOrder dishOrder : dishOrders) {
            Dish dish = dishOrder.getDish();
            Integer quantity = dishOrder.getQuantity();

            if (dish.getSellingPrice() == null) {
                throw new IllegalArgumentException(
                        "Selling price is null for dish : " + dish.getName()
                );
            }
            total += dish.getSellingPrice() * quantity;
        }
        return total;
    }

    public Double getTotalAmountWithVAT() {
        final double VAT_RATE = 0.20;
        double totalWithoutVAT = getTotalAmountWithoutVAT();
        return totalWithoutVAT * (1 + VAT_RATE);
    }
}
