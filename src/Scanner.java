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
        this.codigo = codigo + " ";
    }

    //Metodos

    /**
     * Analiza un codigo dado y verifica que el lexico sea correcto segun los automatas vistos en clase
     *
     * @return una lista de tokens
     */

    public List<Token> scan(){
        int estado = 0; //estado del automata
        int numeroLinea = 1;
        String lexema = ""; //elemento a analizar
        char c;
        Token t;

        for (int i = 0; i < codigo.length(); i++) {
            c = codigo.charAt(i);

            switch (estado){
                case 0:
                    if ( c == '>') {
                        estado = 1;
                        lexema += c;
                    } else if ( c == '<') {
                        estado = 4;
                        lexema += c;
                    } else if ( c == '=') {
                        estado = 7;
                        lexema += c;
                    } else if ( c == '!') {
                        estado = 10;
                        lexema += c;
                    } else if (Character.isLetter(c)) {
                        estado = 13;
                        lexema += c;
                    } else if (Character.isDigit(c)) {
                        estado = 15;
                        lexema += c;
                    } else if (c == '"'){
                        estado = 24;
                        lexema += c;
                    } else if ( c == '/') {
                        estado = 26;
                        lexema += c;
                    } else if ( c == '+') {
                        lexema += c;
                        t = new Token(TipoToken.PLUS,lexema);
                        tokens.add(t);
                        lexema = "";
                    } else if ( c == '-') {
                        lexema += c;
                        t = new Token(TipoToken.MINUS,lexema);
                        tokens.add(t);
                        lexema = "";
                    } else if ( c == '*') {
                        lexema += c;
                        t = new Token(TipoToken.STAR,lexema);
                        tokens.add(t);
                        lexema = "";
                    } else if ( c == '{') {
                        lexema += c;
                        t = new Token(TipoToken.LEFT_BRACE,lexema);
                        tokens.add(t);
                        lexema = "";
                    } else if ( c == '}') {
                        lexema += c;
                        t = new Token(TipoToken.RIGHT_BRACE,lexema);
                        tokens.add(t);
                        lexema = "";
                    } else if ( c == '(') {
                        lexema += c;
                        t = new Token(TipoToken.LEFT_PAREN,lexema);
                        tokens.add(t);
                        lexema = "";
                    } else if ( c == ')') {
                        lexema += c;
                        t = new Token(TipoToken.RIGHT_PAREN,lexema);
                        tokens.add(t);
                        lexema = "";
                    } else if ( c == ',') {
                        lexema += c;
                        t = new Token(TipoToken.COMMA,lexema);
                        tokens.add(t);
                        lexema = "";
                    } else if ( c == '.') {
                        lexema += c;
                        t = new Token(TipoToken.DOT,lexema);
                        tokens.add(t);
                        lexema = "";
                    } else if ( c == ';') {
                        lexema += c;
                        t = new Token(TipoToken.SEMICOLON,lexema);
                        tokens.add(t);
                        lexema = "";
                    } else if ( c == '\n') {
                        numeroLinea++;
                    } else if ( !(c == ' ' || c ==  '\t' )) {
                        Main.error(numeroLinea, "Simbolo no valido");
                    }
                    break;
                case 1:
                    if (c == '='){
                        lexema += c;
                        t = new Token(TipoToken.GREATER_EQUAL, lexema);
                        tokens.add(t);
                    }else{
                        t = new Token(TipoToken.GREATER, lexema);
                        tokens.add(t);
                        i--;
                    }
                    estado = 0;
                    lexema = "";
                    break;
                case 4:
                    if (c == '='){
                        lexema += c;
                        t = new Token(TipoToken.LESS_EQUAL, lexema);
                        tokens.add(t);
                    }else{
                        t = new Token(TipoToken.LESS, lexema);
                        tokens.add(t);
                        i--;
                    }
                    estado = 0;
                    lexema = "";
                    break;
                case 7:
                    if (c == '='){
                        lexema += c;
                        t = new Token(TipoToken.EQUAL_EQUAL, lexema);
                        tokens.add(t);
                    }else{
                        t = new Token(TipoToken.EQUAL, lexema);
                        tokens.add(t);
                        i--;
                    }
                    estado = 0;
                    lexema = "";
                    break;
                case 10:
                    if (c == '='){
                        lexema += c;
                        t = new Token(TipoToken.BANG_EQUAL, lexema);
                        tokens.add(t);
                    }else{
                        t = new Token(TipoToken.BANG, lexema);
                        tokens.add(t);
                        i--;
                    }
                    estado = 0;
                    lexema = "";
                    break;
                case 13:
                    if (Character.isLetterOrDigit(c)){
                        estado = 13;
                        lexema += c;
                    }else {
                        TipoToken tt = palabrasReservadas.get(lexema);

                        if (tt == null) {
                            t = new Token(TipoToken.IDENTIFIER, lexema);
                            tokens.add(t);
                        }else{
                            t = new  Token(tt, lexema);
                            tokens.add(t);
                        }

                        estado = 0;
                        lexema = "";
                        i--;
                    }
                    break;
                case 15:
                    if(Character.isDigit(c)){
                        estado = 15;
                        lexema += c;
                    }
                    else if ( c == '.'){
                        //numero decimal
                        estado = 16;
                        lexema += c;
                    }
                    else if ( c == 'E') {
                        //numero con exponente
                        estado = 18;
                        lexema += c;
                    } else {
                        //numero entero
                        t = new Token (TipoToken.NUMBER, lexema, Integer.valueOf(lexema));
                        tokens.add(t);

                        estado = 0;
                        lexema = "";
                        i--;
                    }
                    break;
                case 16:
                    if (Character.isDigit(c)){
                        estado = 17;
                        lexema += c;
                    } else if (c == 'E') {
                        estado = 18;
                        lexema += c;
                    } else{
                        //error
                        Main.error(numeroLinea, "Numero decimal solo posee punto");
                    }
                    break;
                case 17:
                    if (Character.isDigit(c)){
                        estado = 17;
                        lexema += c;
                    } else if (c == 'E') {
                        //Decimal con exponente
                        estado = 18;
                        lexema += c;
                    } else{
                        //Generar token decimal
                        t = new Token (TipoToken.NUMBER, lexema, Double.valueOf(lexema));
                        tokens.add(t);

                        estado = 0;
                        lexema = "";
                        i--;
                    }
                    break;
                case 18:
                    if (Character.isDigit(c)){
                        estado = 20;
                        lexema += c;
                    } else if (c == '+' || c == '-') {
                        estado = 19;
                        lexema += c;
                    } else{
                        //error
                        Main.error(numeroLinea, "Exponentesin numero");
                    }
                    break;
                case 19:
                    if (Character.isDigit(c)){
                        estado = 20;
                        lexema += c;
                    }else {
                        //Error
                        Main.error(numeroLinea, "Exponente con signo sin numero");
                    }
                    break;
                case 20:
                    if (Character.isDigit(c)){
                        estado = 20;
                        lexema += c;
                    }else {
                        //Generar token con exponencial
                        t = new Token (TipoToken.NUMBER, lexema, Double.parseDouble(lexema));
                        tokens.add(t);

                        estado = 0;
                        lexema = "";
                        i--;
                    }
                    break;
                case 24:
                    if (c == '"') { 
                        // La existencia de una ingresar nuevamente comillas dobles cierra la cadena, pasa a estado 0.
                        estado = 0;
                        lexema += c;
                        t = new Token(TipoToken.STRING, lexema, lexema.substring(1, lexema.length() - 1));
                        tokens.add(t);
                        lexema = "";
                    } else if (c == '\n') { 
                        // Un salto de línea determina un error.
                        numeroLinea++;
                        Main.error(numeroLinea, "Error no se cerro '\"' ");
                        estado = 0;
                        lexema = "";
                    } else {
                        lexema += c;
                    }
                    break;
                case 26:
                    if(c == '*'){
                        // Si se detecta un asterisco, se avanza al estado 27
                        estado = 27;
                    }
                    else if(c == '/'){
                        // Si se detecta otra diagonal, se avanza al estado 30
                        estado = 30;
                    } else{
                        // La diagonal fue única e indica división
                        //Se genera token para '/'
                        //Se regresa al estado 0 del autómata y se reinicia el lexema
                        t = new Token (TipoToken.SLASH, lexema, null);
                        tokens.add(t);
                        i--;
                        estado = 0;
                        lexema = "";
                    }
                    break;
                case 27:
                    if (c == '\n'){
                        numeroLinea++;
                    }
                    if(c == '*'){
                        //Si se detecta un asterisco, se avanza al estado 28
                        estado = 28;
                    } else{
                        estado = 27;
                    }
                    break;
                case 28:
                    if(c == '/'){
                        //Si se detecta una diagonal, indica que el comentario multilínea llegó a su fin
                        //Se regresa al estado 0 y se reinicia el lexema
                        estado = 0;
                        lexema = "";
                    }
                    else if(c == '*'){
                        //Si se detecta un asterisco se mantiene en el mismo estado
                        estado = 28;
                    } else{
                        //Si se detecta cualquier otro caracter, se regresa al estado 27
                        estado = 27;
                    }
                    break;
                case 30:
                    if(c == '\n'){
                        //Si se detecta un salto de línea, se se regresa al estado 0 y se reinicia el lexema
                        estado = 0;
                        lexema = "";
                        numeroLinea++;
                    } else{
                        //Si se detecta cualquier otro caracter, se mantiene en el estado 30
                        estado = 30;
                    }
                    break;
            }
        }
        if (estado == 27 || estado == 28){
            Main.error(numeroLinea, "Comentario multilinea sin cerrar");
        } else if (estado == 24){
            Main.error(numeroLinea, "Error no se cerro '\"' ");
        }
        Token t1 = new Token(TipoToken.EOF,"");
        tokens.add(t1);
        return tokens;
    }
}
