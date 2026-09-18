SET NAMES 'utf8mb4';

-- Borrar los que están mal
DELETE FROM actividades_paraescolares;

INSERT INTO actividades_paraescolares (nombre, descripcion) VALUES 
('Fútbol Varonil', 'Desarrollo de habilidades técnicas, tácticas y competencia física en equipo.'),
('Fútbol Femenil', 'Entrenamiento deportivo enfocado en la disciplina y representación institucional en torneos.'),
('Básquetbol', 'Práctica deportiva para mejorar la condición física, coordinación y trabajo bajo presión.'),
('Voleibol', 'Actividad enfocada en el desarrollo de reflejos, estrategia de juego y convivencia grupal.'),
('Banda de Guerra', 'Fomento de valores cívicos, disciplina y coordinación rítmica institucional.'),
('Escolta de Bandera', 'Práctica de formalidad cívica, respeto a los símbolos patrios y gallardía.'),
('Danza Folclórica', 'Preservación de la cultura mexicana a través del baile tradicional y expresión corporal.'),
('Ajedrez', 'Desarrollo del pensamiento lógico, estratégico y concentración mental.'),
('Club de Lectura', 'Fomento al hábito de la lectura, análisis crítico de textos y expresión oral.'),
('Música y Coro', 'Instrucción en teoría musical básica, canto y ejecución de instrumentos grupales.');
