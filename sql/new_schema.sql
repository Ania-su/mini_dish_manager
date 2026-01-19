ALTER TABLE Ingredient DROP COLUMN id_dish;
ALTER TABLE Ingredient DROP COLUMN required_quantity;

ALTER TABLE Dish ADD COLUMN IF NOT EXISTS selling_price numeric;

create type unit as enum ('PCS', 'KG', 'L');
create table DishIngredient (
                                id serial primary key unique not null,
                                id_dish INT not null ,
                                FOREIGN KEY (id_dish) REFERENCES Dish(id),
                                id_ingredient INT not null ,
                                FOREIGN KEY  (id_ingredient) REFERENCES Ingredient(id),
                                quantity_required numeric (10, 2) not null ,
                                unit_type unit not null,
                                UNIQUE (id_dish, id_ingredient)
);

INSERT INTO DishIngredient (id, id_dish, id_ingredient, quantity_required, unit_type)
values (default, 1, 1, 0.20, 'KG'),
       (default, 1, 2, 0.15, 'KG'),
       (default, 2, 3, 1.00, 'KG'),
       (default, 4, 4, 0.30, 'KG'),
       (default, 4, 5, 0.20, 'KG');

UPDATE Dish SET selling_price = 3500.00 WHERE id = 1;
UPDATE Dish SET selling_price = 12000.00 WHERE id = 2;
UPDATE Dish SET selling_price = 8000.00 WHERE id = 4;
UPDATE Dish SET selling_price = NULL WHERE id = 3 or id = 5;