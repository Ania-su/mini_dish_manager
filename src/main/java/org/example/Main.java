package org.example;

import org.example.entity.Dish;
import org.example.entity.Ingredient;
import org.example.entity.StockMovement;
import org.example.entity.StockValue;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws SQLException {
        DataRetriever dataRetriever = new DataRetriever(new DBConnection());

        int[] dishID = {1, 2, 3, 4, 5};
        for( int id : dishID ) {
            try {
                Dish dish = dataRetriever.findDishById(id);
                System.out.println(dish.getName());

                try {
                    double cost = dish.getDishCost();
                    System.out.println("Cost : " + cost);
                } catch (IllegalArgumentException e) {
                    System.out.println("Cost : Exeption " + e.getMessage());
                }

                try {
                    double grossMargin = dish.getGrossMargin();
                    System.out.println("Gross Margin : " + grossMargin);
                } catch (IllegalArgumentException e) {
                    System.out.println("Gross Margin : Exeption " + e.getMessage());
                }
                System.out.println();

            } catch (RuntimeException e) {
                System.out.println("Error : " + e.getMessage());
            }
        }

        Instant t = Instant.parse("2024-01-06T12:00:00Z");
        List <Ingredient> ingredientList = dataRetriever.findIngredients(1, 100);
        List <StockMovement> stockMovement = new ArrayList<>();


        for (Ingredient ingredient : ingredientList) {
            StockValue stockValue = ingredient.getStockValueAt(t);

            System.out.println(
                    "Ingredient : " + ingredient.getName() + "\n" +
                    "Stock : " + stockValue.getQuantity()+ "\n"
            );
        }

//        try {
//            Dish dish = dataRetriever.findDishById(4);
//
//            System.out.println("Dish name : " + dish.getName());
//            System.out.println("Type : " + dish.getDishType());
//            System.out.println("Ingredients : " + dish.getIngredients().size());
//
//
//            for (Ingredient i : dish.getIngredients()) {
//                System.out.println("- " + i.getName() + " (" + i.getCategory() + ")");
//            }
//
//            double cost = dish.getDishCost();
//            System.out.println("Total cost : " + cost);
//
//        } catch (RuntimeException e) {
//            System.out.println("Error : " + e.getMessage());
//        }
//
//
//        try {
//            Dish newCake = new Dish(6, "Nouveau gâteau", Dish.DishTypeEnum.DESSERT, List.of(
//                    new Ingredient(7, "farine", 1200.0, Ingredient.CategoryEnum.DIARY, 1.0)
//            ));
//
//            Dish saved = dataRetriever.saveDish(newCake);
//            System.out.println("Saved : " + saved.getName());
//
//            double cost = saved.getDishCost();
//            System.out.println("Total cost : " + cost);
//
//        } catch (RuntimeException e) {
//            System.out.println("Error : " + e.getMessage());
//        }
    }

}