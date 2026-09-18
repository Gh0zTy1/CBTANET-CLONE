package mx.edu.cbta.sistemaescolar.estructura.service.impl;

import lombok.extern.slf4j.Slf4j;

import mx.edu.cbta.sistemaescolar.estructura.dto.MateriaDTO;
import mx.edu.cbta.sistemaescolar.estructura.mapper.MateriaMapper;
import mx.edu.cbta.sistemaescolar.estructura.domain.model.Materia;
import mx.edu.cbta.sistemaescolar.estructura.repository.MateriaRepository;
import mx.edu.cbta.sistemaescolar.estructura.service.MateriaService;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.ImportarMateriasSISEEMSException;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.MateriaDuplicadaException;
import mx.edu.cbta.sistemaescolar.estructura.domain.exception.MateriaNoEncontradaException;

import mx.edu.cbta.sistemaescolar.estructura.domain.exception.SemestreMateriaNoValidoException;
import mx.edu.cbta.sistemaescolar.estructura.util.LectorMateriasSISEEMS;
import mx.edu.cbta.sistemaescolar.estructura.util.impl.LectorMateriasSISEEMSImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class MateriaServiceImpl implements MateriaService {

    private final MateriaRepository materiaRepository;

    private LectorMateriasSISEEMS lectorMateriasSISEEMS;

    @Autowired
    private MateriaMapper materiaMapper;

    public MateriaServiceImpl(MateriaRepository materiaRepository) {
        this.materiaRepository = materiaRepository;
        this.lectorMateriasSISEEMS = new LectorMateriasSISEEMSImpl();
    }

    @Override
    public MateriaDTO obtenerMateriaPorId(Long id) throws MateriaNoEncontradaException {
        return materiaRepository.findById(id).map(materiaMapper::toDto)
                .orElseThrow(() -> new MateriaNoEncontradaException("No se encontró la materia con id: " + id));
    }

    @Override
    public MateriaDTO registrarMateria(MateriaDTO materia) throws SemestreMateriaNoValidoException, MateriaDuplicadaException {

        if (!(materia.getSemestre() >= 1 && materia.getSemestre() <= 6)) {
            throw new SemestreMateriaNoValidoException("El semestre debe ser de Primer semestre al Sexto semestre.");
        }

        boolean duplicada = this.materiaRepository.existsByNombre(materia.getNombre());
        if (duplicada) {
            throw new MateriaDuplicadaException("Ya existe una materia registrada con el nombre '%s'.".formatted(materia.getNombre()));
        }

        Materia nuevaMateria = this.materiaMapper.toEntity(materia);
        nuevaMateria.setNombre(normalizarTexto(nuevaMateria.getNombre()));
        Materia registrada = materiaRepository.save(nuevaMateria);
        return this.materiaMapper.toDto(registrada);
    }

    @Override
    public List<MateriaDTO> obtenerTodasLasMaterias() {
        return materiaRepository.findAll()
                .stream()
                .map(materiaMapper::toDto)
                .toList();
    }

    @Override
    public List<MateriaDTO> obtenerTodasPorAreaPropedeutica(Long areaPropedeuticaId) {
        return this.materiaRepository.findByAreaPropedeuticaId(areaPropedeuticaId)
                .stream()
                .map(materiaMapper::toDto)
                .toList();
    }

    @Override
    public List<MateriaDTO> obtenerMateriasPorCarrera(Long carreraTecnicaId) {
        return materiaRepository.findByCarreraTecnicaId(carreraTecnicaId)
                .stream()
                .map(materiaMapper::toDto)
                .toList();
    }

    @Override
    public List<MateriaDTO> obtenerMateriasPorSemestre(int semestre) {
        return materiaRepository.findBySemestre(semestre)
                .stream()
                .map(materiaMapper::toDto)
                .toList();
    }

    @Override
    public List<MateriaDTO> obtenerMateriasPorSemestreYCarrera(int semestre, Long carreraTecnicaId) {
        return materiaRepository.findBySemestreAndCarreraTecnicaId(semestre, carreraTecnicaId)
                .stream()
                .map(materiaMapper::toDto)
                .toList();
    }

    @Override
    public List<MateriaDTO> obtenerMateriasPorSemestreYCarreraYArea(int semestre, Long carreraTecnicaId, Long areaPropedeuticaId) {
        return materiaRepository.findBySemestreAndCarreraTecnicaIdAndAreaPropedeuticaId(semestre, carreraTecnicaId, areaPropedeuticaId)
                .stream()
                .map(materiaMapper::toDto)
                .toList();
    }

    /**
     * Método para normalizar texto:
     * - Elimina acentos (Á -> A)
     * - Elimina caracteres especiales (solo deja letras y espacios)
     * - Convierte a MAYÚSCULAS
     */
    private String normalizarTexto(String texto) {
        if (texto == null) return null;

        String normalizado = java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD);
        normalizado = normalizado.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

        return normalizado.replaceAll("[^a-zA-Z\\s]", "")
                .toUpperCase()
                .trim();
    }

    @Override
    public void importarMateriasSISEEMS(MultipartFile archivoSISEEMS) throws ImportarMateriasSISEEMSException {
        try {
            List<Materia> materiasEncontradas = this.lectorMateriasSISEEMS.obtenerMaterias(archivoSISEEMS.getInputStream());

            System.out.println("Materias encontradas en Excel: %d".formatted(materiasEncontradas.size()));

            List<Materia> materiasFiltradas = materiasEncontradas.stream()
                    .peek(m -> m.setNombre(normalizarTexto(m.getNombre())))
                    .filter(m -> !materiaRepository.existsByNombre(m.getNombre()))
                    .toList();

            System.out.println("Materias para registrar: %d".formatted(materiasEncontradas.size()));

            long insertados = this.materiaRepository.saveAll(materiasFiltradas).size();

            log.info("Materias importadas desde archivo de SISEEMS: %d".formatted(insertados));
        }catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            throw new ImportarMateriasSISEEMSException(ex.getMessage());
        }
    }
}
