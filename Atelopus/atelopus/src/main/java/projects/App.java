package projects;

public class App {

    public static void main(String[] args) throws Exception {
        long startTime = System.nanoTime();

        // Add method, if worker, run worker class. 

        Scheduler sched = new Scheduler((short) 8, false, false);
        sched.createSimulators(2000);
        sched.runSimulators();
        sched.writeResults();

        //sim.simulateScenarios(2000);


        long endTime = System.nanoTime();
        long timeElapsed = endTime - startTime;
        System.out.println("Execution time in milliseconds : " + timeElapsed / 1000000);
    }
}

// TODO: make docker container
// TODO: make sim out file of some sort (maybe JSON text)
// TODO BONUS: make scheduler or server mode apps. scheduler distributes workloads and receives results.
// TODO: send as many tasks as executor can handle at server + 1 or 2 as buffer. have the server's thread return the response along with server name to get an additional task. 

// FUTURE
// Receive simulator requests via HTTPS (server)
// Receive user input [scheduler] (given a list of servers in network)
