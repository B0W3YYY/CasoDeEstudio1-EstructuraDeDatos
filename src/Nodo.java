/**
 * Nodo de la pila.
 *
 * Segun la consigna, para el analisis de cadenas de impresion cada nodo
 * es un token que forma parte de la cadena por analizar: una cadena
 * literal ("Hola"), una variable de cadena (nombre), el operador de
 * concatenacion (+) o un parentesis.
 *
 * Ademas del tipo y del texto del token se guarda la posicion donde
 * empieza dentro de la expresion, dato que permite informar al usuario
 * el lugar exacto de un error.
 */
public class Nodo {

    private TipoToken tipo;   // clase de token almacenado
    private String lexema;    // texto del token tal como aparece en la expresion
    private int posicion;     // indice donde empieza el token dentro de la expresion
    private Nodo siguiente;   // referencia al nodo que esta debajo en la pila

    /** Crea un nodo con el token indicado. */
    public Nodo(TipoToken tipo, String lexema, int posicion) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.posicion = posicion;
        this.siguiente = null;
    }

    public TipoToken getTipo() {
        return tipo;
    }

    public String getLexema() {
        return lexema;
    }

    public int getPosicion() {
        return posicion;
    }

    public Nodo getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(Nodo siguiente) {
        this.siguiente = siguiente;
    }

    /**
     * Texto con el que se muestra el nodo dentro de la pila. Una expresion
     * ya reconocida se muestra como EXPR para que la pila se lea con facilidad.
     */
    @Override
    public String toString() {
        if (tipo == TipoToken.EXPRESION) {
            return "EXPR";
        }
        return lexema;
    }
}
