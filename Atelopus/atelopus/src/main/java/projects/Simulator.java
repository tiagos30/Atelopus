package projects;

public class Simulator implements Runnable {
    final Scheduler scheduler;
    /* DEFAULT INITIAL CONDITIONS */
    final int INIT_ZOOSPORES = 10000;
    final int INIT_TADS = 60;
    final int INIT_FROGS = 60;
    /* DEFAULT PARAMETERS */
    // Zoospore parameters
    float ZoosporeDeathRate = (float) 0.8;  // in weeks
    float ZoosporeReleaseRate = (float) 0.8;  // from frog to reservoir
    short ZoosporeGrowthRate = 7;  // inside the frog
    // Infection parameters
    float AdultZoosporeAcqRate = (float) 0.0125;  // of adult frog
    float TadpoleZoosporeAcqRate = 0;  // of adult frog
    // Frog mortality parameters
    float uA = (float) 0.07;
    float uP = (float) 0.20;
    float gA = (float) 0.000001;
    float gP = (float) 0.000001; 
    float uD = (float) 0.00004; 
    // Frog reproduction parameters
    float fMax = 70;  // maximum fecundity
    float fMin = 45;  // minimum fecundity
    float plMax = 200;  // maximum pluviosity
    float plMin = 10;  // minimum pluviosity
    float est = 75;  // estacionalidad

    int weeks;  // simulation lenght
    byte result;  // simulation result. 0 if both dead. 1 if frogs alive and zoospore dead. 2 if both alive.


    /* CONSTRUCTORS */ 
    public Simulator(Scheduler scheduler, int weeks) {
        // Constructor for default values
        this.scheduler = scheduler;
        this.weeks = weeks;
    }
    
    /* GETTERS */ 
    public byte getResult() {
        return result;
    }

    public byte determineResult(Reservoir r) {
        if (r.getNFrogsAlive() <= 0) {
            // Frogs are extict
            // EXTINCTION =0
            return (byte) 0;
        } else {
            // Frogs are not extinct
            if ((r.getTotalInVitroZoospores() + r.getZoospores()) <= 0) {
                // FROGS =1
                return (byte) 1;
            } else {
                // COEXISTANT =2
                return (byte) 2;
            }  
        }
    }

    public void run() {
        // Create a new reservoir 

        Reservoir r = new Reservoir(this.INIT_ZOOSPORES, this.INIT_TADS, this.INIT_FROGS, this.ZoosporeDeathRate, this.ZoosporeReleaseRate, this.ZoosporeGrowthRate,
        this.AdultZoosporeAcqRate, this.TadpoleZoosporeAcqRate, this.uA, this.uP, this.gA, this.gP, this.uD, this.fMax, this.fMin, this.plMax, this.plMin, this.est);

        // timestep the amount of weeks
        for (int i=0; i < this.weeks; i++) {
            // simulate timestep
            r.timestep();
            // every 2 weeks check if anyone won to prevent oversimulating
            if (i%20 == 0 && determineResult(r) != 2) {
                break;
            }

        }
        this.result = determineResult(r);

        // if on worker mode, send itself back to the IP address using the HTTP requester set by Server
        if (this.scheduler.workerMode) {
            // make the HTTP send the object back via here
            return;
        }
    }
}
