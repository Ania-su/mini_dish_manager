insert into dish values ( default, 'Salade fraîche', 'START'),
                        ( default, 'Poulet grillé' , 'MAIN'),
                        ( default, 'Riz au légumes', 'MAIN'),
                        ( default, 'Gâteau au chocolat', 'DESSERT'),
                        ( default, 'Salade de fruits', 'DESSERT');

insert into Ingredient values (default, 'Laitue', 800.00, 'VEGETABLE', 1),
                              (default, 'Tomate', 600.00, 'VEGETABLE', 1),
                              (default, 'Poulet', 4500.00, 'ANIMAL', 2),
                              (default, 'Chocolat', 3000.00, 'OTHER', 4),
                              (default, 'Beurre', 2500.00, 'DIARY', 4);

update Ingredient set required_quantity = 1 where name = 'Laitue';
update Ingredient set required_quantity = 2 where name = 'Tomate';
update Ingredient set required_quantity = 0.5 where name = 'Poulet';
update Ingredient set required_quantity = null where name = 'Chocolat';
update Ingredient set required_quantity = null where name = 'Beurre';
