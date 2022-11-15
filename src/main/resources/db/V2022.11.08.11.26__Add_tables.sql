CREATE TABLE product
(
    inner_code INT UNIQUE NOT NULL,
    name VARCHAR NOT NULL,
    CONSTRAINT product_pk PRIMARY KEY (inner_code)
);

CREATE TABLE company
(
    company_id INT UNIQUE NOT NULL,
    name VARCHAR NOT NULL,
    TIN INT UNIQUE NOT NULL,
    checking_account INT NOT NULL,
    CONSTRAINT company_pk PRIMARY KEY (company_id)
);

CREATE TABLE consingment
(
    consingment_id INT UNIQUE NOT NULL,
    order_date DATE NOT NULL,
    company_id INT NOT NULL REFERENCES company (company_id) ON UPDATE CASCADE ON DELETE SET NULL,
    CONSTRAINT consingment_pk PRIMARY KEY (consingment_id)
);

CREATE TABLE position
(
    cost INT NOT NULL,
    inner_code INT NOT NULL REFERENCES product (inner_code) ON UPDATE CASCADE ON DELETE SET NULL,
    amount INT NOT NULL,
    consingment_id INT NOT NULL REFERENCES consingment (consingment_id) ON UPDATE CASCADE ON DELETE SET NULL
);