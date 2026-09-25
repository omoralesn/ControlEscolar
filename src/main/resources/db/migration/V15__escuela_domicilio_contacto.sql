-- Domicilio y contacto de la escuela. El CP vive en la localidad:
-- al elegir localidad (tras el CP) salen municipio y estado.

ALTER TABLE localidades ADD COLUMN codigo_postal varchar(5);

ALTER TABLE instituciones ADD COLUMN domicilio_id bigint REFERENCES domicilios (id);
ALTER TABLE instituciones ADD COLUMN telefono varchar(30);
ALTER TABLE instituciones ADD COLUMN celular varchar(30);
ALTER TABLE instituciones ADD COLUMN correo varchar(120);
ALTER TABLE instituciones ADD COLUMN contacto_nombre varchar(160);
