package org.example;

import org.example.entity.Dish;
import org.example.entity.Ingredient;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataRetriever {
    private final DBConnection dbconnection;

    public DataRetriever(DBConnection dbconnection) {
        this.dbconnection = dbconnection;
    }

    public Dish findDishById(int id) {
        String sql = """
            SELECT d.id AS dish_id,
                   d.name AS dish_name,
                   d.dish_type AS dish_type,
                   i.id AS ingredient_id,
                   i.name AS ingredient_name,
                   i.price AS ingredient_price,
                   i.category AS ingredient_category
            FROM dish d
            LEFT JOIN ingredient i ON d.id = i.id_dish
            WHERE d.id = ?
            """;

        try (Connection connection = dbconnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();

            Dish dish = null;
            List<Ingredient> ingredients = new ArrayList<>();

            while (rs.next()) {
                if (dish == null) {
                    dish = new Dish();
                    dish.setId(rs.getInt("dish_id"));
                    dish.setName(rs.getString("dish_name"));
                    dish.setDishType(
                            Dish.DishTypeEnum.valueOf(rs.getString("dish_type"))
                    );
                }

                if (rs.getObject("ingredient_id") != null) {
                    Ingredient ingredient = new Ingredient();
                    ingredient.setId(rs.getInt("ingredient_id"));
                    ingredient.setName(rs.getString("ingredient_name"));
                    ingredient.setPrice(rs.getDouble("ingredient_price"));
                    ingredient.setCategory(
                            Ingredient.CategoryEnum.valueOf(rs.getString("ingredient_category"))
                    );
                    ingredient.setDish(dish);
                    ingredients.add(ingredient);
                }
            }

            if (dish == null) {
                throw new RuntimeException("Dish not found with id " + id);
            }

            dish.setIngredients(ingredients);
            return dish;


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Ingredient> findIngredients(int page, int size) {
        List<Ingredient> ingredients = new ArrayList<>();

        int offset = (page - 1) * size;

        String sql = """
            SELECT id, name, price, category
            FROM ingredient
            ORDER BY id
            LIMIT ? OFFSET ?
        """;

        try (Connection connection = dbconnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, size);
            statement.setInt(2, offset);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Ingredient ingredient = new Ingredient();
                    ingredient.setId(rs.getInt("id"));
                    ingredient.setName(rs.getString("name"));
                    ingredient.setPrice(rs.getDouble("price"));
                    ingredient.setCategory(
                            Ingredient.CategoryEnum.valueOf(rs.getString("category"))
                    );
                    ingredients.add(ingredient);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return ingredients;
    }

    public List<Ingredient> createIngredients(List<Ingredient> newIngredients) throws SQLException {

        Set<String> names = new HashSet<>();
        for (Ingredient ingredient : newIngredients) {
            if (!names.add(ingredient.getName())) {
                throw new RuntimeException(
                        "Duplicate ingredient in provided list: " + ingredient.getName()
                );
            }
        }

        String sql = """
            INSERT INTO ingredient (name, price, category, id_dish)
            VALUES (?, ?, ?::category, ?)
        """;

        DBConnection dbconnection = new DBConnection();
        Connection connection = dbconnection.getConnection();

        connection.setAutoCommit(false);

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            for (Ingredient ingredient : newIngredients) {
                statement.setString(1, ingredient.getName());
                statement.setDouble(2, ingredient.getPrice());
                statement.setObject(3, ingredient.getCategory(), Types.OTHER);

                if (ingredient.getDish() != null) {
                    statement.setInt(4, ingredient.getDish().getId());
                } else {
                    statement.setObject(4, null);
                }

                statement.addBatch();
            }

            statement.executeBatch();
            connection.commit();
            return newIngredients;

        } catch (SQLException e) {
            connection.rollback();
            throw new RuntimeException(e);

        } finally {
            try {
                connection.close();

            } catch (SQLException e) {
                e.printStackTrace();

            }

        }

    }


}
