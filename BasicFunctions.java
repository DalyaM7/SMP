import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class BasicFunctions {
	
	public static BufferedReader cin = new BufferedReader(new InputStreamReader(System.in)); //object used to get user input
	
	// Get a string - there are no errors possible for this
	public static String getString(String prompt) throws IOException {
		System.out.print(prompt);
		String str = cin.readLine();
		return str;
	}
	
	// Get an integer in the range [LB, UB] from the user. Prompt the user repeatedly until a valid value is entered.
	public static int getInteger(String prompt, int LB, int UB) {
		String intValue = "int";	// This string will be used for printing the error message: When printing the error message, it must be passed on what type of number we are dealing with
		int x = 0;
		boolean valid;
		do {
			valid = true;
			// ask the user for an integer
			System.out.print(prompt);
			
			try {
				// try converting the string to an integer
				x = Integer.parseInt(cin.readLine());
				// check if integer is within bounds
				if (x < LB || x > UB) {
					printErrorMsg(intValue, String.valueOf(LB), String.valueOf(UB));
					
					valid = false;
				}
				
			}
			catch (NumberFormatException e) {
				// print out error message - this will be if user's input is not an integer (since we already dealt with out of bounds)
				printErrorMsg(intValue, String.valueOf(LB), String.valueOf(UB));
				valid = false;
			}
			
			catch (IOException e) {
				System.out.println("ERROR: IO exception!\n");
				valid = false;
			}
			
		} while (!valid);

		return x;
	} // end of getInteger()
	
	
	//Get a real number in the range [LB, UB] from the user. Prompt the user repeatedly until a valid value is entered.
	public static double getDouble(String prompt, double LB, double UB) {
		String doubleValue = "double";	// This string will be used for printing the error message: When printing the error message, it must be passed on what type of number we are dealing with
		double x = 0;
		boolean valid = true;
		// get the value from the user and keep on iterating until user enters valid number
		do {
			valid = true;
			System.out.print(prompt);
			try {
				x = Double.parseDouble(cin.readLine());
				// x cannot be outside of the range of valid numbers
				if (x < LB || x > UB) {
					// print out error message
					printErrorMsg(doubleValue, String.valueOf(LB), String.valueOf(UB));
					valid = false;
				}
			}
			
			catch (NumberFormatException e) {
				// print out error message. Since we checked for out of bounds, this is to see if the character ended is in fact a double
				printErrorMsg(doubleValue, String.valueOf(LB), String.valueOf(UB));
				valid = false;
			}
			
			catch (IOException e) {
				System.out.println("ERROR: IO exception!\n");
				valid = false;
			}
			
		} while (!valid);
		return x;
	} // end of getDouble()
	
	
	// prints an error message for when the user types an incorrect number
	// Arguments: numberType is the type of number we are dealing with (either integer or double), LB and UB are the lower and upper bounds of the range of numbers the user is allowed to input.
	// LB and UB must be a string because we don't know if it is a double or an integer
	public static void printErrorMsg(String numberType, String LB, String UB) {
		// This error message will be for integers
		if (numberType.equals("int")) {
			
			// if LB or UB equals + or - infinity, change their string values to that
			if (LB.equals(String.valueOf(Integer.MIN_VALUE))) {
				LB = "-infinity";
			}
			
			if (UB.equals(String.valueOf(Integer.MAX_VALUE))) {
				UB = "infinity";
			}
			System.out.format("\nERROR: Input must be an integer in [%s, %s]!\n\n", LB, UB);
		}
		
		// This error message will be for real numbers
		else if (numberType.equals("double")) {
			// If an integer is a double, it will be displayed with one decimal place (e.g. "1.0") when converted to a string, but we need two decimal places.
			if (LB.length() == 3) {
				LB += "0";
			}
			if (UB.length() == 3) {
				UB += "0";
			}
			
			// if LB or UB equals + or - infinity, change their string values to that.
			if (LB.equals(String.valueOf(-Double.MAX_VALUE))) {
				LB = "-infinity";
			}
		
			if (UB.equals(String.valueOf(Double.MAX_VALUE))) {
				UB = "infinity";
			}
			System.out.format("\nERROR: Input must be a real number in [%s, %s]!\n\n", LB, UB);
		}
	} //end of printErrorMsg

	// When printing the student/school info, find the number of spaces that must be printed after a student or school name in the table to ensure that everything is aligned.
	public static String getSpaces(int nameLength) {
    	// Use 26 because that is the smallest number of spaces between a name and a following table element. Specifically, in the school info table, it is the spaces between assigned student and preferred school order.
		// To make up for the lack of spaces in the other areas of the table, spaces were manually entered
		int numSpaces = 26 - nameLength;
    	String spaces = "";
    	for (int i = 0; i <= numSpaces; i++) {
    		spaces += " ";
    	}
    	return spaces;
	}
	
	public static String getFileName(String prompt) throws IOException {
		boolean valid = true;
		String fileName = "";
		do {
			System.out.print(prompt);
			fileName = cin.readLine();
			if (fileExists(fileName) || fileName.equals("0")) {
				valid = true;
			}
			else {
				System.out.println("\nERROR: File not found!\n");
				valid = false;
			}
		} while (!valid);
		return fileName;
	}
	
    // Check if a filename provided by the user exists
	public static boolean fileExists(String fileName) {
		File file = new File(fileName);
		boolean exists = file.exists();
		if (!exists) {
			return false;
		}
		else {
			return true;
		}
	}
}