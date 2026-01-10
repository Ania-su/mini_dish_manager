package org.example;

import jdk.jfr.Category;
import org.example.entity.Dish;
import org.example.entity.Ingredient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
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
    void shouldReturnEmptyListFindIngredientsTest() {
        List<Ingredient> ingredients = dataRetriever.findIngredients(3, 5);

        assertNotNull(ingredients);
        assertTrue(ingredients.isEmpty());
    }

    @Test
    void createIngredientsFirstTest() throws SQLException {
        Ingredient fromage = new Ingredient(
                12,
                "Fromage",
                1200.0,
                Ingredient.CategoryEnum.DIARY,
                null
        );

        Ingredient oignon = new Ingredient(
                13,
                "Oignon",
                500.0,
                Ingredient.CategoryEnum.VEGETABLE,
                null
        );

        List<Ingredient> ingredients = List.of(fromage, oignon);

        List<Ingredient> result =
                dataRetriever.createIngredients(ingredients);

        assertEquals(2, result.size());
        assertTrue(
                result.stream().anyMatch(i -> i.getName().equals("Fromage"))
        );
        assertTrue(
                result.stream().anyMatch(i -> i.getName().equals("Oignon"))
        );
    }

    @Test
    void createIngredientsSecondTest() throws SQLException {

        int beforeCount = dataRetriever.findIngredients(1, 100).size();

        Ingredient carotte = new Ingredient(
                18,
                "Carotte",
                2000.0,
                Ingredient.CategoryEnum.VEGETABLE,
                null
        );

        Ingredient laitue = new Ingredient(
                8,
                "Laitue",
                2000.0,
                Ingredient.CategoryEnum.VEGETABLE,
                null
        );

        List<Ingredient> ingredients = List.of(carotte, laitue);

        assertThrows(
                RuntimeException.class,
                () -> dataRetriever.createIngredients(ingredients)
        );

        int afterCount = dataRetriever.findIngredients(1, 100).size();

        assertEquals(beforeCount, afterCount, "Transaction should be rolled back");
    }
}