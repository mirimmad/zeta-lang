package zeta;

import java.util.List;

interface ZetaCallable {
    int arity();
    Object call(Interpreter interpreter, List<Object> arguments);
}