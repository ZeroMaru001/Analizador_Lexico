
public class StmtVar extends Statement {
    final Token name;
    final Expression initializer;

    StmtVar(Token name, Expression initializer) {
        this.name = name;
        this.initializer = initializer;
    }

    @Override
    public String toString(){
        if(initializer == null)
            return "var " + name.lexema;
        else
            return "var " + name.lexema+" = "+initializer;
    }
}
