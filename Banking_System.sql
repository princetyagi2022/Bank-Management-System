CREATE DATABASE bankdb; 
 -- Use the database 
USE bankdb; 
 
CREATE TABLE accounts (
    acc_id BIGINT PRIMARY KEY,
    username VARCHAR(50),
    password VARCHAR(50),
    balance DOUBLE
);


-- Set AUTO_INCREMENT starting from 8218486900
ALTER TABLE accounts AUTO_INCREMENT = 8218485900;

-- 3. Insert sample account data
INSERT INTO accounts (acc_id,username, password, balance) VALUES
(101,'Prince', 'prince123', 50000.0),
(102,'Arjun', 'Aj123', 30000.0);

SELECT * FROM accounts;

drop table accounts;
