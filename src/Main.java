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
		boolean isFraction = false;

		System.out.println("Welcome to Calculator!");
		while (true) {
			System.out.println("Enter an expression (RPN to change to RPN mode or normal to change back) (help for a list of options): ");
			
			if (isRPN) {
                System.out.println("Current Mode: RPN");
            } else if (isFraction) {
                System.out.println("Current Mode: Fraction - Note: Do Not Enter Spaces In Fractions");
            } else {
                System.out.println("Current Mode: Normal");
            }
			
			expression = input.nextLine();
			
			if (expression.toLowerCase().equals("rpn")) {
                isRPN = true;
                isFraction = false;
                System.out.println("Mode changed to RPN");
                continue;
            } else if (expression.toLowerCase().equals("normal")) {
                isRPN = false;
                isFraction = false;
                System.out.println("Mode changed to Normal");
                continue;
            } else if (expression.toLowerCase().equals("f")) {
                isFraction = true;
                isRPN = false;
                System.out.println("Mode changed to Fraction");
                continue;
            }
			
			if (expression.toLowerCase().equals("help")) {
				help();
				continue;
			}
			
			result = calculate(expression, isRPN, isFraction);

			if (result.equals("ERROR")) {
				System.out.println("There was an error.");
			} else if (result.equals("quit")) {
				break;
			} else {
				System.out.println(expression + " = " + result);
			}
		}
		System.out.println("Thank You For Using Calculator!");
		input.close();
	}

	public static String calculate(String exp, boolean isRPN, boolean isFraction) {
		if (exp.toLowerCase().equals("quit")) {
			return "quit";
		}

		if (!isRPN) {
			exp = toPostfix(exp, isFraction);
		}
		
		if (exp.equals("ERROR")) return "ERROR";

		Stack<String> calc = new Stack<>();
		Scanner tokenizer = new Scanner(exp);

		String num;
		double num1 = 0, num2 = 0;
		String op = "";
		String result = "";
		while (tokenizer.hasNext()) {
			num = tokenizer.next();
			
			if (isFraction) {
                Fraction f1, f2;
                int numerator, denominator;
                if (isValidFraction(num)) {
                    calc.push(num);
                } else {
                    String n1 = "", n2 = "";
                    if (calc.isEmpty()) {
                    	tokenizer.close();
                    	return "ERROR";
                    }
                    if (isValidFraction(calc.peek())) {
                        n1 = calc.pop();
                        numerator = findNumerator(n1);
                        denominator = findDenominator(n1);
                        f1 = new Fraction(numerator, denominator);
                    } else {
                    	tokenizer.close();
                        return "ERROR";
                    }
                    
                    if (calc.isEmpty()) {
                    	tokenizer.close();
                    	return "ERROR";
                    }

                    if (isValidFraction(calc.peek())) {
                        n2 = calc.pop();
                        numerator = findNumerator(n2);
                        denominator = findDenominator(n2);
                        f2 = new Fraction(numerator, denominator);
                    } else {
                    	tokenizer.close();
                        return "ERROR";
                    }

                    op = num;
                    result = fractionOperate(f1, f2, op);
                    if (result.equals("ERROR")) {
                    	tokenizer.close();
                    	return "ERROR";
                    }

                    calc.push(result);
                }
            } else {
                if (isNumeric(num)) {
                    calc.push(num);
                } else {
                    try {
                        num1 = Double.parseDouble(calc.pop());
                    } catch (Exception e) {
                        tokenizer.close();
                        return "ERROR";
                    }
                    op = num;
                    if (!calc.isEmpty() && operandType(op).equals("double")) {
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
			
		}
		tokenizer.close();
		if (result.equals("")) {
			return "ERROR";
		}
		
		return result;
	}

	public static String toPostfix(String exp, boolean isFraction) {
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
		operators.put("!", 3);

		String postfix = "";

		String op;
		while (infix.hasNext()) {
			op = infix.next();

			if (isFraction && isValidFraction(op)) {
				postfix += op + " ";
			} else if (isNumeric(op)) {
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
				try {
					convert.pop();
				} catch (Exception e) {
					infix.close();
					return "ERROR";
				}
				
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
		case "!":
			if (a != Math.floor(a)) return "ERROR";
			return "" + factorial((int) a, new int[(int) a + 1]);
		}

		return "ERROR";
	}
	
	
	public static int factorial(int n, int[] memo) {
		int result;
		if (memo[n] != 0) return memo[n];
		
		if (n == 1) {
			result = 1;
		} else {
			result = n * factorial(n - 1, memo);
		}
		memo[n] = result;
		return result;
	}

	public static boolean isNumeric(String number) {
		try {
			double num = Double.parseDouble(number);
		} catch (NumberFormatException | NullPointerException e) {
			return false;
		}

		return true;

	}
	
	public static String fractionOperate(Fraction f1, Fraction f2, String op) {
        switch (op) {
            case "+":
                return "" + Fraction.add(f1, f2);
            case "-":
                return "" + Fraction.subtract(f2, f1);
            case "*":
                return "" + Fraction.multiply(f1, f2);
            case "/":
                return "" + Fraction.divide(f2, f1);
            default:
                return "ERROR";
        }
    }
	
	public static boolean isValidFraction(String fraction) {
        return fraction.matches("(\\d+)(\\/)(\\d+)");
    }
	
	public static int findNumerator(String fraction) {
        String num = fraction.substring(0, fraction.indexOf("/"));
        return Integer.parseInt(num);
    }

    public static int findDenominator(String fraction) {
        String num = fraction.substring(fraction.indexOf("/") + 1, fraction.length());
        return Integer.parseInt(num);
    }
	
	public static String operandType(String operand) {
		if (operand.equals("+") || operand.equals("-") || operand.equals("*") || operand.equals("/") || operand.equals("%") ||
				operand.equals("^")) {
			return "double";
		}
		return "single";
	}

	public static void help() {
		System.out.println("These are all the operations supported by the Calculator:");
		System.out.println("Addition (+)");
		System.out.println("Subtraction (-)");
		System.out.println("Multiplication (*)");
		System.out.println("Division (/)");
		System.out.println("Modulus (%)");
		System.out.println("Exponentiation (^)");
		System.out.println("Rounding (~)");
		System.out.println("Absolute Value (|)");
		System.out.println("Square Root (v)");
		System.out.println("Sin (s)");
		System.out.println("Cos (c)");
		System.out.println("Tan (t)");
		System.out.println("Arcsin (asin)");
		System.out.println("Arccos (acos)");
		System.out.println("Arccos (acos)");
		System.out.println("Factorial (!)");
	}

}


class Fraction {

	private int numerator;
	private int denominator;

	public Fraction(int numerator, int denominator) {
		this.numerator = numerator;
		this.denominator = denominator;
	}

	// Printing Methods
	private static String fractionToString(Fraction fraction) {
		if (wholeNumberCheck(fraction) > 0) {
			return Integer.toString(wholeNumberCheck(fraction));
		} 
		return fraction.getNumerator() + "/" + fraction.getDenominator();
	}

	// Operation Methods
	public static String multiply(Fraction fraction, Fraction fraction2) {
		int newNumerator = fraction2.getNumerator() * fraction.getNumerator();
		int newDenominator = fraction2.getDenominator() * fraction.getDenominator();
		Fraction fraction3 = new Fraction(newNumerator, newDenominator);

		simplify(fraction3);
		return fractionToString(fraction3);
	}

	public static String divide(Fraction fraction, Fraction fraction2) {
		int newNumerator = fraction.getNumerator() * fraction2.getDenominator();
		int newDenominator = fraction.getDenominator() * fraction2.getNumerator();
		Fraction fraction3 = new Fraction(newNumerator, newDenominator);

		simplify(fraction3);
		return fractionToString(fraction3);
	}
	
	public static String add(Fraction fraction, Fraction fraction2) {
		int commonDenominator = lcm(fraction, fraction2);
		int newNumerator = fraction.getNumerator() * (commonDenominator / fraction.getDenominator());
		int newNumerator2 = fraction2.getNumerator() * (commonDenominator / fraction2.getDenominator());
		int addedNumerator = newNumerator + newNumerator2;
		Fraction fraction3 = new Fraction(addedNumerator, commonDenominator);
		
		simplify(fraction3);
		return fractionToString(fraction3);
		
	}
	
	public static String subtract(Fraction fraction, Fraction fraction2) {
		int commonDenominator = lcm(fraction, fraction2);
		int newNumerator = fraction.getNumerator() * (commonDenominator / fraction.getDenominator());
		int newNumerator2 = fraction2.getNumerator() * (commonDenominator / fraction2.getDenominator());
		int subtractedNumerator = newNumerator - newNumerator2;
		Fraction fraction3 = new Fraction(subtractedNumerator, commonDenominator);
		
		simplify(fraction3);
		return fractionToString(fraction3);
	}
	
	// Operation Helpers
	private static int wholeNumberCheck(Fraction fraction) {
		int newFraction = 0;
		if (fraction.getNumerator() % fraction.getDenominator() == 0) {
			newFraction = fraction.getNumerator() / fraction.getDenominator();
		}
		return newFraction;
	}
	
	private static void simplify(Fraction fraction) {
		int gcd = gcd(fraction);
		
		if (gcd > 1) {
			int newNumerator = fraction.getNumerator() / gcd;
			int newDenominator = fraction.getDenominator() / gcd;
			fraction.setNumerator(newNumerator);
			fraction.setDenominator(newDenominator);
		}
	}

	private static int gcd(Fraction fraction) {
		int numerator = fraction.getNumerator();
		int denominator = fraction.getDenominator();
		
		if (numerator < 0) {
			numerator *= -1;
		}
		
		if (denominator < 0) {
			denominator *= -1;
		}
		
		int gcd = 1;
		int min = numerator < denominator ? numerator : denominator;
		
		for (int i = 1; i <= min; i++) {
			if (numerator % i == 0 && denominator % i == 0) {
				gcd = i;
			}
		}
		return gcd;
	}
	
	private static int lcm(Fraction fraction, Fraction fraction2) {
		int denominator = fraction.getDenominator();
		int denominator2 = fraction2.getDenominator();
		int max = denominator * denominator2;
		int lcm = max;
		
		for (int i = max; i >= 1; i--) {
			if (i % denominator == 0 && i % denominator2 == 0) {
				lcm = i;
			}
		}
		
		return lcm;
	}

	// Getters and Setters
	private int getNumerator() {
		return numerator;
	}

	public void setNumerator(int numerator) {
		this.numerator = numerator;
	}

	private int getDenominator() {
		return denominator;
	}

	public void setDenominator(int denominator) {
		this.denominator = denominator;
	}
}

