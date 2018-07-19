package zeta;

import java.util.Map;
import java.util.List;

class ZetaClass implements ZetaCallable{
    final String name;
    private final Map<String, ZetaFunction> methods;
    final ZetaClass superclass;

    ZetaClass(String name, ZetaClass superclass, Map<String, ZetaFunction> methods) {
        this.name = name;
        this.superclass = superclass;
        this.methods = methods;
    }

    ZetaFunction findMethod(ZetaInstance instance, String name) {
        if(methods.containsKey(name)) return methods.get(name).bind(instance);

        if(superclass != null) return superclass.findMethod(instance, name);

        return null;
    }

    @Override
    public int arity() {
        ZetaFunction initializer = methods.get(name);
        if(initializer == null) return 0;
        return initializer.arity();
    } 

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        ZetaInstance instance = new ZetaInstance(this);
        ZetaFunction initializer = methods.get(name);
        if(initializer != null) initializer.bind(instance).call(interpreter, arguments);
        return instance;
    }

    @Override
    public String toString() {
        return name;
    }
}