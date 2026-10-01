# Estudio de caso 1: análisis de cadenas de impresión con una pila

Universidad CENFOTEC, Escuela de Software
Curso SOFT-10, Estructuras de Datos. Sección SCV5, periodo C3-2026
Docente facilitador: Romario Salas Cerdas

Estudiante: Braden Lee Knuter Cordoba

## Descripción

Programa en Java que analiza cadenas de impresión y determina si están bien
formadas, usando una pila implementada con nodos enlazados, sin clases de
colección del lenguaje.

Los tokens que contempla son los que indica la consigna: cadenas literales
delimitadas por comillas dobles (`"Hola"`), variables de cadena (`nombre`) y el
operador de concatenación (`+`). A ellos se agregan los paréntesis de
agrupación, que obligan a recordar contextos abiertos y hacen necesaria la pila.

Ejemplo de expresión válida:

```
"Hola, " + nombre + ("!" + salto)
```

La pila almacena los delimitadores de apertura. Cada comilla o paréntesis que se
abre se inserta con `push` y se retira con `pop` cuando aparece su cierre. Si al
terminar el recorrido la pila no está vacía, quedó un delimitador sin cerrar y
el programa informa su posición exacta.

## Estructura del proyecto

```
src/
  Nodo.java                 Nodo de la pila: un caracter y su posicion en la expresion
  Pila.java                 Pila LIFO con nodos enlazados
  AnalizadorCadenas.java    Logica del analisis de la cadena de impresion
  Main.java                 Entorno de ejecucion: main() y menu()
docs/
  EstudioDeCaso1_Pilas_AnalisisCadenasDeImpresion.pdf
```

## Compilación y ejecución

Requiere un JDK 17 o superior. Se probó con el JDK 26.

```bash
javac -d out src/*.java
java -cp out Main
```

## Menú del programa

1. Analizar una cadena de impresión
2. Analizar una cadena mostrando la traza de la pila, paso a paso
3. Ejecutar los ejemplos de prueba, válidos e inválidos
4. Operar la pila manualmente: push, pop, peek, mostrar y vaciar
5. Salir

## Operaciones de la clase Pila

| Método | Descripción | Costo |
| --- | --- | --- |
| `push(char, int)` | Inserta un carácter en la cima | O(1) |
| `pop()` | Retira y devuelve la cima, o `null` si está vacía | O(1) |
| `peek()` | Consulta la cima sin retirarla, o `null` si está vacía | O(1) |
| `estaVacia()` | Indica si no hay elementos | O(1) |
| `getTamano()` | Cantidad de elementos | O(1) |
| `vaciar()` | Retira todos los elementos | O(1) |
| `contenido()` | Devuelve el contenido como texto, de la cima a la base | O(n) |
| `mostrar()` | Imprime el estado actual de la pila | O(n) |

El análisis completo de una expresión de longitud n se resuelve en una sola
pasada, en tiempo O(n).

## Errores que detecta el analizador

- comilla de apertura sin cerrar
- paréntesis de apertura sin cerrar
- paréntesis de cierre sin apertura
- operador `+` sin operando a la izquierda o a la derecha
- dos operandos consecutivos sin operador entre ellos
- caracteres no permitidos

Cada mensaje indica la posición exacta dentro de la expresión.

