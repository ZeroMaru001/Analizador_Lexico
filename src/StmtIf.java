
public class StmtIf extends Statement {
    final Expression condition;
    final Statement thenBranch;
    final Statement elseBranch;

    StmtIf(Expression condition, Statement thenBranch, Statement elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    @Override
    public String toString(){
        String stmtIf = "if " + condition + " then\n" + thenBranch;
        if(elseBranch != null)
            stmtIf += "\nelse\n" + elseBranch;
        return stmtIf;
    }
}
