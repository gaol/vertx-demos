-- Creation of product table
CREATE TABLE IF NOT EXISTS DataEntry (
  id INT NOT NULL,
  name varchar(50),
  message varchar(250),
  PRIMARY KEY (id)
);