/**
 * Analizador de cadenas de impresion.
 *
 * Revisa expresiones del tipo que recibe una instruccion de impresion,
 * formadas por tres clases de tokens:
 *
 *   - cadenas literales, delimitadas por comillas dobles:  "Hola"
 *   - variables de cadena (identificadores):               nombre
 *   - el operador de concatenacion:                        +
 *
 * y por parentesis de agrupacion. Ejemplo de expresion valida:
 *
 *   "Hola, " + nombre + ("!" + salto)
 *
 * El analisis recorre la expresion caracter por caracter y usa una pila
 * para llevar el control de los delimitadores abiertos: cada comilla o
 * parentesis de apertura se inserta con push y se retira con pop cuando
 * aparece su cierre. Si al terminar el recorrido la pila no esta vacia,
 * quedo un delimitador sin cerrar.
 */
public class AnalizadorCadenas {

    private Pila pila;              // pila de delimitadores abiertos
    private boolean mostrarTraza;   // true para imprimir el estado paso a paso
    private int literales;          // cadenas literales encontradas
    private int variables;          // variables de cadena encontradas
    private int operadores;         // operadores + encontrados

    public AnalizadorCadenas() {
        this.pila = new Pila();
    }

    /** Devuelve la pila utilizada, para poder consultar su estado final. */
    public Pila getPila() {
        return pila;
    }

    /**
     * Analiza la expresion recibida.
     *
     * @param expresion    cadena de impresion por revisar
     * @param mostrarTraza si es true, imprime el estado de la pila en cada paso
     * @return true si la expresion es valida, false si tiene algun error
     */
    public boolean analizar(String expresion, boolean mostrarTraza) {
        this.mostrarTraza = mostrarTraza;
        this.literales = 0;
        this.variables = 0;
        this.operadores = 0;
        pila.vaciar();

        // Indica si en la posicion actual corresponde un operando
        // (una cadena literal, una variable o un parentesis de apertura).
        boolean esperaOperando = true;
        int i = 0;

        while (i < expresion.length()) {
            char c = expresion.charAt(i);

            // Los espacios no forman parte del analisis.
            if (c == ' ' || c == '\t') {
                i++;
                continue;
            }

            if (c == '"') {
                if (!esperaOperando) {
                    return error("falta el operador + antes de la cadena literal", i);
                }
                pila.push(c, i);                     // se abre la cadena literal
                traza("push", c, i);
                int cierre = buscarComillaDeCierre(expresion, i + 1);
                if (cierre == -1) {
                    return error("la comilla de apertura no fue cerrada", i);
                }
                pila.pop();                          // se cierra la cadena literal
                traza("pop", '"', cierre);
                literales++;
                esperaOperando = false;
                i = cierre + 1;
                continue;
            }

            if (c == '(') {
                if (!esperaOperando) {
                    return error("falta el operador + antes del parentesis de apertura", i);
                }
                pila.push(c, i);
                traza("push", c, i);
                i++;
                continue;
            }

            if (c == ')') {
                if (esperaOperando) {
                    return error("falta un operando antes del parentesis de cierre", i);
                }
                Nodo retirado = pila.pop();
                if (retirado == null) {
                    return error("el parentesis de cierre no tiene apertura", i);
                }
                if (retirado.getDato() != '(') {
                    return error("el delimitador de cierre no coincide con la apertura", i);
                }
                traza("pop", ')', i);
                esperaOperando = false;
                i++;
                continue;
            }

            if (c == '+') {
                if (esperaOperando) {
                    return error("el operador + no tiene un operando a su izquierda", i);
                }
                operadores++;
                esperaOperando = true;
                i++;
                continue;
            }

            if (esInicioDeVariable(c)) {
                if (!esperaOperando) {
                    return error("falta el operador + antes de la variable", i);
                }
                int fin = i;
                while (fin < expresion.length() && esParteDeVariable(expresion.charAt(fin))) {
                    fin++;
                }
                variables++;
                esperaOperando = false;
                i = fin;
                continue;
            }

            return error("caracter no permitido '" + c + "'", i);
        }

        // Validaciones finales.
        if (literales == 0 && variables == 0) {
            System.out.println("ERROR: la expresion no contiene ningun operando.");
            return false;
        }
        if (esperaOperando) {
            System.out.println("ERROR: la expresion termina con el operador + y falta un operando.");
            return false;
        }
        if (!pila.estaVacia()) {
            Nodo pendiente = pila.peek();
            return error("el delimitador '" + pendiente.getDato() + "' no fue cerrado",
                         pendiente.getPosicion());
        }

        System.out.println("RESULTADO: la cadena de impresion es VALIDA.");
        System.out.println("  Cadenas literales: " + literales);
        System.out.println("  Variables de cadena: " + variables);
        System.out.println("  Operadores de concatenacion: " + operadores);
        System.out.println("  Estado final de la pila: " + pila.contenido());
        return true;
    }

    /** Busca la comilla que cierra una cadena literal. Devuelve -1 si no existe. */
    private int buscarComillaDeCierre(String expresion, int desde) {
        for (int i = desde; i < expresion.length(); i++) {
            if (expresion.charAt(i) == '"') {
                return i;
            }
        }
        return -1;
    }

    /** Una variable de cadena inicia con letra o guion bajo. */
    private boolean esInicioDeVariable(char c) {
        return Character.isLetter(c) || c == '_';
    }

    /** Despues del primer caracter, una variable admite letras, digitos y guion bajo. */
    private boolean esParteDeVariable(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    /** Informa un error indicando la posicion exacta y devuelve false. */
    private boolean error(String mensaje, int posicion) {
        System.out.println("ERROR en la posicion " + posicion + ": " + mensaje + ".");
        return false;
    }

    /** Imprime el paso realizado y el estado de la pila, si la traza esta activa. */
    private void traza(String operacion, char caracter, int posicion) {
        if (mostrarTraza) {
            System.out.println("  " + operacion + " '" + caracter + "' (posicion " + posicion
                    + ")  ->  " + pila.contenido());
        }
    }
}
