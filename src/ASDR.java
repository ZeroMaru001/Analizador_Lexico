import java.util.List;
public class ASDR implements Parser {
    private int i = 0;
    private boolean hayErrores = false;
    private Token preanalisis;
    private final List<Token> tokens;


    public ASDR(List<Token> tokens){
        this.tokens = tokens;
        preanalisis = this.tokens.get(i);
    }

    @Override
    public boolean parse() {
        PROGRAM();

        if(preanalisis.tipo == TipoToken.EOF && !hayErrores){
            System.out.println("Consulta correcta");
            return  true;
        }else {
            System.out.println("Se encontraron errores");
        }
        return false;
    }

    // PROGRAM -> DECLARATION

    private void PROGRAM(){
        DECLARATION();
    }

    /** DECLARACIONES **/

    // DECLARATION -> FUN_DECL DECLARATION
    //             -> VAR_DECL DECLARATION
    //             -> STATEMENT DECLARATION
    //             -> Ɛ
    private void DECLARATION(){
        if(hayErrores)
            return;

        if(TipoToken.FUN == preanalisis.tipo){
            // -> FUN_DECL DECLARATION
            FUN_DECL();
            DECLARATION();
        } else if (TipoToken.VAR == preanalisis.tipo) {
            // -> VAR_DECL DECLARATION
            VAR_DECL();
            DECLARATION();
        } else if(TipoToken.BANG == preanalisis.tipo ||
                  TipoToken.MINUS == preanalisis.tipo ||
                  TipoToken.TRUE == preanalisis.tipo ||
                  TipoToken.FALSE == preanalisis.tipo ||
                  TipoToken.NULL == preanalisis.tipo ||
                  TipoToken.NUMBER == preanalisis.tipo ||
                  TipoToken.STRING == preanalisis.tipo ||
                  TipoToken.IDENTIFIER == preanalisis.tipo ||
                  TipoToken.LEFT_PAREN == preanalisis.tipo ||
                  TipoToken.FOR == preanalisis.tipo ||
                  TipoToken.IF == preanalisis.tipo ||
                  TipoToken.PRINT == preanalisis.tipo ||
                  TipoToken.RETURN == preanalisis.tipo ||
                  TipoToken.WHILE == preanalisis.tipo ||
                  TipoToken.LEFT_BRACE == preanalisis.tipo ) {
            // -> STATEMENT DECLARATION
            STATEMENT();
            DECLARATION();
        }
    }

    //FUN_DECL -> fun FUNCTION
    private void FUN_DECL(){
        if(hayErrores)
            return;

        if(TipoToken.FUN == preanalisis.tipo){
            match(TipoToken.FUN);
            //FUNCTION(); para alberto
        } else {
            hayErrores = true;
            System.out.println("Se esperaba un fun");
        }
    }

    //VAR_DECL -> var id VAR_INIT ;
    private void VAR_DECL(){
        if(hayErrores)
            return;

        if (TipoToken.VAR == preanalisis.tipo){
            match(TipoToken.VAR);
            match(TipoToken.IDENTIFIER);
            VAR_INIT();
            match(TipoToken.SEMICOLON);
        }else {
            hayErrores = true;
            System.out.println("Se esperaba un var");
        }
    }

    //VAR_INIT -> = EXPRESSION | Ɛ
    private void VAR_INIT(){
        if(hayErrores)
            return;

        if (TipoToken.EQUAL == preanalisis.tipo){
            match(TipoToken.EQUAL);
            //EXPRESSION(); para el gus
        }

    }

    /** SENTENCIAS **/

    //STATEMENT -> EXPR_STMT
    //-> FOR_STMT
    //-> IF_STMT
    //-> PRINT_STMT
    //-> RETURN_STMT
    //-> WHILE_STMT
    //-> BLOCK
    private void STATEMENT(){
        if(hayErrores)
            return;

        if(TipoToken.FOR == preanalisis.tipo){
            //-> FOR_STMT
            FOR_STMT();
        } else if (TipoToken.IF == preanalisis.tipo) {
            //-> IF_STMT
            IF_STMT();
        } else if (TipoToken.PRINT == preanalisis.tipo) {
            //-> PRINT_STMT
            PRINT_STMT();
        } else if (TipoToken.RETURN == preanalisis.tipo) {
            //-> RETURN_STMT
            RETURN_STMT();
        } else if (TipoToken.WHILE == preanalisis.tipo) {
            //-> WHILE_STMT
            WHILE_STMT();
        } else if (TipoToken.LEFT_BRACE == preanalisis.tipo) {
            //-> BLOCK
            BLOCK();
        } else {
            //-> EXPR_STMT
            EXPR_STMT();
        }
    }

    //EXPR_STMT -> EXPRESSION ;
    private void EXPR_STMT(){
        if(hayErrores)
            return;
       // EXPRESION();  para el gus
        match(TipoToken.SEMICOLON);
    }

    //FOR_STMT -> for ( FOR_STMT_1 FOR_STMT_2 FOR_STMT_3 ) STATEMENT
    private void FOR_STMT(){
        if(hayErrores)
            return;

        if (TipoToken.FOR == preanalisis.tipo){
            match(TipoToken.FOR);
            match(TipoToken.LEFT_PAREN);
            FOR_STMT_1();
            FOR_STMT_2();
            FOR_STMT_3();
            match(TipoToken.RIGHT_PAREN);
            STATEMENT();
        } else {
            hayErrores = true;
            System.out.println("Se esperaba un for");
        }
    }

    //FOR_STMT_1 -> VAR_DECL
    //           -> EXPR_STMT
    //           -> ;
    private void FOR_STMT_1(){
        if(hayErrores)
            return;

        if(TipoToken.VAR == preanalisis.tipo){
            //-> VAR_DECL
            VAR_DECL();
        } else if (TipoToken.SEMICOLON == preanalisis.tipo) {
            //-> ;
            match(TipoToken.SEMICOLON);
        } else {
            //-> EXPR_STMT
            EXPR_STMT();
        }
    }

    //FOR_STMT_2 -> EXPRESSION;
    //           -> ;
    private void FOR_STMT_2(){
        if(hayErrores)
            return;

        if(TipoToken.SEMICOLON == preanalisis.tipo){
            //-> ;
            match(TipoToken.SEMICOLON);
        } else {
            //->EXPRESSION;
            //EXPRESSION(); para el gus
            match(TipoToken.SEMICOLON);
        }
    }

    //FOR_STMT_3 -> EXPRESSION
    //           -> Ɛ
    private void FOR_STMT_3(){
        if(hayErrores)
            return;

        if (TipoToken.BANG == preanalisis.tipo ||
                TipoToken.MINUS == preanalisis.tipo ||
                TipoToken.TRUE == preanalisis.tipo ||
                TipoToken.FALSE == preanalisis.tipo ||
                TipoToken.NULL == preanalisis.tipo ||
                TipoToken.NUMBER == preanalisis.tipo ||
                TipoToken.STRING == preanalisis.tipo ||
                TipoToken.IDENTIFIER == preanalisis.tipo ||
                TipoToken.LEFT_PAREN == preanalisis.tipo){
            //EXPRESSION();para  el gus
        }

    }

    //IF_STMT -> if (EXPRESSION) STATEMENT ELSE_STATEMENT
    private void IF_STMT(){
        if(hayErrores)
            return;

        if (TipoToken.IF == preanalisis.tipo){
            match(TipoToken.IF);
            match(TipoToken.LEFT_PAREN);
            //EXPRESSION(); para el gus
            match(TipoToken.RIGHT_PAREN);
            STATEMENT();
            ELSE_STATEMENT();
        }else {
            hayErrores = true;
            System.out.println("Se esperaba un if");
        }
    }

    // ELSE_STATEMENT -> else STATEMENT
    //                -> Ɛ
    private void ELSE_STATEMENT(){
        if(hayErrores)
            return;

        if (TipoToken.ELSE == preanalisis.tipo){
            match(TipoToken.ELSE);
            STATEMENT();
        }
    }

    //PRINT_STMT -> print EXPRESSION ;
    private void PRINT_STMT(){
        if(hayErrores)
            return;

        if (TipoToken.PRINT == preanalisis.tipo){
            match(TipoToken.PRINT);
            //EXPRESSION(); para el gus
            match(TipoToken.SEMICOLON);
        }else {
            hayErrores = true;
            System.out.println("Se esperaba un print");
        }
    }

    //RETURN_STMT -> return RETURN_EXP_OPC ;
    private void RETURN_STMT(){
        if(hayErrores)
            return;

        if (TipoToken.RETURN == preanalisis.tipo){
            match(TipoToken.RETURN);
            RETURN_EXP_OPC();
            match(TipoToken.SEMICOLON);
        }else {
            hayErrores = true;
            System.out.println("Se esperaba un return");
        }
    }

    //RETURN_EXP_OPC -> EXPRESSION
    //               -> Ɛ
    private void RETURN_EXP_OPC(){
        if(hayErrores)
            return;
        if (TipoToken.BANG == preanalisis.tipo ||
                TipoToken.MINUS == preanalisis.tipo ||
                TipoToken.TRUE == preanalisis.tipo ||
                TipoToken.FALSE == preanalisis.tipo ||
                TipoToken.NULL == preanalisis.tipo ||
                TipoToken.NUMBER == preanalisis.tipo ||
                TipoToken.STRING == preanalisis.tipo ||
                TipoToken.IDENTIFIER == preanalisis.tipo ||
                TipoToken.LEFT_PAREN == preanalisis.tipo){
            //EXPRESSION();para  el gus
        }
    }

    //WHILE_STMT -> while ( EXPRESSION ) STATEMENT
    private void WHILE_STMT(){
        if(hayErrores)
            return;

        if (TipoToken.WHILE == preanalisis.tipo){
            match(TipoToken.WHILE);
            match(TipoToken.LEFT_PAREN);
            //EXPRESSION(); para el gus
            match(TipoToken.RIGHT_PAREN);
            STATEMENT();
        }else {
            hayErrores = true;
            System.out.println("Se esperaba un while");
        }
    }

    //BLOCK -> { DECLARATION }
    private void BLOCK(){
        if(hayErrores)
            return;

        if (TipoToken.LEFT_BRACE == preanalisis.tipo){
            match(TipoToken.LEFT_BRACE);
            DECLARATION();
            match(TipoToken.RIGHT_BRACE);
        }else {
            hayErrores = true;
            System.out.println("Se esperaba un {");
        }
    }

    /**EXPRESIONES*/

    //Comparar tipo de token
    private void match(TipoToken tt){
        if(preanalisis.tipo == tt){
            i++;
            preanalisis = tokens.get(i);
        }
        else{
            hayErrores = true;
            System.out.println("Error encontrado:");
            System.out.println("Se puso:" + preanalisis.tipo);
            System.out.println("Se esperaba:" + tt);
        }

    }
}
