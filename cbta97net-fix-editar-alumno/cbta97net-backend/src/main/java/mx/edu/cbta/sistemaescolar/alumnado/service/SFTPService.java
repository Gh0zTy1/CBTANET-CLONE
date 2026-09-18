package mx.edu.cbta.sistemaescolar.alumnado.service;

import java.io.InputStream;

/**
 * Servicio para operaciones de almacenamiento y recuperación de archivos
 * en un servidor SFTP
 *
 * Proporciona métodos para guardar, reemplazar, obtener y crear directorios
 * de manera recursiva en el servidor
 */
public interface SFTPService {

    /**
     * Verifica si un archivo existe en la ruta especificada.
     * @param rutaRemota Ruta del archivo.
     * @return true si existe, false en caso contrario.
     */
    boolean existeArchivo(String rutaRemota);

    /**
     * Método para guardar o reemplazar un archivo en el servidor sftp
     * @param inputStream archivo a guardar o reemplazar
     * @param rutaRemota ruta donde se guardará el archivo
     */
    public void guardarOReemplazar(InputStream inputStream, String rutaRemota);

    /**
     * Método para obtener un archivo en especifico del servidor sftp
     * @param rutaRemota ruta del archivo especifico el cual queremos recuperar
     * @return cadena de bytes con el contenido del archivo
     */
    public byte[] obtenerArchivo(String rutaRemota);

    /**
     * Método para eliminar un archivo específico del servidor sftp
     * @param rutaRemota ruta del archivo especifico el cual queremos eliminar
     * @return regresa verdadero si encontro el archivo y lo elimino de manera correcta, false en caso contrario
     */
    public boolean eliminarArchivo(String rutaRemota);
}
