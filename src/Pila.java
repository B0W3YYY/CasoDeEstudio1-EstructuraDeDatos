/**
 * Pila implementada con nodos enlazados (sin usar librerias de colecciones).
 *
 * La pila sigue la politica LIFO (Last In, First Out): el ultimo caracter
 * que entra es el primero que sale. Todas las operaciones trabajan
 * unicamente sobre la cima, por lo que su costo es constante O(1).
 */
public class Pila {

    private Nodo cima;   // nodo que esta en la parte superior de la pila
    private int tamano;  // cantidad de nodos almacenados

    /** Crea una pila vacia. */
    public Pila() {
        this.cima = null;
        this.tamano = 0;
    }

    /** Inserta un caracter en la cima de la pila. */
    public void push(char dato, int posicion) {
        Nodo nuevo = new Nodo(dato, posicion);
        nuevo.setSiguiente(cima);  // el nuevo nodo apunta a la cima anterior
        cima = nuevo;              // el nuevo nodo pasa a ser la cima
        tamano++;
    }

    /**
     * Elimina y devuelve el nodo que esta en la cima.
     * Devuelve null si la pila esta vacia, para que quien llame al metodo
     * pueda avisar al usuario sin que el programa se detenga.
     */
    public Nodo pop() {
        if (estaVacia()) {
            return null;
        }
        Nodo retirado = cima;
        cima = cima.getSiguiente();
        retirado.setSiguiente(null);
        tamano--;
        return retirado;
    }

    /** Devuelve el nodo de la cima sin retirarlo, o null si la pila esta vacia. */
    public Nodo peek() {
        return cima;
    }

    /** Indica si la pila no contiene elementos. */
    public boolean estaVacia() {
        return cima == null;
    }

    /** Devuelve la cantidad de elementos almacenados. */
    public int getTamano() {
        return tamano;
    }

    /** Retira todos los elementos de la pila. */
    public void vaciar() {
        cima = null;
        tamano = 0;
    }

    /**
     * Devuelve el contenido de la pila como texto, de la cima hacia la base.
     * Ejemplo: [cima] " ( [base]
     */
    public String contenido() {
        if (estaVacia()) {
            return "(pila vacia)";
        }
        String texto = "[cima] ";
        Nodo actual = cima;
        while (actual != null) {
            texto = texto + actual.getDato() + " ";
            actual = actual.getSiguiente();
        }
        return texto + "[base]";
    }

    /** Muestra en consola el estado actual de la pila. */
    public void mostrar() {
        System.out.println("Pila (" + tamano + " elemento(s)): " + contenido());
    }
}
