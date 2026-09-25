ALTER TABLE grupos ADD COLUMN edificio varchar(80);
ALTER TABLE grupos ADD COLUMN aula varchar(40);
ALTER TABLE grupos ADD COLUMN capacidad integer NOT NULL DEFAULT 0;
