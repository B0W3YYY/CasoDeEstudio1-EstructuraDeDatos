/**
 * Tipos de token que puede contener una cadena de impresion.
 *
 * Los tres primeros son los que pide la consigna: cadenas literales,
 * variables de cadena y el operador de concatenacion. Los parentesis
 * permiten agrupar, y EXPRESION representa una parte de la cadena que
 * ya fue reconocida como correcta y se reemplazo por un solo nodo.
 */
public enum TipoToken {

    LITERAL("cadena literal"),
    VARIABLE("variable de cadena"),
    CONCATENACION("operador de concatenacion"),
    PARENTESIS_APERTURA("parentesis de apertura"),
    PARENTESIS_CIERRE("parentesis de cierre"),
    EXPRESION("expresion reconocida");

    private final String descripcion;   // nombre legible para los mensajes

    TipoToken(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /** Indica si el token puede actuar como operando de una concatenacion. */
    public boolean esOperando() {
        return this == LITERAL || this == VARIABLE || this == EXPRESION;
    }
}
