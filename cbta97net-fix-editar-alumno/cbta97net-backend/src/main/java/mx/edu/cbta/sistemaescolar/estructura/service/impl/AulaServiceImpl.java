package mx.edu.cbta.sistemaescolar.estructura.service.impl;

import lombok.extern.slf4j.Slf4j;
import mx.edu.cbta.sistemaescolar.estructura.dto.AulaDTO;
import mx.edu.cbta.sistemaescolar.estructura.mapper.AulaMapper;
import mx.edu.cbta.sistemaescolar.estructura.domain.model.Aula;
import mx.edu.cbta.sistemaescolar.estructura.repository.AulaRepository;
import mx.edu.cbta.sistemaescolar.estructura.service.AulaService;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.AulaDuplicadaException;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.AulaNoEncontradaException;

import mx.edu.cbta.sistemaescolar.estructura.domain.exception.EliminarAulaException;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.RegistrarAulaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AulaServiceImpl implements AulaService {

    private AulaRepository aulaRepository;

    @Autowired
    private AulaMapper aulaMapper;

    public AulaServiceImpl(AulaRepository aulaRepository) {
        this.aulaRepository = aulaRepository;
    }

    @Override
    public AulaDTO obtenerAulaPorId(Long idAula) throws AulaNoEncontradaException {
        return aulaRepository.findById(idAula)
                .map(this.aulaMapper::toDTO)
                .orElseThrow(() -> new AulaNoEncontradaException("No se encontró el aula con id: " + idAula));
    }

    @Override
    public AulaDTO obtenerAulaPorClave(String clave) throws AulaNoEncontradaException {
        Aula aula = aulaRepository.findByClave(clave);
        if(aula == null){
            throw new AulaNoEncontradaException("No se encontró el aula con clave: " + clave);
        }
        return this.aulaMapper.toDTO(aula);
    }

    @Override
    public AulaDTO registrarAula(AulaDTO aula) throws AulaDuplicadaException, RegistrarAulaException {

        if (this.aulaRepository.existsByClave(aula.getClave())) {
            throw new AulaDuplicadaException("Ya existe un aula con la clave: '%s'.".formatted(aula.getClave()));
        }

        Aula registrada = this.aulaRepository.save(this.aulaMapper.toEntity(aula));

        return this.aulaMapper.toDTO(registrada);
    }

    @Override
    public List<AulaDTO> obtenerTodasLasAulas() {
        return aulaRepository.findAll().stream().map(this.aulaMapper::toDTO).toList();
    }

    @Override
    public void eliminarAulaPorId(Long aulaId) throws AulaNoEncontradaException, EliminarAulaException {
        if (!this.aulaRepository.existsById(aulaId)) {
            throw new AulaNoEncontradaException("No existe un aula con el ID: '%s'.".formatted(aulaId));
        }

        try {
            this.aulaRepository.deleteById(aulaId);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new EliminarAulaException("No se pudo eliminar el aula. Intente de nuevo más tarde.");
        }
    }

    @Override
    public void eliminarAulaPorClave(String clave) throws AulaNoEncontradaException, EliminarAulaException {
        if (!this.aulaRepository.existsByClave(clave)) {
            throw new AulaNoEncontradaException("No existe un aula con la clave: '%s'.".formatted(clave));
        }

        try {
            this.aulaRepository.deleteByClave(clave);
        } catch (DataIntegrityViolationException ex) {
            log.error(ex.getMessage(), ex);
            throw new EliminarAulaException("No se puede eliminar el aula porque tiene registros asociados (horarios o grupos).");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new EliminarAulaException("No se pudo eliminar el aula. Intente de nuevo más tarde.");
        }
    }
}
