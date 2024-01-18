
//import javax.swing.plaf.nimbus.State;
import java.util.List;

public class StmtBlock extends Statement{
    final List<Statement> statements;

    StmtBlock(List<Statement> statements) {
        this.statements = statements;
    }

    @Override
    public String toString(){
        String bloque = "";
        if (statements != null) {
            for (int i = 0; i < statements.size(); i++) {
                if (statements.get(i) != null)
                    bloque += "\t"+ statements.get(i).toString();
                else{
                    bloque += "null";
                }
                if (i < statements.size() - 1)
                    bloque += "\n";
            }
        }
        return bloque;
    }
}
