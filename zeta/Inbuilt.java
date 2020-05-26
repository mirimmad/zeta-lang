package zeta;

import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Date;
import java.util.Random;
import java.lang.Math;

class Inbuilt {
    public static Map<String, ZetaCallable> inBuilts  = new HashMap<>();

    static{
    inBuilts.put("readInt", new ZetaCallable(){
        @Override
        public int arity() {
            return 0;
        }
        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            String number;
            Scanner scanner = new Scanner(System.in);
            try {
                  number = scanner.next();
                } catch(InputMismatchException e) {
                    return e.getMessage();
                }
                return Double.parseDouble(number);
            }
            public String toString() {
            return "zeta inbuilt";
        }
        
    }); 

    inBuilts.put("readString", new ZetaCallable(){
        @Override
        public int arity() {
            return 0;
        }
        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            String str;
            Scanner scanner =  new Scanner(System.in);
            try{
                str = scanner.next();
            } catch(InputMismatchException e) {
                return e.getMessage();
            }
            return str;
        }
        public String toString() {
            return "zeta inbuilt";
        }
    });
    
    inBuilts.put("date", new ZetaCallable(){
        @Override
        public int arity() {
            return 0;
        }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            return new Date();
        }
        public String toString() {
            return "zeta inbuilt";
        }
    });
    
    inBuilts.put("random", new ZetaCallable(){
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            Random rand = new Random();
            int number;
            if(arguments.get(0) instanceof Double) {
                number = rand.nextInt(((Double)arguments.get(0)).intValue());
            } else {
                return "random : argument must be a number.";
            }
            return number;
        }
        public String toString() {
            return "zeta inbuilt";
        }
    });
    
    inBuilts.put("sqrt", new ZetaCallable(){
    	@Override
    	public int arity() {
    		return 1;
    	}
    	@Override
    	public Object call(Interpreter interpreter, List<Object> arguments) {
    		Double numSqrt;
    		if(arguments.get(0) instanceof Double) {
    			numSqrt = Math.sqrt((Double)arguments.get(0));
    		} else {
    			return "sqrt : argument must be a number";
    		}
    		return numSqrt;
    	}
    	public String toString() {
            return "zeta inbuilt";
        }
    });
    
    inBuilts.put("type", new ZetaCallable(){
        @Override
        public int arity() {
            return 1;
        }
        
        @Override
        public Object call(Interpreter interpret, List<Object> arguments) {
            Object arg = arguments.get(0);
            if (arg instanceof Double) {
                return "number";
            }
           
            if (arg instanceof String) {
                return "string";
            }           
            
            if (arg instanceof ZetaCallable && arg instanceof ZetaClass) {
                ZetaClass z = (ZetaClass) arg;
                return z.name;
            }
            
            if (arg instanceof ZetaCallable && arg instanceof ZetaFunction) {
                return "function";
            }
            if (arg instanceof ZetaCallable) {
                return "inbuillt";
            }
            if (arg instanceof ZetaInstance) {
                return "instance";
            }
            return "undefined";
        }
        
        public String toString() {
            return "zeta inbuilt";
        }
    
    });
 }
}
