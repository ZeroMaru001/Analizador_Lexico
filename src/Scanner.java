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
                String lexema = ""; //elemento a analizar
                char c;

                for (int i = 0; i < codigo.length(); i++) {
                        c = codigo.charAt(i);

                        switch (estado){
                                case 0:
                                        if (Character.isLetter(c)) {
                                                estado = 13;
                                                lexema += c;
                                        } else if (Character.isDigit(c)) {
                                                estado = 15;
                                                lexema += c;
                                        } else if (c=='/'){
                                                estado = 26;
                                                lexema += c;
                                        }
                                        break;
                                case 13:
                                        if (Character.isLetterOrDigit(c)){
                                                estado = 13;
                                                lexema += c;
                                        }else {
                                                TipoToken tt = palabrasReservadas.get(lexema);
                                                Token t;

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

                                        }
                                        else if ( c == 'E') {

                                        } else {

                                                Token t = new Token (TipoToken.NUMBER, lexema, Integer.valueOf(lexema));
                                                tokens.add(t);

                                                estado = 0;
                                                lexema = "";
                                                i--;
                                        }
                                case 26:
                                        // Si se detecta un asterisco, se avanza al estado 27
                                        if(c == '*'){
                                                estado = 27;

                                        }
                                        // Si se detecta otra diagonal, se avanza al estado 30
                                        else if(c == '/'){
                                                estado = 30;
                                        }
                                        // La diagonal fue única e indica división
                                        //Se genera token para '/'
                                        //Se regresa al estado 0 del autómata y se reinicia el lexema
                                        else{
                                                Token t = new Token (TipoToken.SLASH, lexema, null);
                                                tokens.add(t);
                                                i--;
                                                estado = 0;
                                                lexema = "";
                                        }
                                        break;

                                case 27:
                                        //Si se detecta un asterisco, se avanza al estado 28
                                        if(c == '*'){
                                                estado = 28;
                                        }
                    else{
                                        estado = 27;
                                }
                                break;

                                case 28:
                                        //Si se detecta una diagonal, indica que el comentario multilínea llegó a su fin
                                        //Se regresa al estado 0 y se reinicia el lexema
                                        if(c == '/'){
                                                estado = 0;
                                                lexema = "";
                                        }
                    //Si se detecta un asterisco se mantiene en el mismo estado
                    else if(c == '*'){
                                        estado = 28;
                                }
                    //Si se detecta cualquier otro caracter, se regresa al estado 27
                    else{
                                        estado = 27;
                                }
                                break;

                                case 30:
                                        //Si se detecta un salto de línea, se se regresa al estado 0 y se reinicia el lexema
                                        if(c == '\n'){
                                                estado = 0;
                                                lexema = "";
                                          //Si se detecta cualquier otro caracter, se mantiene en el estado 30
                                        } else{
                                                estado = 30;
                                        }
                                        break;
                        }
                }
                return tokens;
        }
}