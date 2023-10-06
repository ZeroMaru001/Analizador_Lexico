public class Token {
    /*
    * Atributos de la clase token
    * tipo: indica el tipo de token
    * lexema: cadena de caracteres o símbolos que componen a los elementos como palabras reservadas, identificadores, etc
    * literal: Guarda el valor numerico si es que el lexema es numerico
    */
    final TipoToken tipo;

    final String lexema;

    final Object literal;

    //Constructores

    //Constructor 1

    public Token (TipoToken tipo, String lexema){
        this.tipo = tipo;
        this.lexema = lexema;
        this.literal = null;
    }

    //Constructor 2

    public Token (TipoToken tipo, String lexema, Object literal){
        this.tipo = tipo;
        this.lexema = lexema;
        this.literal = literal;
    }

    //Metodos de la clase token

    /**
     * Metodo para transformar los tokens a un string
     * @return una cadena que indica la informacion de un token
     */

    public String toString() {
        return "<" + tipo + ", " + lexema + ", " + literal + ">";
    }
}
