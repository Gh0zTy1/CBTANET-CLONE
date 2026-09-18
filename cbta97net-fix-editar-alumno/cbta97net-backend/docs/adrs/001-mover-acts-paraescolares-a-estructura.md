# ADR-001: Mover operaciones del módulo `Paraescolares` al módulo `Estructura`

**Fecha:** 15-03-2026
**Estado:** ACEPTADO
**Autores:** Saúl Neri

## 1. Contexto
El módulo `Paraescolares` (encargado de operaciones CRUD para actividades paraescolares)
no cuenta con una responsabilidad mayor que la de insertar y consultar datos. Su existencia
solamente hace que el módulo `GruposParaescolares` tenga una dependencia extra, así como también
una dependencia extra para el modulo `Horario`. Además, el nombre de dicho módulo (`Paraescolares`)
es muy ambiguo, y no representa algo específico en el dominio.

## 2. Decisión
Mover las operaciones CRUD relacionadas con la entidad `ActividadParaescolar` al módulo
de `Estructura` para reducir dependencias y a su vez complejidad. Donde la  `ActividadParaescolar` tiene
más sentido, ya que convive con el entorno de CBTa97, entrando fácilmente en el lenguaje ubícuo del dominio. 

## 3. Alternativas Consideradas
- Mantener la gestión de las actividades paraescolares en su módulo existente (`Paraescolares`). 

## 4. Consecuencias

### Positivas:
* Menor complejidad en la arquitectura del proyecto Backend (`cbta97net-backend`) (Complejidad).
* Menor fricción en el entendimiento del backend para miembros nuevos del equipo 
del desarrollo (Aprendibilidad).

### Negativas / Riesgos:
* Cambios en el modulo de estructura (mínimos).