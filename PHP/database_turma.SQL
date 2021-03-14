CREATE TABLE IF NOT EXISTS educando
(
  e_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  e_nome VARCHAR(100) NOT NULL,
  e_idade INT NOT NULL,
  e_morada VARCHAR(200) NOT NULL,
  e_sexo INT NOT NULL,
  e_contacto VARCHAR(10) NOT NULL
);

CREATE TABLE IF NOT EXISTS atividade
(
  a_sumario VARCHAR(300) NOT NULL,
  a_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  a_data VARCHAR(200) NOT NULL
);

CREATE TABLE IF NOT EXISTS alergias
(
  al_id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
  al_nome VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS faltas
(
  e_id INT NOT NULL,
  a_id INT NOT NULL,
  PRIMARY KEY (e_id, a_id),
  FOREIGN KEY (e_id) REFERENCES educando(e_id),
  FOREIGN KEY (a_id) REFERENCES atividade(a_id)
);

CREATE TABLE IF NOT EXISTS relatorio
(
  r_comer INT NOT NULL,
  r_dormir INT NOT NULL,
  r_coment VARCHAR(500) NOT NULL,
  r_necessidades INT NOT NULL,
  r_curativos INT NOT NULL,
  e_id INT NOT NULL,
  a_id INT NOT NULL,
  PRIMARY KEY (e_id, a_id),
  FOREIGN KEY (e_id) REFERENCES educando(e_id),
  FOREIGN KEY (a_id) REFERENCES atividade(a_id)
);

CREATE TABLE IF NOT EXISTS contem
(
  al_id INT NOT NULL,
  e_id INT NOT NULL,
  PRIMARY KEY (al_id, e_id),
  FOREIGN KEY (al_id) REFERENCES alergias(al_id),
  FOREIGN KEY (e_id) REFERENCES educando(e_id)
);
