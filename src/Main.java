import java.util.Stack;
import java.util.Scanner;
import java.util.HashMap;
import java.lang.Math;

public class Main {

	public static void main(String[] args) {
		startCalc();
	}

	public static void startCalc() {
		Scanner input = new Scanner(System.in);
		String expression = "", result = "";
		boolean isRPN = false;

		while (true) {
			System.out.println("Enter an expression (or RPN to change to RPN mode or normal to change back): ");
			System.out.println("Current Mode: " + ((isRPN) ? "RPN" : "Normal"));
			expression = input.nextLine();
			if (expression.toLowerCase().equals("rpn")) {
				isRPN = true;
				System.out.println("Mode changed to RPN");
				continue;
			} else if (expression.toLowerCase().equals("normal")) {
				isRPN = false;
				System.out.println("Mode changed to Normal");
				continue;
			}
			result = calculate(expression, isRPN);

			if (result.equals("ERROR")) {
				System.out.println("There was an error.");
			} else if (result.equals("quit")) {
				break;
			} else {
				System.out.println(expression + " = " + result);
			}
		}
	}

	public static String calculate(String exp, boolean isRPN) {
		if (exp.toLowerCase().equals("quit")) {
			return "quit";
		}

		if (!isRPN) {
			exp = toPostfix(exp);
		}

		Stack<String> calc = new Stack<>();
		Scanner tokenizer = new Scanner(exp);

		String num;
		double num1 = 0, num2 = 0;
		String op = "";
		String result = "";
		while (tokenizer.hasNext()) {
			num = tokenizer.next();
			if (isNumeric(num)) {
				calc.push(num);
			} else {
				try {
					num1 = Double.parseDouble(calc.pop());
				} catch (Exception e) {
					return "ERROR";
				}
				op = num;
				if (!calc.isEmpty()) {
					num2 = Double.parseDouble(calc.pop());
				} else {
					num2 = 0;
				}

				if (num2 != 0) {
					result = operate(num1, num2, op);
				} else {
					result = operate(num1, op);
				}
				calc.push(result);

			}
		}

		if (result.equals("")) {
			return "ERROR";
		}

		return result;
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
		operators.put("v", 3);
		operators.put("s", 3);
		operators.put("c", 3);
		operators.put("t", 3);
		operators.put("asin", 3);
		operators.put("acos", 3);
		operators.put("atan", 3);
		operators.put("|", 3);
		operators.put("~", 3);

		String postfix = "";

		String op;
		while (infix.hasNext()) {
			op = infix.next();

			if (isNumeric(op)) {
				postfix += op + " ";
			} else if (operators.containsKey(op)) {
				while (operators.containsKey(op) && !convert.isEmpty() && operators.containsKey(convert.peek())
						&& operators.get(convert.peek()) >= operators.get(op)) {
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
		
		infix.close();
		return postfix;

	}

	public static String operate(double a, double b, String op) {
		switch (op) {
		case "+":
			return "" + (a + b);
		case "-":
			return "" + (b - a);
		case "/":
			return "" + (b / a);
		case "*":
			return "" + (a * b);
		case "%":
			return "" + (a % b);
		case "^":
			return "" + Math.pow(b, a);

		}

		return "ERROR";
	}

	// TODO: Convert to Radians
	public static String operate(double a, String op) {
		switch (op) {
		case "~":
			return "" + Math.round(a);
		case "c":
			return "" + Math.cos(Math.toRadians(a));
		case "s":
			return "" + Math.sin(Math.toRadians(a));
		case "t":
			return "" + Math.tan(Math.toRadians(a));
		case "acos":
			return "" + Math.acos(Math.toRadians(a));
		case "asin":
			return "" + Math.asin(Math.toRadians(a));
		case "atan":
			return "" + Math.atan(Math.toRadians(a));
		case "v":
			if (a < 0)
				return "ERROR";
			return "" + Math.sqrt(a);
		case "|":
			return "" + Math.abs(a);
		}

		return "ERROR";
	}

	public static boolean isNumeric(String number) {
		try {
			double num = Double.parseDouble(number);
		} catch (NumberFormatException | NullPointerException e) {
			return false;
		}

		return true;

	}

	public static void help() {
		System.out.println("Addition (+)");
		System.out.println("Subtraction (-)");
		System.out.println("Multiplication (*)");
		System.out.println("Division (/)");
		System.out.println("Modulus (%)");
		System.out.println("Exponentiation (^)");
		System.out.println("Rounding (~)");
		System.out.println("Absolute Value (|)");
	}

}
