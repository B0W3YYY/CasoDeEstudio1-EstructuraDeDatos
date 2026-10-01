/**
 * Nodo de la pila.
 *
 * Segun la consigna, para el analisis de cadenas de impresion cada nodo
 * almacena un caracter que forma parte de la cadena por analizar
 * (una comilla o un parentesis de apertura, por ejemplo).
 *
 * Ademas del caracter se guarda la posicion que ese caracter ocupa dentro
 * de la expresion, dato que permite informar al usuario el lugar exacto
 * donde se encuentra un delimitador sin cerrar.
 */
public class Nodo {

    private char dato;        // caracter almacenado en el nodo
    private int posicion;     // indice del caracter dentro de la expresion
    private Nodo siguiente;   // referencia al nodo que esta debajo en la pila

    /** Crea un nodo con el caracter y la posicion indicados. */
    public Nodo(char dato, int posicion) {
        this.dato = dato;
        this.posicion = posicion;
        this.siguiente = null;
    }

    public char getDato() {
        return dato;
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
}
