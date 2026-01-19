package org.example;

import org.example.entity.Dish;
import org.example.entity.DishIngredient;
import org.example.entity.Ingredient;
import org.example.entity.IngredientRowMapper;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataRetriever {
    private final DBConnection dbconnection;
    private IngredientRowMapper ingredientRowMapper = new IngredientRowMapper();

    public DataRetriever(DBConnection dbconnection) {
        this.dbconnection = dbconnection;
    }

    public Dish findDishById(int id) {
        String sql = """
            SELECT d.id AS dish_id,
                   d.name AS dish_name,
                   d.dish_type AS dish_type,
                   d.selling_price
                   i.id AS ingredient_id,
                   i.name AS ingredient_name,
                   i.price AS ingredient_price,
                   i.category AS ingredient_category,
                   di.id AS dishIngredient_id,
                   di.required_quantity,
                   di.unit_type
            FROM dish d
            JOIN DishIngredient di ON d.id = di.id_dish
            JOIN Ingredient i ON di.id_ingredient = i.id
            WHERE d.id = ?
            """;

        try (Connection connection = dbconnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();

            Dish dish = null;
            List<DishIngredient> dishIngredients = new ArrayList<>();

            while (rs.next()) {
                if (dish == null) {
                    dish = new Dish();
                    dish.setId(rs.getInt("dish_id"));
                    dish.setName(rs.getString("dish_name"));
                    dish.setDishType(
                            Dish.DishTypeEnum.valueOf(rs.getString("dish_type"))
                    );
                    dish.setSellingPrice(rs.getObject("selling_price") != null ? rs.getDouble("selling_price") : null);
                }

                if (rs.getObject("ingredient_id") != null) {
                    Ingredient ingredient = new Ingredient(
                            rs.getInt("ingredient_id"),
                            rs.getString("ingredient_neme"),
                            rs.getDouble("ingredient_price"),
                            Ingredient.CategoryEnum.valueOf(rs.getString("ingredient_category"))
                    );

                    DishIngredient di = new DishIngredient();
                    di.setId(rs.getInt("dish_ingredient_id"));
                    di.setDish(dish);
                    di.setIngredient(ingredient);
                    di.setQuantity_required(rs.getObject("quantity_required") != null ? rs.getDouble("quantity_required") : null);
                    di.setUnit_type(rs.getString("unit_type") != null ?
                            DishIngredient.Unit.valueOf(rs.getString("unit_type")) : null);

                    dishIngredients.add(di);
                }
            }

            if (dish == null) {
                throw new RuntimeException("Dish not found with id " + id);
            }

            dish.setIngredients(dishIngredients);
            return dish;


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Ingredient> findIngredients(int page, int size) {
        List<Ingredient> ingredients = new ArrayList<>();

        int offset = (page - 1) * size;

        String sql = """
            SELECT 
                id AS ingredient_id, 
                name AS ingredient_name, 
                price AS ingredient_price, 
                category AS ingredient_category, 
                required_quantity
            FROM Ingredient
            ORDER BY id
            LIMIT ? OFFSET ?
        """;

        try (Connection connection = dbconnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, size);
            statement.setInt(2, offset);

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    ingredients.add(ingredientRowMapper.map(rs));
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

        String checkSql = "SELECT 1 FROM Ingredient WHERE name = ?";
        String sql = """
            INSERT INTO Ingredient (id ,name, price, category, id_dish, required_quantity)
            VALUES (?, ?, ?, ?::category, ?, ?)
        """;

        try (Connection connection = dbconnection.getConnection();){
            connection.setAutoCommit(false);

            try (PreparedStatement checkStatement = connection.prepareStatement(checkSql)) {
                for (Ingredient ingredient : newIngredients) {
                    checkStatement.setString(1, ingredient.getName());
                    try (ResultSet rs = checkStatement.executeQuery()) {
                        if (rs.next()) {
                            connection.rollback();
                            throw new RuntimeException(
                                    "Ingredient already exists in database: " + ingredient.getName()
                            );
                        }
                    }
                }
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {

                for (Ingredient ingredient : newIngredients) {
                    statement.setInt(1, ingredient.getId());
                    statement.setString(2, ingredient.getName());
                    statement.setDouble(3, ingredient.getPrice());
                    statement.setObject(4, ingredient.getCategory(), Types.OTHER);

                    if (ingredient.getDish() != null) {
                        statement.setInt(5, ingredient.getDish().getId());
                    } else {
                        statement.setObject(5, null);
                    }

                    statement.addBatch();
                }

                statement.executeBatch();
                connection.commit();
                return newIngredients;

            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException("Not allowed", e);
            }
        }

        catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public Dish saveDish(Dish dishToSave) {
        try (Connection connection = dbconnection.getConnection()) {
            connection.setAutoCommit(false);
            try{
            String sql = """
                    INSERT INTO Dish (id, name, dish_type, selling_price)
                    VALUES (?, ?, ?, ?)
                    ON CONFLICT(id) DO UPDATE
                    SET name = EXCLUDED.name,
                        dish_type = EXCLUDED.dish_type
                        selling_price = EXCLUDED.selling_price
                    """;

                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setInt(1, dishToSave.getId());
                    statement.setString(2, dishToSave.getName());
                    statement.setObject(3, dishToSave.getDishType().name(), Types.OTHER);
                    if (dishToSave.getSellingPrice() != null) {
                        statement.setDouble(4, dishToSave.getSellingPrice());
                    } else {
                        statement.setNull(4, Types.OTHER);
                    }
                    statement.executeUpdate();
                }
                if (dishToSave.getIngredients() != null && !dishToSave.getIngredients().isEmpty()) {
                    try (PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM Dish WHERE id = ?")) {
                        deleteStatement.setInt(1, dishToSave.getId());
                        deleteStatement.executeUpdate();
                    }

                    try (PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO DishIngredient (id_dish, id_ingredient, quantity_required, unit_type ) VALUES (?, ?, ?, ?::unit_type)")) {
                        for (DishIngredient di : dishToSave.getIngredients()) {
                            insertStatement.setInt(1, dishToSave.getId());
                            insertStatement.setInt(2, di.getIngredient().getId());
                            if (di.getQuantity_required() != null) {
                                insertStatement.setDouble(3, di.getQuantity_required());
                            } else {
                                insertStatement.setNull(3, Types.OTHER);
                            }

                            if (di.getUnit_type() != null) {
                                insertStatement.setObject(4, di.getUnit_type());
                            } else {
                                insertStatement.setNull(4, Types.OTHER);
                            }

                            insertStatement.addBatch();
                        }
                        insertStatement.executeBatch();
                    }
                }
            connection.commit();
            return dishToSave;

            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException("Dish was not saved",e);
            }

        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }


    public List<Dish> findDishByIngredientName(String ingredientName) throws SQLException {

        String sql = """
              SELECT DISTINCT d.id AS dish_id, d.name AS dish_name, d.dish_type, d.selling_price
              FROM Dish d 
              JOIN DishIngredient di ON d.id = di.id_dish
              JOIN Ingredient i ON di.id_ingredient = i.id
              WHERE i.name ILIKE ?
        """;

        List<Dish> dishes = new ArrayList<>();

        try (Connection connection = dbconnection.getConnection()) {

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, "%" + ingredientName + "%");


            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                Dish dish = new Dish();
                dish.setId(rs.getInt("dish_id"));
                dish.setName(rs.getString("dish_name"));
                dish.setDishType(Dish.DishTypeEnum.valueOf(rs.getString("dish_type")));
                dish.setSellingPrice(rs.getObject("selling_price") != null ? rs.getDouble("selling_price") : null);
                dishes.add(dish);
            }

            return dishes;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public List<Ingredient> findIngredientsByCriteria(String ingredientName, Ingredient.CategoryEnum category, String dishName, int page, int size) throws SQLException {

        StringBuilder sql = new StringBuilder(
                "SELECT " +
                    "i.id AS ingredient_id, " +
                    "i.name AS ingredient_name, " +
                    "i.price AS ingredient_price," +
                    "i.category AS ingredient_category, " +
                    "i.required_quantity, " +
                    "d.id AS dish_id, " +
                    "d.name AS dish_name, " +
                    "d.dish_type " +
                "FROM Ingredient i " +
                "LEFT JOIN Dish d ON i.id_dish = d.id" +
                "LEFT JOIN DishIngredient");

        List<Object> param = new ArrayList<>();
        List<Ingredient> ingredients = new ArrayList<>();
        List<String> conditions = new ArrayList<>();

        if (ingredientName != null && !ingredientName.isEmpty()) {
            conditions.add("i.name ILIKE ?");
            param.add("%" + ingredientName + "%");
        }
        if (category != null) {
            conditions.add("i.category = ?::category");
            param.add(category.name());
        }
        if (dishName != null && !dishName.isEmpty()) {
            conditions.add("d.name ILIKE ?");
            param.add("%" + dishName + "%");
        }

        if (!conditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", conditions));
        }

        sql.append(" ORDER BY i.name");

        int offset = (page - 1) * size;
        sql.append(" LIMIT ? OFFSET ?");
        param.add(size);
        param.add(offset);

        try (Connection connection = dbconnection.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql.toString());

            for (int i = 0; i < param.size(); i++) {
                statement.setObject(i + 1, param.get(i));
            }

            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    Ingredient ingredient = ingredientRowMapper.map(rs);
                    int dishId = rs.getInt("dish_id");
                    if (!rs.wasNull()) {
                        Dish dish = new Dish(
                                dishId,
                                rs.getString("dish_name"),
                                Dish.DishTypeEnum.valueOf(rs.getString("dish_type")),
                                new ArrayList<>()
                        );
                        dish.getIngredients().add(ingredient);
                    }
                    ingredients.add(ingredient);
                }
            }

        } catch (RuntimeException e){
            throw new RuntimeException("Error",e);
        }
        return ingredients;
    }

}
