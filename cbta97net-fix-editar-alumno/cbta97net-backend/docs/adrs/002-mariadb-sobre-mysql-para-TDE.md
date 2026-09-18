# ADR-001: Adopción de MariaDB con TDE para el cumplimiento de normativas de seguridad y protección de datos

**Fecha:** 15-03-2026
**Estado:** PROPUESTO
**Autores:** Saúl Neri

## 1. Contexto
Debido a que el sistema ya no será desplegado de manera local (u on-premise) sino en un VPS, el sistema necesita de 
una capa de seguridad adicional, que proteja la información de accesos no autorizados, dejando a los atacantes 
información  completamente cifrada e inútil. Se necesita de Transparent Data Encryptio (DTO), que hace que el medio
físico donde se almacenan los datos del proveedor de base de datos estén completamente cifrados y solo permita 
visualizarlos  cuando un cliente los consume y se mueven a la memoria RAM. Esta capacidad es comúnmente de paga y solo 
es provista por pocos proveedores de base de datos.

## 2. Decisión
Se propone utilizar `MariaDB` como base de datos para CBTa97net, ya que su entorno permite el uso de `TDE` por medio
de un Plugin gratuito. `MariaDB` ofrece cifrado a nivel de tablespace de forma nativa y abierta, mientras que en `MySQL`
es una característica de la versión Enterprise.

## 3. Alternativas Consideradas
- **Pagar por un servicio de TDE del proveedor VPS**: Esta opción quedó prácticamente descartada, ya que el equipo ni
la escuela está dispuesta a pagar más que el proveedor VPS.
- **Proveedores de Base de Datos Edición Enterpise**: Descartada por la misma razón mencionada en el punto anterior.

## 4. Consecuencias

### Positivas:
* Mayor protección de la información de la escuela, cumpliendo con la Ley General de Protección de Datos Personales 
en Posesión de Sujetos Obligados (LGPDPPSO). Lo que provoca una reducción de la superfice de ataque para todo el 
sistema (Seguridad).
* Mayor capacidad de realizar modificaciones y agregación de nuevos Plugins de `MariaDB` (Configurabilidad, Seguridad). 
* El componente `cbta97net-backend` no requiere de cambios.

### Negativas / Riesgos:
* Configuración del entorno del VPS para soportar el TDE para `MariaDB` (considerables debido a desconocimiento en el área).
* Cambios en el archivo de Docker Compose (mínimos).