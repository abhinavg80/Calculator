import java.util.Stack;
import java.util.Scanner;
import java.util.HashMap;
public class Main {
	
	public static void main(String[] args) {
		
	}
	
	public static String toPostfix(String exp) {
		Scanner infix = new Scanner(exp);
		Stack<Character> convert = new Stack<>();
		HashMap<Character, Integer> operators = new HashMap<>();
		operators.put('+', 1);
		operators.put('-', 1);
		operators.put('/', 2);
		operators.put('*', 2);
		operators.put('%', 2);
		operators.put('^', 3);
		String postfix = "";
		
		String token;
		char op;
		char stackOp;
		while (infix.hasNext()) {
			token = infix.next();
			op = token.charAt(0);
			
			if (Character.isDigit(op)) {
				postfix += op + " ";
			} else if (operators.containsKey(op)){
				while (!convert.isEmpty() && operators.get(convert.peek()) >= operators.get(op)) {
					postfix += convert.pop() + " ";
				}
				convert.push(op);
			} else if (op == '(') {
				convert.push(op);
			} else if (op == ')') {
				while (!convert.isEmpty() && convert.peek() != '(') {
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

}
