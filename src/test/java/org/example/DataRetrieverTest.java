package org.example;

import org.example.entity.Dish;
import org.example.entity.Ingredient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DataRetrieverTest {

    private DataRetriever dataRetriever;

    @BeforeEach
    protected void setUp() {
        DBConnection dbconnection = new DBConnection();
        dataRetriever = new DataRetriever(dbconnection);
    }

    @Test
    void findDishesByIngredientsID1Test() {
        Dish dish = dataRetriever.findDishById(1);

        assertNotNull(dish);
        assertEquals("Salade fraîche", dish.getName());
        assertEquals(2, dish.getIngredients().size());

        List<String> ingredientNames = dish.getIngredients()
                .stream()
                .map(Ingredient::getName)
                .toList();

        assertTrue(ingredientNames.contains("Laitue"));
        assertTrue(ingredientNames.contains("Tomate"));
    }

    @Test
    void throwExeptionOfNotFoundDishTest() {
        assertThrows(
                RuntimeException.class,
                () -> dataRetriever.findDishById(999)
        );
    }

    @Test
    void shouldReturnSecondPageOfIngredients() {
        List<Ingredient> ingredients = dataRetriever.findIngredients(2, 2);

        assertEquals(2, ingredients.size());
        assertEquals("Poulet", ingredients.get(0).getName());
        assertEquals("Chocolat", ingredients.get(1).getName());
    }

    @Test
    void shouldReturnEmptyList_whenPageIsOutOfRange() {
        List<Ingredient> ingredients = dataRetriever.findIngredients(3, 5);

        assertNotNull(ingredients);
        assertTrue(ingredients.isEmpty());
    }
}