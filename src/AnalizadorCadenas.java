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
 * El analisis se hace en una sola pasada. La expresion se divide en
 * tokens y cada token se inserta en la pila (push). Cada vez que la cima
 * forma un patron completo, ese patron se retira con pop y se reemplaza
 * por un solo nodo EXPR:
 *
 *   operando + operando   ->  EXPR
 *   ( operando )          ->  EXPR
 *
 * La cadena es valida si al terminar la pila contiene un unico operando.
 */
public class AnalizadorCadenas {

    private Pila pila;              // pila de tokens pendientes de reducir
    private boolean mostrarTraza;   // true para imprimir el estado paso a paso

    private String expresion;       // expresion que se esta analizando
    private int indice;             // posicion del siguiente caracter por leer
    private String errorLexico;     // mensaje si un token esta mal escrito
    private int posicionError;      // posicion del error lexico

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
        iniciarLectura(expresion);
        pila.vaciar();

        Nodo token = siguienteToken();
        while (token != null) {
            int pos = token.getPosicion();

            switch (token.getTipo()) {
                case LITERAL:
                case VARIABLE:
                case PARENTESIS_APERTURA:
                    // Un operando o una apertura solo puede ir al inicio,
                    // despues de un + o despues de otro parentesis de apertura.
                    if (!esperaOperando()) {
                        return error("falta el operador + antes de " + token.getLexema()
                                + " (" + token.getTipo().getDescripcion() + ")", pos);
                    }
                    pila.push(token);
                    traza("push " + token.getLexema() + "  ("
                            + token.getTipo().getDescripcion() + ", posicion " + pos + ")");
                    if (token.getTipo().esOperando()) {
                        reducirConcatenacion();
                    }
                    break;

                case CONCATENACION:
                    if (!cimaEsOperando()) {
                        return error("el operador + no tiene un operando a su izquierda", pos);
                    }
                    operadores++;
                    pila.push(token);
                    traza("push +  (operador de concatenacion, posicion " + pos + ")");
                    break;

                case PARENTESIS_CIERRE:
                    if (!cimaEsOperando()) {
                        return error("falta un operando antes del parentesis de cierre", pos);
                    }
                    if (!cerrarParentesis()) {
                        return error("el parentesis de cierre no tiene apertura", pos);
                    }
                    traza("lee ) en la posicion " + pos
                            + ": pop del operando y del (, push EXPR");
                    reducirConcatenacion();
                    break;

                default:
                    return error("token inesperado", pos);
            }
            token = siguienteToken();
        }

        if (errorLexico != null) {
            return error(errorLexico, posicionError);
        }
        return verificarEstadoFinal();
    }

    /**
     * Convierte un texto en un solo token. Se usa en el menu para insertar
     * tokens en la pila de forma manual.
     *
     * @return el token, o null si el texto no corresponde a exactamente un token
     */
    public Nodo convertirEnToken(String texto) {
        iniciarLectura(texto);
        Nodo token = siguienteToken();
        if (token == null || siguienteToken() != null || errorLexico != null) {
            return null;
        }
        return token;
    }

    // ------------------------------------------------------------------
    // Operaciones sobre la pila
    // ------------------------------------------------------------------

    /** Indica si en este punto de la expresion corresponde un operando. */
    private boolean esperaOperando() {
        Nodo cima = pila.peek();
        return cima == null
                || cima.getTipo() == TipoToken.CONCATENACION
                || cima.getTipo() == TipoToken.PARENTESIS_APERTURA;
    }

    /** Indica si la cima de la pila es un operando. */
    private boolean cimaEsOperando() {
        Nodo cima = pila.peek();
        return cima != null && cima.getTipo().esOperando();
    }

    /**
     * Si la cima tiene la forma  operando + operando , retira esos tres
     * nodos e inserta un solo nodo EXPR que los representa.
     */
    private void reducirConcatenacion() {
        Nodo derecho = pila.pop();
        Nodo operador = pila.peek();
        if (operador == null || operador.getTipo() != TipoToken.CONCATENACION) {
            pila.push(derecho);   // no hay nada que reducir: se deja como estaba
            return;
        }
        pila.pop();               // retira el +
        Nodo izquierdo = pila.pop();
        pila.push(new Nodo(TipoToken.EXPRESION,
                izquierdo.getLexema() + " + " + derecho.getLexema(),
                izquierdo.getPosicion()));
        traza("pop de operando, + y operando, push EXPR");
    }

    /**
     * Atiende un parentesis de cierre: la cima debe ser un operando y debajo
     * debe estar el parentesis de apertura. Ambos se retiran y se inserta un
     * nodo EXPR en su lugar.
     *
     * @return false si no hay un parentesis de apertura con el cual emparejar
     */
    private boolean cerrarParentesis() {
        Nodo interior = pila.pop();
        Nodo apertura = pila.peek();
        if (apertura == null || apertura.getTipo() != TipoToken.PARENTESIS_APERTURA) {
            pila.push(interior);
            return false;
        }
        pila.pop();
        pila.push(new Nodo(TipoToken.EXPRESION,
                "(" + interior.getLexema() + ")",
                apertura.getPosicion()));
        return true;
    }

    /** Revisa la pila al terminar de leer la expresion. */
    private boolean verificarEstadoFinal() {
        if (pila.estaVacia()) {
            System.out.println("ERROR: la expresion no contiene ningun token.");
            return false;
        }
        if (pila.peek().getTipo() == TipoToken.CONCATENACION) {
            return error("la expresion termina con el operador + y falta un operando",
                         pila.peek().getPosicion());
        }
        if (pila.getTamano() == 1 && cimaEsOperando()) {
            System.out.println("RESULTADO: la cadena de impresion es VALIDA.");
            System.out.println("  Cadenas literales: " + literales);
            System.out.println("  Variables de cadena: " + variables);
            System.out.println("  Operadores de concatenacion: " + operadores);
            System.out.println("  Estado final de la pila: " + pila.contenido());
            return true;
        }

        // Quedo al menos un parentesis sin cerrar. Se busca el mas reciente.
        Nodo nodo = pila.pop();
        while (nodo.getTipo() != TipoToken.PARENTESIS_APERTURA) {
            nodo = pila.pop();
        }
        return error("el parentesis de apertura no fue cerrado", nodo.getPosicion());
    }

    // ------------------------------------------------------------------
    // Lectura de tokens
    // ------------------------------------------------------------------

    /** Prepara la lectura de una nueva expresion desde su primer caracter. */
    private void iniciarLectura(String texto) {
        this.expresion = texto;
        this.indice = 0;
        this.errorLexico = null;
    }

    /**
     * Lee el siguiente token de la expresion.
     *
     * @return el token leido, o null si se termino la expresion o si se
     *         encontro un error (en ese caso queda guardado en errorLexico)
     */
    private Nodo siguienteToken() {
        // Los espacios no forman parte de ningun token.
        while (indice < expresion.length()
                && (expresion.charAt(indice) == ' ' || expresion.charAt(indice) == '\t')) {
            indice++;
        }
        if (indice >= expresion.length()) {
            return null;
        }

        int inicio = indice;
        char c = expresion.charAt(indice);

        if (c == '"') {
            int cierre = buscarComillaDeCierre(inicio + 1);
            if (cierre == -1) {
                return errorDeLectura("la comilla de apertura no fue cerrada", inicio);
            }
            indice = cierre + 1;
            literales++;
            return new Nodo(TipoToken.LITERAL, expresion.substring(inicio, indice), inicio);
        }
        if (c == '(') {
            indice++;
            return new Nodo(TipoToken.PARENTESIS_APERTURA, "(", inicio);
        }
        if (c == ')') {
            indice++;
            return new Nodo(TipoToken.PARENTESIS_CIERRE, ")", inicio);
        }
        if (c == '+') {
            indice++;
            return new Nodo(TipoToken.CONCATENACION, "+", inicio);
        }
        if (esInicioDeVariable(c)) {
            while (indice < expresion.length() && esParteDeVariable(expresion.charAt(indice))) {
                indice++;
            }
            variables++;
            return new Nodo(TipoToken.VARIABLE, expresion.substring(inicio, indice), inicio);
        }
        return errorDeLectura("caracter no permitido '" + c + "'", inicio);
    }

    /** Busca la comilla que cierra una cadena literal. Devuelve -1 si no existe. */
    private int buscarComillaDeCierre(int desde) {
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

    /** Guarda un error de lectura y detiene la lectura de tokens. */
    private Nodo errorDeLectura(String mensaje, int posicion) {
        errorLexico = mensaje;
        posicionError = posicion;
        indice = expresion.length();
        return null;
    }

    // ------------------------------------------------------------------
    // Mensajes
    // ------------------------------------------------------------------

    /** Informa un error indicando la posicion exacta y devuelve false. */
    private boolean error(String mensaje, int posicion) {
        System.out.println("ERROR en la posicion " + posicion + ": " + mensaje + ".");
        return false;
    }

    /** Imprime el paso realizado y el estado de la pila, si la traza esta activa. */
    private void traza(String paso) {
        if (mostrarTraza) {
            System.out.println("  " + paso);
            System.out.println("      ->  " + pila.contenido());
        }
    }
}
