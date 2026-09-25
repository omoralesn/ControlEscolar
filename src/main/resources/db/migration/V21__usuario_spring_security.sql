ALTER TABLE usuarios ADD COLUMN bloqueado boolean NOT NULL DEFAULT false;
ALTER TABLE usuarios ADD COLUMN credenciales_vigentes boolean NOT NULL DEFAULT true;
