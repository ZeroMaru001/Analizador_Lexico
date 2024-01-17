import java.util.ArrayList;
import java.util.List;

public class ASDR implements Parser{
    private int i = 0;
    private boolean hayErrores = false;
    private Token preanalisis;
    private final List<Token> tokens;

    public ASDR(List<Token> tokens) {
        this.tokens = tokens;
        if(!this.tokens.isEmpty())
            preanalisis = this.tokens.get(i);
    }

    @Override
    public boolean parse(){
        if(this.tokens.isEmpty()){
            System.out.println("Consulta correcta");
            return true;
        }
        List<Statement> stats = PROGRAM();
        System.out.println(stats);
        if((i == tokens.size()) && !hayErrores){
            System.out.println("Consulta correcta");
            return true;
        }else System.out.println("Se encontraron errores");
        return false;
    }

    private List<Statement> PROGRAM(){
        List<Statement> statements = new ArrayList<>();
        return DECLARATION(statements);
    }

    private List<Statement> DECLARATION(List<Statement> statements){
        //System.out.println("DECLARATION");
        if(hayErrores) return null;
        if(preanalisis.tipo == TipoToken.FUN){
            Statement fun = FUN_DECL();
            statements.add(fun);
            statements = DECLARATION(statements);
        }else if(preanalisis.tipo == TipoToken.VAR){
            Statement var = VAR_DECL();
            statements.add(var);
            statements = DECLARATION(statements);
        }else if(primeroSTATEMENT(preanalisis.tipo)){
            Statement stmt = STATEMENT();
            statements.add(stmt);
            statements = DECLARATION(statements);
        } return statements;
    }

    private Statement FUN_DECL(){
        //System.out.println("FUN_DECL");
        if(hayErrores) return null;
        match(TipoToken.FUN);
        Statement fun = FUNCTION();
        return fun;
    }

    private Statement VAR_DECL(){
        //System.out.println("VAR_DECL");
        if(hayErrores) return null;
        match(TipoToken.VAR);
        match(TipoToken.IDENTIFIER);
        Token name = previous();
        Expression init = VAR_INIT();
        match(TipoToken.SEMICOLON);
        Statement var = new StmtVar(name, init);
        return var;
    }

    private Expression VAR_INIT(){
        //System.out.println("VAR_INIT");
        if(hayErrores) return null;
        if(preanalisis.tipo == TipoToken.EQUAL){
            match(TipoToken.EQUAL);
            Expression initializer = EXPRESSION();
            return initializer;
        }return null;
    }

    private Statement STATEMENT(){
        //System.out.println("STATEMENT");
        if(hayErrores) return null;
        if(preanalisis.tipo == TipoToken.FOR){
            return FOR_STMT();
        }else if(preanalisis.tipo == TipoToken.IF){
            return IF_STMT();
        }else if(preanalisis.tipo == TipoToken.PRINT){
            return PRINT_STMT();
        }else if(preanalisis.tipo == TipoToken.RETURN){
            return RETURN_STMT();
        }else if(preanalisis.tipo == TipoToken.WHILE){
            return WHILE_STMT();
        }else if(preanalisis.tipo == TipoToken.LEFT_BRACE){
            return BLOCK();
        }else if(primeroEXPR_STMT(preanalisis.tipo)){
            return EXPR_STMT();
        }else {
            hayErrores = true;
            return null;
        }
    }

    private Statement EXPR_STMT(){
        //System.out.println("EXPR_STMT");
        if(hayErrores) return null;
        Expression expression = EXPRESSION();
        match(TipoToken.SEMICOLON);
        return new StmtExpression(expression);
    }

    private Statement FOR_STMT(){
        //System.out.println("FOR_STMT");
        if(hayErrores) return null;
        match(TipoToken.FOR);
        match(TipoToken.LEFT_PAREN);
        Statement initializer = FOR_STMT_1();
        Expression condition = FOR_STMT_2();
        Expression increment = FOR_STMT_3();
        match(TipoToken.RIGHT_PAREN);
        Statement body = STATEMENT();
        if (increment != null) {
            List<Statement> aux = new ArrayList<>();
            aux.add(body);
            aux.add(new StmtExpression(increment));
            body = new StmtBlock(aux);
        }if(condition == null) condition = new ExprLiteral(true);
        body = new StmtLoop(condition, body);
        if(initializer != null){
            List<Statement> aux = new ArrayList<>();
            aux.add(initializer);
            aux.add(body);
            body = new StmtBlock(aux);
        } return body;
    }

    private Statement FOR_STMT_1(){
        //System.out.println("FOR_STMT_1");
        if(hayErrores) return null;
        if(preanalisis.tipo == TipoToken.VAR){
            return VAR_DECL();
        }else if(preanalisis.tipo == TipoToken.SEMICOLON){
            match(TipoToken.SEMICOLON);
            return null;
        }else if(primeroEXPR_STMT(preanalisis.tipo)){
            return EXPR_STMT();
        }else hayErrores = true;
        return null;
    }

    private Expression FOR_STMT_2(){
        //System.out.println("FOR_STMT_2");
        if(hayErrores) return null;
        if(preanalisis.tipo == TipoToken.SEMICOLON){
            match(TipoToken.SEMICOLON);
            return null;
        }else if(primeroEXPR_STMT(preanalisis.tipo)){
            Expression expr = EXPRESSION();
            match(TipoToken.SEMICOLON);
            return expr;
        }else hayErrores = true;
        return null;
    }

    private Expression FOR_STMT_3(){
        //System.out.println("FOR_STMT_3");
        if(hayErrores) return null;
        if(primeroEXPR_STMT(preanalisis.tipo)) return EXPRESSION();
        return null;
    }

    private Statement IF_STMT(){
        //System.out.println("if_STMT");
        if(hayErrores) return null;
        match(TipoToken.IF);
        match(TipoToken.LEFT_PAREN);
        Expression condition = EXPRESSION();
        match(TipoToken.RIGHT_PAREN);
        Statement thenS = STATEMENT();
        Statement elseS = ELSE_STATEMENT();
        return new StmtIf(condition, thenS, elseS);
    }

    private Statement ELSE_STATEMENT(){
        //System.out.println("ELSE_STATEMENT");
        if(hayErrores) return null;
        if(preanalisis.tipo == TipoToken.ELSE){
            match(TipoToken.ELSE);
            return STATEMENT();
        }return null;
    }

    private Statement PRINT_STMT(){
        //System.out.println("PRINT_STMT");
        if(hayErrores) return null;
        match(TipoToken.PRINT);
        Expression expr = EXPRESSION();
        match(TipoToken.SEMICOLON);
        return new StmtPrint(expr);
    }

    private Statement RETURN_STMT(){
        //System.out.println("RETURN_STMT");
        if(hayErrores) return null;
        match(TipoToken.RETURN);
        Expression expr = RETURN_EXP_OPC();
        match(TipoToken.SEMICOLON);
        return new StmtReturn(expr);
    }

    private Expression RETURN_EXP_OPC(){
        //System.out.println("RETURN_EXP_OPC");
        if(hayErrores) return null;
        if(primeroEXPR_STMT(preanalisis.tipo)){
            return EXPRESSION();
        }return null;
    }

    private Statement WHILE_STMT(){
        //System.out.println("WHILE_STMT");
        if(hayErrores) return null;
        match(TipoToken.WHILE);
        match(TipoToken.LEFT_PAREN);
        Expression expr = EXPRESSION();
        match(TipoToken.RIGHT_PAREN);
        Statement stmt = STATEMENT();
        return new StmtLoop(expr, stmt);
    }

    private StmtBlock BLOCK(){
        //System.out.println("BLOCK");
        if(hayErrores) return null;
        match(TipoToken.LEFT_BRACE);
        List<Statement> blocks = new ArrayList<>();
        StmtBlock statement = new StmtBlock(DECLARATION(blocks));
        match(TipoToken.RIGHT_BRACE);
        return statement;
    }

    private Expression EXPRESSION(){
        //System.out.println("EXPRESSION");
        if(hayErrores) return null;
        return ASSIGNMENT();
    }

    //Incompleta por ASSIGNMENT_OPC
    private Expression ASSIGNMENT(){
        if(hayErrores) return null;
        Expression expr = LOGIC_OR();
        return ASSIGNMENT_OPC(expr);
    }


    //ExprAssign necesita un Token, pero tenemos una Expresion en su lugar (asg)
    private Expression ASSIGNMENT_OPC(Expression expr){
        if(hayErrores) return null;
        if(preanalisis.tipo == TipoToken.EQUAL){
            Token equalsToken = preanalisis;
            match(TipoToken.EQUAL);
            Expression value = EXPRESSION();
            if (expr instanceof ExprVariable) {
                Token name = ((ExprVariable) expr).name;
                return new ExprAssign(name, value);
            }
            hayErrores = true;
        }
        return expr;
    }


    private Expression LOGIC_OR(){
        //System.out.println("LOGIC_OR");
        if(hayErrores) return null;
        Expression expr = LOGIC_AND();
        expr = LOGIC_OR_2(expr);
        return expr;
    }

    private Expression LOGIC_OR_2(Expression expr){
        //System.out.println("LOGIC_OR_2");
        if(hayErrores) return null;
        if(preanalisis.tipo == TipoToken.OR){
            match(TipoToken.OR);
            Token operador = previous();
            Expression expr2 = LOGIC_AND();
            Expression expl = new ExprLogical(expr, operador, expr2);
            return LOGIC_OR_2(expl);
        }return expr;
    }

    private Expression LOGIC_AND(){
        //System.out.println("LOGIC_AND");
        if(hayErrores) return null;
        Expression expr = EQUALITY();
        expr = LOGIC_AND_2(expr);
        return expr;
    }

    private Expression LOGIC_AND_2(Expression expr){
        //System.out.println("LOGIC_AND_2");
        if(hayErrores) return null;
        if(preanalisis.tipo == TipoToken.AND){
            match(TipoToken.AND);
            Token operador = previous();
            Expression expr2 = EQUALITY();
            Expression expl = new ExprLogical(expr, operador, expr2);
            return LOGIC_AND_2(expl);
        }
        return expr;
    }

    private Expression EQUALITY(){
        //System.out.println("EQUALITY");
        if(hayErrores) return null;
        Expression expr = COMPARISON();
        expr = EQUALITY_2(expr);
        return expr;
    }

    private Expression EQUALITY_2(Expression expr){
        //System.out.println("EQUALITY_2");
        if(hayErrores) return null;
        if(preanalisis.tipo == TipoToken.BANG_EQUAL){
            match(TipoToken.BANG_EQUAL);
            Token operador = previous();
            Expression expr2 = COMPARISON();
            Expression expb = new ExprBinary(expr, operador, expr2);
            return EQUALITY_2(expb);
        } else if(preanalisis.tipo == TipoToken.EQUAL_EQUAL){
            match(TipoToken.EQUAL_EQUAL);
            Token operador = previous();
            Expression expr2 = COMPARISON();
            Expression expb = new ExprBinary(expr, operador, expr2);
            return EQUALITY_2(expb);
        }return null;
    }

    private Expression COMPARISON(){
        //System.out.println("COMPARISON");
        if(hayErrores) return null;
        Expression expr = TERM();
        expr = COMPARISON_2(expr);
        return expr;
    }

    private Expression COMPARISON_2(Expression expr){
        //System.out.println("COMPARISON_2");
        if(hayErrores) return null;
        if(preanalisis.tipo == TipoToken.GREATER){
            match(TipoToken.GREATER);
            Token operador = previous();
            Expression expr2 = TERM();
            Expression expb = new ExprBinary(expr, operador, expr2);
            return COMPARISON_2(expb);
        } else if(preanalisis.tipo == TipoToken.GREATER_EQUAL){
            match(TipoToken.GREATER_EQUAL);
            Token operador = previous();
            Expression expr2 = TERM();
            Expression expb = new ExprBinary(expr, operador, expr2);
            return COMPARISON_2(expb);
        } else if(preanalisis.tipo == TipoToken.LESS){
            match(TipoToken.LESS);
            Token operador = previous();
            Expression expr2 = TERM();
            Expression expb = new ExprBinary(expr, operador, expr2);
            return COMPARISON_2(expb);
        } else if(preanalisis.tipo == TipoToken.LESS_EQUAL){
            match(TipoToken.LESS_EQUAL);
            Token operador = previous();
            Expression expr2 = TERM();
            Expression expb = new ExprBinary(expr, operador, expr2);
            return COMPARISON_2(expb);
        } return expr;
    }

    private Expression TERM(){
        //System.out.println("TERM");
        if(hayErrores) return null;
        Expression expr = FACTOR();
        expr = TERM_2(expr);
        return expr;
    }

    private Expression TERM_2(Expression expr){
        //System.out.println("TERM_2");
        if(hayErrores) return null;
        if(preanalisis.tipo == TipoToken.MINUS){
            match(TipoToken.MINUS);
            Token operador = previous();
            Expression expr2 = FACTOR();
            Expression expb = new ExprBinary(expr, operador, expr2);
            return TERM_2(expb);
        } else if(preanalisis.tipo == TipoToken.PLUS){
            match(TipoToken.PLUS);
            Token operador = previous();
            Expression expr2 = FACTOR();
            Expression expb = new ExprBinary(expr, operador, expr2);
            return TERM_2(expb);
        } return null;
    }

    private Expression FACTOR(){
        //System.out.println("FACTOR");
        if(hayErrores) return null;
        Expression expr = UNARY();
        expr = FACTOR_2(expr);
        return expr;
    }

    private Expression FACTOR_2(Expression expr){
        //System.out.println("FACTOR_2");
        if(hayErrores) return null;
        if(preanalisis.tipo == TipoToken.SLASH){
            match(TipoToken.SLASH);
            Token operador = previous();
            Expression expr2 = UNARY();
            ExprBinary expb = new ExprBinary(expr, operador, expr2);
            return FACTOR_2(expb);
        } else if(preanalisis.tipo == TipoToken.STAR){
            match(TipoToken.STAR);
            Token operador = previous();
            Expression expr2 = UNARY();
            ExprBinary expb = new ExprBinary(expr, operador, expr2);
            return FACTOR_2(expb);
        }return null;
    }

    private Expression UNARY(){
        //System.out.println("UNARY");
        if(hayErrores) return null;
        if(preanalisis.tipo == TipoToken.BANG){
            match(TipoToken.BANG);
            Token operador = previous();
            Expression expr = UNARY();
            return new ExprUnary(operador, expr);
        } else if(preanalisis.tipo == TipoToken.MINUS){
            match(TipoToken.MINUS);
            Token operador = previous();
            Expression expr = UNARY();
            return new ExprUnary(operador, expr);
        } else {
            return CALL();
        }
    }

    private Expression CALL(){
        //System.out.println("CALL");
        if(hayErrores) return null;
        Expression expr = PRIMARY();
        expr = CALL_2(expr);
        return expr;
    }

    private Expression CALL_2(Expression expr){
        //System.out.println("CALL_2");
        if(hayErrores) return null;
        if(preanalisis.tipo == TipoToken.LEFT_PAREN){
            match(TipoToken.LEFT_PAREN);
            List<Expression> lstArguments = ARGUMENTS_OPC();
            match(TipoToken.RIGHT_PAREN);
            //CALL_2(); No se usa, dicho por el profesor
            ExprCallFunction ecf = new ExprCallFunction(expr, lstArguments);
            return CALL_2(ecf);
        } return null;
    }

    private Expression PRIMARY(){
        //System.out.println("PRIMARY");
        if(hayErrores) return null;
        if(preanalisis.tipo == TipoToken.TRUE){
            match(TipoToken.TRUE);
            return new ExprLiteral(true);
        } else if(preanalisis.tipo == TipoToken.FALSE){
            match(TipoToken.FALSE);
            return new ExprLiteral(false);
        } else if(preanalisis.tipo == TipoToken.NULL){
            match(TipoToken.NULL);
            return new ExprLiteral(null);
        } else if(preanalisis.tipo == TipoToken.NUMBER){
            match(TipoToken.NUMBER);
            Token numero = previous();
            return new ExprLiteral(numero.getLiteral());
        } else if(preanalisis.tipo == TipoToken.STRING){
            match(TipoToken.STRING);
            Token cadena = previous();
            return new ExprLiteral(cadena.getLiteral());
        } else if(preanalisis.tipo == TipoToken.IDENTIFIER){
            match(TipoToken.IDENTIFIER);
            Token id = previous();
            return new ExprVariable(id);
        } else if(preanalisis.tipo == TipoToken.LEFT_PAREN){
            match(TipoToken.LEFT_PAREN);
            Expression expr = EXPRESSION();
            match(TipoToken.RIGHT_PAREN);
            return new ExprGrouping(expr);
        }return null;
    }

    private Statement FUNCTION(){
        //System.out.println("FUNCTION");
        if(hayErrores) return null;
        match(TipoToken.IDENTIFIER);
        Token id = previous();
        match(TipoToken.LEFT_PAREN);
        List<Token> parameters = new ArrayList<>();
        parameters.addAll(PARAMETERS_OPC(parameters));
        match(TipoToken.RIGHT_PAREN);
        StmtBlock body = BLOCK();
        Statement statement = new StmtFunction(id, parameters, body);
        return statement;
    }

    /*private void FUNCTIONS(){
        if(hayErrores) return;
        else if(preanalisis.tipo == TipoToken.FUN){
            match(TipoToken.FUN);
            FUNCTIONS();
        }
    }*/

    private List<Token> PARAMETERS_OPC(List<Token> parameters){
        //System.out.println("PARAMETERS_OPC");
        if(hayErrores) return null;
        if(preanalisis.tipo == TipoToken.IDENTIFIER){
            parameters.addAll(PARAMETERS(parameters));
        } return parameters;
    }

    private List<Token> PARAMETERS(List<Token> parameters){
        //System.out.println("PARAMETERS");
        if(hayErrores) return null;
        match(TipoToken.IDENTIFIER);
        Token id = previous();
        parameters.add(id);
        parameters.addAll(PARAMETERS_2());
        return parameters;
    }

    private List<Token> PARAMETERS_2(){
        //System.out.println("PARAMETERS_2");
        if(hayErrores) return null;
        if(preanalisis.tipo == TipoToken.COMMA){
            match(TipoToken.COMMA);
            match(TipoToken.IDENTIFIER);
            Token id = previous();
            List<Token> exp = new ArrayList<>();
            exp.add(id);
            List<Token> aux = PARAMETERS_2();
            if(aux != null){
                exp.addAll(aux);
            }return exp;
        }return null;
    }

    private List<Expression> ARGUMENTS_OPC(){
        //System.out.println("ARGUMENTS_OPC");
        if(hayErrores) return null;
        if(primeroEXPR_STMT(preanalisis.tipo)){
            List<Expression> lstArguments =  new ArrayList<Expression>();
            lstArguments.add(EXPRESSION());
            lstArguments.addAll(ARGUMENTS());
            return lstArguments;
        } return null;
    }

    private List<Expression> ARGUMENTS(){
        //System.out.println("ARGUMENTS");
        if(hayErrores) return null;
        if(preanalisis.tipo == TipoToken.COMMA){
            List<Expression> lstArguments =  new ArrayList<Expression>();
            match(TipoToken.COMMA);
            lstArguments.add(EXPRESSION());
            List<Expression> aux = ARGUMENTS();
            if(aux != null){
                lstArguments.addAll(aux);
            }
            return lstArguments;
        }return null;
    }

    private void match(TipoToken tt){
        if(preanalisis.tipo == tt){
            i++;
            if(i < tokens.size()) preanalisis = tokens.get(i);
        }else{
            hayErrores = true;
            System.out.println("Error encontrado, no se tiene "+tt);
        }
    }

    private Token previous() {
        return this.tokens.get(i - 1);
    }

    private boolean primeroSTATEMENT(TipoToken tt){
        switch (tt) {
            case BANG:
            case MINUS:
            case TRUE:
            case FALSE:
            case NULL:
            case NUMBER:
            case STRING:
            case IDENTIFIER:
            case LEFT_PAREN:
            case FOR:
            case IF:
            case PRINT:
            case RETURN:
            case WHILE:
            case LEFT_BRACE:
                return true;
            default:
                return false;
        }
    }

    private boolean primeroEXPR_STMT(TipoToken tt){
        switch (tt) {
            case BANG:
            case MINUS:
            case TRUE:
            case FALSE:
            case NULL:
            case NUMBER:
            case STRING:
            case IDENTIFIER:
            case LEFT_PAREN:
                return true;
            default:
                return false;
        }
    }
}