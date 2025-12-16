package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class Dish {

    enum DishTypeEnum {

    }

    private int id;
    private String name;
    private DishTypeEnum dishType;
    private List<Ingredient> ingredients;

//    public double getDishPrice() {
//
//    }

}
