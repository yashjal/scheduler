Name: Yash Jalan
E-mail: yj627@nyu.edu

To compile: javac Process.java Scheduler.java
To run: java Scheduler …

The first command line argument is an optional —-verbose otherwise the name of the file with the input data. If verbose, then the name of the file should be the second argument. Also most IMPORTANTly, the last command line argument should specify the algorithm that the user wants. If none given, then defaults to FCFS. The options include “FCFS”, “RR”, “uni” or “SJF”. So, if the user wants to use RR, then to run: 

java Scheduler -—verbose <name_of_the_file> RR
		OR
java Scheduler <name_of_the_file> RR

Lastly, the program assumes existence of a file called “random-numbers.txt” in the current directory.