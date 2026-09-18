package mx.edu.cbta.sistemaescolar.estructura.util.impl;

import lombok.extern.slf4j.Slf4j;
import mx.edu.cbta.sistemaescolar.estructura.domain.model.Materia;
import mx.edu.cbta.sistemaescolar.estructura.util.LectorMateriasSISEEMS;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class LectorMateriasSISEEMSImpl implements LectorMateriasSISEEMS {

    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static ArrayList<String> encabezados = new ArrayList<>();
    public static String SHEET = "Hoja1";

    private Boolean tieneFormatoExcelValido(MultipartFile file) {
        return (TYPE.equals(file.getContentType()));
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return BigDecimal.valueOf(cell.getNumericCellValue()).toPlainString();
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "UNKNOWN";
        }
    }

    private int indexColumnaEspecifica(String columna) {
        return encabezados.indexOf(columna);
    }

    private void cargarEncabezados(Iterator<Cell> cellIterator) {
        encabezados.clear();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            String cellValue = getCellValueAsString(cell);
            encabezados.add(cellValue);
        }
    }

    @Override
    public List<Materia> obtenerMaterias(InputStream archivo) throws IOException {
        try {
            Workbook workbook = new XSSFWorkbook(archivo);
            Sheet sheet = workbook.getSheet(SHEET);
            Iterator<Row> rowIterator = sheet.iterator();

            List<Materia> materias = new ArrayList<>();
            int colNomAsignatura = 0;
            int colSemestre = 0;

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();

                if (row.getRowNum() == 0) {
                    cargarEncabezados(cellIterator);
                    colNomAsignatura = indexColumnaEspecifica("NOMBRE ASIGNATURA");
                    colSemestre = indexColumnaEspecifica("SEMESTRE");
                    continue;
                }

                Materia materia = new Materia();

                String nombreMateria = getCellValueAsString(row.getCell(colNomAsignatura));
                materia.setNombre(nombreMateria);

                String semestreValue = getCellValueAsString(row.getCell(colSemestre));
                if (!semestreValue.isEmpty()) {
                    try {
                        int semestre = (int) Double.parseDouble(semestreValue);
                        materia.setSemestre(semestre);
                    } catch (NumberFormatException e) {
                        materia.setSemestre(0);
                    }
                }

                long repeticionesDeMateria = materias.stream()
                        .filter(m -> m.getNombre().equals(materia.getNombre()))
                        .count();

                if (repeticionesDeMateria < 1 && !materia.getNombre().isEmpty()) {
                    materia.setHorasPorSemana(0); // valor por defecto...
                    System.out.println(materia.getNombre());
                    materias.add(materia);
                }
            }
            workbook.close();
            return materias;
        } catch (IOException e) {
            throw new IOException("No se pudo obtener las materias del archivo cargado");
        }
    }
}