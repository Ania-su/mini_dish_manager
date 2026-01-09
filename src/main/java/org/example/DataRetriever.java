package org.example;

import org.example.entity.Dish;
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
                   i.id AS ingredient_id,
                   i.name AS ingredient_name,
                   i.price AS ingredient_price,
                   i.category AS ingredient_category,
                   i.required_quantity
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
                    ingredients.add(ingredientRowMapper.map(rs));
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
            SELECT id, name, price, category, required_quantity
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

        String sql = """
            INSERT INTO ingredient (name, price, category, id_dish, required_quantity)
            VALUES (?, ?, ?::category, ?, ?)
        """;

        DBConnection dbconnection = new DBConnection();
        Connection connection = dbconnection.getConnection();

        connection.setAutoCommit(false);

        try (PreparedStatement statement = connection.prepareStatement(sql)) {

            for (Ingredient ingredient : newIngredients) {
                statement.setString(1, ingredient.getName());
                statement.setDouble(2, ingredient.getPrice());
                statement.setObject(3, ingredient.getCategory(), Types.OTHER);
                statement.setDouble(4, ingredient.getRequiredQuantity());
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

//    public Dish saveDish(Dish dishToSave) throws SQLException {
//
//        DBConnection dbconnection = new DBConnection();
//        Connection connection = dbconnection.getConnection();
//
//        String insertSql = "INSERT INTO Dish (id, name, dish_type) VALUES (?, ?, ?) ON CONFLICT id DO NOTHING;";
//        String updateSql = "UPDATE Dish SET name = ?, dish_type = ? WHERE id = ?;";
//        String deleteAssocSql = "DELETE FROM ingredient WHERE id_dish = ?;";
//        String insertAssocSql = "INSERT INTO ingredient (id_dish, id) VALUES (?, ?);";
//
//        try {
//            connection.setAutoCommit(false);
//
//            try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
//                insertStmt.setInt(1, dishToSave.getId());
//                insertStmt.setString(2, dishToSave.getName());
//                insertStmt.setObject(3, dishToSave.getDishType().name(), Types.OTHER);
//                insertStmt.executeUpdate();
//            } catch (SQLException e) {
//                try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
//                    updateStmt.setString(1, dishToSave.getName());
//                    updateStmt.setObject(2, dishToSave.getDishType().name(), Types.OTHER);
//                    updateStmt.setInt(3, dishToSave.getId());
//                    updateStmt.executeUpdate();
//                }
//            }
//
//            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteAssocSql)) {
//                deleteStmt.setInt(1, dishToSave.getId());
//                deleteStmt.executeUpdate();
//            }
//
//            if (dishToSave.getIngredients() != null) {
//                try (PreparedStatement insertAssocStmt = connection.prepareStatement(insertAssocSql)) {
//                    for (Ingredient ing : dishToSave.getIngredients()) {
//                        insertAssocStmt.setInt(1, dishToSave.getId());
//                        insertAssocStmt.setInt(2, ing.getId());
//                        insertAssocStmt.addBatch();
//                    }
//                    insertAssocStmt.executeBatch();
//                }
//            }
//
//            connection.commit();
//            return dishToSave;
//
//        } catch (SQLException ex) {
//            connection.rollback();
//            throw new RuntimeException(ex);
//
//        } finally {
//            connection.close();
//        }
//    }

    public Dish saveDish(Dish dishToSave) {
        try (Connection connection = dbconnection.getConnection()) {
            connection.setAutoCommit(false);
            try{
            String sql = """
                    INSERT INTO Dish (id, name, dish_type)
                    VALUES (?, ?, ?)
                    ON CONFLICT(id) DO UPDATE
                    SET name = EXCLUDED.name,
                        dish_type = EXCLUDED.dish_type
                    """;

                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setInt(1, dishToSave.getId());
                    statement.setString(2, dishToSave.getName());
                    statement.setObject(3, dishToSave.getDishType().name(), Types.OTHER);
//                    statement.setDouble(4, dishToSave.getDishCost());
                    statement.executeUpdate();
                }
                if (dishToSave.getIngredients() != null && !dishToSave.getIngredients().isEmpty()) {
                    try (PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM Ingredient WHERE id_dish = ?")) {
                        deleteStatement.setInt(1, dishToSave.getId());
                        deleteStatement.executeUpdate();
                    }

                    try (PreparedStatement insertAssocStatement = connection.prepareStatement("INSERT INTO Ingredient (id_dish, id, name, price, category, required_quantity ) VALUES (?, ?, ?, ?, ?, ?)")) {
                        for (Ingredient ingredient : dishToSave.getIngredients()) {
                            insertAssocStatement.setInt(1, dishToSave.getId());
                            insertAssocStatement.setInt(2, ingredient.getId());
                            insertAssocStatement.setString(3, ingredient.getName());
                            insertAssocStatement.setDouble(4, ingredient.getPrice());
                            insertAssocStatement.setObject(5, ingredient.getCategory(), Types.OTHER);
                            insertAssocStatement.setDouble(6, ingredient.getRequiredQuantity());
                            insertAssocStatement.addBatch();
                        }
                        insertAssocStatement.executeBatch();
                    }
                }
            connection.commit();
            return dishToSave;

            } catch (SQLException e) {
                e.printStackTrace();
                connection.rollback();
                throw new RuntimeException("Dish was not saved",e);
            }

        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }


    public List<Dish> findDishByIngredientName(String ingredientName) throws SQLException {

        DBConnection dbconnection = new DBConnection();
        Connection connection = dbconnection.getConnection();

        String sql = """
              SELECT DISTINCT d.id AS dish_id, d.name AS dish_name, d.dish_type AS dish_type 
              FROM dish d 
              JOIN ingredient i ON d.id = i.id_dish 
              WHERE i.name ILIKE ?
        """;

        List<Dish> dishes = new ArrayList<>();

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, "%" + ingredientName + "%");

        ResultSet resultSet = statement.executeQuery();

        while (resultSet.next()) {
            Dish dish = new Dish(
                    resultSet.getInt("dish_id"),
                    resultSet.getString("dish_name"),
                    Dish.DishTypeEnum.valueOf(resultSet.getString("dish_type")),
                    new ArrayList<>()
            );

            dishes.add(dish);
        }

        return dishes;
    }

    public List<Ingredient> findIngredientsByCriteria(String ingredientName, Ingredient.CategoryEnum category, String dishName, int page, int size) throws SQLException {

        StringBuilder sql = new StringBuilder("SELECT i.id AS ingredient_id, i.name AS ingredient_name, i.price, i.category, d.id AS dish_id, d.name AS dish_name, d.dish_type FROM Ingredient i LEFT JOIN Dish d ON i.id_dish = d.id");

        List<Object> params = new ArrayList<>();
        List<Ingredient> ingredients = new ArrayList<>();
        List<String> conditions = new ArrayList<>();

        if (ingredientName != null && !ingredientName.isEmpty()) {
            conditions.add("i.name LIKE ?");
            params.add("%" + ingredientName + "%");
        }
        if (category != null) {
            conditions.add("i.category = ?::Category");
            params.add(category.toString());
        }
        if (dishName != null && !dishName.isEmpty()) {
            conditions.add("d.name LIKE ?");
            params.add("%" + dishName + "%");
        }

        if (!conditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", conditions));
        }

        sql.append(" LIMIT ? OFFSET ?");
        int offset = (page - 1) * size;

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            connection = dbconnection.getConnection();
            statement = connection.prepareStatement(sql.toString());

            int paramIndex = 1;
            for (Object param : params) {
                statement.setObject(paramIndex++, param);
            }
            statement.setInt(paramIndex++, size);
            statement.setInt(paramIndex, offset);

            rs = statement.executeQuery();

            while (rs.next()) {
                Dish dish = null;
                int dishId = rs.getInt("dish_id");
                if (!rs.wasNull()) {
                    dish = new Dish(
                            dishId,
                            rs.getString("dish_name"),
                            Dish.DishTypeEnum.valueOf(rs.getString("dish_type")),
                            new ArrayList<>()
                    );
                }

                Ingredient ingredient = new Ingredient(
                                        rs.getInt("ingredient_id"),
                                        rs.getString("ingredient_name"),
                                        rs.getDouble("price"),
                                        Ingredient.CategoryEnum.valueOf(rs.getString("category")),
                                        dish,
                                        rs.getDouble("required_quantity")
                                );

                if(dish != null) {
                    dish.getIngredients().add(ingredient);
                }

                ingredients.add(ingredient);
            }

        } finally {
            if (rs != null) rs.close();
            if (statement != null) statement.close();
            if (connection != null) connection.close();
        }

        return ingredients;
    }

}
