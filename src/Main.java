import java.util.Stack;
import java.util.Scanner;
import java.util.HashMap;
public class Main {
	
	//TODO use Strings in HashMap and throughout the project instead of Characters
	
	public static void main(String[] args) {
		
	}
	
	public static String toPostfix(String exp) {
		Scanner infix = new Scanner(exp);
		Stack<String> convert = new Stack<>();
		HashMap<String, Integer> operators = new HashMap<>();
		operators.put("+", 1);
		operators.put("-", 1);
		operators.put("/", 2);
		operators.put("*", 2);
		operators.put("%", 2);
		operators.put("^", 3);
                
		String postfix = "";
		
		
		String op;
		String stackOp;
		while (infix.hasNext()) {
			op = infix.next();
			
			
			if (isNumeric(op)) {
				postfix += op + " ";
			} else if (operators.containsKey(op)){
				while (operators.containsKey(op) && !convert.isEmpty() && operators.containsKey(convert.peek()) && operators.get(convert.peek()) >= operators.get(op)) {
					postfix += convert.pop() + " ";     
				}
				convert.push(op);
			} else if (op.equals("(")) {
				convert.push(op);
			} else if (op.equals(")")) {
				while (!convert.isEmpty() && !convert.peek().equals("(")) {
					postfix += convert.pop() + " ";
				}
				convert.pop();
			}
		}
		
		while (!convert.isEmpty()) {
			postfix += convert.pop() + " ";
		}
		
		return postfix;
		
		
	}
        
        public static boolean isNumeric(String number) {
            try {
                double num = Double.parseDouble(number);
            } catch (NumberFormatException | NullPointerException e) {
                return false;
            }
            
            return true;
            
        }

}
