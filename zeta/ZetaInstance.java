package zeta;

import java.util.Map;
import java.util.HashMap;

class ZetaInstance {

    private ZetaClass klass;
    final private Map<String, Object> fields = new HashMap<>();

    ZetaInstance(ZetaClass klass) {
        this.klass = klass;
    } 

    Object get(Token name) {
        if(fields.containsKey(name.lexeme)) {
            return fields.get(name.lexeme);
        }

        ZetaFunction method = klass.findMethod(this, name.lexeme);
        if(method != null) return method;
        throw new RuntimeError(name, "Undefined Property '" + name.lexeme +"'.");
    }

    void set(Token name, Object value) {
        fields.put(name.lexeme, value);
    }

    @Override
    public String toString() {
      
        return klass.name + " instance";
    }
}
