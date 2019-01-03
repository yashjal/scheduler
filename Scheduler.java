import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Implements a scheduler using four different algorithms.
 * Please check the README to learn how to run.
 * @author Yash Jalan <yj627@nyu.edu>
 */
public class Scheduler {

	public static void main(String[] args) throws FileNotFoundException {
		File randNum = new File("random-numbers.txt");
		File data;
		boolean isVerbose = false;
		boolean isDefault = false;
		String x = null;
		
		if (args[0].equals("--verbose")) {
			isVerbose = true;
			data = new File(args[1]);
			if (args.length == 2) {
				isDefault = true;
			} else {
				x = args[2];
			}
		} else {
			data = new File(args[0]);
			if (args.length == 1) {
				isDefault = true;
			} else {
				x = args[1];
			}
		}
		
		Scanner readRandom = new Scanner(randNum);
		Scanner readData = new Scanner(data);
		Scanner read = new Scanner(data);
		read.useDelimiter("[\\p{javaWhitespace}()]+");
		readData.useDelimiter("[\\p{javaWhitespace}()]+");
		
		int n = readData.nextInt();
		Process[] nProcesses = new Process[n];
		
		for (int i = 0; i < n; i++) {
			int A = readData.nextInt();
			int B = readData.nextInt();
			int C = readData.nextInt();
			int M = readData.nextInt();
			nProcesses[i] = new Process(A,B,C,M, i);
		}
		
		System.out.println("Original input: " + read.nextLine());
		Arrays.sort(nProcesses);
		System.out.print("Sorted input: " + nProcesses.length + " ");
		for (int i = 0; i < nProcesses.length; i++) {
			System.out.print("(" + nProcesses[i].A + " " + nProcesses[i].B + " " 
					+ nProcesses[i].C + " " + nProcesses[i].M +") ");
		}
		System.out.println("\n");
		
		if (isDefault || x.equalsIgnoreCase("FCFS")) {
			System.out.println("The scheduling algorithm used was FCFS\n");
			FCFS(nProcesses, readRandom, isVerbose);
		} else if (x.equalsIgnoreCase("RR")) {
			System.out.println("The scheduling algorithm used was RR\n");
			RR2(nProcesses, readRandom, isVerbose);
		} else if (x.equalsIgnoreCase("uni")) {
			System.out.println("The scheduling algorithm used was Uniprogramming\n");
			uni(nProcesses, readRandom, isVerbose);
		} else if (x.equalsIgnoreCase("SJF")) {
			System.out.println("The scheduling algorithm used was SJF\n");
			SJF(nProcesses, readRandom, isVerbose);
		}
		
		readData.close();
		read.close();
		readRandom.close();
	}
	
	/**
	 * Normalizes a random number.
	 * @param X random number to be normalized
	 * @param U boundary
	 * @return normalized number
	 */
	public static int randomOS(int X, int U) {
		return 1 + (X%U);
	}
	
	/**
	 * Implements FCFS.
	 * @param nProcesses array of Processes
	 * @param readRandom Scanner to read random numbers
	 * @param isVerbose
	 */
	public static void FCFS(Process[] nProcesses, Scanner readRandom, boolean isVerbose) {
		int n = 0;
		int runTime = 0;
		int blockedTime = 0;
		Process runningProcess = null;
		
		while (true) {
			for (int i = 0; i < nProcesses.length; i++) {
				if (nProcesses[i].A == n) {
					nProcesses[i].isReady = true;
					nProcesses[i].readyTime = n;
				}
			}
					
			if (runningProcess != null && runningProcess.cpuTimeRemaining == 0) {
				runningProcess.finishTime = n;
				runningProcess.isFinished = true;
				runningProcess = null;
			} else if (runningProcess != null && runningProcess.cpuBurstRemaining == 0
					&& runningProcess.IOTimeRemaining == 0) {
				runningProcess.isReady = true;
				runningProcess.readyTime = n;
				runningProcess = null;

			} else if (runningProcess != null && runningProcess.cpuBurstRemaining == 0) {
				runningProcess.isBlocked = true;
				runningProcess.isReady = false;
				runningProcess = null;
			}
			
			for (int i = 0; i < nProcesses.length; i++) {
				if (nProcesses[i].isBlocked) {
					if (nProcesses[i].IOTimeRemaining == 0) {
						nProcesses[i].isReady = true;
						nProcesses[i].readyTime = n;
						nProcesses[i].isBlocked = false;
					}					
				}
			}
			
			//check for least ready time
			Process least = null;
			boolean gotIt = false;
			for (int i = 0; i < nProcesses.length; i++) {
				if (!gotIt && nProcesses[i].isReady && nProcesses[i].cpuTimeRemaining != 0) {
					least = nProcesses[i];
					gotIt = true;
				}
				if (least != null && nProcesses[i].isReady && nProcesses[i].cpuTimeRemaining != 0) {
					if (nProcesses[i].readyTime < least.readyTime) {
						least = nProcesses[i];
					}
				}
			}
			
			int count = 0;
			for (int i = 0; i < nProcesses.length; i++) {
				if (nProcesses[i].cpuTimeRemaining == 0)
					count++;
			}
			if (count == nProcesses.length)
				break;
			
			if (runningProcess == null && gotIt) {	
					int randomNum = readRandom.nextInt();
					if (isVerbose)
						System.out.println("Random number: " + randomNum);
					runningProcess = least;
					runningProcess.isReady = false;
					runningProcess.cpuBurstRemaining = randomOS(randomNum,runningProcess.B);
					runningProcess.IOTimeRemaining = runningProcess.M * runningProcess.cpuBurstRemaining;
						
			}

			if (runningProcess != null) {
				runningProcess.cpuBurstRemaining--;
				runningProcess.cpuTimeRemaining--;
				runTime++;
			}
			
			for (int i = 0; i < nProcesses.length; i++) {
				if (nProcesses[i].isBlocked) {
					nProcesses[i].IOTimeRemaining--;
					nProcesses[i].IOTime++;
					blockedTime++;
				} 
				if (nProcesses[i].isReady && runningProcess != null) {
					nProcesses[i].waitTime++;
				}
			}
					
			n++;
			
			if (isVerbose) {
				System.out.print("Before cycle " + n +": ");
				for (int i = 0; i < nProcesses.length; i++) {
					if (runningProcess != null && nProcesses[i].id == runningProcess.id) {
						System.out.print("running " + (nProcesses[i].cpuBurstRemaining + 1) + "    ");
					} else if (nProcesses[i].isBlocked) {
						System.out.print("blocked " + (nProcesses[i].IOTimeRemaining + 1) + "    ");
					} else if (nProcesses[i].isReady) {
						System.out.print("ready 0    ");
					} else if (nProcesses[i].isFinished) {
						System.out.print("terminated 0    ");
					} else {
						System.out.print("unstarted 0    ");
					}
				}
				System.out.println("");
			}
		}
		
		int sumWait = 0;
		int sumTurn = 0;
		System.out.println("");
		for (int i = 0; i < nProcesses.length; i++) {
			System.out.println("Process " + i);
			System.out.println("(A,B,C,M) = (" + nProcesses[i].A + "," + nProcesses[i].B + "," + 
			nProcesses[i].C + "," + nProcesses[i].M + ")");
			System.out.println("Finishing time: " + nProcesses[i].finishTime);
			System.out.println("Turnaround time: " + (nProcesses[i].finishTime - nProcesses[i].A));
			sumTurn += (nProcesses[i].finishTime - nProcesses[i].A);
			System.out.println("IO time: " + nProcesses[i].IOTime);
			System.out.println("Waiting time: " + nProcesses[i].waitTime);
			sumWait += nProcesses[i].waitTime;
			System.out.println("");

		}
		
		System.out.println("Summary Data: ");
		System.out.println("Finishing time: " + n);
		System.out.println("CPU Utilization: " + ((float)(runTime)/n));
		System.out.println("IO Utilization: " + ((float)(blockedTime)/n));
		System.out.println("Throughput: " + (nProcesses.length/((float)(n)/100)) + " processes per 100 cycles");
		System.out.println("Average Turnaround time: " + ((float)(sumTurn)/nProcesses.length));
		System.out.println("Average Waiting time: " + ((float)(sumWait)/nProcesses.length));
		
	}
	
	/**
	 * Implements Round Robin with quantum = 2.
	 * @param nProcesses array of Processes
	 * @param readRandom Scanner to read random numbers
	 * @param isVerbose
	 */
	public static void RR2(Process[] nProcesses, Scanner readRandom, boolean isVerbose) {
		int n = 0;
		int runTime = 0;
		int blockedTime = 0;
		Process runningProcess = null;
		
		while (true) {
			for (int i = 0; i < nProcesses.length; i++) {
				if (nProcesses[i].A == n) {
					nProcesses[i].isReady = true;
					nProcesses[i].readyTime = n;
				}
			}
								
			if (runningProcess != null && runningProcess.cpuTimeRemaining == 0) {
				runningProcess.finishTime = n;
				runningProcess.isFinished = true;
				runningProcess = null;
			} else if (runningProcess != null && runningProcess.runTime >= 2 
					&& runningProcess.cpuBurstRemaining != 0) {
				runningProcess.runTime = 0;
				runningProcess.isReady = true;
				runningProcess.readyTime = n;
				runningProcess = null;
			} else if (runningProcess != null && runningProcess.cpuBurstRemaining == 0
					&& runningProcess.IOTimeRemaining == 0) {
				runningProcess.runTime = 0;
				runningProcess.isReady = true;
				runningProcess.readyTime = n;
				runningProcess = null;
			} else if (runningProcess != null && runningProcess.cpuBurstRemaining == 0) {
				runningProcess.runTime = 0;
				runningProcess.isBlocked = true;
				runningProcess.isReady = false;
				runningProcess = null;
			}
			
			for (int i = 0; i < nProcesses.length; i++) {
				if (nProcesses[i].isBlocked) {
					if (nProcesses[i].IOTimeRemaining == 0) {
						nProcesses[i].isReady = true;
						nProcesses[i].readyTime = n;
						nProcesses[i].isBlocked = false;
					}					
				}
			}
			
			//check for least ready time
			Process least = null;
			boolean gotIt = false;
			for (int i = 0; i < nProcesses.length; i++) {
				if (!gotIt && nProcesses[i].isReady && nProcesses[i].cpuTimeRemaining != 0) {
					least = nProcesses[i];
					gotIt = true;
				}
				if (least != null && nProcesses[i].isReady && nProcesses[i].cpuTimeRemaining != 0) {
					if (nProcesses[i].readyTime < least.readyTime) {
						least = nProcesses[i];
					}
				}
			}
			
			int count = 0;
			for (int i = 0; i < nProcesses.length; i++) {
				if (nProcesses[i].cpuTimeRemaining == 0)
					count++;
			}
			if (count == nProcesses.length)
				break;
			
			if (runningProcess == null && gotIt) {
					runningProcess = least;
					if (runningProcess.cpuBurstRemaining == 0) {
						int randomNum = readRandom.nextInt();
						if (isVerbose)
							System.out.println("Random number: " + randomNum);
						runningProcess.cpuBurstRemaining = randomOS(randomNum,runningProcess.B);
						runningProcess.IOTimeRemaining = runningProcess.M * runningProcess.cpuBurstRemaining;
					}
					runningProcess.isReady = false;
					
			}

			if (runningProcess != null) {
				runningProcess.cpuBurstRemaining--;
				runningProcess.cpuTimeRemaining--;
				runningProcess.runTime++;
				runTime++;
			}
			
			for (int i = 0; i < nProcesses.length; i++) {
				if (nProcesses[i].isBlocked) {
					nProcesses[i].IOTimeRemaining--;
					nProcesses[i].IOTime++;
					blockedTime++;
				} 
				if (nProcesses[i].isReady && runningProcess != null) {
					nProcesses[i].waitTime++;
				}
			}
					
			n++;
			if (isVerbose) {
				System.out.print("Before cycle " + n +": ");
				for (int i = 0; i < nProcesses.length; i++) {
					if (runningProcess != null && nProcesses[i].id == runningProcess.id) {
						System.out.print("running " + (nProcesses[i].cpuBurstRemaining + 1) + "    ");
					} else if (nProcesses[i].isBlocked) {
						System.out.print("blocked " + (nProcesses[i].IOTimeRemaining + 1) + "    ");
					} else if (nProcesses[i].isReady) {
						System.out.print("ready 0    ");
					} else if (nProcesses[i].isFinished) {
						System.out.print("terminated 0    ");
					} else {
						System.out.print("unstarted 0    ");
					}
				}
				System.out.println("");
			}
		}
		
		int sumWait = 0;
		int sumTurn = 0;
		System.out.println("");
		for (int i = 0; i < nProcesses.length; i++) {
			System.out.println("Process " + i);
			System.out.println("(A,B,C,M) = (" + nProcesses[i].A + "," + nProcesses[i].B + "," + 
			nProcesses[i].C + "," + nProcesses[i].M + ")");
			System.out.println("Finishing time: " + nProcesses[i].finishTime);
			System.out.println("Turnaround time: " + (nProcesses[i].finishTime - nProcesses[i].A));
			sumTurn += (nProcesses[i].finishTime - nProcesses[i].A);
			System.out.println("IO time: " + nProcesses[i].IOTime);
			System.out.println("Waiting time: " + nProcesses[i].waitTime);
			sumWait += nProcesses[i].waitTime;
			System.out.println("");

		}
		
		System.out.println("Summary Data: ");
		System.out.println("Finishing time: " + n);
		System.out.println("CPU Utilization: " + ((float)(runTime)/n));
		System.out.println("IO Utilization: " + ((float)(blockedTime)/n));
		System.out.println("Throughput: " + (nProcesses.length/((float)(n)/100)) + " processes per 100 cycles");
		System.out.println("Average Turnaround time: " + ((float)(sumTurn)/nProcesses.length));
		System.out.println("Average Waiting time: " + ((float)(sumWait)/nProcesses.length));	
	}
	
	/**
	 * Implements Uniprogramming.
	 * @param nProcesses array of processes
	 * @param readRandom Scanner to read random numbers
	 * @param isVerbose
	 */
	public static void uni(Process[] nProcesses, Scanner readRandom, boolean isVerbose) {
		int n = 0;
		int runTime = 0;
		int blockedTime = 0;
		Process runningProcess = null;
		
		while (true) {
			for (int i = 0; i < nProcesses.length; i++) {
				if (nProcesses[i].A == n) {
					nProcesses[i].isReady = true;
					nProcesses[i].readyTime = n;
				}
			}
					
			if (runningProcess != null && runningProcess.cpuTimeRemaining == 0) {
				runningProcess.finishTime = n;
				runningProcess.isFinished = true;
				runningProcess = null;
			} else if (runningProcess != null && runningProcess.cpuBurstRemaining == 0
					&& runningProcess.IOTimeRemaining == 0) {
				int randomNum = readRandom.nextInt();
				if (isVerbose)
					System.out.println("Random number: " + randomNum);
				runningProcess.isReady = false;
				runningProcess.isBlocked = false;
				runningProcess.cpuBurstRemaining = randomOS(randomNum,runningProcess.B);
				runningProcess.IOTimeRemaining = runningProcess.M * runningProcess.cpuBurstRemaining;
			} else if (runningProcess != null && runningProcess.cpuBurstRemaining == 0) {
				runningProcess.isBlocked = true;
				runningProcess.isReady = false;
			}
			
			if (runningProcess == null) {

				for (int i = 0; i < nProcesses.length; i++) {
					if (nProcesses[i].isFinished) {
						continue;
					} else if (nProcesses[i].isReady) {
						runningProcess = nProcesses[i];
						int randomNum = readRandom.nextInt();
						if (isVerbose)
							System.out.println("Random number: " + randomNum);
						runningProcess.isReady = false;
						runningProcess.cpuBurstRemaining = randomOS(randomNum,runningProcess.B);
						runningProcess.IOTimeRemaining = runningProcess.M * runningProcess.cpuBurstRemaining;
						break;
					}
				}
			}
			
			int count = 0;
			for (int i = 0; i < nProcesses.length; i++) {
				if (nProcesses[i].cpuTimeRemaining == 0)
					count++;
			}
			if (count == nProcesses.length)
				break;


			if (runningProcess != null && !(runningProcess.isBlocked)) {
				runningProcess.cpuBurstRemaining--;
				runningProcess.cpuTimeRemaining--;
				runTime++;
			} else if (runningProcess != null && runningProcess.isBlocked) {
				runningProcess.IOTimeRemaining--;
				runningProcess.IOTime++;
				blockedTime++;
			}
			
			for (int i = 0; i < nProcesses.length; i++) {
				if (nProcesses[i] != runningProcess && nProcesses[i].isReady && runningProcess != null) {
					nProcesses[i].waitTime++;
				}
			}
					
			n++;
			
			if (isVerbose) {
				System.out.print("Before cycle " + n +": ");
				for (int i = 0; i < nProcesses.length; i++) {
					if (runningProcess != null && nProcesses[i].id == runningProcess.id 
							&& !(runningProcess.isBlocked)) {
						System.out.print("running " + (nProcesses[i].cpuBurstRemaining + 1) + "    ");
					} else if (nProcesses[i].isBlocked) {
						System.out.print("blocked " + (nProcesses[i].IOTimeRemaining + 1) + "    ");
					} else if (nProcesses[i].isReady) {
						System.out.print("ready 0    ");
					} else if (nProcesses[i].isFinished) {
						System.out.print("terminated 0    ");
					} else {
						System.out.print("unstarted 0    ");
					}
				}
				System.out.println("");
			}
		}
		
		int sumWait = 0;
		int sumTurn = 0;
		System.out.println("");
		for (int i = 0; i < nProcesses.length; i++) {
			System.out.println("Process " + i);
			System.out.println("(A,B,C,M) = (" + nProcesses[i].A + "," + nProcesses[i].B + "," + 
			nProcesses[i].C + "," + nProcesses[i].M + ")");
			System.out.println("Finishing time: " + nProcesses[i].finishTime);
			System.out.println("Turnaround time: " + (nProcesses[i].finishTime - nProcesses[i].A));
			sumTurn += (nProcesses[i].finishTime - nProcesses[i].A);
			System.out.println("IO time: " + nProcesses[i].IOTime);
			System.out.println("Waiting time: " + nProcesses[i].waitTime);
			sumWait += nProcesses[i].waitTime;
			System.out.println("");

		}
		
		System.out.println("Summary Data: ");
		System.out.println("Finishing time: " + n);
		System.out.println("CPU Utilization: " + ((float)(runTime)/n));
		System.out.println("IO Utilization: " + ((float)(blockedTime)/n));
		System.out.println("Throughput: " + (nProcesses.length/((float)(n)/100)) + " processes per 100 cycles");
		System.out.println("Average Turnaround time: " + ((float)(sumTurn)/nProcesses.length));
		System.out.println("Average Waiting time: " + ((float)(sumWait)/nProcesses.length));
	}
	
	/**
	 * Implements SJF.
	 * @param nProcesses array of Processes
	 * @param readRandom Scanner to read random numbers
	 * @param isVerbose
	 */
	public static void SJF(Process[] nProcesses, Scanner readRandom, boolean isVerbose) {
		int n = 0;
		int runTime = 0;
		int blockedTime = 0;
		Process runningProcess = null;
		
		while (true) {
			for (int i = 0; i < nProcesses.length; i++) {
				if (nProcesses[i].A == n) {
					nProcesses[i].isReady = true;
					nProcesses[i].readyTime = n;
				}
			}
					
			if (runningProcess != null && runningProcess.cpuTimeRemaining == 0) {
				runningProcess.finishTime = n;
				runningProcess.isFinished = true;
				runningProcess = null;
			} else if (runningProcess != null && runningProcess.cpuBurstRemaining == 0
					&& runningProcess.IOTimeRemaining == 0) {
				runningProcess.isReady = true;
				runningProcess = null;

			} else if (runningProcess != null && runningProcess.cpuBurstRemaining == 0) {
				runningProcess.isBlocked = true;
				runningProcess.isReady = false;
				runningProcess = null;
			}
			
			for (int i = 0; i < nProcesses.length; i++) {
				if (nProcesses[i].isBlocked) {
					if (nProcesses[i].IOTimeRemaining == 0) {
						nProcesses[i].isReady = true;
						nProcesses[i].isBlocked = false;
					}					
				}
			}
			
			//check for least CPU time
			Process least = null;
			boolean gotIt = false;
			for (int i = 0; i < nProcesses.length; i++) {
				if (!gotIt && nProcesses[i].isReady && nProcesses[i].cpuTimeRemaining != 0) {
					least = nProcesses[i];
					gotIt = true;
				}
				if (least != null && nProcesses[i].isReady && nProcesses[i].cpuTimeRemaining != 0) {
					if (nProcesses[i].cpuTimeRemaining < least.cpuTimeRemaining) {
						least = nProcesses[i];
					}
				}
			}
			
			int count = 0;
			for (int i = 0; i < nProcesses.length; i++) {
				if (nProcesses[i].cpuTimeRemaining == 0)
					count++;
			}
			if (count == nProcesses.length)
				break;
			
			if (runningProcess == null && gotIt) {	
					int randomNum = readRandom.nextInt();
					if (isVerbose)
						System.out.println("Random number: " + randomNum);
					runningProcess = least;
					runningProcess.isReady = false;
					runningProcess.cpuBurstRemaining = randomOS(randomNum,runningProcess.B);
					runningProcess.IOTimeRemaining = runningProcess.M * runningProcess.cpuBurstRemaining;
						
			}

			if (runningProcess != null) {
				runningProcess.cpuBurstRemaining--;
				runningProcess.cpuTimeRemaining--;
				runTime++;
			}
			
			for (int i = 0; i < nProcesses.length; i++) {
				if (nProcesses[i].isBlocked) {
					nProcesses[i].IOTimeRemaining--;
					nProcesses[i].IOTime++;
					blockedTime++;
				} 
				if (nProcesses[i].isReady && runningProcess != null) {
					nProcesses[i].waitTime++;
				}
			}
					
			n++;
			
			if (isVerbose) {
				System.out.print("Before cycle " + n +": ");
				for (int i = 0; i < nProcesses.length; i++) {
					if (runningProcess != null && nProcesses[i].id == runningProcess.id) {
						System.out.print("running " + (nProcesses[i].cpuBurstRemaining + 1) + "    ");
					} else if (nProcesses[i].isBlocked) {
						System.out.print("blocked " + (nProcesses[i].IOTimeRemaining + 1) + "    ");
					} else if (nProcesses[i].isReady) {
						System.out.print("ready 0    ");
					} else if (nProcesses[i].isFinished) {
						System.out.print("terminated 0    ");
					} else {
						System.out.print("unstarted 0    ");
					}
				}
				System.out.println("");
			}
		}
		
		int sumWait = 0;
		int sumTurn = 0;
		System.out.println("");
		for (int i = 0; i < nProcesses.length; i++) {
			System.out.println("Process " + i);
			System.out.println("(A,B,C,M) = (" + nProcesses[i].A + "," + nProcesses[i].B + "," + 
			nProcesses[i].C + "," + nProcesses[i].M + ")");
			System.out.println("Finishing time: " + nProcesses[i].finishTime);
			System.out.println("Turnaround time: " + (nProcesses[i].finishTime - nProcesses[i].A));
			sumTurn += (nProcesses[i].finishTime - nProcesses[i].A);
			System.out.println("IO time: " + nProcesses[i].IOTime);
			System.out.println("Waiting time: " + nProcesses[i].waitTime);
			sumWait += nProcesses[i].waitTime;
			System.out.println("");

		}
		
		System.out.println("Summary Data: ");
		System.out.println("Finishing time: " + n);
		System.out.println("CPU Utilization: " + ((float)(runTime)/n));
		System.out.println("IO Utilization: " + ((float)(blockedTime)/n));
		System.out.println("Throughput: " + (nProcesses.length/((float)(n)/100)) + " processes per 100 cycles");
		System.out.println("Average Turnaround time: " + ((float)(sumTurn)/nProcesses.length));
		System.out.println("Average Waiting time: " + ((float)(sumWait)/nProcesses.length));
		
	}

}
