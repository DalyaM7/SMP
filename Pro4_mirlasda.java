/*
File Name: Pro4_mirlasda.java
Programmer Name: Dalya Mirlas
Date: November 14
Project Description: 	The user can build a database of students and schools by loading them from input files. An input file for a student would 
						contain the students' names, GPAs, extracurricular scores, and their rankings of schools. The input file for schools
						would contain school-names, and GPA weights (each school automatically ranks each student based on its GPA weight). After 
						students and schools have been loaded, the user can perform the program's functionality of optimally matching the 
						students and schools using the Gale-Shapley algorithm. At any point, the user has the option of editing the data, and 
						printing it in an organized tabular format. Additionally, using the given data, the program will be able to display 
						whether the current matches in place that the program has made are stable as well as display the regrets of the students 
						and schools. Note that it is possible for a match to not be stable if edits have been made.
Formats for input files:
	Student input file:
	 - Each line contains the following: <Student Name>,<GPA>,<ES>,<ranking 1>,<ranking 2>,...,<ranking nSchools><\n>
	School input file:
	 - Each line contains the following: <School Name>,<GPA Weight><\n>
*/

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Pro4_mirlasda {

	public static void main(String[] args) throws IOException {

		String menuChoice = "";
		ArrayList<Student> S = new ArrayList<Student>();
		ArrayList<School> H = new ArrayList<School>();
    	SMPSolver matchingData = new SMPSolver(S, H);
    	boolean matchesExist = false;
		int nSchools = 0;
		int nStudents = 0;
		
		do {
			
			menuChoice = Menu.getMenuChoice("main");
			
			if (menuChoice.equals("L")) {	// Get file names from the user, then load the files.
				int nSchoolsBefore = H.size();	// this will be used to check if the number of schools after the nSchools function has been implemented is the same or greater than before.
				nSchools = loadSchools(H);
				// If schools have already been loaded before this occasion, clear all the students (NOTE: this is because rankings are set manually by user).
				if (nStudents > 0 && nSchools > nSchoolsBefore) {	// writing nStudents > 0 is not necessary but saves time since clearing an array is harder than comparing integer sizes
					matchingData.clearMatches();	// set all matches to -1 which is their default value
					for (int i = 0; i < nSchools; i++) {	// clear the rankings array of each school
						H.get(i).clearRankings();	// Make the rankings array not contain anything.
					}
					S.clear();	// clear the array of student objects
				}
				int nStudentsBefore = S.size();	// read comment for nSchoolsBefore
				nStudents = loadStudents(S, H);
				if (nSchools > 0 && nStudents > nStudentsBefore) {	// read comment for previous if-statement. In this case, ensuring that nSchools > 0 saves even more time than the previous if-statement, since a for-loop is involved. However, to be fair this will never happen since it is impossible to have students but not schools.
					for (int i = 0; i < nSchools; i++) {
						H.get(i).clearRankings();
					}
				}
				// calculate (or recalculate) rankings and set them to each school object
				setSchoolRankings(S, H);
			}
			
    		else if (menuChoice.equals("E")) {	// Go to a sub-area with its own menu where students and schools can be edited.
    			editData(S, H);
    		}
	   		
    		else if (menuChoice.equals("P")) {	// Print student and school information, including any existing matches and rankings.
    	    	// Note: there are two functions for printing error messages when the user wants to print the info. One is used before students are printed and one is used after students are printed. This is to remove any complications of printing new lines so as to make it general for all cases.
    			displayFirstPrintError(nStudents, nSchools);	// If there are neither students nor schools OR if there are schools but no students
    			if (nStudents > 0) {
    				System.out.println("\nSTUDENTS:");
            		printStudents(S, H);
            		System.out.println();	// the new line is being printed here as opposed to in the function because the same function is called when editing and in that case, the new line is not supposed to be there.
    			}
    			displaySecondPrintError(nStudents, nSchools);	// If there are students but no schools
    			if (nSchools > 0) {
    				System.out.println("SCHOOLS:");
        			printSchools(S, H);
        			System.out.println();
    			}
    		}
			
    		else if (menuChoice.equals("M")) {	// Perform matching using the Gale-Shapley algorithm.
    			matchingData.reset(S, H);
    			matchesExist = makeMatches(matchingData);
    		}
			
    		else if (menuChoice.equals("D")) {	// Display the matches and statistics, which are whether or not the matching is stable, average regret for students and schools, and average regret overall.
    			if (matchesExist) {
    				matchingData.print();
    			}
    			else {
    				System.out.println("\nERROR: No matches exist!\n");
    			}
    		}
			
    		else if (menuChoice.equals("R")) {	// Clear out the students and schools.
    			S.clear();
    			H.clear();
    			nStudents = 0;
    			nSchools = 0;
    			matchingData.reset(S, H);
    			matchesExist = false;
    			System.out.println("\nDatabase cleared!\n");
    		}
			
    		else {	// Quit the program.
    			menuChoice = "Q";
    			System.out.println("\nArrivederci!");
    		}
		} while (menuChoice.toUpperCase() != "Q");
	}
	
	//Load student information from a user-provided file and return the number of new students. New students are added to the list of existing students.
	public static int loadStudents(ArrayList<Student> S, ArrayList<School> H) throws IOException {
		System.out.println();
		String fileName = BasicFunctions.getFileName("Enter student file name (0 to cancel): ");
		if (fileName.equals("0")) {
			System.out.println("\nFile loading process canceled.\n");
			return S.size();
		}
		// We need to know how many schools there are so that we know how many student rankings of schools there are to loop through.
		// Also, if a student file contains a student that does not have the correct number of rankings of schools, or contains a ranking that is greater than the number of schools, that student will not be loaded.
		int nSchools = H.size();
		// load the file that the user named
		BufferedReader fin = new BufferedReader(new FileReader(fileName));
		String line;
		int numLines = 0;	// this will count the number of lines - i.e. number of students - in the file
		int notAdded = 0;	// this will keep track of the number of students that have not been added due to invalidity of the inputs for that student
		// This do-while loop goes through every line in the file and uses the information there to add student objects to the list of students. The loop stops when the line is empty.
		do {
			line = fin.readLine();
			if (line != null) {	//do the following operations on each student until there are no more students (i.e. until line = null)
				numLines++;		//as mentioned before, this keeps track of the number of students.
				String[] splitString = line.split(",");	// every time a comma is present in the line, the part of the string before the comma is added as an individual element in an array of strings. E.g.: Umona Hart,1.73,4,2,5,1,4,3 turns into [Umona Hart,1.73,4,2,5,1,4,3]
				String name = splitString[0];	// there is no way for this line to produce an error since even a number or special character can be a name. Therefore, this line is not included in try catch statement further on.
				// Initialize an indicator of whether a student can or cannot be loaded into the database, based on the numbers in the input file.
				boolean valid = true;
				// GPA, ES and rankings have to be converted to doubles or integers. In case this goes wrong, create a try-catch statement. (If the string couldn't be converted, then valid is false and the student cannot be added)
				try {
					double GPA = Double.parseDouble(splitString[1]);
					int ES = Integer.parseInt(splitString[2]);
					Student student = new Student(name, GPA, ES);
					// loop through the rankings section of spliString and add the rankings to the student object.
					int splitStringSize = splitString.length;
					for (int i = 3; i < splitStringSize; i++) {
						int ranking = Integer.parseInt(splitString[i]);	// convert the ranking from a string to an integer
							student.addToRankings(ranking);
						}
					valid = student.isValid(nSchools);
				
					if (valid) {
						S.add(student);
					}
					else {
						notAdded++;		// Reminder: this represents every time a student is not added
					}
				}
				// If the input could not be converted into the number format set out to do (integer or double), then the program will not add the student.
				catch (NumberFormatException e) {
					notAdded++;
				}
			}
		} while(line != null);	// keep looping until there are no more lines, meaning there are no more students
		fin.close();
		
		System.out.format("\n%d of %d students loaded!\n\n", numLines - notAdded, numLines);	// inform the user how many students have been added
		return S.size();
	}
	
	//Load school information from a user-provided file and return the number of new schools. New schools are added to the list of existing schools.
	public static int loadSchools(ArrayList<School> H) throws IOException {
		System.out.println();
		String fileName = BasicFunctions.getFileName("Enter school file name (0 to cancel): ");
		if (fileName.equals("0")) {
			System.out.println("\nFile loading process canceled.");
			return H.size();
		}
		BufferedReader fin = new BufferedReader(new FileReader(fileName));
		String line;
		int numLines = 0;
		int notAdded = 0;
		do {
			line = fin.readLine();
			if (line != null) {
				numLines++;
				String[] splitString = line.split(",");
				String name = splitString[0];
				boolean valid = true;	// initiate an indicator of whether a school can be loaded into the database.
				try {
					double alpha = Double.parseDouble(splitString[1]);
					School school = new School(name, alpha);
					valid = school.isValid();
					if (valid) {
						H.add(school);
					}
					else {
						notAdded++;
					}
				}
				catch (NumberFormatException e) {
					notAdded++;
				}
			}
		} while(line != null);
		fin.close();
		
		System.out.format("\n%d of %d schools loaded!\n", numLines - notAdded, numLines);
		
		return H.size();
		
	}
	
	// Calculate the schools' rankings of students.
	public static void setSchoolRankings(ArrayList<Student> S, ArrayList<School> H) {
		int nSchools = H.size();
    	for (int i = 0; i < nSchools; i++) {
    		H.get(i).calcRankings(S);
    	}
	}
	
    // Sub-area menu to edit students and schools.
    public static void editData(ArrayList<Student> S, ArrayList<School> H) throws IOException {
    	System.out.println();
    	// initiate the indicator of whether the user wants to quit the sub-menu to edit student/school data
    	boolean quit = false;
    	int nStudents = S.size();
    	int nSchools = H.size();
    	do {
	    	String choice = Menu.getMenuChoice("edit");
	    	// If the user chose S or H, before letting the user edit students or schools, make sure that nStudents/nSchools is greater than zero. If it is, let them edit.
	    	if (choice.equals("S")) {
	    		if (nStudents == 0) {
	    			System.out.println("\nERROR: No students are loaded!\n");
	    		}
	    		else {
	    			editStudents(S, H);
	    		}
	    	}
	    	else if (choice.equals("H")) {
	    		if (nSchools == 0) {
	    			System.out.println("\nERROR: No schools are loaded!\n");
	    		}
	    		else {
	        		editSchools(S, H);
	    		}
	    	}
	    	else if (choice.equals("Q")) {
	    		System.out.println();
	    		quit = true;
	    	}
    	} while (!quit);	// If the user wants to quite (i.e. quit is true), then the do-while loop will exit
    }	// End of editData
    
    
    // Sub-area to edit students. The edited student’s regret is updated if needed. Any existing school rankings and regrets are re-calculated after editing a student.
    public static void editStudents(ArrayList<Student> S, ArrayList<School> H) throws IOException{
    	int nStudents = S.size();
    	int nSchools = H.size();
    	// Initiate boolean variable to indicate whether the user wants to quit or edit again.
    	boolean editAgain = true;
    	// Initiate the student index that the user chooses to edit
    	int studentInd;
    	// Ask the user to choose a student to edit. Loop until the user doesn't want to edit any more students.
    	do {
    		printStudents(S, H);	// Display the students and their info for user's convenience
    		studentInd = BasicFunctions.getInteger("Enter student (0 to quit): ", 0, nStudents);	// Ask for student index
    		System.out.println();
    		if (studentInd != 0) {
    			// The user enters an index that is 1 whole number greater from the required index of the object that must be edited
    			S.get(studentInd - 1).editInfo(H);	// On top of editing the students, if their rankings are updated, the regret is also updated here.
    		}
    		else {
    			editAgain = false;
    		}
    	} while (editAgain);
    	
    	// re-calculate the rankings and regret of the schools. The program doesn't have to first check if there are in fact any schools because it is impossible to upload students if there haven't already been schools uploaded.
    	for (int i = 0; i < nSchools; i++) {
    		H.get(i).clearRankings();
	    	H.get(i).calcRankings(S);
	    	H.get(i).getRegret();
    	}
    } // End of editStudents

    
    //Sub-area to edit schools. Any existing rankings and regret for the edited school are updated.
    public static void editSchools(ArrayList<Student> S, ArrayList<School> H) throws IOException {
    	int nSchools = H.size();
    	boolean editAgain = true;
    	int schoolInd;
    	do {
    		printSchools(S, H);
    		schoolInd = BasicFunctions.getInteger("Enter school (0 to quit): ", 0, nSchools);
    		System.out.println();
    		if (schoolInd != 0) {
    			H.get(schoolInd - 1).editInfo(S);	// rankings and regret are also updated here if rankings have been set
    		}
    		else {
    			editAgain = false;
    		}
    	} while (editAgain);
    	
    	// In editStudents, school rankings were updated. Here, student rankings of school stay the same, so no need to update them.
    	
    } // End of editSchools
	
	
	

    // Print students to the screen, including matched school (if one exists).
    public static void printStudents(ArrayList<Student> S, ArrayList<School> H) {
    	System.out.println();
    	
    	int nStudents = S.size();
			
    	// It will be set up like a table, so first print the column headings
    	System.out.println(" #  Name                            GPA  ES  Assigned school            Preferred school order");
    	System.out.println("----------------------------------------------------------------------------------------------");
    	// Print the info out for each student one by one
    	for (int i = 0; i < nStudents; i++) {
    		if (i < 9) {
	    		System.out.print(" " + (i + 1) + ". ");	// Print the student number
    		}
    		else if (i >= 9) {
	    		System.out.print((i + 1) + ". ");	// Print the student number
    		}
    		S.get(i).print(H);	// Print the rest of the student info
    	}
    	System.out.println("----------------------------------------------------------------------------------------------");
    } // End of printStudents
    
    
    // Print schools to the screen, including matched student (if one exists).
    public static void printSchools(ArrayList<Student> S, ArrayList<School> H) {
    	System.out.println();
    	
    	int nSchools = H.size();
			
    	// It will be set up like a table, so first print the column headings
    	System.out.println(" #  Name                          Weight  Assigned student           Preferred student order");
    	System.out.println("--------------------------------------------------------------------------------------------");
    	// print the info for each school one by one

    	for (int i = 0; i < nSchools; i++) {
    		if (i < 9) {
	    		System.out.print(" " + (i + 1) + ". ");	// Print the school number
    		}
    		else if (i >= 9) {
	    		System.out.print((i + 1) + ". ");	// Print the school number
    		}
    		H.get(i).print(S); // Print the rest of the school info
    	}
		System.out.println("--------------------------------------------------------------------------------------------");
    } // End of printSchools
    
	
    // Display error messages messages when the user tries to print the student or school info. This takes into account when there are neither students, nor schools and when there are students but no schools.
    public static void displayFirstPrintError(int nStudents, int nSchools) {
		// cannot include errors/headings in print functions because there are some instances when headings aren't used when printing students/schools.
    	if (nStudents == 0 && nSchools == 0) {
    		System.out.println("\nERROR: No students are loaded!\n");
    		System.out.println("ERROR: No schools are loaded!\n");
    	}
    	else if (nStudents == 0) {
			System.out.println("\nERROR: No students are loaded!\n");
		}
    }
    
    // Display error messages messages when the user tries to print the student or school info. This takes into account when there are schools but no students.
    public static void displaySecondPrintError(int nStudents, int nSchools) {
		if (nSchools == 0 && nStudents != 0) {
			System.out.println("ERROR: No schools are loaded!\n");
		}
    }
    
    public static boolean makeMatches(SMPSolver matchingData) {
    	boolean matchesExist;
    	if (matchingData.matchingCanProceed()) {
    		matchesExist = matchingData.match();
    	}
    	else {
    		matchesExist = false;
    	}
    	return matchesExist;
    }
}