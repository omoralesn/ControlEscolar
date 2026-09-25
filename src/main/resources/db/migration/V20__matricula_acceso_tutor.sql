ALTER TABLE alumnos ADD COLUMN matricula varchar(20);
CREATE UNIQUE INDEX alumnos_matricula_uk ON alumnos (matricula);
