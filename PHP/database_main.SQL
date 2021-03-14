CREATE USER 'teachersfriend'@'localhost' IDENTIFIED BY 'userpwd';
GRANT ALL PRIVILEGES ON *.* TO 'teachersfriend'@'localhost';
FLUSH PRIVILEGES;

CREATE DATABASE main;
USE main;

CREATE TABLE users
(
  u_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  u_nome VARCHAR(50) NOT NULL,
  u_idade INT NOT NULL,
  u_morada VARCHAR(200) NOT NULL,
  u_sexo INT NOT NULL,
  u_email VARCHAR(100) NOT NULL,
  u_pwd VARCHAR(200) NOT NULL
);

CREATE TABLE admin
(
  u_id INT NOT NULL PRIMARY KEY,
  FOREIGN KEY (u_id) REFERENCES users(u_id)
);

CREATE TABLE turmas
(
  t_token VARCHAR(200) NOT NULL,
  t_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  t_utilizada INT NOT NULL,
  u_id INT NOT NULL,
  FOREIGN KEY (u_id) REFERENCES users(u_id),
  UNIQUE (t_token)
);

CREATE TABLE to_regist
(
  tr_email VARCHAR(100) NOT NULL,
  tr_sexo INT NOT NULL,
  tr_morada VARCHAR(200) NOT NULL,
  tr_idade INT NOT NULL,
  tr_nome VARCHAR(50) NOT NULL,
  tr_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  tr_pwd VARCHAR(200) NOT NULL
);


