import java.util.Scanner;

/** Entorno de ejecucion: menu para analizar cadenas y operar la pila. */
public class Main {

    private static Scanner entrada = new Scanner(System.in);
    private static Pila pilaManual = new Pila();                        // pila del submenu
    private static AnalizadorCadenas lector = new AnalizadorCadenas();  // reconoce tokens

    private static final String[] EJEMPLOS = {
        "\"Hola, \" + nombre + \"!\"",
        "\"Total: \" + (moneda + monto)",
        "mensaje",
        "\"Hola\" + ",
        "\"Hola\" nombre",
        "\"Hola, \" + nombre)",
        "(\"a\" + b",
        "\"Saldo: @\" + 5saldo"
    };

    public static void main(String[] args) {
        System.out.println("===================================================");
        System.out.println("  ANALIZADOR DE CADENAS DE IMPRESION CON UNA PILA");
        System.out.println("  Universidad CENFOTEC - Estructuras de Datos");
        System.out.println("===================================================");

        boolean continuar = true;
        while (continuar) {
            menu();
            if (!entrada.hasNextLine()) {   // se acabo la entrada
                System.out.println();
                System.out.println("No hay mas datos de entrada. Programa finalizado.");
                break;
            }
            switch (leerOpcion()) {
                case 1: analizarCadena(false); break;
                case 2: analizarCadena(true); break;
                case 3: ejecutarEjemplos(); break;
                case 4: menuPilaManual(); break;
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

    // --------------------------------------------------------------- entrada

    /** Lee una linea, o devuelve "" si ya no hay entrada. */
    private static String leerLinea() {
        return entrada.hasNextLine() ? entrada.nextLine() : "";
    }

    /** Lee un numero de opcion, o devuelve -1 si el texto no es un numero. */
    private static int leerOpcion() {
        try {
            return Integer.parseInt(leerLinea().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // ------------------------------------------------------------- analisis

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
        new AnalizadorCadenas().analizar(expresion, conTraza);
    }

    private static void ejecutarEjemplos() {
        AnalizadorCadenas analizador = new AnalizadorCadenas();
        for (int i = 0; i < EJEMPLOS.length; i++) {
            System.out.println("\nEjemplo " + (i + 1) + ": " + EJEMPLOS[i]);
            analizador.analizar(EJEMPLOS[i], false);
        }
    }

    // ---------------------------------------------------------- pila manual

    private static void menuPilaManual() {
        boolean enSubmenu = true;
        while (enSubmenu) {
            System.out.println("\n--------------- OPERACIONES DE LA PILA ------------");
            System.out.println(" 1. push (insertar un token)");
            System.out.println(" 2. pop (retirar el token de la cima)");
            System.out.println(" 3. peek (consultar la cima)");
            System.out.println(" 4. Mostrar la pila");
            System.out.println(" 5. Vaciar la pila");
            System.out.println(" 6. Volver al menu principal");
            System.out.println("---------------------------------------------------");
            System.out.print("Seleccione una opcion: ");

            if (!entrada.hasNextLine()) {   // se acabo la entrada
                System.out.println();
                return;
            }
            switch (leerOpcion()) {
                case 1: insertarToken(); break;
                case 2: retirarToken(); break;
                case 3: consultarCima(); break;
                case 4: pilaManual.mostrar(); break;
                case 5:
                    pilaManual.vaciar();
                    System.out.println("La pila quedo vacia.");
                    break;
                case 6: enSubmenu = false; break;
                default: System.out.println("Opcion invalida. Elija un numero del 1 al 6.");
            }
        }
    }

    private static void insertarToken() {
        System.out.println("Escriba un token: una cadena literal (\"Hola\"),");
        System.out.print("una variable (nombre), + o un parentesis: ");
        Nodo token = lector.convertirEnToken(leerLinea());
        if (token == null) {
            System.out.println("ERROR: debe escribir exactamente un token valido.");
            return;
        }
        pilaManual.push(token);
        System.out.println("Se inserto el token " + token
                + " (" + token.getTipo().getDescripcion() + ").");
        pilaManual.mostrar();
    }

    private static void retirarToken() {
        Nodo retirado = pilaManual.pop();
        if (retirado == null) {
            System.out.println("La pila esta vacia, no hay nada que retirar.");
            return;
        }
        System.out.println("Se retiro el token " + retirado + ".");
        pilaManual.mostrar();
    }

    private static void consultarCima() {
        Nodo cima = pilaManual.peek();
        if (cima == null) {
            System.out.println("La pila esta vacia, no hay cima.");
            return;
        }
        System.out.println("En la cima esta el token " + cima
                + " (" + cima.getTipo().getDescripcion() + ").");
    }
}
