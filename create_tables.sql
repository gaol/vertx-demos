-- Creation of product table
CREATE TABLE IF NOT EXISTS TimedEntry (
  id INT NOT NULL,
  time TIMESTAMP,
  message varchar(250) ,
  PRIMARY KEY (id)
);
