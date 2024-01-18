import java.util.ArrayList;
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
        List <Statement> statements = PROGRAM();

        if(preanalisis.tipo == TipoToken.EOF && !hayErrores){
            System.out.println("Consulta correcta");
            return  true;
        }else {
            System.out.println("Se encontraron errores");
        }
        return false;
    }

    // PROGRAM -> DECLARATION

    private List<Statement> PROGRAM(){
        List<Statement> statements = new ArrayList<>();
        DECLARATION (statements);
        return  statements;
    }

    /** DECLARACIONES **/

    // DECLARATION -> FUN_DECL DECLARATION
    //             -> VAR_DECL DECLARATION
    //             -> STATEMENT DECLARATION
    //             -> Ɛ
    private void DECLARATION(List<Statement> statements){
        if(hayErrores)
            return ;
        if(TipoToken.FUN == preanalisis.tipo){
            // -> FUN_DECL DECLARATION
            Statement fun = FUN_DECL();
            statements.add(fun);
            DECLARATION(statements);
        } else if (TipoToken.VAR == preanalisis.tipo) {
            // -> VAR_DECL DECLARATION
            Statement var = VAR_DECL();
            statements.add(var);
            DECLARATION(statements);
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
            Statement statement = STATEMENT();
            statements.add(statement);
            DECLARATION(statements);
        }
    }

    //FUN_DECL -> fun FUNCTION
    private Statement FUN_DECL(){
        if(hayErrores)
            return null;
        Statement fun;
        if(TipoToken.FUN == preanalisis.tipo){
            match(TipoToken.FUN);
            fun = FUNCTION();
        } else {
            hayErrores = true;
            System.out.println("Se esperaba un fun");
            return null;
        }
        return fun;
    }

    //VAR_DECL -> var id VAR_INIT ;
    private Statement VAR_DECL(){
        if(hayErrores)
            return null;
        Statement var;
        if (TipoToken.VAR == preanalisis.tipo){
            match(TipoToken.VAR);
            match(TipoToken.IDENTIFIER);
            Token nombreVar = anterior();
            Expression varInit = VAR_INIT();
            match(TipoToken.SEMICOLON);
            var = new StmtVar(nombreVar, varInit);
        }else {
            hayErrores = true;
            System.out.println("Se esperaba un var");
            return null;
        }
        return var;
    }

    //VAR_INIT -> = EXPRESSION | Ɛ
    private Expression VAR_INIT(){
        if(hayErrores)
            return null;

        if (TipoToken.EQUAL == preanalisis.tipo){
            match(TipoToken.EQUAL);
            return EXPRESSION();
        }
        return null;
    }

    /** SENTENCIAS **/

    //STATEMENT -> EXPR_STMT
    //-> FOR_STMT
    //-> IF_STMT
    //-> PRINT_STMT
    //-> RETURN_STMT
    //-> WHILE_STMT
    //-> BLOCK
    private Statement STATEMENT(){
        if(hayErrores)
            return null;

        if(TipoToken.FOR == preanalisis.tipo){
            //-> FOR_STMT
           return FOR_STMT();
        } else if (TipoToken.IF == preanalisis.tipo) {
            //-> IF_STMT
            return IF_STMT();
        } else if (TipoToken.PRINT == preanalisis.tipo) {
            //-> PRINT_STMT
            return PRINT_STMT();
        } else if (TipoToken.RETURN == preanalisis.tipo) {
            //-> RETURN_STMT
            return RETURN_STMT();
        } else if (TipoToken.WHILE == preanalisis.tipo) {
            //-> WHILE_STMT
            return WHILE_STMT();
        } else if (TipoToken.LEFT_BRACE == preanalisis.tipo) {
            //-> BLOCK
            return BLOCK();
        } else {
            //-> EXPR_STMT
            return EXPR_STMT();
        }
    }

    //EXPR_STMT -> EXPRESSION ;
    private Statement EXPR_STMT(){
        if(hayErrores)
            return null;
        Expression expresion = EXPRESSION();
        match(TipoToken.SEMICOLON);
        return new StmtExpression(expresion);
    }

    //FOR_STMT -> for ( FOR_STMT_1 FOR_STMT_2 FOR_STMT_3 ) STATEMENT
    private Statement FOR_STMT(){
        if(hayErrores)
            return null;

        if (TipoToken.FOR == preanalisis.tipo) {
            match(TipoToken.FOR);
            match(TipoToken.LEFT_PAREN);
            Statement init = FOR_STMT_1();
            Expression condicion = FOR_STMT_2();
            Expression incremento = FOR_STMT_3();
            match(TipoToken.RIGHT_PAREN);
            Statement sentencias = STATEMENT();
            //aumentar cada vez que termine de ejecutar los statement(agregar una expresion de incremento)
            if (incremento != null) {
                List<Statement> auxiliar = new ArrayList<>();
                auxiliar.add(sentencias);
                auxiliar.add(new StmtExpression(incremento));
                sentencias = new StmtBlock(auxiliar);
            }
            //existe condicion ?
            if (condicion == null)
                condicion = new ExprLiteral(true);
            sentencias = new StmtLoop(condicion, sentencias);
            //inicializar con una var
            if (init  != null){
                List<Statement> auxiliar = new ArrayList<>();
                auxiliar.add(init);
                auxiliar.add(sentencias);
                sentencias = new StmtBlock(auxiliar);
            }
            return sentencias;
        } else {
            hayErrores = true;
            System.out.println("Se esperaba un for");
            return null;
        }
    }

    //FOR_STMT_1 -> VAR_DECL
    //           -> EXPR_STMT
    //           -> ;
    private Statement FOR_STMT_1(){
        if(hayErrores)
            return null;

        if(TipoToken.VAR == preanalisis.tipo){
            //-> VAR_DECL
            return VAR_DECL();
        } else if (TipoToken.SEMICOLON == preanalisis.tipo) {
            //-> ;
            match(TipoToken.SEMICOLON);
            return null;
        } else {
            //-> EXPR_STMT
            return EXPR_STMT();
        }
    }

    //FOR_STMT_2 -> EXPRESSION;
    //           -> ;
    private Expression FOR_STMT_2(){
        if(hayErrores)
            return null;

        if(TipoToken.SEMICOLON == preanalisis.tipo){
            //-> ;
            match(TipoToken.SEMICOLON);
            return null;
        } else {
            //->EXPRESSION;
            Expression expresion = EXPRESSION();
            match(TipoToken.SEMICOLON);
            return expresion;
        }
    }

    //FOR_STMT_3 -> EXPRESSION
    //           -> Ɛ
    private Expression FOR_STMT_3(){
        if(hayErrores)
            return null;

        if (TipoToken.BANG == preanalisis.tipo ||
                TipoToken.MINUS == preanalisis.tipo ||
                TipoToken.TRUE == preanalisis.tipo ||
                TipoToken.FALSE == preanalisis.tipo ||
                TipoToken.NULL == preanalisis.tipo ||
                TipoToken.NUMBER == preanalisis.tipo ||
                TipoToken.STRING == preanalisis.tipo ||
                TipoToken.IDENTIFIER == preanalisis.tipo ||
                TipoToken.LEFT_PAREN == preanalisis.tipo){
            return EXPRESSION();
        }
        return null;
    }

    //IF_STMT -> if (EXPRESSION) STATEMENT ELSE_STATEMENT
    private Statement IF_STMT(){
        if(hayErrores)
            return null;

        if (TipoToken.IF == preanalisis.tipo){
            match(TipoToken.IF);
            match(TipoToken.LEFT_PAREN);
            Expression condicion = EXPRESSION();
            match(TipoToken.RIGHT_PAREN);
            Statement sentencias = STATEMENT();
            Statement sino = ELSE_STATEMENT();
            return new StmtIf(condicion, sentencias, sino);
        }else {
            hayErrores = true;
            System.out.println("Se esperaba un if");
            return null;
        }
    }

    // ELSE_STATEMENT -> else STATEMENT
    //                -> Ɛ
    private Statement ELSE_STATEMENT(){
        if(hayErrores)
            return null;

        if (TipoToken.ELSE == preanalisis.tipo){
            match(TipoToken.ELSE);
            return STATEMENT();
        }
        return null;
    }

    //PRINT_STMT -> print EXPRESSION ;
    private Statement PRINT_STMT(){
        if(hayErrores)
            return null;

        if (TipoToken.PRINT == preanalisis.tipo){
            match(TipoToken.PRINT);
            Expression expresion = EXPRESSION();
            match(TipoToken.SEMICOLON);
            return new StmtPrint(expresion);
        }else {
            hayErrores = true;
            System.out.println("Se esperaba un print");
            return null;
        }
    }

    //RETURN_STMT -> return RETURN_EXP_OPC ;
    private Statement RETURN_STMT(){
        if(hayErrores)
            return null;

        if (TipoToken.RETURN == preanalisis.tipo){
            match(TipoToken.RETURN);
            Expression expresion = RETURN_EXP_OPC();
            match(TipoToken.SEMICOLON);
            return new StmtReturn(expresion);
        }else {
            hayErrores = true;
            System.out.println("Se esperaba un return");
            return null;
        }
    }

    //RETURN_EXP_OPC -> EXPRESSION
    //               -> Ɛ
    private Expression RETURN_EXP_OPC(){
        if(hayErrores)
            return null;
        if (TipoToken.BANG == preanalisis.tipo ||
                TipoToken.MINUS == preanalisis.tipo ||
                TipoToken.TRUE == preanalisis.tipo ||
                TipoToken.FALSE == preanalisis.tipo ||
                TipoToken.NULL == preanalisis.tipo ||
                TipoToken.NUMBER == preanalisis.tipo ||
                TipoToken.STRING == preanalisis.tipo ||
                TipoToken.IDENTIFIER == preanalisis.tipo ||
                TipoToken.LEFT_PAREN == preanalisis.tipo){
            return EXPRESSION();
        }
         return null;
    }

    //WHILE_STMT -> while ( EXPRESSION ) STATEMENT
    private Statement WHILE_STMT(){
        if(hayErrores)
            return null;

        if (TipoToken.WHILE == preanalisis.tipo){
            match(TipoToken.WHILE);
            match(TipoToken.LEFT_PAREN);
            Expression expresion = EXPRESSION();
            match(TipoToken.RIGHT_PAREN);
            Statement sentencias = STATEMENT();
            return new StmtLoop(expresion, sentencias);
        }else {
            hayErrores = true;
            System.out.println("Se esperaba un while");
            return null;
        }
    }

    //BLOCK -> { DECLARATION }
    private StmtBlock BLOCK() {
        if (hayErrores)
            return null;

        if (TipoToken.LEFT_BRACE == preanalisis.tipo) {
            match(TipoToken.LEFT_BRACE);
            List<Statement> sentencias = new ArrayList<>();
            DECLARATION(sentencias);
            match(TipoToken.RIGHT_BRACE);
            return new StmtBlock(sentencias);
        } else {
            hayErrores = true;
            System.out.println("Se esperaba un {");
            return null;
        }
    }

    /**EXPRESIONES*/

    //EXPRESSION -> ASSIGNMENT
    private Expression EXPRESSION(){
        if (hayErrores)
            return null;

        return ASSIGNMENT();
    }

    //ASSIGNMENT -> LOGIC_OR ASSIGNMENT_OPC
    private Expression ASSIGNMENT(){
        if (hayErrores)
            return null;

        Expression expresion = LOGIC_OR();
        if(this.preanalisis.getTipo() == TipoToken.EQUAL){
            return ASSIGNMENT_OPC();
        }
        return expresion;
    }

    //ASSIGNMENT_OPC -> = EXPRESSION
    //               -> Ɛ
    private Expression ASSIGNMENT_OPC(){
        if (hayErrores)
            return null;

        if(this.preanalisis.getTipo() == TipoToken.EQUAL){
            Token nombre = anterior();
            match(TipoToken.EQUAL);
            Expression valor = EXPRESSION();
            return new ExprAssign(nombre, valor);
        }
        return null;
    }

    //LOGIC_OR -> LOGIC_AND LOGIC_OR_2
    private Expression LOGIC_OR(){
        if (hayErrores)
            return null;

        Expression expresion = LOGIC_AND();
        if(this.preanalisis.getTipo() == TipoToken.OR){
            return LOGIC_OR_2(expresion);
        }
        return expresion;
    }

    //LOGIC_OR_2 -> or LOGIC_AND LOGIC_OR_2
    //           -> Ɛ
    private Expression LOGIC_OR_2(Expression expresion){
        if (hayErrores)
            return null;

        if(this.preanalisis.getTipo() == TipoToken.OR){
            match(TipoToken.OR);
            Token operador = anterior();
            Expression expresionSig = LOGIC_AND();
            Expression expressionCompleta = new ExprLogical(expresion, operador, expresionSig);
            return LOGIC_OR_2(expressionCompleta);
        }
        return expresion;
    }

    //LOGIC_AND -> EQUALITY LOGIC_AND_2
    private Expression LOGIC_AND(){
        if (hayErrores)
            return null;

        Expression expresion = EQUALITY();
        if(this.preanalisis.getTipo() == TipoToken.AND){
            return LOGIC_AND_2(expresion);
        }
        return expresion;
    }

    //LOGIC_AND_2 -> and EQUALITY LOGIC_AND_2
    //            -> Ɛ
    private Expression LOGIC_AND_2( Expression expresion) {
        if (hayErrores)
            return null;

        if(this.preanalisis.getTipo() == TipoToken.AND){
            match(TipoToken.AND);
            Token operador = anterior();
            Expression expresionSig = EQUALITY();
            Expression expressionCompleta = new ExprLogical(expresion, operador, expresionSig);
            return LOGIC_AND_2(expressionCompleta);
        }
        return expresion;
    }

    //EQUALITY -> COMPARISON EQUALITY_2
    private Expression EQUALITY(){
        if (hayErrores)
            return null;

        Expression expresion = COMPARISON();
        if (this.preanalisis.getTipo() == TipoToken.BANG_EQUAL || this.preanalisis.getTipo() == TipoToken.EQUAL )
            return EQUALITY_2(expresion);
        return expresion;
    }

    //EQUALITY_2 -> != COMPARISON EQUALITY_2
    //           -> == COMPARISON EQUALITY_2
    //           -> Ɛ
    private Expression EQUALITY_2(Expression expresion){
        if (hayErrores)
            return null;
        Token operador;
        Expression expresionSig;
        Expression expresionCompleta;
        switch (this.preanalisis.getTipo()){
            case BANG_EQUAL:
                match(TipoToken.BANG_EQUAL);
                operador = anterior();
                expresionSig = COMPARISON();
                expresionCompleta = new ExprBinary(expresion, operador, expresionSig );
                return EQUALITY_2(expresionCompleta);
            case EQUAL_EQUAL:
                match(TipoToken.EQUAL_EQUAL);
                operador = anterior();
                expresionSig = COMPARISON();
                expresionCompleta = new ExprBinary(expresion, operador, expresionSig );
                return EQUALITY_2(expresionCompleta);
            default:
                return expresion;
        }
    }

    //COMPARISON -> TERM COMPARISON_2
    private Expression COMPARISON(){
        if (hayErrores)
            return null;
        Expression expresion = TERM();
        if (this.preanalisis.getTipo() == TipoToken.LESS || this.preanalisis.getTipo() == TipoToken.LESS_EQUAL ||
                this.preanalisis.getTipo() == TipoToken.GREATER || this.preanalisis.getTipo() == TipoToken.GREATER_EQUAL)
            return COMPARISON_2(expresion);
        return  expresion;
    }

    //COMPARISON_2 -> > TERM COMPARISON_2
    //             -> >= TERM COMPARISON_2
    //             -> < TERM COMPARISON_2
    //             -> <= TERM COMPARISON_2
    //             -> Ɛ
    private Expression COMPARISON_2(Expression expresion){
        if (hayErrores)
            return null;

        switch (this.preanalisis.getTipo()){
            case GREATER:
                match(TipoToken.GREATER);
                break;

            case GREATER_EQUAL:
                match(TipoToken.GREATER_EQUAL);
                break;

            case LESS:
                match(TipoToken.LESS);
                break;

            case LESS_EQUAL:
                match(TipoToken.LESS_EQUAL);
                break;

            default:
                return expresion;
        }
        Token operador = anterior();
        Expression expresionSiguiente = TERM();
        Expression expresionCompleta = new ExprBinary(expresion, operador, expresionSiguiente);
        return COMPARISON_2(expresionCompleta);
    }

    //TERM -> FACTOR TERM_2
    private Expression TERM(){
        if (hayErrores)
            return null;

        Expression expresion = FACTOR();
        expresion = TERM_2(expresion);
        return expresion;
    }

    //TERM_2 -> - FACTOR TERM_2
    //       -> + FACTOR TERM_2
    //       -> Ɛ
    private Expression TERM_2(Expression expresion){
        if (hayErrores)
            return null;

        switch (this.preanalisis.getTipo()){
            case MINUS:
                match(TipoToken.MINUS);
                break;

            case PLUS:
                match(TipoToken.PLUS);
                break;

            default:
                return expresion;
        }
        Token operador = anterior();
        Expression expresionSiguiente = FACTOR();
        Expression expresionCompleta = new ExprBinary(expresion, operador, expresionSiguiente);
        return TERM_2(expresionCompleta);
    }

    //FACTOR -> UNARY FACTOR_2
    private Expression FACTOR() {
        if (hayErrores)
            return null;

        Expression expresion = UNARY();
        expresion = FACTOR_2(expresion);
        return expresion;
    }

    //FACTOR_2 -> / UNARY FACTOR_2
    //         -> * UNARY FACTOR_2
    //         -> Ɛ
    private Expression FACTOR_2(Expression expresion){
        if (hayErrores)
            return null;

        switch(this.preanalisis.getTipo()){
            case SLASH:
                match(TipoToken.SLASH);
                break;
            case STAR:
                match(TipoToken.STAR);
                break;
            default:
                return expresion;
        }
        Token operador = anterior();
        Expression expresionSiguiente = UNARY();
        Expression expresionCompleta = new ExprBinary(expresion, operador, expresionSiguiente);
        return FACTOR_2(expresionCompleta);
    }

    //UNARY -> ! UNARY
    //      -> - UNARY
    //      -> CALL
    private Expression UNARY(){
        if (hayErrores)
            return null;
        Token operador;
        Expression expresion ;
        switch(this.preanalisis.getTipo()){
            case BANG:
                match(TipoToken.BANG);
                operador = anterior();
                expresion = UNARY();
                return new ExprUnary(operador, expresion);

            case MINUS:
                match(TipoToken.MINUS);
                operador = anterior();
                expresion = UNARY();
                return new ExprUnary(operador, expresion);

            case TRUE,FALSE,NULL,NUMBER,STRING,IDENTIFIER,LEFT_PAREN:
                return CALL();

            default:
                this.hayErrores = true;
                return null;
        }
    }

    //CALL -> PRIMARY CALL_2
    private Expression CALL(){
        if (hayErrores)
            return null;

        Expression expresion = PRIMARY();
        expresion = CALL_2(expresion);
        return expresion;
    }

    //CALL_2 -> ( ARGUMENTS_OPC ) CALL_2
    //       -> Ɛ
    private Expression CALL_2(Expression expresion){
        if (hayErrores)
            return null;

        if (preanalisis.tipo == TipoToken.LEFT_PAREN){
            match(TipoToken.LEFT_PAREN);
            List<Expression> argumentos = ARGUMENTS_OPC();
            match(TipoToken.RIGHT_PAREN);
            //CALL_2();
            return new ExprCallFunction(expresion, argumentos);
        } return expresion;
    }

    //PRIMARY -> true
    //        -> false
    //        -> null
    //        -> number
    //        -> string
    //        -> id
    //        -> ( EXPRESSION )
    private Expression PRIMARY() {
        if (hayErrores)
            return null;
        switch (this.preanalisis.getTipo()) {
            case TRUE:
                match(TipoToken.TRUE);
                return new ExprLiteral(true);

            case FALSE:
                match(TipoToken.FALSE);
                return new ExprLiteral(false);

            case NULL:
                match(TipoToken.NULL);
                return new ExprLiteral(null);

            case NUMBER:
                match(TipoToken.NUMBER);
                Token numero = anterior();
                return new ExprLiteral(numero.getLiteral());

            case STRING:
                match(TipoToken.STRING);
                Token cadena = anterior();
                return new ExprLiteral(cadena.getLiteral());

            case IDENTIFIER:
                match(TipoToken.IDENTIFIER);
                Token identificador = anterior();
                return new ExprVariable(identificador);

            case LEFT_PAREN:
                match(TipoToken.LEFT_PAREN);
                Expression expresion = EXPRESSION();
                match(TipoToken.RIGHT_PAREN);
                return new ExprGrouping(expresion);

            default:
                this.hayErrores = true;
                return null;
        }
    }


    /**OTRAS**/
    // FUNCTION -> id ( PARAMETERS_OPC ) BLOCK
    private Statement FUNCTION() {
        if (hayErrores)
            return null;

        // FUNCTION -> id ( PARAMETERS_OPC ) BLOCK
        if (TipoToken.IDENTIFIER == preanalisis.tipo) {
            match(TipoToken.IDENTIFIER);
            Token identificador = anterior();
            match(TipoToken.LEFT_PAREN);
            List<Token> parametros = PARAMETERS_OPC();
            match(TipoToken.RIGHT_PAREN);
            StmtBlock bloque = BLOCK();
            return new StmtFunction(identificador, parametros, bloque);
        } else {
            hayErrores = true;
            System.out.println("Se esperaba id");
            return null;
        }
    }

    // FUNCTIONS -> FUN_DECL FUNCTIONS | Ɛ
    private void FUNCTIONS() {
        if (hayErrores)
            return;

        while (TipoToken.FUN == preanalisis.tipo) {
            FUN_DECL();
            FUNCTIONS();
        }
    }

    // PARAMETERS_OPC -> PARAMETERS | Ɛ
    private List<Token> PARAMETERS_OPC() {
        if (hayErrores)
            return null;

        if (TipoToken.IDENTIFIER  == preanalisis.tipo) {
            List <Token> parametros = new ArrayList<>();
            parametros = PARAMETERS(parametros);
            return parametros;
        }
        return null;
    }

    // PARAMETERS -> id PARAMETERS_2
    private List<Token> PARAMETERS(List<Token> parametros) {
        if (hayErrores) return null;

        // PARAMETERS -> id PARAMETERS_2
        if (TipoToken.IDENTIFIER  == preanalisis.tipo) {
            match(TipoToken.IDENTIFIER);
            Token identificador = anterior();
            parametros.add(identificador);
            parametros = PARAMETERS_2(parametros);
            return parametros;
        } else {
            hayErrores = true;
            System.out.println("Se esperaba id");
            return null;
        }
    }

    // PARAMETERS_2 -> , id PARAMETERS_2 | Ɛ
    private List<Token> PARAMETERS_2(List <Token> parametros) {
        if (hayErrores)
            return null;
        if (TipoToken.COMMA == preanalisis.tipo) {
            match(TipoToken.COMMA);
            match(TipoToken.IDENTIFIER);
            Token identificador = anterior();
            parametros.add(identificador);
            parametros = PARAMETERS_2(parametros);
        } return parametros;
    }

    // ARGUMENTS_OPC -> EXPRESSION ARGUMENTS | Ɛ
    private List<Expression> ARGUMENTS_OPC() {
        if (hayErrores)
            return null;

        if (TipoToken.BANG == preanalisis.tipo ||
            TipoToken.MINUS == preanalisis.tipo ||
            TipoToken.TRUE == preanalisis.tipo ||
            TipoToken.FALSE == preanalisis.tipo ||
            TipoToken.NULL == preanalisis.tipo ||
            TipoToken.NUMBER == preanalisis.tipo ||
            TipoToken.STRING == preanalisis.tipo ||
            TipoToken.IDENTIFIER == preanalisis.tipo ||
            TipoToken.LEFT_PAREN == preanalisis.tipo){
            List<Expression> argumentos =  new ArrayList<Expression>();
            argumentos.add(EXPRESSION());
            argumentos = ARGUMENTS(argumentos);
            return argumentos;
        } return null;
    }

    // ARGUMENTS -> , EXPRESSION ARGUMENTS | Ɛ
    private List<Expression> ARGUMENTS(List<Expression> argumentos) {
        if (hayErrores)
            return null;

        if (preanalisis.tipo == TipoToken.COMMA) {
            match(TipoToken.COMMA);
            argumentos.add(EXPRESSION());
            ARGUMENTS(argumentos);
        }
        return argumentos;
    }

    //Comparar tipo de token
    private void match(TipoToken tt){
        if (hayErrores)
            return;

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
    private Token anterior(){
        return this.tokens.get(i-1);
    }
}
