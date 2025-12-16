package org.example;

import org.example.entity.Dish;
import org.example.entity.Ingredient;

import javax.xml.crypto.Data;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        DataRetriever dataRetriever = new DataRetriever(new DBConnection());

        try {
            Dish dish = dataRetriever.findDishById(1);

            System.out.println("Nom du plat : " + dish.getName());
            System.out.println("Type : " + dish.getDishType());
            System.out.println("Nombre d'ingrédients : " + dish.getIngredients().size());

            for (Ingredient i : dish.getIngredients()) {
                System.out.println("- " + i.getName() + " (" + i.getCategory() + ")");
            }

        } catch (RuntimeException e) {
            System.out.println("Erreur : " + e.getMessage());
        }

        try {
            dataRetriever.findDishById(999);
            System.out.println("ERREUR");
        } catch (RuntimeException e) {
            System.out.println("Exception attendue : " + e.getMessage());
        }
    }
}