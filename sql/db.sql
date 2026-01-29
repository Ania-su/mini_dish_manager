create database mini_dish_db;

create user mini_dish_db_manager;

grant connect ON DATABASE product_management_db TO product_manager_user;

grant create, select, update, delete ON DATABASE product_management_db TO product_manager_user;