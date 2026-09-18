package mx.edu.cbta.sistemaescolar.usuarios.service;

import mx.edu.cbta.sistemaescolar.usuarios.dto.InformacionBasicaUsuarioDTO;
import mx.edu.cbta.sistemaescolar.usuarios.dto.PermisoDTO;
import mx.edu.cbta.sistemaescolar.usuarios.dto.RolDTO;
import mx.edu.cbta.sistemaescolar.usuarios.domain.exception.*;
import mx.edu.cbta.sistemaescolar.usuarios.dto.UsuarioDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface UsuarioService {

    /**
     * Inicia sesión de un usuario validando sus credenciales.
     *
     * <p>Este método verifica la existencia del usuario, su estado
     * (activo/inactivo) y la validez de la contraseña proporcionada.</p>
     *
     * @param idUsuario identificador único del usuario
     * @param contrasena contraseña en texto plano proporcionada por el usuario
     * @return un mapa con información relevante de la sesión iniciada (access token, expiration).
     * @throws UsuarioException si el usuario existe pero se encuentra inactivo o en un estado no válido
     * @throws CredencialesInvalidasException si el usuario no existe o la contraseña es incorrecta
     */
    Map<String, Object> iniciarSesion(Long idUsuario, String contrasena) throws UsuarioException, CredencialesInvalidasException;


    /**
     * Obtiene un listado paginado de todos los usuarios registrados en el sistema.
     *
     * <p>Este método es utilizado principalmente para vistas administrativas
     * donde se requiere consultar grandes volúmenes de usuarios de forma eficiente.</p>
     *
     * @param pageable información de paginación y ordenamiento
     * @return una página de usuarios representados como {@link UsuarioDTO}
     */
    Page<UsuarioDTO> obtenerUsuariosTodos(Pageable pageable);

    UsuarioDTO obtenerUsuarioPorId(Long usuarioId) throws UsuarioNoEncontradoException;

    /**
     * Verifica si un usuario existe en el sistema a partir de su identificador.
     *
     * @param usuarioId identificador único del usuario
     * @return {@code true} si el usuario existe, {@code false} en caso contrario
     */
    boolean usuarioExiste(Long usuarioId);
    /**
     * Obtiene el nombre del usuario.
     *
     * @param usuarioId identificador único del usuario
     * @return nombre del usuario
     */
    String obtenerNombre(Long usuarioId);

    /**
     * Obtiene el apellido paterno del usuario.
     *
     * @param usuarioId identificador único del usuario
     * @return apellido paterno del usuario
     */
    String obtenerApellidoPaterno(Long usuarioId);

    /**
     * Obtiene el apellido materno del usuario.
     *
     * @param usuarioId identificador único del usuario
     * @return apellido materno del usuario
     */
    String obtenerApellidoMaterno(Long usuarioId);

    /**
     * Obtiene el correo electrónico del usuario.
     *
     * @param usuarioId identificador único del usuario
     * @return correo electrónico del usuario
     */
    String obtenerCorreoElectronico(Long usuarioId);

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * <p>Este método valida que el usuario no exista previamente,
     * aplica las reglas de negocio necesarias y almacena la contraseña
     * de forma segura (por ejemplo, usando hashing).</p>
     *
     * @param usuarioDTO datos del usuario a registrar
     * @param contrasena contraseña inicial del usuario
     * @return el usuario registrado representado como {@link UsuarioDTO}
     * @throws UsuarioException si el usuario ya existe o los datos son inválidos
     * @throws RegistrarUsuarioException si ocurre un error durante el proceso de registro
     */
    UsuarioDTO registrar(UsuarioDTO usuarioDTO, String contrasena) throws UsuarioException, RegistrarUsuarioException;

    /**
     * Modifica la información de un usuario existente.
     *
     * <p>Este método actualiza únicamente los datos permitidos
     * por las reglas de negocio del sistema.</p>
     *
     * @param idUsuario identificador del usuario a modificar
     * @param usuarioDTO nuevos datos del usuario
     * @throws UsuarioException si el usuario no existe o la modificación no es válida
     */
    void modificar(Long idUsuario, UsuarioDTO usuarioDTO) throws UsuarioException ;

    /**
     * Elimina un usuario del sistema.
     *
     * <p>Dependiendo de la implementación, esta operación puede
     * representar una eliminación lógica (desactivación)
     * o una eliminación física.</p>
     *
     * @param idUsuario identificador del usuario a eliminar
     * @throws UsuarioException si el usuario no existe o no puede ser eliminado
     */
    void eliminar(Long idUsuario) throws UsuarioException;

    /**
     * Obtiene la lista de roles disponibles en el sistema.
     *
     * @return lista de roles
     * @throws RolesException si ocurre un error al consultar los roles
     */
    List<RolDTO> obtenerRolesDisponibles() throws RolesException;

    /**
     * Obtiene la lista de permisos disponibles en el sistema.
     *
     * @return lista de permisos
     * @throws PermisosException si ocurre un error al consultar los permisos
     */
    List<PermisoDTO> obtenerPermisosDisponibles() throws PermisosException;

    /**
     * Recupera la lista de roles de nivel de Realm asignados específicamente a un usuario.
     * <p>
     * Este método consulta el servidor de identidad (Keycloak) para obtener los roles
     * globales (como ADMIN, DOCENTE) vinculados al ID del usuario. Los roles se filtran
     * para excluir roles técnicos del sistema y se retornan ordenados alfabéticamente.
     * </p>
     *
     * @param usuarioId El identificador único del usuario en el sistema.
     * @return Una lista de {@link RolDTO} con los roles asignados actualmente.
     * @throws RolesException Si ocurre un error de comunicación con Keycloak o de procesamiento.
     * @throws UsuarioNoEncontradoException Si el usuario no existe en la base de datos local o en Keycloak.
     */
    List<RolDTO> obtenerRolesUsuario(Long usuarioId) throws RolesException, UsuarioNoEncontradoException;

    /**
     * Recupera los permisos (Client Roles) específicos de la aplicación asignados a un usuario.
     * <p>
     * A diferencia de los roles de Realm, este método obtiene los roles asociados
     * exclusivamente al Cliente (Client ID) configurado. Representan permisos granulares
     * dentro de la plataforma (ej. EDITAR_CALIFICACIONES, PASAR_LISTA).
     * </p>
     *
     * @param usuarioId El identificador único del usuario en el sistema.
     * @return Una lista de {@link PermisoDTO} que representan las capacidades del usuario en la app.
     * @throws PermisosException Si hay fallos al consultar los recursos del cliente en Keycloak.
     * @throws UsuarioNoEncontradoException Si el ID proporcionado no corresponde a un usuario válido.
     */
    List<PermisoDTO> obtenerPermisosUsuario(Long usuarioId) throws PermisosException, UsuarioNoEncontradoException;

    /**
     * Asigna uno o más roles a un usuario.
     *
     * <p>Este método reemplaza o actualiza la relación entre el usuario
     * y sus roles, según las reglas del sistema.</p>
     *
     * @param usuarioId identificador del usuario
     * @param listaRoles lista de nombres de roles a asignar
     * @throws RolesException si el usuario no existe o alguno de los roles es inválido
     */
    void asignarRolesUsuario(Long usuarioId, List<String> listaRoles) throws RolesException, UsuarioNoEncontradoException;

    /**
     * Asigna uno o más permisos directos a un usuario.
     *
     * <p>Este método se utiliza cuando los permisos no dependen
     * exclusivamente de los roles.</p>
     *
     * @param usuarioId identificador del usuario
     * @param listaPermisos lista de nombres de permisos a asignar
     * @throws PermisosException si el usuario no existe o alguno de los permisos es inválido
     */
    void asignarPermisosUsuario(Long usuarioId, List<String> listaPermisos) throws PermisosException, UsuarioNoEncontradoException;

    void removerRolesUsuario(Long usuarioId, List<String> listaRoles) throws RolesException, UsuarioNoEncontradoException;
    void removerPermisosUsuario(Long usuarioId, List<String> listaPermisos) throws PermisosException, UsuarioNoEncontradoException;

    Page<InformacionBasicaUsuarioDTO> obtenerUsuariosPorCredenciales(String credenciales, Pageable pageable);

    void cerrarSesion(String refreshToken) throws UsuarioException;

    boolean tieneSesionActiva(String jwt) throws UsuarioException;

    Map<String, Object> refrescarToken(String refreshToken) throws UsuarioException;
}
