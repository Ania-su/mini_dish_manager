package org.example;

import org.example.entity.Dish;
import org.example.entity.Ingredient;

import javax.xml.crypto.Data;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        DataRetriever dataRetriever = new DataRetriever(new DBConnection());

        try {
            Dish dish = dataRetriever.findDishById(4);

            System.out.println("Dish name : " + dish.getName());
            System.out.println("Type : " + dish.getDishType());
            System.out.println("Ingredients : " + dish.getIngredients().size());

            for (Ingredient i : dish.getIngredients()) {
                System.out.println("- " + i.getName() + " (" + i.getCategory() + ")");
            }

        } catch (RuntimeException e) {
            System.out.println("Error : " + e.getMessage());
        }

        try {
            System.out.println("\nTest 1 : page = 2, size = 2");
            List<Ingredient> result1 = dataRetriever.findIngredients(2, 2);

            if (result1.isEmpty()) {
                System.out.println("Résultat : liste vide");
            } else {
                result1.forEach(i ->
                        System.out.println("- " + i.getName() + " (" + i.getCategory() + ")")
                );
            }

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}