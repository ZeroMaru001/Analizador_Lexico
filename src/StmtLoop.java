
public class StmtLoop extends Statement {
    final Expression condition;
    final Statement body;

    StmtLoop(Expression condition, Statement body) {
        this.condition = condition;
        this.body = body;
    }
    @Override
    public String toString(){
        String stmtLoop = "Loop " + condition;
        if(body != null)
            stmtLoop += "\n" + body;
        return stmtLoop;
    }
}
