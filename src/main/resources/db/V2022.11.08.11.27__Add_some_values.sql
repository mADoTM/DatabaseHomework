INSERT INTO product (name, inner_code)
VALUES ('best laptop in the world', 123450),
       ('Good, but not the best', 21345),
       ('IPhone', 1464),
       ('Oracle database ', 3),
       ('PostgreSQL', 12381913),
       ('VK', 1234787);

INSERT INTO company (company_id, name, TIN, checking_account)
VALUES (1, 'Vkontake EcoSystem', 123456, 4176812),
       (2, 'SBER MEGA MARKET', 9999, 999999),
       (3, 'Ozonzonzon', 303415, 123414),
       (4, 'Mail.ru', 1312471, 1238915),
       (5, 'Huawei', 1512314, 1512368),
       (6, 'Xiaomi', 1991237, 1758748),
       (7, 'lu4shaya product company', 192194, 675838),
       (8, 'New name', 1930149, 21347),
       (9, 'Unknown', 10109239, 124757),
       (10, 'JavaScript development', 124992, 149851),
       (11, 'Funny name', 104912, 492140);

INSERT INTO consingment (consingment_id, order_date, company_id)
VALUES (1, '2014-09-16', 4),
       (2, '2014-09-16', 2),
       (3, '2014-09-16', 4);


INSERT INTO position (cost, inner_code, amount, consingment_id)
VALUES (10000000, 1234787, 2, 1),
       (0, 12381913, 10, 1),
       (10000000, 1234787, 1, 1);
