/**
 * Analiza cadenas de impresion como:  "Hola, " + nombre + ("!" + salto)
 *
 * Tokens: cadenas literales, variables, el operador + y parentesis.
 * Cada token entra en la pila y, cuando la cima forma un patron completo,
 * ese patron se reemplaza por un solo nodo EXPR:
 *
 *   operando + operando  ->  EXPR
 *   ( operando )         ->  EXPR
 *
 * La cadena es valida si al final queda un solo operando en la pila.
 */
public class AnalizadorCadenas {

    private Pila pila = new Pila();   // tokens pendientes de reducir
    private boolean mostrarTraza;     // imprime cada paso si es true

    private String expresion;         // texto que se esta leyendo
    private int indice;               // siguiente caracter por leer
    private String errorLexico;       // error al leer un token, si lo hay
    private int posicionError;

    private int literales, variables, operadores;   // contadores de tokens

    /** Analiza la expresion y devuelve true si es valida. */
    public boolean analizar(String expresion, boolean mostrarTraza) {
        this.mostrarTraza = mostrarTraza;
        literales = variables = operadores = 0;
        iniciarLectura(expresion);
        pila.vaciar();

        for (Nodo token = siguienteToken(); token != null; token = siguienteToken()) {
            if (!procesar(token)) {
                return false;
            }
        }
        if (errorLexico != null) {
            return error(errorLexico, posicionError);
        }
        return verificarEstadoFinal();
    }

    /** Convierte un texto en un solo token, o devuelve null si no lo es. */
    public Nodo convertirEnToken(String texto) {
        iniciarLectura(texto);
        Nodo token = siguienteToken();
        if (token == null || siguienteToken() != null || errorLexico != null) {
            return null;
        }
        return token;
    }

    // ---------------------------------------------------------------- pila

    /** Aplica a la pila la operacion que corresponde al token. */
    private boolean procesar(Nodo token) {
        int pos = token.getPosicion();

        switch (token.getTipo()) {
            case LITERAL:
            case VARIABLE:
            case PARENTESIS_APERTURA:
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
                return true;

            case CONCATENACION:
                if (!cimaEsOperando()) {
                    return error("el operador + no tiene un operando a su izquierda", pos);
                }
                operadores++;
                pila.push(token);
                traza("push +  (operador de concatenacion, posicion " + pos + ")");
                return true;

            case PARENTESIS_CIERRE:
                if (!cimaEsOperando()) {
                    return error("falta un operando antes del parentesis de cierre", pos);
                }
                if (!cerrarParentesis()) {
                    return error("el parentesis de cierre no tiene apertura", pos);
                }
                traza("lee ) en la posicion " + pos + ": pop del operando y del (, push EXPR");
                reducirConcatenacion();
                return true;

            default:
                return error("token inesperado", pos);
        }
    }

    /** Hay que leer un operando al inicio, despues de un + o de un (. */
    private boolean esperaOperando() {
        Nodo cima = pila.peek();
        return cima == null
                || cima.getTipo() == TipoToken.CONCATENACION
                || cima.getTipo() == TipoToken.PARENTESIS_APERTURA;
    }

    private boolean cimaEsOperando() {
        Nodo cima = pila.peek();
        return cima != null && cima.getTipo().esOperando();
    }

    /** Si la cima es  operando + operando , la reemplaza por un EXPR. */
    private void reducirConcatenacion() {
        Nodo derecho = pila.pop();
        Nodo operador = pila.peek();
        if (operador == null || operador.getTipo() != TipoToken.CONCATENACION) {
            pila.push(derecho);   // no hay nada que reducir
            return;
        }
        pila.pop();
        Nodo izquierdo = pila.pop();
        pila.push(new Nodo(TipoToken.EXPRESION,
                izquierdo.getLexema() + " + " + derecho.getLexema(), izquierdo.getPosicion()));
        traza("pop de operando, + y operando, push EXPR");
    }

    /** Reemplaza  ( operando  por un EXPR. Devuelve false si no hay apertura. */
    private boolean cerrarParentesis() {
        Nodo interior = pila.pop();
        Nodo apertura = pila.peek();
        if (apertura == null || apertura.getTipo() != TipoToken.PARENTESIS_APERTURA) {
            pila.push(interior);
            return false;
        }
        pila.pop();
        pila.push(new Nodo(TipoToken.EXPRESION,
                "(" + interior.getLexema() + ")", apertura.getPosicion()));
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
        // Quedo un parentesis sin cerrar: se informa el mas reciente.
        Nodo nodo = pila.pop();
        while (nodo.getTipo() != TipoToken.PARENTESIS_APERTURA) {
            nodo = pila.pop();
        }
        return error("el parentesis de apertura no fue cerrado", nodo.getPosicion());
    }

    // ------------------------------------------------------ lectura de tokens

    private void iniciarLectura(String texto) {
        expresion = texto;
        indice = 0;
        errorLexico = null;
    }

    /** Lee el siguiente token. Devuelve null al terminar o si hay un error. */
    private Nodo siguienteToken() {
        while (indice < expresion.length() && esEspacio(expresion.charAt(indice))) {
            indice++;
        }
        if (indice >= expresion.length()) {
            return null;
        }

        int inicio = indice;
        char c = expresion.charAt(indice);

        if (c == '"') {
            int cierre = expresion.indexOf('"', inicio + 1);   // busqueda lineal
            if (cierre == -1) {
                return errorDeLectura("la comilla de apertura no fue cerrada", inicio);
            }
            indice = cierre + 1;
            literales++;
            return new Nodo(TipoToken.LITERAL, expresion.substring(inicio, indice), inicio);
        }
        if (c == '(' || c == ')' || c == '+') {
            indice++;
            return new Nodo(tipoDeSimbolo(c), String.valueOf(c), inicio);
        }
        if (Character.isLetter(c) || c == '_') {
            while (indice < expresion.length() && esParteDeVariable(expresion.charAt(indice))) {
                indice++;
            }
            variables++;
            return new Nodo(TipoToken.VARIABLE, expresion.substring(inicio, indice), inicio);
        }
        return errorDeLectura("caracter no permitido '" + c + "'", inicio);
    }

    private TipoToken tipoDeSimbolo(char c) {
        if (c == '(') {
            return TipoToken.PARENTESIS_APERTURA;
        }
        if (c == ')') {
            return TipoToken.PARENTESIS_CIERRE;
        }
        return TipoToken.CONCATENACION;
    }

    private boolean esEspacio(char c) {
        return c == ' ' || c == '\t';
    }

    /** Una variable admite letras, digitos y guion bajo. */
    private boolean esParteDeVariable(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    /** Guarda el error de lectura y detiene la lectura. */
    private Nodo errorDeLectura(String mensaje, int posicion) {
        errorLexico = mensaje;
        posicionError = posicion;
        indice = expresion.length();
        return null;
    }

    // --------------------------------------------------------------- mensajes

    /** Muestra el error con su posicion y devuelve false. */
    private boolean error(String mensaje, int posicion) {
        System.out.println("ERROR en la posicion " + posicion + ": " + mensaje + ".");
        return false;
    }

    private void traza(String paso) {
        if (mostrarTraza) {
            System.out.println("  " + paso);
            System.out.println("      ->  " + pila.contenido());
        }
    }
}
