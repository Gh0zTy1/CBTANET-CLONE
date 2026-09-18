package mx.edu.cbta.sistemaescolar.alumnado.service.impl;

import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;
import org.springframework.integration.file.remote.session.Session;

import mx.edu.cbta.sistemaescolar.alumnado.service.SFTPService;

import org.springframework.stereotype.Service;
import org.apache.sshd.sftp.client.SftpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class SFTPServiceImpl implements SFTPService {

    private final SftpRemoteFileTemplate sftpTemplate;

    public SFTPServiceImpl(SftpRemoteFileTemplate sftpTemplate) {
        this.sftpTemplate = sftpTemplate;
    }

    /**
     * Método para guardar o reemplazar un archivo en el servidor sftp
     *
     * @param inputStream archivo a guardar o reemplazar
     * @param rutaRemota  ruta donde se guardará el archivo
     */
    @Override
    public void guardarOReemplazar(InputStream inputStream, String rutaRemota) {
        sftpTemplate.execute(session -> {
            String directorio = "";
            if (rutaRemota.contains("/")) {
                directorio = rutaRemota.substring(0, rutaRemota.lastIndexOf("/"));
            }

            if (!directorio.isEmpty() && !session.exists(directorio)) {
                crearDirectoriosRecursivos(directorio, session);
            }

            session.write(inputStream, rutaRemota);
            return null;
        });
    }

    /**
     * Método para obtener un archivo en especifico del servidor sftp
     *
     * @param rutaRemota ruta del archivo especifico el cual queremos recuperar
     * @return cadena de bytes con el contenido del archivo
     */
    @Override
    public byte[] obtenerArchivo(String rutaRemota) {
        return sftpTemplate.execute(session -> {
            if (!session.exists(rutaRemota)) {
                throw new RuntimeException("El archivo no existe en SFTP: " + rutaRemota);
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            session.read(rutaRemota, outputStream);
            return outputStream.toByteArray();
        });
    }

    /**
     * Método para eliminar un archivo específico del servidor sftp
     *
     * @param rutaRemota ruta del archivo especifico el cual queremos eliminar
     * @return regresa verdadero si encontro el archivo y lo elimino de manera correcta, false en caso contrario
     */
    @Override
    public boolean eliminarArchivo(String rutaRemota) {
        return sftpTemplate.execute(session -> session.remove(rutaRemota));
    }

    /**
     * Metodo para crear direcctorios en el servidor sftp solo si no estan creados
     * @param path direccion entera de los directorios a crear
     * @param session conexion temporal con el servidor sftp
     */
    private void crearDirectoriosRecursivos(String path, Session<SftpClient.DirEntry> session) throws IOException {
        StringBuilder currentPath = new StringBuilder();
        boolean first = true;

        for (String dir : path.split("/")) {
            if (!dir.isEmpty()) {
                if (!first) {
                    currentPath.append("/");
                }
                currentPath.append(dir);
                first = false;

                String targetDir = currentPath.toString();

                if (!session.exists(targetDir)) {
                    try {
                        session.mkdir(targetDir);
                    } catch (IOException e) {
                        if (!session.exists(targetDir)) {
                            throw e;
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean existeArchivo(String rutaRemota) {
        return sftpTemplate.execute(session -> session.exists(rutaRemota));
    }

}
