package mx.edu.cbta.sistemaescolar.alumnado.util.impl;

import lombok.extern.slf4j.Slf4j;
import mx.edu.cbta.sistemaescolar.alumnado.domain.model.Alumno;
import mx.edu.cbta.sistemaescolar.alumnado.util.ExcelUtility;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;


import java.math.BigDecimal;
import java.util.ArrayList;

import java.io.IOException;
import java.io.InputStream;

import java.util.Iterator;
import java.util.List;

@Slf4j
public class ExcelUtilityImpl implements ExcelUtility {

    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static ArrayList<String> encabezados = new ArrayList<>();
    public static String SHEET = "Hoja1";

    private Boolean hasExcelFormat(MultipartFile file){

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

        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();

            String cellValue = getCellValueAsString(cell);

            encabezados.add(cellValue);

        }
    }

    @Override
    public List<Alumno> readExcel(InputStream file) {
        try {

            encabezados.clear();

            Workbook workbook = new XSSFWorkbook(file);

            Sheet sheet = workbook.getSheet(SHEET);

            Iterator<Row> rowIterator = sheet.iterator();

            List<Alumno> alumnos = new ArrayList<>();

            int rowNum = 0;
            int colNCAlumno = 0;
            int colNomAlumno = 0;
            int colAPAlumno = 0;
            int colAMAlumno = 0;
            int colCurpAlumno = 0;
            int colGeneracionAlumno = 0;
            int colSemestreAlumno = 0;

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                Iterator<Cell> cellIterator = row.cellIterator();

                if (row.getRowNum() == 0) {

                    cargarEncabezados(cellIterator);

                    colNCAlumno = indexColumnaEspecifica("NO CONTROL");
                    colNomAlumno = indexColumnaEspecifica("NOMBRE");
                    colAPAlumno = indexColumnaEspecifica("PATERNO");
                    colAMAlumno = indexColumnaEspecifica("MATERNO");
                    colCurpAlumno = indexColumnaEspecifica("CURP");
                    colGeneracionAlumno = indexColumnaEspecifica("GENERACION");
                    colSemestreAlumno = indexColumnaEspecifica("SEMESTRE");

                    rowNum++;
                    continue;
                }

                Alumno alumno = new Alumno();

                int celIdx = 0;

                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();

                    String cellValue = getCellValueAsString(cell);

                    if (celIdx == colNCAlumno) {
                        alumno.setMatricula(cellValue);
                    } else if (celIdx == colNomAlumno) {
                        alumno.setNombre(cellValue);
                    } else if (celIdx == colAPAlumno) {
                        alumno.setApellidoPaterno(cellValue);
                    } else if (celIdx == colAMAlumno) {
                        alumno.setApellidoMaterno(cellValue);
                    } else if (celIdx == colCurpAlumno) {
                        alumno.setCurp(cellValue);
                    } else if (celIdx == colGeneracionAlumno) {
                        alumno.setGeneracion(cellValue);
                        System.out.println("### GENERACION-----: %s".formatted(cellValue));
                    } else if (celIdx == colSemestreAlumno) {
                        if (cellValue != null && !cellValue.isEmpty()) {
                            try {
                                String valorLimpio = cellValue.split("\\.")[0];
                                System.out.println("### SEMESTRE: %s".formatted(valorLimpio));
                                alumno.setSemestre(Integer.parseInt(valorLimpio));
                            } catch (NumberFormatException e) {
                                log.error("Error al convertir semestre: " + cellValue);
                                alumno.setSemestre(0);
                            }
                        }
                    }

                    celIdx++;
                }

                /*
                Long repeticionesDeAlumno = alumnos.stream()
                        .filter(alumnoAux -> alumnoAux.getMatricula().equals(alumno.getMatricula()))
                        .count();
                       if (repeticionesDeAlumno < 1) {
                    alumnos.add(alumno);
                }
                       */

                alumno.calcularFechaDesdeCurp();

                alumnos.add(alumno);
            }
            workbook.close();
            return alumnos;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }
}
