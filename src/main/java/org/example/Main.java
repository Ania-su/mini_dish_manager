package org.example;

import org.example.entity.Dish;
import org.example.entity.Ingredient;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws SQLException {
        DataRetriever dataRetriever = new DataRetriever(new DBConnection());

        try {
            Dish dish = dataRetriever.findDishById(4);

            System.out.println("Dish name : " + dish.getName());
            System.out.println("Type : " + dish.getDishType());
            System.out.println("Ingredients : " + dish.getIngredients().size());


            for (Ingredient i : dish.getIngredients()) {
                System.out.println("- " + i.getName() + " (" + i.getCategory() + ")");
            }

            double cost = dish.getDishCost();
            System.out.println("Total cost : " + cost);

        } catch (RuntimeException e) {
            System.out.println("Error : " + e.getMessage());
        }


        try {
            Dish newCake = new Dish(6, "Nouveau gâteau", Dish.DishTypeEnum.DESSERT, List.of(
                    new Ingredient(7, "farine", 1200.0, Ingredient.CategoryEnum.DIARY, 1.0)
            ));

            Dish saved = dataRetriever.saveDish(newCake);
            System.out.println("Saved : " + saved.getName());

            double cost = saved.getDishCost();
            System.out.println("Total cost : " + cost);

        } catch (RuntimeException e) {
            System.out.println("Error : " + e.getMessage());
        }

    }

}