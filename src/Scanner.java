import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {
    //Atributos de la clase Scanner

    //palabrasReservadas: Guardar las plabras reservadas en un mapa hash
    private static final Map<String, TipoToken> palabrasReservadas;
    //Iniicializar el atributo de palabras reservadas mediante la creacion de objetos
    static {
        palabrasReservadas = new HashMap<>();
        palabrasReservadas.put("and",    TipoToken.AND);
        palabrasReservadas.put("else",   TipoToken.ELSE);
        palabrasReservadas.put("false",  TipoToken.FALSE);
        palabrasReservadas.put("for",    TipoToken.FOR);
        palabrasReservadas.put("fun",    TipoToken.FUN);
        palabrasReservadas.put("if",     TipoToken.IF);
        palabrasReservadas.put("null",   TipoToken.NULL);
        palabrasReservadas.put("or",     TipoToken.OR);
        palabrasReservadas.put("print",  TipoToken.PRINT);
        palabrasReservadas.put("return", TipoToken.RETURN);
        palabrasReservadas.put("true",   TipoToken.TRUE);
        palabrasReservadas.put("var",    TipoToken.VAR);
        palabrasReservadas.put("while",  TipoToken.WHILE);
    }

    //codigo: codigo a analizar

    private final String codigo;

    //tokens: lista de tokens

    private final List<Token> tokens = new ArrayList<>();

    //constructor
    public Scanner (String codigo){
        this.codigo = codigo;
    }

    //Metodos

    /**
     *
     * @return una lista de tokens
     */

    public List<Token> scan(){

        return tokens;
    }
}
