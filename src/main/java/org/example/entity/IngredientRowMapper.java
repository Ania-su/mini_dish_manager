package org.example.entity;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IngredientRowMapper {

    public Ingredient map(ResultSet rs) throws SQLException {

        int id = rs.getInt("ingredient_id");

        String name = rs.getString("ingredient_name");

        double price = rs.getDouble("ingredient_price");

        Ingredient.CategoryEnum category = Ingredient.CategoryEnum.valueOf(rs.getString("ingredient_category"));

        return new Ingredient(
                id, name, price, category
        );
    }
}
