package mx.edu.cbta.sistemaescolar.estructura.util.impl;

import lombok.extern.slf4j.Slf4j;
import mx.edu.cbta.sistemaescolar.estructura.domain.model.CarreraTecnica;
import mx.edu.cbta.sistemaescolar.estructura.util.LectorCarrerasTecnicasSISEEMS;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class LectorCarrerasTecnicasSISEEMSImpl implements LectorCarrerasTecnicasSISEEMS {
    private static final String SHEET = "Hoja1";
    private static final List<String> encabezados = new ArrayList<>();

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> BigDecimal.valueOf(cell.getNumericCellValue()).toPlainString();
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            case BLANK -> "";
            default -> "UNKNOWN";
        };
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
    public List<CarreraTecnica> obtenerCarrerasTecnicas(InputStream archivo) throws IOException {
        try {
            Workbook workbook = new XSSFWorkbook(archivo);
            Sheet sheet = workbook.getSheet(SHEET);
            Iterator<Row> rowIterator = sheet.iterator();

            List<CarreraTecnica> carreras = new ArrayList<>();
            int colCarrera = 0;

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();

                // Fila de encabezados
                if (row.getRowNum() == 0) {
                    cargarEncabezados(cellIterator);
                    colCarrera = indexColumnaEspecifica("CARRERA");
                    continue;
                }

                CarreraTecnica carrera = new CarreraTecnica();
                String nombreCarrera = getCellValueAsString(row.getCell(colCarrera));

                if (!nombreCarrera.isEmpty()) {
                    carrera.setNombre(nombreCarrera);
                    carrera.setDescripcion(nombreCarrera);

                    long repeticiones = carreras.stream()
                            .filter(c -> c.getNombre().equalsIgnoreCase(carrera.getNombre()))
                            .count();

                    if (repeticiones < 1) {
                        carreras.add(carrera);
                    }
                }
            }
            workbook.close();
            return carreras;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new IOException("No se pudo obtener las carreras técnicas del archivo cargado");
        }
    }
}
