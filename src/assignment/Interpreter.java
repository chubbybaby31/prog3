package assignment;

import java.io.IOException;
import java.io.FileReader;
import java.util.*;

import assignment.Critter.HungerLevel;

/**
 * Responsible for loading critter species from text files and interpreting the
 * simple Critter language.
 * 
 * For more information on the purpose of the below two methods, see the
 * included API/ folder and the project description.
 */
public class Interpreter implements CritterInterpreter {

	private int jumpCalculator(String argument, int line, Critter c) {
		if (argument.charAt(0) == '+') {
			line += Integer.parseInt(argument.substring(1));
		} else if (argument.charAt(0) == '-') {
			line -= Integer.parseInt(argument.substring(1));
		} else {
			line = Integer.parseInt(argument) - 1; // Accounting for 0-based indexing
		}

		if (line < 0 || line >= c.getCode().size()) {
			System.err.println("Can not jump to line: " + line);
			System.exit(-1);
		}

		return line;
	}

	private int[] parseArgs(int numberOfArgs, String[] args) {
		String command = args[0];
		if (args.length - 1 != numberOfArgs) {
			System.err.println("Incorrect number of arguments passed for command: " + command + " | expected: " + numberOfArgs + " received: " + (args.length - 1));
		} 
		int numericalArgs[] = new int[numberOfArgs];
		for (int i = 1; i < args.length; i++) {
			try {
				numericalArgs[i - 1] = Integer.parseInt(args[i]);
			} catch (NumberFormatException e){
				System.err.println("Failed to parse: " + args[i] + " | from command: " + command);
				System.exit(-1);
			}
		}
		return numericalArgs;
	}

	private int[] parseArgs(int numberOfArgs, String[] args, int line, int jumpIndex, Critter c) {
		String command = args[0];
		if (args.length - 1 != numberOfArgs) {
			System.err.println("Incorrect number of arguments passed for command: " + command + " | expected: " + numberOfArgs + " received: " + (args.length - 1));
		} 
		int numericalArgs[] = new int[numberOfArgs];
		for (int i = 1; i < args.length; i++) {
			try {
				if (i - 1 != jumpIndex) {
					numericalArgs[i - 1] = Integer.parseInt(args[i]);
				} else {
					numericalArgs[i - 1] = jumpCalculator(args[i], line, c);
				}
			} catch (NumberFormatException e){
				System.err.println("Failed to parse: " + args[i] + " | from command: " + command);
				System.exit(-1);
			}
		}
		return numericalArgs;
	}

	public void executeCritter(Critter c) {
		int line = 0;
		List<String> code = c.getCode();
		String args[];
		int[] numArgs;
		String function;
		while (true) {
			line = c.getNextCodeLine();
			if (line >= code.size()) {
				break;
			}
			args = code.get(line).split(" ");
			function = args[0];
			switch(function) {
				case "hop":
					c.hop();
					c.setNextCodeLine(line + 1);
					return;
				
				case "left":
					c.left();
					c.setNextCodeLine(line + 1);
					return;
				
				case "right":
					c.right();
					c.setNextCodeLine(line + 1);
					return;

				case "infect":
					if (args.length == 1) {
						c.infect();
					} else {
						numArgs = parseArgs(1, args, line, 0, c);
						c.infect(numArgs[0]);
					}
					c.setNextCodeLine(line + 1);
					return;
				
				case "eat":
					c.eat();
					c.setNextCodeLine(line + 1);
					return;
				
				case "go":
					numArgs = parseArgs(1, args, line, 0, c);
					c.setNextCodeLine(numArgs[0]);
					break;

				case "ifrandom":
					numArgs = parseArgs(1, args, line, 0, c);
					if (c.ifRandom()) {
						c.setNextCodeLine(numArgs[0]);
					} else {
						c.setNextCodeLine(line + 1);
					}
					break;
				
				case "ifhungry":
					if (c.getHungerLevel() == HungerLevel.HUNGRY || c.getHungerLevel() == HungerLevel.STARVING) {
						numArgs = parseArgs(1, args, line, 0, c);
						c.setNextCodeLine(numArgs[0]);
					} else {
						c.setNextCodeLine(line + 1);
					}
					break;
				
				case "ifstarving":
					if (c.getHungerLevel() == HungerLevel.STARVING) {
						numArgs = parseArgs(1, args, line, 0, c);
						c.setNextCodeLine(numArgs[0]);
					} else {
						c.setNextCodeLine(line + 1);
					}
					break;
				
				case "ifempty":
					numArgs = parseArgs(2, args, line, 1, c);
					if (c.getCellContent(numArgs[0]) == Critter.EMPTY) {
						c.setNextCodeLine(numArgs[1]);
					} else {
						c.setNextCodeLine(line + 1);
					}
					break;
				
				case "ifally":
					numArgs = parseArgs(2, args, line, 1, c);
					if (c.getCellContent(numArgs[0]) == Critter.ALLY) {
						c.setNextCodeLine(numArgs[1]);
					} else {
						c.setNextCodeLine(line + 1);
					}
					break;
				
				case "ifenemy":
					numArgs = parseArgs(2, args, line, 1, c);
					if (c.getCellContent(numArgs[0]) == Critter.ENEMY) {
						c.setNextCodeLine(numArgs[1]);
					} else {
						c.setNextCodeLine(line + 1);
					}
					break;
				
				case "ifwall":
					numArgs = parseArgs(2, args, line, 1, c);
					if (c.getCellContent(numArgs[0]) == Critter.WALL) {
						c.setNextCodeLine(numArgs[1]);
					} else {
						c.setNextCodeLine(line + 1);
					}
					break;
				
				case "ifangle":
					numArgs = parseArgs(3, args, line, 2, c);
					if (c.getOffAngle(numArgs[0]) == numArgs[1]) {
						c.setNextCodeLine(numArgs[2]);
					} else {
						c.setNextCodeLine(line + 1);
					}
					break;
				
				case "write":
					numArgs = parseArgs(2, args);
					c.setReg(numArgs[0], numArgs[1]);
					c.setNextCodeLine(line + 1);
					break;

				case "add":
					numArgs = parseArgs(2, args);
					int sum = c.getReg(numArgs[0]) + c.getReg(numArgs[1]);
					c.setReg(numArgs[0], sum);
					c.setNextCodeLine(line + 1);
					break;
				
				case "sub":
					numArgs = parseArgs(2, args);
					int diff = c.getReg(numArgs[0]) - c.getReg(numArgs[1]);
					c.setReg(numArgs[0], diff);
					c.setNextCodeLine(line + 1);
					break;
				
				case "inc":
					numArgs = parseArgs(1, args);
					c.setReg(numArgs[0], c.getReg(numArgs[0]) + 1);
					c.setNextCodeLine(line + 1);
					break;
				
				case "dec":
					numArgs = parseArgs(1, args);
					c.setReg(numArgs[0], c.getReg(numArgs[0]) - 1);
					c.setNextCodeLine(line + 1);
					break;
				
				case "iflt":
					numArgs = parseArgs(3, args, line, 2, c);
					if (c.getReg(numArgs[0]) < c.getReg(numArgs[1])) {
						c.setNextCodeLine(numArgs[2]);
					} else {
						c.setNextCodeLine(line + 1);
					}
					break;

				case "ifgt":
					numArgs = parseArgs(3, args, line, 2, c);
					if (c.getReg(numArgs[0]) > c.getReg(numArgs[1])) {
						c.setNextCodeLine(numArgs[2]);
					} else {
						c.setNextCodeLine(line + 1);
					}
					break;
				
				case "ifeq":
					numArgs = parseArgs(3, args, line, 2, c);
					if (c.getReg(numArgs[0]) == c.getReg(numArgs[1])) {
						c.setNextCodeLine(numArgs[2]);
					} else {
						c.setNextCodeLine(line + 1);
					}
					break;
				default:
					System.err.println("Unkown command: " + args[0]);
					c.setNextCodeLine(line + 1);
			}
		}
	}

	public CritterSpecies loadSpecies(String filename) throws IOException {
		FileReader reader = new FileReader(filename);
		int character = reader.read();
		StringBuffer input = new StringBuffer();

		while (character != -1) {
			input.append((char) character);
			character = reader.read();
		}
		reader.close();

		String[] lines = input.toString().split("\n\n")[0].split("\n");
		ArrayList<String> commands = new ArrayList<String>();
		String name = "";
		for (int i = 0; i < lines.length; i++) {
			if (i == 0) {
				name = lines[i];
			} else {
				commands.add(lines[i]);
			}
		}
		
		CritterSpecies returnval = new CritterSpecies(name, commands);

		return returnval;
	}
}
