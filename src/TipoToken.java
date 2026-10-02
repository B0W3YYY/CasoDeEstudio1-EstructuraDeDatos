/** Tipos de token que puede tener una cadena de impresion. */
public enum TipoToken {

    LITERAL("cadena literal"),
    VARIABLE("variable de cadena"),
    CONCATENACION("operador de concatenacion"),
    PARENTESIS_APERTURA("parentesis de apertura"),
    PARENTESIS_CIERRE("parentesis de cierre"),
    EXPRESION("expresion reconocida");   // parte de la cadena ya validada

    private final String descripcion;

    TipoToken(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() { return descripcion; }

    /** Indica si el token puede ir a un lado de un +. */
    public boolean esOperando() {
        return this == LITERAL || this == VARIABLE || this == EXPRESION;
    }
}
