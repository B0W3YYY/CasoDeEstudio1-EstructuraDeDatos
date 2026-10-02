/** Nodo de la pila: guarda un token de la cadena por analizar. */
public class Nodo {

    private TipoToken tipo;   // clase de token
    private String lexema;    // texto del token, por ejemplo "Hola" o nombre
    private int posicion;     // indice donde empieza dentro de la expresion
    private Nodo siguiente;   // nodo que esta debajo en la pila

    public Nodo(TipoToken tipo, String lexema, int posicion) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.posicion = posicion;
    }

    public TipoToken getTipo() { return tipo; }
    public String getLexema() { return lexema; }
    public int getPosicion() { return posicion; }
    public Nodo getSiguiente() { return siguiente; }
    public void setSiguiente(Nodo siguiente) { this.siguiente = siguiente; }

    /** Una expresion ya reconocida se muestra como EXPR. */
    @Override
    public String toString() {
        return tipo == TipoToken.EXPRESION ? "EXPR" : lexema;
    }
}
