package org.example;

import org.example.entity.Dish;
import org.example.entity.DishIngredient;
import org.example.entity.Ingredient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DishTest {

    private DBConnection dbconnection;
    private DataRetriever dataRetriever;
    @BeforeEach
    void setUp() throws SQLException {
        dbconnection = new DBConnection();
        dataRetriever = new DataRetriever(dbconnection);

    }


    @Test
    void saveDish() {
        Ingredient tomato = new Ingredient();
        tomato.setId(100);
        tomato.setName("Tomato");
        tomato.setPrice(0.5);
        tomato.setCategory(Ingredient.CategoryEnum.VEGETABLE);

        Dish dish = new Dish();
        dish.setId(100);
        dish.setName("Salad");
        dish.setDishType(Dish.DishTypeEnum.START);
        dish.setSellingPrice(5.0);
        dish.setIngredients(new ArrayList<>());
        dataRetriever.saveDish(dish);


        assertEquals("Salad", dish.getName());
    }

    @Test
    void findDishById() throws SQLException {
        Dish dish = dataRetriever.findDishById(100);
        assertNotNull(dish);
        assertEquals("Salad", dish.getName());
    }

    @Test
    void findDishByIngredientName() throws SQLException {
        List<Dish> dishes = dataRetriever.findDishByIngredientName("Tomato");
        for (Dish dish : dishes) {
            boolean hasSalad = dish.getIngredients().stream()
                    .anyMatch(di -> "Salad".equals(di.getIngredient().getName()));
            assertTrue(hasSalad, "Dish " + dish.getName() + " does not contain Salad");
        }
    }
}