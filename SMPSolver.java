import java.util.ArrayList;

public class SMPSolver {
	// member fields
	private ArrayList<Student> S = new ArrayList<Student>();	// suitors
	private ArrayList<School> R = new ArrayList<School>();	// receivers
	private double avgSuitorRegret;		// average suitor regret
	private double avgReceiverRegret;	// average receiver regret
	private double avgTotalRegret;		// average total regret
	private boolean matchesExist;		// whether or not matches exist
	
	
	// Used to create an SMPSolver object
	private void initSMPSolver(ArrayList<Student> S, ArrayList<School> R) {
		this.S = S;
		this.R = R;
		this.avgSuitorRegret = 0;
		this.avgReceiverRegret = 0;
		this.avgTotalRegret = 0;
		this.matchesExist = false;
	}
	
	// constructors
	public SMPSolver() {
		this.initSMPSolver(new ArrayList<Student>(), new ArrayList<School>());
	}
	public SMPSolver(ArrayList<Student> S, ArrayList<School> R) {
		this.initSMPSolver(S, R);
	}
	
	/*
	 NOTICE: If this code must be made more general in the future so as to be able to be used for any class of suitors and receivers, only the 
	 getters and setters must be changed, since the actual algorithms in this program do not explicitly use methods from any one specific class of 
	 suitor or receiver but rather only use methods from this class. All getters and setters that reference other classes have ///////////////////
	 at the end of the line so that they can be easily traceable.
	 */
	
	// getters
	public int getSuitorRanking(int suitor, int ranking) { return this.S.get(suitor).getRanking(ranking); }	//////////////////
	public int getReceiverRanking(int receiver, int ranking) { return this.R.get(receiver).getRanking(ranking); }	//////////////////
	public int findSuitorRankingByID(int suitor, int receiver) { return this.S.get(suitor).findRankingByID(receiver + 1); }	//////////////////
	public int findReceiverRankingByID(int suitor, int receiver) { return this.R.get(receiver).findRankingByID(suitor + 1); }	//////////////////
	public int getSuitor(int receiver) { return this.R.get(receiver).getStudent(); }	//////////////////
	public int getReceiver(int suitor) { return this.S.get(suitor).getSchool(); }	//////////////////
	public double getSuitorRegret(int i) { return this.S.get(i).getRegret(); }	//////////////////
	public double getReceiverRegret(int i) { return this.R.get(i).getRegret(); }	//////////////////
	public String getSuitorName(int i) { return this.S.get(i).getName(); }	//////////////////
	public String getReceiverName(int i) { return this.R.get(i).getName(); }	//////////////////
	public double getAvgSuitorRegret() { return this.avgSuitorRegret; }
	public double getAvgReceiverRegret() { return this.avgReceiverRegret; }
	public double getAvgTotalRegret() { return this.avgTotalRegret; }
	public boolean matchesExist() {												//////////////////
		if (S.get(0).getSchool() == -1) {	// Just check the first receiver, because if any receiver has a match, matches exist for both suitors and receivers
			this.matchesExist = false; // matches do not exist
		}
		else {
			this.matchesExist = true; // matches do exist
		}
		return matchesExist;
	}
	
	public boolean suitorRankingsSet() {	// check if rankings have been set for the suitor
		boolean set = true;
		int nSuitors = this.S.size();
		for (int i = 0; i < nSuitors; i++) {
			if (this.S.get(i).getNumRankings() > 0) {	//////////////////
				set = true;
			}
			else {
				set = false;
				break;
			}
		}
		return set;
	}

	public boolean receiverRankingsSet() {	// check if rankings have been set for the receiver
		int nReceivers = this.R.size();
		boolean set = true;
		for (int i = 0; i < nReceivers; i++) {
			if (this.R.get(i).getNumRankings() > 0) {
				set = true;
			}
			else {
				set = false;
				break;
			}
		}
		return set;
	}
	
	// Setters
	public void setSuitor(int suitor, int receiver) { this.R.get(receiver).setStudent(suitor); }
	public void setReceiver(int suitor, int receiver) { this.S.get(suitor).setSchool(receiver); }
	public void calcAndSetRegrets() {	// Once matching has been performed, set the regrets. It doesn't matter whether the loop goes through nSuitors or nReceivers since they are the same.
    	int nSuitors = this.S.size();
		for (int i = 0; i < nSuitors; i++) {
    		this.S.get(i).calcAndSetRegret();	////////////////////
    		this.R.get(i).calcAndSetRegret();	////////////////////
    	}
	}
	public void setSuitorRegret(double r) {
		this.avgSuitorRegret = r;
	}
	public void setReceiverRegret(double r) {
		this.avgReceiverRegret = r;
	}
	public void setTotalRegret(double r) {
		this.avgTotalRegret = r;
	}
	
	// reset everything with new suitors and receivers
	public void reset(ArrayList<Student> S, ArrayList<School> R) {
		this.S = S;
		this.R = R;
	}
	
	// clear all matches
	public void clearMatches() {
		int nSuitors = this.S.size();
		for (int i = 0; i < nSuitors; i++) {
			this.S.get(i).setSchool(-1);
			this.R.get(i).setStudent(-1);
		}
	}
	
	// methods for matching
	public boolean match() {	// Gale-Shapley algorithm to match; students are suitors
		long start = System.currentTimeMillis (); // Get current time
		this.clearMatches();
		boolean freeSuitorExists = true, proposalAccepted = false;
		int nSuitors = this.S.size();
		int nSuitorsRankings = nSuitors;	// even though it is the same numbers as the suitors, the following for loops will technically be operating on two different things, so for clarity's sake, they will have different names depending on what will be operated on.
		while (freeSuitorExists) {	// create a match for every suitor until there are no more free suitors
			// loop through each suitor and match each one to a receiver.
			for (int i = 0; i < nSuitors; i++) {
				if (this.getReceiver(i) == -1) {	// check if suitor is already engaged. If yes, then skip the suitor in question.
					for (int j = 0; j < nSuitorsRankings; j++) {	// loop through the suitors' rankings of receivers (which are the same number as the suitors)
						// Find the highest ranked receiver who the suitor has not yet proposed to
						// start by finding the index of the suitor's (i) first choice of receiver (j) which would be the zeroth receiver index - when "j" = 0. Keep on looping and increasing the "j"s until a receiver is free. Subtract 1 because the rankings array starts from rank 1, but list indices start from index 0.
						int receiver = this.getSuitorRanking(i, j) - 1;
						proposalAccepted = makeProposal(i, receiver);
						if (proposalAccepted) {
							// Make the previous fiance of the woman free, now that she dumped him. But first make sure she did in fact have an old fiance.
							int oldFianceOfReceiver = this.getSuitor(receiver);
							if (oldFianceOfReceiver != -1) {
								this.setReceiver(oldFianceOfReceiver, -1);
							}
							makeEngagement(i, receiver);
							break;
						}
					}
				}
			}
			// Go through every suitor and see whether any are still unmatched to a receiver
			for (int i = 0; i < nSuitors; i++) {
				if (this.getReceiver(i) == -1) {
					freeSuitorExists = true;
					break;	// once a free suitor has been found, stop going through the suitors and reiterate the proposal process again.
				}
				else {
					freeSuitorExists = false;	// Once the program gets to this point, all suitors and receivers will have had been matched
				}
			}
		}
		long elapsedTime = System.currentTimeMillis() - start; // Get elapsed time in ms
		this.calcRegrets();
		this.printStats();
		System.out.format("%d matches made in %dms!\n\n", nSuitors, elapsedTime);	// Let the user know how much time the matching process took
		return this.matchesExist();
	}
	
	private boolean makeProposal(int suitor, int receiver) {	// suitor proposes
		boolean proposalAccepted = false;
		if (this.getSuitor(receiver) == -1) {
			proposalAccepted = true;
		}
		// if the receiver's ranking of the suitor who just proposed is LESS than the receiver's ranking of the current fiance, the receiver dumps the old one has goes with the new one.
		else if (this.findReceiverRankingByID(suitor, receiver) < this.findReceiverRankingByID(this.getSuitor(receiver), receiver)) {
		//		receiverObject.getRanking(suitor) < receiverObject.getRanking(receiverObject.getStudent())) {
			proposalAccepted = true;
		}
		else {
			proposalAccepted = false;
		}
		return proposalAccepted;
	}
	
	private void makeEngagement(int suitor, int receiver) {	// make engagement
		this.setSuitor(suitor, receiver);
		this.setReceiver(suitor, receiver);
	}
	
	public boolean matchingCanProceed() {	// check that matching rules are satisfied
		int nSuitors = this.S.size();
		int nReceivers = this.R.size();
    	// First condition: number of suitors must be greater than zero
    	if (nSuitors == 0) {
    		System.out.println("\nERROR: No suitors are loaded!\n");
    		return false;
    	}
    	//	Second condition: number of receivers must be greater than zero
    	else if (nReceivers == 0) {
    		System.out.println("\nERROR: No receivers are loaded!\n");
    		return false;
    	}
    	// Third condition: number of receivers must be equal to number of suitors
    	else if (nSuitors != nReceivers) {
    		System.out.println("\nERROR: The number of suitors and receivers must be equal!\n");
    		return false;
    	}
    	// Fourth condition: Students and schools must have rankings for each other
    	else if (!(this.suitorRankingsSet() || this.receiverRankingsSet())) {
    		System.out.println("ERROR: Student and school rankings must be set before matching!\n");
    		return false;
    	}
    	
    	else {
    		return true;
    	}
	}
	
	
	public void calcRegrets() {	// calculate regrets
		int nSuitors = this.S.size();
		int nReceivors = this.R.size();
    	// Set the regrets for each suitor and receiver.
		this.calcAndSetRegrets();
    	
		// sum up all the regrets of the suitors
    	double sum1 = 0;
    	for (int i = 0; i < nSuitors; i++) {
    		sum1 += this.getSuitorRegret(i);
    	}
    	// find the average regret of the suitors
    	double averageSuitorRegret = sum1/nSuitors;
    	this.setSuitorRegret(averageSuitorRegret);
    	
    	// sum up all the regrets of the schools
    	double sum2 = 0;
    	for (int i = 0; i < nReceivors; i++) {
    		sum2 += this.getReceiverRegret(i);
    	}
    	// find the average regret of the receivers
    	double averageReceiverRegret = sum2/nReceivors;
    	this.setReceiverRegret(averageReceiverRegret);
    	
    	// find the average total regret of both the students and schools
    	double averageTotalRegret = (sum1 + sum2)/(nSuitors + nReceivors);
    	this.setTotalRegret(averageTotalRegret);
	}
	
	public boolean isStable() {	// check if a matching is stable
		int nSuitors = this.S.size();
		int nReceivers = this.R.size();
		// Loop through all the matches from the school's perspective (the number of matches is the same as nSchools or nStudents, so nSchools was chosen arbitrarily)
		boolean stability = false;
		for (int i = 0; i < nReceivers; i++) {
			boolean done = false;	// An indicator to break out of outer loop
			for (int j = 0; j < nSuitors; j++) {
				// The matching is unstable if: (1) the school is not analyzing its own match (2) the school's ranking of the student is better than the school's ranking of the matched student (3) The student's ranking of the school is better than the student's ranking of the student's matched school
				// IMPORTANT: a better ranking means a small ranking (E.g. being ranked 1 is better than being ranked 3)
				if (j != this.getSuitor(i) && this.findReceiverRankingByID(j, i) < this.findReceiverRankingByID(this.getSuitor(i), i) && this.findSuitorRankingByID(j, i) < this.findSuitorRankingByID(j, this.getReceiver(j))) {

					stability = false;
					// If the program finds just one unstable match, break both the inner loop and outer loop
					done = true;
					break;
				}
				else {
					stability = true;
				}
			}
			if (done) {
				break;
			}
		}
		return stability;
	}
	
	// print methods
	public void print() {	// print the matching results and statistics
		this.printMatches();
		this.printStats();
	}
	
	public void printMatches() {	// print matches
    	// Print out the matches heading
    	System.out.println("\nMatches:");
    	System.out.println("--------");
    	int nReceivers = this.R.size();
    	for (int i = 0; i < nReceivers; i++) {
    		String receiverName = this.getReceiverName(i);
    		// "receiverMatch" is the name of the receiver that the receiver is matched to. Note: this.getSuitor(i) is the index of the suitor that is matched to the receiver. We need to get the name of that suitor.
    		String receiverMatch = this.getSuitorName(this.getSuitor(i));
    		// Print out the matches
    		System.out.format("%s: %s\n", receiverName, receiverMatch);
    	}
	}
	
	public void printStats() {	// print matching statistics
    	// print out whether the match is stable
    	boolean stability = this.isStable();
    	// If matching is stable, tell the user "Yes". If the matching is unstable, tell the user "No".
		String stabilityYN = (stability) ? "Yes" : "No";
    	System.out.format("\nStable matching? %s\n", stabilityYN);
    	
    	// calculate and set all the regrets
    	this.calcRegrets();
    	// Print out all the regrets
    	System.out.format("Average student regret: %.2f\n", this.getAvgSuitorRegret());
    	System.out.format("Average school regret: %.2f\n", this.getAvgReceiverRegret());
    	System.out.format("Average total regret: %.2f\n\n", this.getAvgTotalRegret());
	}
}