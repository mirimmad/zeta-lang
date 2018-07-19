package zeta;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Zeta {
    public static boolean hadError = false;
    public static boolean hadRuntimeError = false;
    private static final Interpreter interpreter = new Interpreter();
    public static void main(String[] args) {
            
            if(args.length > 1) {
                System.out.println("Usage zeta [Script.zt]");
            } else if (args.length == 1) {
                runFile(args[0]);
            } else {
                runPrompt();
            }
             
    }

    private static void runFile(String path) {
        if(path.endsWith(".zt")) {
            try {
            byte[] bytes = Files.readAllBytes(Paths.get(path));
            run(new String(bytes, Charset.defaultCharset()));
            if(hadError) System.exit(65);
            } catch(FileNotFoundException e) {
                System.out.println(path + ": File not Found");
            } catch(IOException e) {
                System.out.println(e.getMessage());
            }
        } else {
            System.err.println("Zeta scripts must end with '.zt'.");
        }
    }

    private static void runPrompt() {
        System.out.println("The \u001B[33mZeta\033[0m Programming Language 0.1");
        System.out.println("Copyright \u001B[36mMir Immad\033[0m 2017.\n");
        try{
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        for(;;) {
            System.out.print(">> ");
            run(reader.readLine());
            hadError = false;
         }   
        } catch(IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void run(String source) {
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.scanTokens();
        Parser parser = new Parser(tokens);
        List<Stmt> statements = parser.parse();
        if(hadError) return;
        Resolver resolver = new Resolver(interpreter);
        resolver.resolve(statements);
        if(hadError) return;
        interpreter.interpret(statements);
        
    }

    static void error(int line, String message) {
        report(line, "", message);
    }

    static void error(Token token, String message) {
        if(token.type == TokenType.EOF) {
            report(token.line, "at end", message);
        } else {
            report(token.line, "at '" + token.lexeme + "' " , message);
        }
    }

    static void report(int line, String where, String message) {
        System.err.println("[line " + line + "] Error " + where +" : " + message );
        hadError = true;
    }

    static void runtimeError(RuntimeError error) {
        System.err.println(error.getMessage() + "\n[line " + error.token.line + "]");
        hadRuntimeError = true;
    }
}
