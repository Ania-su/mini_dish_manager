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

CREATE TYPE movement_type AS enum ('IN', 'OUT');
CREATE TABLE StockMovement (
    id serial primary key unique not null,
    id_ingredient int not null,
    FOREIGN KEY (id_ingredient) REFERENCES Ingredient(id),
    quantity numeric (10, 2) not null,
    type movement_type not null,
    unit unit not null,
    creation_datetime TIMESTAMP not null
);

INSERT INTO StockMovement (id, id_ingredient, quantity, type, unit, creation_datetime)
VALUES (default, 1, 5.0, 'IN', 'KG', '2024-01-05 08:00'),
       (default, 1, 0.2, 'OUT', 'KG', '2024-01-06 12:00'),
       (default, 2, 4.0, 'IN', 'KG', '2024-01-05 08:00'),
       (default, 2, 0.15, 'OUT', 'KG', '2024-01-06 12:00'),
       (default, 3, 10.0, 'IN', 'KG', '2024-01-04 09:00'),
       (default, 3, 1.0, 'OUT', 'KG', '2024-01-06 13:00'),
       (default, 4, 3.0, 'IN', 'KG', '2024-01-05 10:00'),
       (default, 4, 0.3, 'OUT', 'KG', '2024-01-06 14:00'),
       (default, 5, 2.5, 'IN', 'KG', '2024-01-05 10:00'),
       (default, 5, 0.2, 'OUT', 'KG', '2024-01-06 14:00');

CREATE TABLE Order (
    id serial primary key unique not null,
    reference varchar (10) generated always as ('ORD' || LPAD(id::text, 5, '0')) STORED,
    creation_datetime timestamp without time zone
);

CREATE TABLE DishOrder (
    id serial primary key unique not null,
    id_order int not null,
    FOREIGN KEY (id_order) REFERENCES Order(id),
    id_dish int not null,
    FOREIGN KEY (id_dish) REFERENCES Dish(id),
    quantity int not null
);

CREATE TABLE Sale (
    id serial primary key unique not null,
    creationDatetime TIMESTAMP not null
);

CREATE TYPE payment_status as enum ('PAID', 'UNPAID');
ALTER TABLE "Order" ADD COLUMN IF NOT EXISTS status payment_status;
ALTER TABLE "Order" ADD COLUMN IF NOT EXISTS id_sale int REFERENCES Sale(id);