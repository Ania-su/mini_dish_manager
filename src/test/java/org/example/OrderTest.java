package org.example;

import org.example.entity.Dish;
import org.example.entity.DishOrder;
import org.example.entity.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    private DBConnection dbconnection;
    private DataRetriever dataRetriever;
    @BeforeEach
    void setUp() {
        dbconnection = new DBConnection();
        dataRetriever = new DataRetriever(dbconnection);
    }

    @Test
    void saveOrder() throws SQLException {
        Dish dish = dataRetriever.findDishById(100);

        DishOrder dishOrder = new DishOrder();
        dishOrder.setDish(dish);
        dishOrder.setQuantity(1);

        Order order = new Order();
        order.setId(100);
        order.setCreationDatetime(Instant.now());
        order.setDishOrders(List.of(dishOrder));

        Order saved = dataRetriever.saveOrder(order);
        assertNotNull(saved);
        assertEquals(1, saved.getDishOrders().size());
    }

    @Test
    void findOrderByReference() throws SQLException {
        Order order = dataRetriever.findOrderByReference("ORD00100");
        assertNotNull(order);
        assertEquals(1, order.getDishOrders().size());
    }

    @Test
    void findDishOrderByIdOrder() throws SQLException {
        List<DishOrder> dishOrders = dataRetriever.findDishOrderByIdOrder(100);
        assertFalse(dishOrders.isEmpty());
    }
}