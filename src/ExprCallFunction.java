

import java.util.List;

public class ExprCallFunction extends Expression{
    final Expression callee;
    // final Token paren;
    final List<Expression> arguments;

    ExprCallFunction(Expression callee, /*Token paren,*/ List<Expression> arguments) {
        this.callee = callee;
        // this.paren = paren;
        this.arguments = arguments;
    }
    @Override
    public String toString() {
        String funciones = "";
        if (arguments != null) {
            for (int i = 0; i < arguments.size(); i++) {
                if (arguments.get(i) != null)
                    funciones += arguments.get(i).toString();
                else {
                    funciones += "null";
                }
                if (i < arguments.size() - 1)
                    funciones += ", ";
            }
        }
        return funciones;
    }
}
