package mx.gob.controlescolar.documentos.aplicacion;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Service
@Profile("!test")
@RequiredArgsConstructor
public class ArchivoGridFs implements ArchivoEscolar {

    private final GridFsTemplate gridFs;

    @Override
    public String guardar(Long institucionId, byte[] contenido) {
        ObjectId id = gridFs.store(new ByteArrayInputStream(contenido), institucionId + ".pdf", "application/pdf");
        return id.toHexString();
    }

    @Override
    public byte[] leer(String archivoId) {
        GridFSFile archivo = gridFs.findOne(Query.query(Criteria.where("_id").is(new ObjectId(archivoId))));
        if (archivo == null) {
            return new byte[0];
        }
        try {
            return gridFs.getResource(archivo).getInputStream().readAllBytes();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
