ALTER TABLE auditorias_eventos ADD COLUMN actor_id bigint;
ALTER TABLE auditorias_eventos ADD COLUMN actor_login varchar(80);
ALTER TABLE auditorias_eventos ADD COLUMN afectado_login varchar(80);
ALTER TABLE auditorias_eventos ADD COLUMN perfil_nombre varchar(120);
ALTER TABLE auditorias_eventos ADD COLUMN ocurrido_en timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP;

INSERT INTO permisos (codigo) VALUES ('ESCUELA_DATOS');
