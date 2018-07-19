package zeta;

import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Date;
import java.util.Random;

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
            int number;
            Scanner scanner = new Scanner(System.in);
            try {
                  number = scanner.nextInt();
                } catch(InputMismatchException e) {
                    return e.getMessage();
                }
                return number;
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
            int number;
            if(arguments.get(0) instanceof Double) {
                number = rand.nextInt(((Double)arguments.get(0)).intValue());
            } else {
                return "Input must be a number.";
            }
            return number;
        }
    });
 }
}
