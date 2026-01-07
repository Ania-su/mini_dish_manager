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

//        try {
//            List<Ingredient> result1 = dataRetriever.findIngredients(2, 2);
//
//            if (result1.isEmpty()) {
//                System.out.println("Empty list");
//            } else {
//                result1.forEach(i ->
//                        System.out.println("- " + i.getName() + " (" + i.getCategory() + ")")
//                );
//            }
//
//        } catch (RuntimeException e) {
//            throw new RuntimeException(e);
//        }


        Dish burger = new Dish(100, "Humberger", Dish.DishTypeEnum.START, List.of(
                new Ingredient(7, "steak", 4000.0, Ingredient.CategoryEnum.ANIMAL, 2.0)
        ));

        Dish saved = dataRetriever.saveDish(burger);
        System.out.println("Saved : " + saved.getName());

        double cost = saved.getDishCost();
        System.out.println("Total cost : " + cost);


    }

}