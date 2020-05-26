package zeta;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.nio.file.Files;
import java.nio.file.Paths;

class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void>{
    
    final Environment globals = new Environment();
    private final Map<Expr, Integer> locals = new HashMap<>();
    private Environment environment = globals;
    private  Boolean prompt;
    private Map<String, Interpreter> modules = new HashMap<>();
    private List<Stmt> statements;
    Interpreter(List<Stmt> stmts /*for import to work*/) {
        statements = stmts;
        if(! Inbuilt.inBuilts.isEmpty()) {
            for(String key : Inbuilt.inBuilts.keySet()) {
                globals.define(key, Inbuilt.inBuilts.get(key));
            }
        }
        globals.define("hi", null);
    }

    void interpret(Boolean p) {
      
        prompt = p;
        try{
            for(Stmt statement : statements) {
                execute(statement);
            }
        } catch(RuntimeError error) {
            Zeta.runtimeError(error);
        }
    }

    private String stringfy(Object object) {
        if(object == null) return "nil";
        if(object instanceof Double) {
            String text = object.toString();
            if(text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
        return text;
        }
        return object.toString();
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitLogicalExpr(Expr.Logical expr) {
        Object left = evaluate(expr.left);
        if(expr.operator.type == TokenType.OR) {
            if(isTruthy(left)) return left;
        } else {
            if(!isTruthy(left)) return left;
        }
        return evaluate(expr.right);
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }
    
    public Object evaluate(Expr expr) {
        return expr.accept(this);
    } 

    public void execute(Stmt stmt) {
         stmt.accept(this);
    }

    void resolve(Expr expr, int depth) {
        locals.put(expr, depth);
    }

    void executeBlock(List<Stmt> statements, Environment environment) {
        Environment previous = this.environment;
        try{
            this.environment = environment;
            for(Stmt statement : statements) {
                execute(statement);
            }
        } finally {
                this.environment = previous;
            }
    }

    @Override
    public Void visitClassStmt(Stmt.Class stmt) {
        Object superclass = null;
        if(stmt.superclass != null) {
           superclass =  evaluate(stmt.superclass);
           if(!(superclass instanceof ZetaClass)) {
               throw new RuntimeError(stmt.superclass.name, "Superclass must be a class.");
           }
        }

        environment.define(stmt.name.lexeme, null);
        if(stmt.superclass != null) {
            environment = new Environment(environment);
            environment.define("super", superclass);
        }

        Map<String, ZetaFunction> methods = new HashMap<>();
        for(Stmt.Function method : stmt.methods) {
            ZetaFunction function = new ZetaFunction(method, environment, ((String)method.name.lexeme).equals((String)stmt.name.lexeme));
            methods.put(method.name.lexeme, function);
        }

        ZetaClass klass = new ZetaClass(stmt.name.lexeme, (ZetaClass) superclass, methods);
        if(superclass != null) {
            environment = environment.enclosing;
        }
        environment.assign(stmt.name, klass);
        return null;
    }
    
    @Override
    public Void visitBlockStmt(Stmt.Block stmt) {
        executeBlock(stmt.statements, new Environment(environment));
        return null;
    }
    @Override
    public Void visitImportStmt(Stmt.Import stmt) {
        globals.define("mm", null);
        String n = ((String) stmt.module.literal); 
        String name = n + ".zt";
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(Paths.get(name));
        } catch (Exception e) {
            throw new RuntimeError(stmt.module, "Couldn't open the file");
        }
        if (modules.containsKey(name)) return null;
        Lexer l = new Lexer(new String(bytes));
        List<Token> tokens = l.scanTokens();
        Parser p = new Parser(tokens, this.prompt);
        List<Stmt> stmts = p.parse();
        Interpreter interpreter = new Interpreter(stmts);
        Resolver r = new Resolver(interpreter);
        r.resolve(stmts);
    /*    modules.put(n, interpreter);
        globals.define(n, null); 
        environment.define("nn", null);
        for(String key : globals.values.keySet()) {
            System.out.println(key);
        } */
        return null;
    }

    @Override
    public Void visitExpressionStmt(Stmt.Expression stmt) {
        if(prompt) {
        System.out.println(stringfy(evaluate(stmt.expression)));
        } else {
            evaluate(stmt.expression);
        }
        return null;
    }

    @Override
    public Void visitFunctionStmt(Stmt.Function stmt) {
        ZetaFunction function = new ZetaFunction(stmt, environment, false);
        environment.define(stmt.name.lexeme, function);
        return null;
    }
    
    @Override
    public Void visitIfStmt(Stmt.If stmt) {
        if(isTruthy(evaluate(stmt.condition))) {
            execute(stmt.thenBranch);
        } else if (stmt.elseBranch != null) {
            execute(stmt.elseBranch);
        }
        return null;
    }

    @Override
    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
        System.out.println(stringfy(value));
        return null;
    }

    @Override
    public Void visitReturnStmt(Stmt.Return stmt) {
        Object value = null;
        if(stmt.value != null) value = evaluate(stmt.value);
        throw new Return(value);
    }

    @Override
    public Void visitBreakStmt(Stmt.Break stmt) {
        throw new Return();
    }

    @Override
    public Void visitVarStmt(Stmt.Var stmt) {
        Object value = null;
        if(stmt.initializer != null) {
            value = evaluate(stmt.initializer);
        }
        environment.define(stmt.name.lexeme, value);
        return null;
    }

    @Override
    public Void visitWhileStmt(Stmt.While stmt) {
        while(isTruthy(evaluate(stmt.condition))) {
            try {
                execute(stmt.body);            
            } catch (Return stop) {
                break;
            }
        }
        return null;
    }
      
    @Override
    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value);
        Integer distance = locals.get(expr);
        if(distance != null) {
            environment.assignAt(distance, expr.name, value);
        } else {
            globals.assign(expr.name, value);
        }
        return value;
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);
        switch(expr.operator.type) {
            case BANG:
                return !isTruthy(right);
            case MINUS:
                checkNumberOperand(expr.operator, right);
                return -(double) right;
        }
        return null;
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return lookUpVariable(expr.name, expr);
    }

    private Object lookUpVariable(Token name, Expr expr) {
        Integer distance = locals.get(expr);
        if(distance != null) {
            return environment.getAt(distance, name.lexeme);
        } else {
            return globals.get(name);
        }
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if(operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void  checkNumberOperands(Token operator, Object left, Object right){
        if(left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "Both operand must be numbers.");
    }

    private boolean isTruth(Object object) {
        if(object == null) return false;
        if(object instanceof Boolean) return (Boolean) object;
        return true;
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);
        switch (expr.operator.type) {
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return (double) left  - (double) right;
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                return (double) left / (double) right;
            case STAR:
                if(left instanceof Double && right instanceof Double){
                    return (double) left * (double) right;
                }
                if(left instanceof String && right instanceof Double) {
                    Double _repeat = (Double) right;
                    String s = (String) left;
                    Integer repeat = _repeat.intValue();
                    return  new String(new char[repeat]).replace("\0", s);
                }
                throw new RuntimeError(expr.operator, "Operands must be number and number or string and number.");
            case PLUS:
                if(left instanceof Double && right instanceof Double) {
                    return (double) left + (double) right;
                }
                if(left instanceof String && right instanceof String) {
                    return (String) left + (String) right;
                }
                if(left instanceof String && right instanceof Double) {
                    right = stringfy(right);
                    return (String) left + (String) right;
                }
                if(left instanceof Double && right instanceof String) {
                    left = stringfy(left);
                    return (String) left + (String) right;
                }
                throw new RuntimeError(expr.operator, "Operands must me numbers or strings");
            case GREATER:
                checkNumberOperands(expr.operator, left, right);
                return (double) left > (double) right;
            case LESS:
                checkNumberOperands(expr.operator, left, right);
                return (double) left < (double) right;
            case GREATER_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double) left >= (double) right;
            case LESS_EQUAL:
                checkNumberOperands(expr.operator, left, right);
                return (double) left <= (double) right;
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
        }
        return null;
    }

     @Override
    public Object visitCallExpr(Expr.Call expr) {
        Object callee = evaluate(expr.callee);
        List<Object> arguments = new ArrayList<>();
        for(Expr argument : expr.arguments) {
            arguments.add(evaluate(argument));
        }
        if(! (callee instanceof ZetaCallable)) {
            throw new RuntimeError(expr.paren, "Can only call functions and classes");
        }
        ZetaCallable function = (ZetaCallable)callee;
        if(arguments.size() !=  function.arity()) {
            throw new RuntimeError(expr.paren, "Expected "+ function.arity() +" argument(s) but got "+ arguments.size() + ".");
        }
        return function.call(this, arguments);
    }

    @Override
    public Object visitGetExpr(Expr.Get expr) {
        Object object = evaluate(expr.object);
        if(object instanceof ZetaInstance) {
            return ((ZetaInstance)object).get(expr.name);
        }
        throw new RuntimeError(expr.name, "Only instances have properties.");
    }
    @Override
    public Object visitSetExpr(Expr.Set expr) {
        Object object = evaluate(expr.object);

        if(!(object instanceof ZetaInstance)) {
            throw new RuntimeError(expr.name, "Only instances have fiedls.");
        }
        Object value = evaluate(expr.value);
        ((ZetaInstance)object).set(expr.name, value);
        return value;
    }

    @Override
    public Object visitSuperExpr(Expr.Super expr) {
        int distance = locals.get(expr);
        ZetaClass superclass = (ZetaClass) environment.getAt(distance, "super");
        ZetaInstance object = (ZetaInstance) environment.getAt(distance - 1, "this");
        ZetaFunction method = superclass.findMethod(object, expr.method.lexeme);
        if(method == null) {
            throw new RuntimeError(expr.method, "Undefined property '" + expr.method.lexeme +"'.");
        }
        return method;
    }

    @Override
    public Object visitThisExpr(Expr.This expr) {
        return lookUpVariable(expr.keyword, expr);
    }
    private boolean isEqual(Object left, Object right) {
        if(left == null && right == null) return true;
        if(left == null || right == null) return false;
        return left.equals(right);
    }
    private boolean isTruthy(Object object) {
        if(object == null) return false;
        if(object instanceof Boolean) return (Boolean) object;
        return true;
    }

}
