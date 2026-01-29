package org.example;

import org.example.entity.Ingredient;
import org.example.entity.StockMovement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IngredientTest {

    private DBConnection dbconnection;
    private DataRetriever dataRetriever;

    @BeforeEach
    public void setup() {
        dbconnection = new DBConnection();
        dataRetriever = new DataRetriever(dbconnection);
    }

    @Test
    void findIngredients() {
        List<Ingredient> ingredients = dataRetriever.findIngredients(1, 10);
        assertFalse(ingredients.isEmpty());
    }

    @Test
    void createIngredients() throws SQLException {
        Ingredient onion = new Ingredient(101, "Onion", 0.3, Ingredient.CategoryEnum.VEGETABLE);
        List<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(onion);

        List<Ingredient> created = dataRetriever.createIngredients(ingredients);
        assertEquals(1, created.size());
        assertEquals("Onion", created.getFirst().getName());
    }

//    @Test
//    void findIngredientsByCriteria() throws SQLException {
//
//        List<Ingredient> resultByCategory = dataRetriever.findIngredientsByCriteria(null, Ingredient.CategoryEnum.VEGETABLE, null, 1, 10);
//        assertFalse(resultByCategory.isEmpty());
//        assertTrue(resultByCategory.stream().allMatch(i -> i.getCategory() == Ingredient.CategoryEnum.VEGETABLE));
//
//        List<Ingredient> resultByDish = dataRetriever.findIngredientsByCriteria(null, null, "Salad", 1, 10);
//        assertFalse(resultByDish.isEmpty());
//
//        List<Ingredient> resultCombined = dataRetriever.findIngredientsByCriteria("Tomato", Ingredient.CategoryEnum.VEGETABLE, "Salad", 1, 10);
//        assertFalse(resultCombined.isEmpty());
//        assertTrue(resultCombined.stream().allMatch(i -> i.getName().contains("Tomato") && i.getCategory() == Ingredient.CategoryEnum.VEGETABLE));
//    }

    @Test
    void saveIngredient() throws SQLException {
        Ingredient tomato = new Ingredient();
        tomato.setId(100);
        tomato.setName("Tomato");
        tomato.setPrice(0.5);
        tomato.setCategory(Ingredient.CategoryEnum.VEGETABLE);

        Ingredient saved = dataRetriever.saveIngredient(tomato);
        assertNotNull(saved);
        assertEquals("Tomato", saved.getName());
    }

    @Test
    public void testFindStockMovementByIngredientId() throws SQLException {
        List<StockMovement> movements = dataRetriever.findStockMovementByIngredientId(100);
        assertNotNull(movements);
    }
}