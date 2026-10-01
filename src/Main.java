import java.util.Scanner;

/**
 * Entorno de ejecucion del programa.
 *
 * Presenta un menu que permite analizar cadenas de impresion con la pila
 * desarrollada, observar el analisis paso a paso, ejecutar un conjunto de
 * ejemplos de prueba y operar la pila de forma manual.
 */
public class Main {

    private static Scanner entrada = new Scanner(System.in);
    private static Pila pilaManual = new Pila();   // pila para las pruebas manuales

    public static void main(String[] args) {
        System.out.println("===================================================");
        System.out.println("  ANALIZADOR DE CADENAS DE IMPRESION CON UNA PILA");
        System.out.println("  Universidad CENFOTEC - Estructuras de Datos");
        System.out.println("===================================================");

        boolean continuar = true;
        while (continuar) {
            menu();
            if (!hayMasEntrada()) {        // no queda texto por leer: se termina el programa
                System.out.println();
                System.out.println("No hay mas datos de entrada. Programa finalizado.");
                break;
            }
            int opcion = leerOpcion();
            switch (opcion) {
                case 1:
                    analizarCadena(false);
                    break;
                case 2:
                    analizarCadena(true);
                    break;
                case 3:
                    ejecutarEjemplos();
                    break;
                case 4:
                    menuPilaManual();
                    break;
                case 5:
                    System.out.println("\nPrograma finalizado. Hasta pronto.");
                    continuar = false;
                    break;
                default:
                    System.out.println("\nOpcion invalida. Elija un numero del 1 al 5.");
            }
        }
        entrada.close();
    }

    /** Muestra las opciones disponibles. */
    private static void menu() {
        System.out.println("\n--------------------- MENU ------------------------");
        System.out.println(" 1. Analizar una cadena de impresion");
        System.out.println(" 2. Analizar una cadena mostrando la traza de la pila");
        System.out.println(" 3. Ejecutar los ejemplos de prueba");
        System.out.println(" 4. Operar la pila manualmente (push / pop / peek)");
        System.out.println(" 5. Salir");
        System.out.println("---------------------------------------------------");
        System.out.print("Seleccione una opcion: ");
    }

    /** Indica si todavia queda texto por leer en la entrada estandar. */
    private static boolean hayMasEntrada() {
        return entrada.hasNextLine();
    }

    /** Lee una linea de texto; devuelve una cadena vacia si ya no hay entrada. */
    private static String leerLinea() {
        if (!entrada.hasNextLine()) {
            return "";
        }
        return entrada.nextLine();
    }

    /** Lee la opcion del usuario y devuelve -1 si el texto no es un numero. */
    private static int leerOpcion() {
        String texto = leerLinea().trim();
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /** Solicita una expresion al usuario y la analiza. */
    private static void analizarCadena(boolean conTraza) {
        System.out.println("\nEscriba la cadena de impresion.");
        System.out.println("Ejemplo: \"Hola, \" + nombre + \"!\"");
        System.out.print("> ");
        String expresion = leerLinea();

        if (expresion.trim().isEmpty()) {
            System.out.println("ERROR: no se escribio ninguna expresion.");
            return;
        }

        System.out.println("\nAnalizando: " + expresion);
        AnalizadorCadenas analizador = new AnalizadorCadenas();
        analizador.analizar(expresion, conTraza);
    }

    /** Analiza un conjunto fijo de expresiones validas e invalidas. */
    private static void ejecutarEjemplos() {
        String[] ejemplos = {
            "\"Hola, \" + nombre + \"!\"",
            "\"Total: \" + (moneda + monto)",
            "mensaje",
            "\"Hola\" + ",
            "\"Hola\" nombre",
            "\"Hola, \" + nombre)",
            "(\"a\" + b",
            "\"Saldo: @\" + 5saldo"
        };

        AnalizadorCadenas analizador = new AnalizadorCadenas();
        for (int i = 0; i < ejemplos.length; i++) {
            System.out.println("\nEjemplo " + (i + 1) + ": " + ejemplos[i]);
            analizador.analizar(ejemplos[i], false);
        }
    }

    /** Submenu que permite usar directamente las operaciones de la pila. */
    private static void menuPilaManual() {
        boolean enSubmenu = true;
        while (enSubmenu) {
            System.out.println("\n--------------- OPERACIONES DE LA PILA ------------");
            System.out.println(" 1. push (insertar un caracter)");
            System.out.println(" 2. pop (retirar el caracter de la cima)");
            System.out.println(" 3. peek (consultar la cima)");
            System.out.println(" 4. Mostrar la pila");
            System.out.println(" 5. Vaciar la pila");
            System.out.println(" 6. Volver al menu principal");
            System.out.println("---------------------------------------------------");
            System.out.print("Seleccione una opcion: ");

            if (!hayMasEntrada()) {        // no queda texto por leer: se vuelve al menu principal
                System.out.println();
                return;
            }
            int opcion = leerOpcion();
            switch (opcion) {
                case 1:
                    System.out.print("Caracter por insertar: ");
                    String texto = leerLinea();
                    if (texto.length() != 1) {
                        System.out.println("ERROR: debe escribir exactamente un caracter.");
                    } else {
                        pilaManual.push(texto.charAt(0), pilaManual.getTamano());
                        System.out.println("Caracter insertado.");
                        pilaManual.mostrar();
                    }
                    break;
                case 2:
                    Nodo retirado = pilaManual.pop();
                    if (retirado == null) {
                        System.out.println("La pila esta vacia, no hay nada que retirar.");
                    } else {
                        System.out.println("Se retiro el caracter '" + retirado.getDato() + "'.");
                        pilaManual.mostrar();
                    }
                    break;
                case 3:
                    Nodo cima = pilaManual.peek();
                    if (cima == null) {
                        System.out.println("La pila esta vacia, no hay cima.");
                    } else {
                        System.out.println("En la cima esta el caracter '" + cima.getDato() + "'.");
                    }
                    break;
                case 4:
                    pilaManual.mostrar();
                    break;
                case 5:
                    pilaManual.vaciar();
                    System.out.println("La pila quedo vacia.");
                    break;
                case 6:
                    enSubmenu = false;
                    break;
                default:
                    System.out.println("Opcion invalida. Elija un numero del 1 al 6.");
            }
        }
    }
}
