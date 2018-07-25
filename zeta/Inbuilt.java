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
            try {
                return (new Scanner(System.in)).nextInt();
            } catch(InputMismatchException e) {
                return e.getMessage();
            }
        }
    }); 

    inBuilts.put("readString", new ZetaCallable(){
        @Override
        public int arity() {
            return 0;
        }
        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            try{
                return (new Scanner(System.in)).next();
            } catch(InputMismatchException e) {
                return e.getMessage();
            }
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
    });
    
    inBuilts.put("random", new ZetaCallable(){
        @Override
        public int arity() {
            return 1;
        }

        @Override
        public Object call(Interpreter interpreter, List<Object> arguments) {
            Random rand = new Random();
            if(arguments.get(0) instanceof Double) {
                return rand.nextInt(((Double)arguments.get(0)).intValue());
            }
            return "random : argument must be a number.";
        }
    });
    
    inBuilts.put("sqrt", new ZetaCallable(){
    	@Override
    	public int arity() {
    		return 1;
    	}
    	@Override
    	public Object call(Interpreter interpreter, List<Object> arguments) {
    		if(arguments.get(0) instanceof Double) {
    			return Math.sqrt((Double)arguments.get(0));
    		}
            return "sqrt : argument must be a number";
    	}
    });
 }
}
