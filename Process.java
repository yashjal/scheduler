/**
 * Represents a process.
 * @author Yash Jalan <yj627@nyu.edu>
 */
public class Process implements Comparable<Process> {
	
	int A;
	int B;
	int C;
	int M;
	boolean isBlocked;
	boolean isReady;
	int finishTime;
	int IOTime;
	int IOTimeRemaining;
	int waitTime;
	int id;
	int cpuTimeRemaining;
	int cpuBurstRemaining;
	int readyTime;
	boolean isFinished;
	int runTime;
	
	Process(int A, int B, int C, int M, int id) {
		this.A = A;
		this.B = B;
		this.C = C;
		this.M = M;
		this.id = id;
		this.cpuTimeRemaining  = C;
		
	}

	@Override
	public int compareTo(Process o) {
		if (this.A < o.A) {
			return -1;
		} else if (this.A > o.A) {
			return 1;
		} else {
			return 0;
		}
		
	}
}
