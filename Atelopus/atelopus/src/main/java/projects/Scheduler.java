package projects;
import java.util.ArrayList;
 
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Scheduler {
    ArrayList<Simulator> simulators;
    short threads;

    boolean serverMode;
    ArrayList<String> workerIPs;

    boolean workerMode;
    String hostIp;

    // Connector connector;
    public Scheduler(short threads, boolean serverMode, boolean workerMode) {
        // Constructor
        this.threads = threads;
        this.serverMode = serverMode;
        this.workerMode = workerMode;
    }
 
    // TODO: Create Simulators based on string of x and y
    public void createSimulators(int weeks) {

        // For the desired intervals and for the desired variables, create a list of x and y Simulators.
        this.simulators = new ArrayList<Simulator>(100);  // allocate memory for at least a 100 sims;
        for (double y = 0.4; y <= 0.6; y += 0.1) {
            for (double x = 0.4; x <= 0.6; x += 0.1) {

                Simulator sim = new Simulator(this, weeks);

                // {EDIT} X and Y variables here!
                sim.ZoosporeDeathRate = (float) x;
                sim.uP = (float) y;

                // Add sim to list
                simulators.add(sim);
            } 
        }
    }

    // Use this method to run simulations
    public void runSimulators() throws Exception {
        if (!this.serverMode) {
            this.runSimulatorsLocally();
        }
        else if (this.serverMode) {
            this.runSimulatorsCluster();
        } else if (this.workerMode) {
            return; // code here: listen as worker
        }
    }

    // Local Mode
    public void runSimulatorsLocally() throws Exception {
        // Create a thread executer based on systems no of threads
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(this.threads);

        // Create an execution task for each sim to be queued and run as threads
        int i = 0;
        for (Simulator sim : simulators) {
            executor.execute(sim);
            i++;
        }
        System.out.println(i + " simulations have been added to the queue.");
        System.out.println("Running on " + executor.getMaximumPoolSize() + " threads.");
        executor.shutdown();
        executor.awaitTermination(Long.MAX_VALUE,TimeUnit.DAYS);
    }

    // Server Mode
    public void runSimulatorsCluster() {
        return;
    }

    // Worker Mode
    // Create executor
    // Listen for HTTP JSON objects
    // Convert to sim
    // Run sim as thread

    public void printResults() {
        // {EDIT:} Edit here title so you can keep track of what is being simulated. 
        System.out.println("X: Zoospore Death Rate. Y: uP.");
        for (Simulator sim : this.simulators) {
            
            // {EDIT} X and Y variables here! Must match simulated variables above. 
            float x = sim.ZoosporeDeathRate;
            float y = sim.uP;
            byte r = sim.result;  // simulation result. 0 if both dead. 1 if frogs alive and zoospore dead. 2 if both alive.
            
            System.out.println("X: " + x + " Y: " + y + " R: " + r);
        }
        
    }

        //this.simulators.add(s);
    



}