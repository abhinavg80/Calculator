import java.util.Stack;
import java.util.Scanner;
import java.util.HashMap;
import java.lang.Math;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/* Calculator Project - Abhinav Gopinath
 * NOTE: if using testCalc(), when calling calculate(), use false for the last two arguments (those are for fraction mode and Reverse Polish Notation)
 * Features:
 * - Additional Operations: Arctan, Arcsine, Arccosine, Factorial
 * - Reverse Polish Notation (separate mode)
 * - Fraction Mode (can only conduct Fraction operations in this mode) - also improper fractions only (NO mixed numbers)
 * - Unlimited Operations (also respects Order of Operations)
 * 
 */

public class Main {

	
	public static void main(String[] args) {
		startCalc();
		try {
			testCalc();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}

	// Welcomes User to Calculator, Gets Their Input, and Calculates It
	public static void startCalc() {
		Scanner input = new Scanner(System.in);
		String expression = "", result = "";
		boolean isRPN = false;
		boolean isFraction = false;

		System.out.println("Welcome to Calculator!");
		while (true) {
			System.out.println("Enter an expression: ");
			System.out.println("Or RPN (to switch to RPN mode), f (to switch to fraction mode), or normal (to switch to normal mode)");
			System.out.print("Enter help for help -");
			
			// Displays Current Mode
			if (isRPN) {
                System.out.println("Current Mode: RPN");
            } else if (isFraction) {
                System.out.println("Current Mode: Fraction - Note: Follow this Format: a/b (no spaces in between numerator, denominator, and slash)");
            } else {
                System.out.println("Current Mode: Normal");
            }
			
			// Accepts User's Expression
			expression = input.nextLine();
			
			// Changes Mode According to User's Input
			if (expression.toLowerCase().trim().equals("rpn")) {
                isRPN = true;
                isFraction = false;
                System.out.println("Mode changed to RPN");
                continue;
            } else if (expression.toLowerCase().trim().equals("normal")) {
                isRPN = false;
                isFraction = false;
                System.out.println("Mode changed to Normal");
                continue;
            } else if (expression.toLowerCase().trim().equals("f")) {
                isFraction = true;
                isRPN = false;
                System.out.println("Mode changed to Fraction");
                continue;
            }
			
			// Displays Help
			if (expression.toLowerCase().trim().equals("help")) {
				help();
				continue;
			}
			
			// Calculates the Result of the Expression 
			result = calculate(expression, isRPN, isFraction);

			// Displays Result
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

	/*Takes an Expression in Postfix notation and Calculates The Result - 
	/*This way, the calculator doesn't have to deal w/ parenthesis or order of operations 
	/*after it is converted into postfix notation*/
	public static String calculate(String exp, boolean isRPN, boolean isFraction) {
		// If User enters Quit, then it returns quit
		if (exp.toLowerCase().equals("quit")) {
			return "quit";
		}

		// If the Calculator is not in RPN mode, it converts the expression 
		if (!isRPN) {
			exp = toPostfix(exp, isFraction);
		}
		// If there is an error in the expression, ERROR is returned
		if (exp.equals("ERROR")) return "ERROR";

		Stack<String> calc = new Stack<>();
		Scanner tokenizer = new Scanner(exp);

		String num;
		double num1 = 0, num2 = 0;
		String op = "";
		String result = "";
		
		// Parses string, calculates as RPN
		
		/* Method Pushes Fractions or Numbers onto Stack. Once operator is found, it returns the result of the operators
		 * and operand, then pushes the result back into the loop. This process repeats until the expression
		 * doesn't have any more tokens remaining. 
		 * Returns the final result at the end
		 */
		while (tokenizer.hasNext()) {
			num = tokenizer.next();
			
			// If Fraction Mode is On, Then it treats the tokens as members of Fraction class, rather than Numbers.
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
                    	// Two Operand Operation
                        result = operate(num1, num2, op);
                    } else {
                    	// One Operand Operation
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

	// Converts Expression to Postfix notation, so it can be calculated like RPN and doesn't have to worry about order of operations or parenthesis
	public static String toPostfix(String exp, boolean isFraction) {
		Scanner infix = new Scanner(exp);
		Stack<String> convert = new Stack<>();
		// Creates map of operands and their respective precedences
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

		/* Loop Parses Expression until it has no more tokens.
		 * If the token is a number or fraction, it will add it to the result string. If the token is an operator, it pops the operators that
		 * are already in the stack and of a equal/higher precedence to the result string (if there are any). It then pushes that token onto the stack. 
		 * If the token is an open parenthesis, then it pushes all the following tokens onto the stack (b/c they have a higher precedence) until
		 * it reaches a closed parenthesis. Once it reaches, the closed parenthesis, the parenthetical token has been parsed and it gets popped
		 * from the stack into the result string.
		 * The result string is returned at the end.
		 * If an error occurs at any time in this process, an error is returned
		 */
		String op;
		while (infix.hasNext()) {
			op = infix.next();
			
			if (isNumeric(op) && (infix.hasNext("s") || infix.hasNext("c") || infix.hasNext("t") || infix.hasNext("v") || infix.hasNext("asin")
					|| infix.hasNext("acos") || infix.hasNext("atan") || infix.hasNext("|") || infix.hasNext("~") || infix.hasNext("!"))) {
				infix.close();
				return "ERROR";
			}

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

	// returns the result of a two operand operation
	public static String operate(double a, double b, String op) {
		switch (op) {
		case "+":
			return "" + (a + b);
		case "-":
			return "" + (b - a);
		case "/":
			if (a == 0) return "ERROR";
			return "" + (b / a);
		case "*":
			return "" + (a * b);
		case "%":
			if (a == 0) return "ERROR";
			return "" + (b % a);
		case "^":
			return "" + Math.pow(b, a);

		}

		return "ERROR";
	}

	// returns the result of a one operand operation
	public static String operate(double a, String op) {
		switch (op) {
		case "~":
			return "" + (double) Math.round(a);
		case "c":
			return "" + Math.cos(a);
		case "s":
			return "" + Math.sin(a);
		case "t":
			return "" + Math.tan(a);
		case "acos":
			return "" + Math.acos(a);
		case "asin":
			return "" + Math.asin(a);
		case "atan":
			return "" + Math.atan(a);
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
	
	// returns the factorial of a number
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

	// checks if a number is numeric
	public static boolean isNumeric(String number) {
		try {
			double num = Double.parseDouble(number);
		} catch (NumberFormatException | NullPointerException e) {
			return false;
		}

		return true;

	}
	
	// Utilizes Fraction class to perform operations of Fraction objects
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
	
	// Checks if a fraction matches the format (a/b)
	public static boolean isValidFraction(String fraction) {
        return fraction.matches("(\\d+)(\\/)(\\d+)");
    }
	
	// finds numerator of a fraction
	public static int findNumerator(String fraction) {
        String num = fraction.substring(0, fraction.indexOf("/"));
        return Integer.parseInt(num);
    }

	// finds denominator of a fraction
    public static int findDenominator(String fraction) {
        String num = fraction.substring(fraction.indexOf("/") + 1, fraction.length());
        return Integer.parseInt(num);
    }
	
    // returns if an operator is a single or double operand operator
	public static String operandType(String operator) {
		if (operator.equals("+") || operator.equals("-") || operator.equals("*") || operator.equals("/") || operator.equals("%") ||
				operator.equals("^")) {
			return "double";
		}
		return "single";
	}

	// prints a list of operations that the calculator can perform
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
		System.out.println("Fraction Addition, Subtraction, Multiplication, and Division");
	}
	
	public static void testCalc() throws FileNotFoundException{
        ArrayList<String> problems = new ArrayList<>();
        ArrayList<String> results = new ArrayList<>();
        // load problems from a file
        File fProblems = new File("problems.txt");
        Scanner sc = new Scanner(fProblems);        
        int count = 0;
        String line = "";
        int problemCount = 0;
        int resultCount = 0;
        while (sc.hasNextLine()){
            line = sc.nextLine();
            if (!line.startsWith("//") && !line.trim().equals("")){        // ignore comments at the beginning
                problems.add(line.substring(3).trim());
                problemCount++;
                if (sc.hasNextLine()){
                    line = sc.nextLine();
                    if (!line.startsWith("//") && !line.trim().equals("")){
                        results.add(line.substring(3).trim());
                        resultCount++;
                    }
                } 
                count++;
            }
        }
        if (problemCount == resultCount){
            // now run the tests
            for (int i=0; i<problemCount; i++){
                String prob = problems.get(i);
                String result = calculate(prob, false, false);
                if (result == null){
                    System.out.println("FAILED test " + i);
                    System.out.println("Expression: " + problems.get(i));
                    System.out.println("Expected result: " + results.get(i));
                    System.out.println("Actual: null String returned from calculate()");
                } else {
                    if (result.equals(results.get(i))){
                        System.out.println("PASSED test " + i);
                    } else {
                        System.out.println("FAILED test " + i);
                        System.out.println("Expression: " + problems.get(i));
                        System.out.println("Expected result: " + results.get(i));
                        System.out.println("Actual: " + result);
                    }
                }
                    
            }
        } else {
            System.out.println("problem file error");
        }    
            
    }

}


// Fraction Class: Fraction Instance has 2 fields: numerator (int) and denominator (int)
class Fraction {

	private int numerator;
	private int denominator;

	public Fraction(int numerator, int denominator) {
		this.numerator = numerator;
		this.denominator = denominator;
	}

	// Printing Methods - Returns Fraction in String Format
	private static String fractionToString(Fraction fraction) {
		if (wholeNumberCheck(fraction) > 0) {
			return Integer.toString(wholeNumberCheck(fraction));
		} 
		return fraction.getNumerator() + "/" + fraction.getDenominator();
	}

	// Operation Methods
	// Multiplies fractions
	public static String multiply(Fraction fraction, Fraction fraction2) {
		int newNumerator = fraction2.getNumerator() * fraction.getNumerator();
		int newDenominator = fraction2.getDenominator() * fraction.getDenominator();
		Fraction fraction3 = new Fraction(newNumerator, newDenominator);

		simplify(fraction3);
		return fractionToString(fraction3);
	}

	// Divides Fractions
	public static String divide(Fraction fraction, Fraction fraction2) {
		int newNumerator = fraction.getNumerator() * fraction2.getDenominator();
		int newDenominator = fraction.getDenominator() * fraction2.getNumerator();
		Fraction fraction3 = new Fraction(newNumerator, newDenominator);

		simplify(fraction3);
		return fractionToString(fraction3);
	}
	
	// Adds Fractions
	public static String add(Fraction fraction, Fraction fraction2) {
		int commonDenominator = lcm(fraction, fraction2);
		int newNumerator = fraction.getNumerator() * (commonDenominator / fraction.getDenominator());
		int newNumerator2 = fraction2.getNumerator() * (commonDenominator / fraction2.getDenominator());
		int addedNumerator = newNumerator + newNumerator2;
		Fraction fraction3 = new Fraction(addedNumerator, commonDenominator);
		
		simplify(fraction3);
		return fractionToString(fraction3);
		
	}
	
	// Subtracts Fractions
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
	// Checks if a fraction can be simplified to a whole number. If so, it returns the simplified fraction.
	private static int wholeNumberCheck(Fraction fraction) {
		int newFraction = 0;
		if (fraction.getNumerator() % fraction.getDenominator() == 0) {
			newFraction = fraction.getNumerator() / fraction.getDenominator();
		}
		return newFraction;
	}
	
	// Simplifies Fraction
	private static void simplify(Fraction fraction) {
		int gcd = gcd(fraction);
		
		if (gcd > 1) {
			int newNumerator = fraction.getNumerator() / gcd;
			int newDenominator = fraction.getDenominator() / gcd;
			fraction.setNumerator(newNumerator);
			fraction.setDenominator(newDenominator);
		}
	}

	//Finds greatest common divisor of numerator and denominator of fraction to help simplify fraction
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
	
	// finds least common multiple of 2 fractions' denominators in order to help w/ addition and subtraction
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

