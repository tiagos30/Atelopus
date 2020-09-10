package projects;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpClient;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.FileWriter;
import com.google.gson.*;


public class Scheduler {
    Queue<Simulator> simulators;
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
        this.simulators = new LinkedList<Simulator>();  // allocate memory for at least a 100 sims;
        for (double y = 50; y <= 75; y += 0.5) {
            for (double x = 0; x <= 0.5; x += 0.1) {

                Simulator sim = new Simulator(this, weeks);

                // {EDIT} X and Y variables here!
                sim.ZoosporeDeathRate = (float) x;
                sim.est = (float) y;

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

    public void writeResults() {
        // {EDIT:} Edit here title so you can keep track of what is being simulated. 
        
        
        try {

            // Create Date time in appropriate format to use as file name
            LocalDateTime dateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm-ss");
            String dateTimeString = dateTime.format(formatter);

            // Create new file
            File outputFile = new File("output/results" + dateTimeString + ".csv");
            if (outputFile.createNewFile()) {
                System.out.println("File created: " + outputFile.getName());
            } else {
                System.out.println("File already exists.");
            }
            
            // Create new file writer
            FileWriter writer = new FileWriter("output/results" + dateTimeString + ".csv");

            // Write headers
            writer.write("Zoospore Death Rate,Est,RESULT\n");

            for (Simulator sim : this.simulators) {

                // {EDIT} X and Y variables here! Must match simulated variables above. 
                float x = sim.ZoosporeDeathRate;
                float y = sim.est;
                byte r = sim.result;  // simulation result. 0 if both dead. 1 if frogs alive and zoospore dead. 2 if both alive.

                writer.write(x + "," + y + "," + r + "\n");
            }

            writer.close();

        } catch (IOException e) {
            System.out.println("An error occurred writing the output file.");
            e.printStackTrace();
        }
        
    }

        //this.simulators.add(s);
    



}