create type category as enum ('VEGETABLE','ANIMAL', 'MARINE','DIARY','OTHER');
create table Ingredient ( id serial primary key unique not null,
                          name varchar(255) not null,
                          price numeric (10, 2) not null,
                          category category not null, id_dish INT,
                          FOREIGN KEY (id_dish) REFERENCES Dish(id));

create type dishes as enum ('START','MAIN','DESSERT');
create table Dish ( id serial primary key unique not null,
                    name varchar (255) not null,
                    dish_type dishes not null);
