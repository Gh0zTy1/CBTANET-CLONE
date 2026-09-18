package mx.edu.cbta.sistemaescolar.alumnado.util;

import mx.edu.cbta.sistemaescolar.alumnado.domain.model.Alumno;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface ExcelUtility {

    public List<Alumno> readExcel(InputStream file) throws IOException;

}
