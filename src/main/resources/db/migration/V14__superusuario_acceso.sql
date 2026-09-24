ALTER TABLE usuarios ADD COLUMN alcance varchar(20);
UPDATE usuarios SET alcance = CASE WHEN institucion_id IS NULL THEN 'PLATAFORMA' ELSE 'ESCUELA' END;
ALTER TABLE usuarios ALTER COLUMN alcance SET NOT NULL;

ALTER TABLE usuarios ADD COLUMN activo boolean NOT NULL DEFAULT true;
ALTER TABLE usuarios ADD COLUMN vigente_desde date;
ALTER TABLE usuarios ADD COLUMN vigente_hasta date;

ALTER TABLE planteles ADD COLUMN activo boolean NOT NULL DEFAULT true;
