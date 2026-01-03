package org.example;

import org.example.entity.Dish;
import org.example.entity.Ingredient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {
    private final DBConnection dbconnection;

    public DataRetriever(DBConnection dbconnection) {
        this.dbconnection = dbconnection;
    }

    public Dish findDishById(int id) {
        Dish dish = new Dish();

        String sql = "SELECT d.id, d.name, i.id, i.name FROM Dish i LEFT JOIN Ingredient on d.id = i.id_dish WHERE id = ?";

        try (Connection connection = dbconnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                dish.setId(rs.getInt("id"));
                dish.setName(rs.getString("name"));
                dish.setDishType(Dish.DishTypeEnum.valueOf(rs.getString("dish_type")));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return dish;
    }

    public List<Ingredient> findIngredients(int page, int size) {
        List<Ingredient> ingredients = new ArrayList<>();

        int offset = (page - 1) * size;

        String sql = "SELECT d.id, d.name, i.id, i.name FROM Ingredient i limit ? offset ?";

        try (Connection connection = dbconnection.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, offset);
            statement.setInt(2, size);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Ingredient ingredient = new Ingredient();
                    ingredient.setId(rs.getInt("id"));
                    ingredient.setName(rs.getString("name"));
                    ingredients.add(ingredient);
                }

            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return ingredients;
    }

}
