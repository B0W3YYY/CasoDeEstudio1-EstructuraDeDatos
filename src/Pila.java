/**
 * Pila de tokens con nodos enlazados (LIFO).
 * Todas las operaciones trabajan sobre la cima, con costo O(1).
 */
public class Pila {

    private Nodo cima;   // nodo superior
    private int tamano;  // cantidad de nodos

    /** Inserta un token en la cima. */
    public void push(Nodo nuevo) {
        nuevo.setSiguiente(cima);
        cima = nuevo;
        tamano++;
    }

    /** Retira y devuelve la cima, o null si la pila esta vacia. */
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

    /** Devuelve la cima sin retirarla, o null si la pila esta vacia. */
    public Nodo peek() { return cima; }

    public boolean estaVacia() { return cima == null; }

    public int getTamano() { return tamano; }

    public void vaciar() {
        cima = null;
        tamano = 0;
    }

    /** Contenido de la cima a la base, por ejemplo: [cima] + | "Hola" [base] */
    public String contenido() {
        if (estaVacia()) {
            return "(pila vacia)";
        }
        String texto = "[cima] ";
        for (Nodo actual = cima; actual != null; actual = actual.getSiguiente()) {
            texto += actual;
            if (actual.getSiguiente() != null) {
                texto += " | ";
            }
        }
        return texto + " [base]";
    }

    public void mostrar() {
        System.out.println("Pila (" + tamano + " elemento(s)): " + contenido());
    }
}
