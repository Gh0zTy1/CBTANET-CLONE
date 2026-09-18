
-- reseteamos la base de datos
SOURCE reset.sql;

USE cbta97net;

-- cargamos la configuracion de tabla de usuarios...
SOURCE setup-users.sql;

-- insertamos un usuario con todos los permisos posibles en el sistema.



INSERT INTO ciclos_escolares (fecha_inicio, fecha_fin)
VALUES ('2025-08-01', '2025-12-30');

INSERT INTO areas_propedeuticas (nombre, descripcion) VALUES
('Físico-Matemática', 'Área enfocada en las ciencias exactas, la física, las matemáticas y la ingeniería.'),
('Químico-Biológica', 'Área centrada en el estudio de la química, la biología y las ciencias de la salud.'),
('Económico-Administrativa', 'Área relacionada con la economía, administración, contabilidad y finanzas.'),
('Humanidades y Ciencias Sociales', 'Área dedicada al análisis del comportamiento humano y las estructuras sociales.'),
('Artes y Diseño', 'Área enfocada en la creatividad, las artes visuales, la música y el diseño.'),
('Informática y Computación', 'Área que abarca programación, desarrollo de software y tecnologías de la información.'),
('Agropecuaria', 'Área orientada a las ciencias agrícolas, ganaderas y el manejo sustentable del campo.'),
('Industrial', 'Área enfocada en los procesos de producción, manufactura y control de calidad.'),
('Turismo y Gastronomía', 'Área relacionada con los servicios turísticos, hotelería y gastronomía.'),
('Educación y Pedagogía', 'Área dedicada al estudio y aplicación de teorías de la enseñanza y aprendizaje.');

INSERT INTO carreras_tecnicas (nombre, descripcion) VALUES
('Técnico en Soporte y Mantenimiento de Equipo de Cómputo', 
 'Forma profesionales capaces de instalar, diagnosticar y dar mantenimiento preventivo y correctivo a equipos de cómputo, redes y periféricos, garantizando su óptimo funcionamiento.'),

('Técnico en Producción Agrícola', 
 'Prepara al estudiante en el manejo sostenible de cultivos agrícolas, aplicando técnicas modernas de siembra, fertilización, riego y control de plagas para mejorar la productividad del campo.'),

('Técnico en Producción Pecuaria', 
 'Capacita para la crianza, alimentación y manejo sanitario de animales domésticos y de granja, optimizando la producción de carne, leche, huevo y otros derivados.'),

('Técnico Agropecuario', 
 'Integra conocimientos de agricultura y ganadería para el aprovechamiento eficiente de los recursos naturales y el desarrollo sustentable de las unidades de producción rural.'),

('Técnico en Desarrollo Integral Comunitario', 
 'Forma profesionales con habilidades para promover el bienestar social, económico y ambiental en comunidades rurales y urbanas, mediante proyectos participativos y de desarrollo sostenible.');

-- Materias de Primer Semestre
INSERT INTO materias (nombre, grado, horas_por_semana) VALUES
('Matemáticas I', 'Primer_Semestre', 5),
('Química I', 'Primer_Semestre', 3),
('Biología General', 'Primer_Semestre', 2),
('Comunicación Oral y Escrita', 'Primer_Semestre', 2),
('Tecnologías de la Información y la Comunicación', 'Primer_Semestre', 3),
('Inglés I', 'Primer_Semestre', 3),
('Formación Sociocultural I', 'Primer_Semestre', 3),
('Introducción a las Ciencias Agropecuarias', 'Primer_Semestre', 3);

-- MATERIAS SEGUNDO SEMESTRE

-- Carrera 1: Técnico en Soporte y Mantenimiento de Equipo de Cómputo
INSERT INTO materias (nombre, grado, carrera_tecnica_id, horas_por_semana) VALUES
('Matemáticas II', 'Segundo_Semestre', 1, 4),
('Inglés II', 'Segundo_Semestre', 1, 4),
('Formación Sociocultural II', 'Segundo_Semestre', 1, 4),
('Electricidad y Electrónica Básica', 'Segundo_Semestre', 1, 4),
('Mantenimiento Preventivo de Equipos de Cómputo', 'Segundo_Semestre', 1, 4),
('Sistemas Operativos', 'Segundo_Semestre', 1, 4),
('Ensambles y Desensambles de PC', 'Segundo_Semestre', 1, 4),
('Redes de Computadoras I', 'Segundo_Semestre', 1, 4);


-- Carrera 2: Técnico en Producción Agrícola
INSERT INTO materias (nombre, grado, carrera_tecnica_id, horas_por_semana) VALUES
('Matemáticas II', 'Segundo_Semestre', 2, 4),
('Química Agrícola', 'Segundo_Semestre', 2, 4),
('Biología Vegetal', 'Segundo_Semestre', 2, 4),
('Edafología (Ciencia del Suelo)', 'Segundo_Semestre', 2, 4),
('Fisiología Vegetal', 'Segundo_Semestre', 2, 4),
('Maquinaria y Herramientas Agrícolas', 'Segundo_Semestre', 2, 4),
('Propagación de Plantas', 'Segundo_Semestre', 2, 4),
('Riego y Drenaje Agrícola', 'Segundo_Semestre', 2, 4);


-- Carrera 3: Técnico en Producción Pecuaria
INSERT INTO materias (nombre, grado, carrera_tecnica_id, horas_por_semana) VALUES
('Matemáticas II', 'Segundo_Semestre', 3, 4),
('Anatomía y Fisiología Animal', 'Segundo_Semestre', 3, 4),
('Nutrición Animal', 'Segundo_Semestre', 3, 4),
('Manejo y Reproducción de Especies Pecuarias', 'Segundo_Semestre', 3, 4),
('Sanidad Animal', 'Segundo_Semestre', 3, 4),
('Producción de Alimentos para el Ganado', 'Segundo_Semestre', 3, 4),
('Prácticas de Campo Pecuario', 'Segundo_Semestre', 3, 4),
('Economía Pecuaria', 'Segundo_Semestre', 3, 4);


-- Carrera 4: Técnico Agropecuario
INSERT INTO materias (nombre, grado, carrera_tecnica_id, horas_por_semana) VALUES
('Matemáticas II', 'Segundo_Semestre', 4, 4),
('Química del Suelo', 'Segundo_Semestre', 4, 4),
('Zootecnia General', 'Segundo_Semestre', 4, 4),
('Agronomía Básica', 'Segundo_Semestre', 4, 4),
('Ecología y Medio Ambiente', 'Segundo_Semestre', 4, 4),
('Manejo de Recursos Naturales', 'Segundo_Semestre', 4, 4),
('Maquinaria Agropecuaria', 'Segundo_Semestre', 4, 4),
('Prácticas Integrales de Campo', 'Segundo_Semestre', 4, 4);

-- Carrera 5: Técnico en Desarrollo Integral Comunitario
INSERT INTO materias (nombre, grado, carrera_tecnica_id, horas_por_semana) VALUES
('Matemáticas II', 'Segundo_Semestre', 5, 4),
('Sociología Rural y Urbana', 'Segundo_Semestre', 5, 4),
('Desarrollo Sustentable', 'Segundo_Semestre', 5, 4),
('Comunicación para el Desarrollo', 'Segundo_Semestre', 5, 4),
('Diseño y Gestión de Proyectos Comunitarios', 'Segundo_Semestre', 5, 4),
('Educación Ambiental', 'Segundo_Semestre', 5, 4),
('Economía Social y Solidaria', 'Segundo_Semestre', 5, 4),
('Práctica Comunitaria I', 'Segundo_Semestre', 5, 4);

-- MATERIAS DE TERCER SEMESTRE

-- Carrera 1: Técnico en Soporte y Mantenimiento de Equipo de Cómputo
INSERT INTO materias (nombre, grado, carrera_tecnica_id, horas_por_semana) VALUES
('Matemáticas III', 'Tercer_Semestre', 1, 4),
('Inglés III', 'Tercer_Semestre', 1, 4),
('Formación Sociocultural III', 'Tercer_Semestre', 1, 4),
('Electrónica Digital', 'Tercer_Semestre', 1, 4),
('Mantenimiento Correctivo de Equipos de Cómputo', 'Tercer_Semestre', 1, 4),
('Redes de Computadoras II', 'Tercer_Semestre', 1, 4),
('Arquitectura de Computadoras', 'Tercer_Semestre', 1, 4),
('Seguridad y Respaldo de Información', 'Tercer_Semestre', 1, 4);


-- Carrera 2: Técnico en Producción Agrícola
INSERT INTO materias (nombre, grado, carrera_tecnica_id, horas_por_semana) VALUES
('Matemáticas III', 'Tercer_Semestre', 2, 4),
('Fertilidad de Suelos', 'Tercer_Semestre', 2, 4),
('Fitotecnia', 'Tercer_Semestre', 2, 4),
('Control de Plagas y Enfermedades Vegetales', 'Tercer_Semestre', 2, 4),
('Topografía y Cartografía Agrícola', 'Tercer_Semestre', 2, 4),
('Manejo de Invernaderos', 'Tercer_Semestre', 2, 4),
('Horticultura', 'Tercer_Semestre', 2, 4),
('Prácticas de Campo Agrícola I', 'Tercer_Semestre', 2, 4);


-- Carrera 3: Técnico en Producción Pecuaria
INSERT INTO materias (nombre, grado, carrera_tecnica_id, horas_por_semana) VALUES
('Matemáticas III', 'Tercer_Semestre', 3, 4),
('Fisiología de la Reproducción Animal', 'Tercer_Semestre', 3, 4),
('Manejo de Pastos y Forrajes', 'Tercer_Semestre', 3, 4),
('Producción Bovina', 'Tercer_Semestre', 3, 4),
('Sanidad y Bioseguridad Pecuaria', 'Tercer_Semestre', 3, 4),
('Producción Porcina', 'Tercer_Semestre', 3, 4),
('Economía y Administración Pecuaria', 'Tercer_Semestre', 3, 4),
('Prácticas de Campo Pecuario II', 'Tercer_Semestre', 3, 4);


-- Carrera 4: Técnico Agropecuario
INSERT INTO materias (nombre, grado, carrera_tecnica_id, horas_por_semana) VALUES
('Matemáticas III', 'Tercer_Semestre', 4, 4),
('Biotecnología Agropecuaria', 'Tercer_Semestre', 4, 4),
('Manejo Integral de Cultivos', 'Tercer_Semestre', 4, 4),
('Producción Animal Sostenible', 'Tercer_Semestre', 4, 4),
('Agroecología', 'Tercer_Semestre', 4, 4),
('Economía y Comercialización Agropecuaria', 'Tercer_Semestre', 4, 4),
('Sistemas de Producción Rural', 'Tercer_Semestre', 4, 4),
('Prácticas Agropecuarias Integradas I', 'Tercer_Semestre', 4, 4);


-- Carrera 5: Técnico en Desarrollo Integral Comunitario
INSERT INTO materias (nombre, grado, carrera_tecnica_id, horas_por_semana) VALUES
('Matemáticas III', 'Tercer_Semestre', 5, 4),
('Psicología Comunitaria', 'Tercer_Semestre', 5, 4),
('Planeación del Desarrollo Local', 'Tercer_Semestre', 5, 4),
('Gestión Social y Participación Ciudadana', 'Tercer_Semestre', 5, 4),
('Educación para la Salud Comunitaria', 'Tercer_Semestre', 5, 4),
('Evaluación de Proyectos Comunitarios', 'Tercer_Semestre', 5, 4),
('Economía del Desarrollo', 'Tercer_Semestre', 5, 4),
('Práctica Comunitaria II', 'Tercer_Semestre', 5, 4);

-- MATERIAS DE CUARTO SEMESTRE

-- Carrera 1: Técnico en Soporte y Mantenimiento de Equipo de Cómputo
INSERT INTO materias (nombre, grado, carrera_tecnica_id, horas_por_semana) VALUES
('Matemáticas IV', 'Cuarto_Semestre', 1, 4),
('Inglés IV', 'Cuarto_Semestre', 1, 4),
('Formación Sociocultural IV', 'Cuarto_Semestre', 1, 4),
('Diagnóstico y Reparación de Fallas en Hardware', 'Cuarto_Semestre', 1, 4),
('Administración de Redes de Computadoras', 'Cuarto_Semestre', 1, 4),
('Instalación y Configuración de Software Especializado', 'Cuarto_Semestre', 1, 4),
('Soporte Técnico a Usuarios', 'Cuarto_Semestre', 1, 4),
('Proyecto Integrador de Mantenimiento de Cómputo', 'Cuarto_Semestre', 1, 4);


-- Carrera 2: Técnico en Producción Agrícola
INSERT INTO materias (nombre, grado, carrera_tecnica_id, horas_por_semana) VALUES
('Matemáticas IV', 'Cuarto_Semestre', 2, 4),
('Agrometeorología', 'Cuarto_Semestre', 2, 4),
('Cultivos Básicos y Estratégicos', 'Cuarto_Semestre', 2, 4),
('Manejo Integrado de Plagas y Enfermedades', 'Cuarto_Semestre', 2, 4),
('Producción en Ambientes Controlados', 'Cuarto_Semestre', 2, 4),
('Postcosecha y Comercialización de Productos Agrícolas', 'Cuarto_Semestre', 2, 4),
('Agroindustria de Transformación', 'Cuarto_Semestre', 2, 4),
('Prácticas de Campo Agrícola II', 'Cuarto_Semestre', 2, 4);


-- Carrera 3: Técnico en Producción Pecuaria
INSERT INTO materias (nombre, grado, carrera_tecnica_id, horas_por_semana) VALUES
('Matemáticas IV', 'Cuarto_Semestre', 3, 4),
('Producción Avícola', 'Cuarto_Semestre', 3, 4),
('Producción Caprina y Ovina', 'Cuarto_Semestre', 3, 4),
('Procesamiento de Productos Pecuarios', 'Cuarto_Semestre', 3, 4),
('Administración de Unidades de Producción Pecuaria', 'Cuarto_Semestre', 3, 4),
('Mejoramiento Genético Animal', 'Cuarto_Semestre', 3, 4),
('Sanidad y Bienestar Animal', 'Cuarto_Semestre', 3, 4),
('Prácticas de Campo Pecuario III', 'Cuarto_Semestre', 3, 4);


-- Carrera 4: Técnico Agropecuario
INSERT INTO materias (nombre, grado, carrera_tecnica_id, horas_por_semana) VALUES
('Matemáticas IV', 'Cuarto_Semestre', 4, 4),
('Producción Sustentable de Cultivos', 'Cuarto_Semestre', 4, 4),
('Producción Animal Intensiva', 'Cuarto_Semestre', 4, 4),
('Manejo de Recursos Hidráulicos', 'Cuarto_Semestre', 4, 4),
('Economía Rural y Mercados Agropecuarios', 'Cuarto_Semestre', 4, 4),
('Gestión Ambiental Agropecuaria', 'Cuarto_Semestre', 4, 4),
('Innovación Tecnológica en el Campo', 'Cuarto_Semestre', 4, 4),
('Prácticas Agropecuarias Integradas II', 'Cuarto_Semestre', 4, 4);


-- Carrera 5: Técnico en Desarrollo Integral Comunitario
INSERT INTO materias (nombre, grado, carrera_tecnica_id, horas_por_semana) VALUES
('Matemáticas IV', 'Cuarto_Semestre', 5, 4),
('Intervención Comunitaria', 'Cuarto_Semestre', 5, 4),
('Diseño de Estrategias de Desarrollo', 'Cuarto_Semestre', 5, 4),
('Gestión de Recursos y Financiamiento Social', 'Cuarto_Semestre', 5, 4),
('Cultura y Diversidad Comunitaria', 'Cuarto_Semestre', 5, 4),
('Liderazgo y Trabajo en Equipo', 'Cuarto_Semestre', 5, 4),
('Evaluación del Impacto Social de Proyectos', 'Cuarto_Semestre', 5, 4),
('Práctica Comunitaria III', 'Cuarto_Semestre', 5, 4);


-- MATERIAS DE QUINTO SEMESTRE

-- Carrera 1: Técnico en Soporte y Mantenimiento de Equipo de Cómputo
INSERT INTO materias (nombre, grado, carrera_tecnica_id, horas_por_semana) VALUES
('Matemáticas V', 'Quinto_Semestre', 1, 4),
('Inglés V', 'Quinto_Semestre', 1, 4),
('Formación Sociocultural V', 'Quinto_Semestre', 1, 4),
('Mantenimiento de Equipos Portátiles y Dispositivos Móviles', 'Quinto_Semestre', 1, 4),
('Administración de Servidores', 'Quinto_Semestre', 1, 4),
('Redes Inalámbricas y Conectividad Avanzada', 'Quinto_Semestre', 1, 4),
('Automatización y Control con Microcontroladores', 'Quinto_Semestre', 1, 4),
('Proyecto Integrador de Redes y Soporte', 'Quinto_Semestre', 1, 4);


-- Carrera 2: Técnico en Producción Agrícola
INSERT INTO materias (nombre, grado, carrera_tecnica_id, horas_por_semana) VALUES
('Matemáticas V', 'Quinto_Semestre', 2, 4),
('Cultivos de Exportación', 'Quinto_Semestre', 2, 4),
('Producción Orgánica y Sustentable', 'Quinto_Semestre', 2, 4),
('Administración de Empresas Agropecuarias', 'Quinto_Semestre', 2, 4),
('Gestión de la Calidad e Inocuidad Agroalimentaria', 'Quinto_Semestre', 2, 4),
('Agroindustria y Valor Agregado', 'Quinto_Semestre', 2, 4),
('Comercialización y Mercadotecnia Agrícola', 'Quinto_Semestre', 2, 4),
('Prácticas de Campo Agrícola III', 'Quinto_Semestre', 2, 4);


-- Carrera 3: Técnico en Producción Pecuaria
INSERT INTO materias (nombre, grado, carrera_tecnica_id, horas_por_semana) VALUES
('Matemáticas V', 'Quinto_Semestre', 3, 4),
('Producción Apícola', 'Quinto_Semestre', 3, 4),
('Producción Cunícola y de Especies Menores', 'Quinto_Semestre', 3, 4),
('Procesamiento de Carne y Lácteos', 'Quinto_Semestre', 3, 4),
('Gestión de la Producción Pecuaria', 'Quinto_Semestre', 3, 4),
('Sistemas de Información para la Producción Pecuaria', 'Quinto_Semestre', 3, 4),
('Normatividad y Bienestar Animal', 'Quinto_Semestre', 3, 4),
('Prácticas de Campo Pecuario IV', 'Quinto_Semestre', 3, 4);


-- Carrera 4: Técnico Agropecuario
INSERT INTO materias (nombre, grado, carrera_tecnica_id, horas_por_semana) VALUES
('Matemáticas V', 'Quinto_Semestre', 4, 4),
('Tecnologías Sustentables de Producción', 'Quinto_Semestre', 4, 4),
('Gestión Empresarial Agropecuaria', 'Quinto_Semestre', 4, 4),
('Biotecnología Aplicada al Campo', 'Quinto_Semestre', 4, 4),
('Innovación y Transferencia de Tecnología', 'Quinto_Semestre', 4, 4),
('Comercialización y Cadenas Productivas', 'Quinto_Semestre', 4, 4),
('Normatividad Ambiental y Agropecuaria', 'Quinto_Semestre', 4, 4),
('Prácticas Agropecuarias Integradas III', 'Quinto_Semestre', 4, 4);


-- Carrera 5: Técnico en Desarrollo Integral Comunitario
INSERT INTO materias (nombre, grado, carrera_tecnica_id, horas_por_semana) VALUES
('Matemáticas V', 'Quinto_Semestre', 5, 4),
('Desarrollo Económico Local', 'Quinto_Semestre', 5, 4),
('Administración de Proyectos Sociales', 'Quinto_Semestre', 5, 4),
('Educación y Capacitación Comunitaria', 'Quinto_Semestre', 5, 4),
('Gestión Cultural y Patrimonio Comunitario', 'Quinto_Semestre', 5, 4),
('Diseño y Evaluación de Políticas Públicas', 'Quinto_Semestre', 5, 4),
('Comunicación Estratégica para el Desarrollo', 'Quinto_Semestre', 5, 4),
('Práctica Comunitaria IV', 'Quinto_Semestre', 5, 4);

-- MATERIAS DE SEXTO SEMESTRE

-- Carrera 1: Técnico en Soporte y Mantenimiento de Equipo de Cómputo
INSERT INTO materias (nombre, grado, carrera_tecnica_id, area_propedeutica_id, horas_por_semana) VALUES
('Matemáticas VI', 'Sexto_Semestre', 1, 6, 4),
('Inglés VI', 'Sexto_Semestre', 1, 6, 4),
('Formación Sociocultural VI', 'Sexto_Semestre', 1, 6, 4),
('Virtualización y Computación en la Nube', 'Sexto_Semestre', 1, 6, 4),
('Administración de Seguridad Informática', 'Sexto_Semestre', 1, 6, 4),
('Diagnóstico y Solución de Fallas Avanzadas', 'Sexto_Semestre', 1, 6, 4),
('Soporte Técnico Empresarial', 'Sexto_Semestre', 1, 6, 4),
('Proyecto Integrador de Mantenimiento y Redes', 'Sexto_Semestre', 1, 6, 4);


-- Carrera 2: Técnico en Producción Agrícola
INSERT INTO materias (nombre, grado, carrera_tecnica_id, area_propedeutica_id, horas_por_semana) VALUES
('Matemáticas VI', 'Sexto_Semestre', 2, 7, 4),
('Producción de Semillas Certificadas', 'Sexto_Semestre', 2, 7, 4),
('Sistemas Agroforestales', 'Sexto_Semestre', 2, 7, 4),
('Manejo Postcosecha y Control de Calidad', 'Sexto_Semestre', 2, 7, 4),
('Gestión Ambiental en la Producción Agrícola', 'Sexto_Semestre', 2, 7, 4),
('Planeación de Proyectos Productivos Agrícolas', 'Sexto_Semestre', 2, 7, 4),
('Innovación y Tecnología Agrícola', 'Sexto_Semestre', 2, 7, 4),
('Prácticas Profesionales Agrícolas', 'Sexto_Semestre', 2, 7, 4);


-- Carrera 3: Técnico en Producción Pecuaria
INSERT INTO materias (nombre, grado, carrera_tecnica_id, area_propedeutica_id, horas_por_semana) VALUES
('Matemáticas VI', 'Sexto_Semestre', 3, 7, 4),
('Producción de Peces y Acuacultura', 'Sexto_Semestre', 3, 7, 4),
('Procesamiento y Conservación de Productos Pecuarios', 'Sexto_Semestre', 3, 7, 4),
('Gestión Ambiental Pecuaria', 'Sexto_Semestre', 3, 7, 4),
('Administración de la Producción Animal', 'Sexto_Semestre', 3, 7, 4),
('Innovación Tecnológica Pecuaria', 'Sexto_Semestre', 3, 7, 4),
('Proyectos de Emprendimiento Pecuario', 'Sexto_Semestre', 3, 7, 4),
('Prácticas Profesionales Pecuarias', 'Sexto_Semestre', 3, 7, 4);


-- Carrera 4: Técnico Agropecuario
INSERT INTO materias (nombre, grado, carrera_tecnica_id, area_propedeutica_id, horas_por_semana) VALUES
('Matemáticas VI', 'Sexto_Semestre', 4, 7, 4),
('Gestión Integral de Unidades de Producción', 'Sexto_Semestre', 4, 7, 4),
('Emprendimiento Agropecuario', 'Sexto_Semestre', 4, 7, 4),
('Sistemas Agroecológicos Sustentables', 'Sexto_Semestre', 4, 7, 4),
('Planeación Estratégica en el Campo', 'Sexto_Semestre', 4, 7, 4),
('Innovación y Tecnología Rural', 'Sexto_Semestre', 4, 7, 4),
('Normas de Inocuidad Agroalimentaria', 'Sexto_Semestre', 4, 7, 4),
('Prácticas Profesionales Agropecuarias', 'Sexto_Semestre', 4, 7, 4);


-- Carrera 5: Técnico en Desarrollo Integral Comunitario
INSERT INTO materias (nombre, grado, carrera_tecnica_id, area_propedeutica_id, horas_por_semana) VALUES
('Matemáticas VI', 'Sexto_Semestre', 5, 4, 4),
('Gestión de Políticas Públicas Locales', 'Sexto_Semestre', 5, 4, 4),
('Evaluación de Programas Comunitarios', 'Sexto_Semestre', 5, 4, 4),
('Mediación y Resolución de Conflictos Sociales', 'Sexto_Semestre', 5, 4, 4),
('Educación para la Transformación Social', 'Sexto_Semestre', 5, 4, 4),
('Sustentabilidad y Responsabilidad Social', 'Sexto_Semestre', 5, 4, 4),
('Proyectos de Innovación Comunitaria', 'Sexto_Semestre', 5, 4, 4),
('Prácticas Profesionales Comunitarias', 'Sexto_Semestre', 5, 4, 4);

INSERT INTO aulas (clave) VALUES 
('AV-100'),
('AV-101'),
('AV-102'),
('AV-103'),
('AV-104'),
('AV-105'),
('AV-106'),
('AV-107'),
('AV-108'),
('AV-109'),
('AV-110'),
('AV-111'),
('AV-112'),
('AV-113'),
('AV-114'),
('AV-115'),
('AV-116'),
('AV-117'),
('AV-118'),
('AV-119'),
('AV-120'),
('AV-121'),
('AV-122'),
('AV-123'),
('AV-124'),
('AV-125'),
('AV-126'),
('AV-127'),
('AV-128'),
('AV-129');


INSERT INTO usuarios (curp, nombre, apellido_paterno, apellido_materno, email, telefono, activo, contrasena)
VALUES
('CURP0001', 'Carlos', 'Gomez', 'Lopez', 'carlos.gomez@cbta97.edu.mx', '5551000001', true, 'pass123'),
('CURP0002', 'Ana', 'Martinez', 'Sanchez', 'ana.martinez@cbta97.edu.mx', '5551000002', true, 'pass123'),
('CURP0003', 'Luis', 'Ramirez', 'Torres', 'luis.ramirez@cbta97.edu.mx', '5551000003', true, 'pass123'),
('CURP0004', 'Marta', 'Hernandez', 'Diaz', 'marta.hernandez@cbta97.edu.mx', '5551000004', true, 'pass123'),
('CURP0005', 'Jose', 'Lopez', 'Gonzalez', 'jose.lopez@cbta97.edu.mx', '5551000005', true, 'pass123'),
('CURP0006', 'Laura', 'Perez', 'Ortiz', 'laura.perez@cbta97.edu.mx', '5551000006', true, 'pass123'),
('CURP0007', 'Miguel', 'Vazquez', 'Rios', 'miguel.vazquez@cbta97.edu.mx', '5551000007', true, 'pass123'),
('CURP0008', 'Sofia', 'Santos', 'Cruz', 'sofia.santos@cbta97.edu.mx', '5551000008', true, 'pass123'),
('CURP0009', 'Diego', 'Morales', 'Mendez', 'diego.morales@cbta97.edu.mx', '5551000009', true, 'pass123'),
('CURP0010', 'Elena', 'Ruiz', 'Alvarez', 'elena.ruiz@cbta97.edu.mx', '5551000010', true, 'pass123');

INSERT INTO docentes (usuario_id, cedula_profesional)
VALUES
(1, 'CED1234561'),
(2, 'CED1234562'),
(3, 'CED1234563'),
(4, 'CED1234564'),
(5, 'CED1234565'),
(6, 'CED1234566'),
(7, 'CED1234567'),
(8, 'CED1234568'),
(9, 'CED1234569'),
(10, 'CED1234570');

-- Docente 1 (Carlos Gomez) - Todas las Matemáticas
INSERT INTO docentes_materias (docente_id, materia_id) VALUES
(1, 1),   -- Matemáticas I
(1, 9),   -- Matemáticas II (Soporte)
(1, 17),  -- Matemáticas II (Agrícola)
(1, 25),  -- Matemáticas II (Pecuaria)
(1, 33),  -- Matemáticas II (Agropecuario)
(1, 41),  -- Matemáticas II (Desarrollo)
(1, 49),  -- Matemáticas III (Soporte)
(1, 57),  -- Matemáticas III (Agrícola)
(1, 65),  -- Matemáticas III (Pecuaria)
(1, 73),  -- Matemáticas III (Agropecuario)
(1, 81),  -- Matemáticas III (Desarrollo)
(1, 89),  -- Matemáticas IV (Soporte)
(1, 97),  -- Matemáticas IV (Agrícola)
(1, 105), -- Matemáticas IV (Pecuaria)
(1, 113), -- Matemáticas IV (Agropecuario)
(1, 121), -- Matemáticas IV (Desarrollo)
(1, 129), -- Matemáticas V (Soporte)
(1, 137), -- Matemáticas V (Agrícola)
(1, 145), -- Matemáticas V (Pecuaria)
(1, 153), -- Matemáticas V (Agropecuario)
(1, 161), -- Matemáticas V (Desarrollo)
(1, 169), -- Matemáticas VI (Soporte)
(1, 177), -- Matemáticas VI (Agrícola)
(1, 185), -- Matemáticas VI (Pecuaria)
(1, 193), -- Matemáticas VI (Agropecuario)
(1, 201); -- Matemáticas VI (Desarrollo)

-- Docente 2 (Ana Martinez) - Inglés y Comunicación
INSERT INTO docentes_materias (docente_id, materia_id) VALUES
(2, 4),   -- Comunicación Oral y Escrita
(2, 6),   -- Inglés I
(2, 10),  -- Inglés II (Soporte)
(2, 44),  -- Comunicación para el Desarrollo
(2, 50),  -- Inglés III (Soporte)
(2, 90),  -- Inglés IV (Soporte)
(2, 130), -- Inglés V (Soporte)
(2, 167), -- Comunicación Estratégica para el Desarrollo
(2, 170); -- Inglés VI (Soporte)

-- Docente 3 (Luis Ramirez) - Soporte (Hardware/Mantenimiento)
INSERT INTO docentes_materias (docente_id, materia_id) VALUES
(3, 5),   -- Tecnologías de la Información y la Comunicación
(3, 12),  -- Electricidad y Electrónica Básica
(3, 13),  -- Mantenimiento Preventivo de Equipos de Cómputo
(3, 15),  -- Ensambles y Desensambles de PC
(3, 53),  -- Mantenimiento Correctivo de Equipos de Cómputo
(3, 92),  -- Diagnóstico y Reparación de Fallas en Hardware
(3, 94),  -- Soporte Técnico a Usuarios
(3, 132), -- Mantenimiento de Equipos Portátiles y Dispositivos Móviles
(3, 174); -- Diagnóstico y Solución de Fallas Avanzadas

-- Docente 4 (Marta Hernandez) - Química y Biología
INSERT INTO docentes_materias (docente_id, materia_id) VALUES
(4, 2),   -- Química I
(4, 3),   -- Biología General
(4, 18),  -- Química Agrícola
(4, 19),  -- Biología Vegetal
(4, 21),  -- Fisiología Vegetal
(4, 26),  -- Anatomía y Fisiología Animal
(4, 34),  -- Química del Suelo
(4, 59),  -- Fitotecnia
(4, 66),  -- Fisiología de la Reproducción Animal
(4, 74),  -- Biotecnología Agropecuaria
(4, 156); -- Biotecnología Aplicada al Campo

-- Docente 5 (Jose Lopez) - Soporte (Software/Redes)
INSERT INTO docentes_materias (docente_id, materia_id) VALUES
(5, 14),  -- Sistemas Operativos
(5, 16),  -- Redes de Computadoras I
(5, 52),  -- Electrónica Digital
(5, 54),  -- Redes de Computadoras II
(5, 55),  -- Arquitectura de Computadoras
(5, 56),  -- Seguridad y Respaldo de Información
(5, 93),  -- Administración de Redes de Computadoras
(5, 95),  -- Instalación y Configuración de Software Especializado
(5, 96),  -- Proyecto Integrador de Mantenimiento de Cómputo
(5, 133), -- Administración de Servidores
(5, 134), -- Redes Inalámbricas y Conectividad Avanzada
(5, 135), -- Automatización y Control con Microcontroladores
(5, 136), -- Proyecto Integrador de Redes y Soporte
(5, 172), -- Virtualización y Computación en la Nube
(5, 173), -- Administración de Seguridad Informática
(5, 175), -- Soporte Técnico Empresarial
(5, 176); -- Proyecto Integrador de Mantenimiento y Redes

-- Docente 6 (Laura Perez) - Formación Sociocultural y Carrera 5 (Desarrollo Comunitario)
INSERT INTO docentes_materias (docente_id, materia_id) VALUES
(6, 7),   -- Formación Sociocultural I
(6, 11),  -- Formación Sociocultural II
(6, 42),  -- Sociología Rural y Urbana
(6, 43),  -- Desarrollo Sustentable
(6, 45),  -- Diseño y Gestión de Proyectos Comunitarios
(6, 46),  -- Educación Ambiental
(6, 47),  -- Economía Social y Solidaria
(6, 48),  -- Práctica Comunitaria I
(6, 51),  -- Formación Sociocultural III
(6, 82),  -- Psicología Comunitaria
(6, 83),  -- Planeación del Desarrollo Local
(6, 84),  -- Gestión Social y Participación Ciudadana
(6, 85),  -- Educación para la Salud Comunitaria
(6, 86),  -- Evaluación de Proyectos Comunitarios
(6, 87),  -- Economía del Desarrollo
(6, 88),  -- Práctica Comunitaria II
(6, 91),  -- Formación Sociocultural IV
(6, 122), -- Intervención Comunitaria
(6, 123), -- Diseño de Estrategias de Desarrollo
(6, 124), -- Gestión de Recursos y Financiamiento Social
(6, 125), -- Cultura y Diversidad Comunitaria
(6, 126), -- Liderazgo y Trabajo en Equipo
(6, 127), -- Evaluación del Impacto Social de Proyectos
(6, 128), -- Práctica Comunitaria III
(6, 131), -- Formación Sociocultural V
(6, 162), -- Desarrollo Económico Local
(6, 163), -- Administración de Proyectos Sociales
(6, 164), -- Educación y Capacitación Comunitaria
(6, 165), -- Gestión Cultural y Patrimonio Comunitario
(6, 166), -- Diseño y Evaluación de Políticas Públicas
(6, 168), -- Práctica Comunitaria IV
(6, 171), -- Formación Sociocultural VI
(6, 202), -- Gestión de Políticas Públicas Locales
(6, 203), -- Evaluación de Programas Comunitarios
(6, 204), -- Mediación y Resolución de Conflictos Sociales
(6, 205), -- Educación para la Transformación Social
(6, 206), -- Sustentabilidad y Responsabilidad Social
(6, 207), -- Proyectos de Innovación Comunitaria
(6, 208); -- Prácticas Profesionales Comunitarias

-- Docente 8 (Sofia Santos) - Carrera 2 (Producción Agrícola)
INSERT INTO docentes_materias (docente_id, materia_id) VALUES
(8, 20),  -- Edafología (Ciencia del Suelo)
(8, 22),  -- Maquinaria y Herramientas Agrícolas
(8, 23),  -- Propagación de Plantas
(8, 24),  -- Riego y Drenaje Agrícola
(8, 58),  -- Fertilidad de Suelos
(8, 60),  -- Control de Plagas y Enfermedades Vegetales
(8, 61),  -- Topografía y Cartografía Agrícola
(8, 62),  -- Manejo de Invernaderos
(8, 63),  -- Horticultura
(8, 64),  -- Prácticas de Campo Agrícola I
(8, 98),  -- Agrometeorología
(8, 99),  -- Cultivos Básicos y Estratégicos
(8, 100), -- Manejo Integrado de Plagas y Enfermedades
(8, 101), -- Producción en Ambientes Controlados
(8, 102), -- Postcosecha y Comercialización de Productos Agrícolas
(8, 103), -- Agroindustria de Transformación
(8, 104), -- Prácticas de Campo Agrícola II
(8, 138), -- Cultivos de Exportación
(8, 139), -- Producción Orgánica y Sustentable
(8, 140), -- Administración de Empresas Agropecuarias
(8, 141), -- Gestión de la Calidad e Inocuidad Agroalimentaria
(8, 142), -- Agroindustria y Valor Agregado
(8, 143), -- Comercialización y Mercadotecnia Agrícola
(8, 144), -- Prácticas de Campo Agrícola III
(8, 178), -- Producción de Semillas Certificadas
(8, 179), -- Sistemas Agroforestales
(8, 180), -- Manejo Postcosecha y Control de Calidad
(8, 181), -- Gestión Ambiental en la Producción Agrícola
(8, 182), -- Planeación de Proyectos Productivos Agrícolas
(8, 183), -- Innovación y Tecnología Agrícola
(8, 184); -- Prácticas Profesionales Agrícolas

-- Docente 9 (Diego Morales) - Carrera 3 (Producción Pecuaria)
INSERT INTO docentes_materias (docente_id, materia_id) VALUES
(9, 27),  -- Nutrición Animal
(9, 28),  -- Manejo y Reproducción de Especies Pecuarias
(9, 29),  -- Sanidad Animal
(9, 30),  -- Producción de Alimentos para el Ganado
(9, 31),  -- Prácticas de Campo Pecuario
(9, 32),  -- Economía Pecuaria
(9, 67),  -- Manejo de Pastos y Forrajes
(9, 68),  -- Producción Bovina
(9, 69),  -- Sanidad y Bioseguridad Pecuaria
(9, 70),  -- Producción Porcina
(9, 71),  -- Economía y Administración Pecuaria
(9, 72),  -- Prácticas de Campo Pecuario II
(9, 106), -- Producción Avícola
(9, 107), -- Producción Caprina y Ovina
(9, 108), -- Procesamiento de Productos Pecuarios
(9, 109), -- Administración de Unidades de Producción Pecuaria
(9, 110), -- Mejoramiento Genético Animal
(9, 111), -- Sanidad y Bienestar Animal
(9, 112), -- Prácticas de Campo Pecuario III
(9, 146), -- Producción Apícola
(9, 147), -- Producción Cunícola y de Especies Menores
(9, 148), -- Procesamiento de Carne y Lácteos
(9, 149), -- Gestión de la Producción Pecuaria
(9, 150), -- Sistemas de Información para la Producción Pecuaria
(9, 151), -- Normatividad y Bienestar Animal
(9, 152), -- Prácticas de Campo Pecuario IV
(9, 186), -- Producción de Peces y Acuacultura
(9, 187), -- Procesamiento y Conservación de Productos Pecuarios
(9, 188), -- Gestión Ambiental Pecuaria
(9, 189), -- Administración de la Producción Animal
(9, 190), -- Innovación Tecnológica Pecuaria
(9, 191), -- Proyectos de Emprendimiento Pecuario
(9, 192); -- Prácticas Profesionales Pecuarias

-- Docente 10 (Elena Ruiz) - Carrera 4 (Agropecuario)
INSERT INTO docentes_materias (docente_id, materia_id) VALUES
(10, 8),   -- Introducción a las Ciencias Agropecuarias
(10, 35),  -- Zootecnia General
(10, 36),  -- Agronomía Básica
(10, 37),  -- Ecología y Medio Ambiente
(10, 38),  -- Manejo de Recursos Naturales
(10, 39),  -- Maquinaria Agropecuaria
(10, 40),  -- Prácticas Integrales de Campo
(10, 75),  -- Manejo Integral de Cultivos
(10, 76),  -- Producción Animal Sostenible
(10, 77),  -- Agroecología
(10, 78),  -- Economía y Comercialización Agropecuaria
(10, 79),  -- Sistemas de Producción Rural
(10, 80),  -- Prácticas Agropecuarias Integradas I
(10, 114), -- Producción Sustentable de Cultivos
(10, 115), -- Producción Animal Intensiva
(10, 116), -- Manejo de Recursos Hidráulicos
(10, 117), -- Economía Rural y Mercados Agropecuarios
(10, 118), -- Gestión Ambiental Agropecuaria
(10, 119), -- Innovación Tecnológica en el Campo
(10, 120), -- Prácticas Agropecuarias Integradas II
(10, 154), -- Tecnologías Sustentables de Producción
(10, 155), -- Gestión Empresarial Agropecuaria
(10, 157), -- Innovación y Transferencia de Tecnología
(10, 158), -- Comercialización y Cadenas Productivas
(10, 159), -- Normatividad Ambiental y Agropecuaria
(10, 160), -- Prácticas Agropecuarias Integradas III
(10, 194), -- Gestión Integral de Unidades de Producción
(10, 195), -- Emprendimiento Agropecuario
(10, 196), -- Sistemas Agroecológicos Sustentables
(10, 197), -- Planeación Estratégica en el Campo
(10, 198), -- Innovación y Tecnología Rural
(10, 199), -- Normas de Inocuidad Agroalimentaria
(10, 200); -- Prácticas Profesionales Agropecuarias
