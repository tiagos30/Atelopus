package projects;
import java.util.ArrayList; // import the ArrayList class
import java.util.Random; 

public class Reservoir {
    ArrayList<Frog> frogs;  // preallocate initial memory for 10,000 frogs/tadpoles
    int zoospores = 0;
    int week = 0;
    float currentRepRate =  0;
    float newbornQueue = 0;
    int zoosporeQueue = 0;
    int accompTadpoles = 0;
    Random rand; 

    /* PARAMETERS */
    // Zoospore parameters
    final float ZoosporeDeathRate;  // in weeks
    final float ZoosporeReleaseRate;  // from frog to reservoir
    final short ZoosporeGrowthRate;  // inside the frog
    // Infection parameters
    final float AdultZoosporeAcqRate;  // of adult frog
    final float TadpoleZoosporeAcqRate;  // of adult frog
    // Frog mortality parameters
    final float uA;
    final float uP;
    final float gA;
    final float gP; 
    final float uD; 
    // Frog reproduction parameters
    final float fMax;  // maximum fecundity
    final float fMin;  // minimum fecundity
    final float plMax;  // maximum pluviosity
    final float plMin;  // minimum pluviosity
    final float est;  // estacionalidad

    /* CONSTRUCTOR */
    public Reservoir (
        final int INIT_ZOOSPORES,
        final int INIT_TADS,
        final int INIT_FROGS,
        final float ZoosporeDeathRate,
        final float ZoosporeReleaseRate,
        final short ZoosporeGrowthRate,
        final float AdultZoosporeAcqRate,
        final float TadpoleZoosporeAcqRate,
        final float uA,
        final float uP,
        final float gA,
        final float gP,
        final float uD,
        final float fMax,
        final float fMin,
        final float plMax,
        final float plMin,
        final float est
    ) {

        this.ZoosporeDeathRate = ZoosporeDeathRate;
        this.ZoosporeReleaseRate = ZoosporeReleaseRate;
        this.ZoosporeGrowthRate = ZoosporeGrowthRate; 
        this.AdultZoosporeAcqRate = AdultZoosporeAcqRate;
        this.TadpoleZoosporeAcqRate = TadpoleZoosporeAcqRate;
        this.uA = uA;
        this.uP = uP;
        this.gA = gA;
        this.gP = gP; 
        this.uD = uD; 
        this.fMax = fMax;
        this.fMin = fMin;
        this.plMax = plMax;
        this.plMin = plMin;
        this.est = est;
        // Initial number of zoospores
        this.zoospores = INIT_ZOOSPORES;

        // Create empty frog list
        frogs = new ArrayList<>(4000);  // preallocate memory for at least 4000 frogs

        // Create random seed
        rand = new Random();
        
        // Initial number of frogs
        for (int i = 0; i < INIT_FROGS; i++) {
            Frog newFrog = new Frog(this);
            newFrog.mature();
            this.frogs.add(newFrog);
        }

        // Initial number of tadpoles
        for (int i = 0; i < INIT_TADS; i++) {
            this.frogs.add(new Frog(this));
        }
    }

    /* GETTERS */
    public int getZoospores() {
        return this.zoospores;
    }
    public int getWeek() {
        return this.week;
    }
    public int getZoosporesQueue() {
        return this.zoosporeQueue;
    }
    public int getNFrogsAlive() {
        int n = 0;
        for (Frog frog : frogs) {
            if (frog.getAlive()) {n++;}
        }
        return n;
    }
    public int getNFrogsAdult() {
        int n = 0;
        for (Frog frog : frogs) {
            if (frog.getAdult() && frog.getAlive()) {n++;}
        }
        return n;
    }
    public int getNFrogsTadpole() {
        int n = 0;
        for (Frog frog : frogs) {
            if (!frog.getAdult() && frog.getAlive()) {n++;}
        }
        return n;
    }
    public int getAccompTadpole() {
        return this.accompTadpoles;
    }
    public float getCurrentRepRate() {
        return this.currentRepRate;
    }
    public int getTotalInVitroZoospores() {
        // Method to get final total zoospores in every frog
        int totalZoozpores = 0;
        for (Frog frog : this.frogs) {
            totalZoozpores += frog.getZoospore();
        }
        return totalZoozpores;
    }

    /* SETTERS */
    public void setZoospores(int zoospores) {
        if (zoospores >= Integer.MAX_VALUE) {
            this.zoospores = Integer.MAX_VALUE;
        } else if (zoospores < 0) {
            this.zoospores = 0;
        } else {
            this.zoospores = zoospores;
        }
    }

    /* METHODS */ 
    public void timestep() {

        // Reference print
        // TODO number of zoospores and frogs grows exponetially. Virtually impossible to simulate past week3. 
        //System.out.println(String.format("Starting week %d.", this.week));
        //System.out.println(String.format("Zoospores: %d", this.zoospores));
        //System.out.println(String.format("No of frogs adult: %d.", this.getNFrogsAdult()));
        //System.out.println(String.format("No of frogs tadpole: %d.", this.getNFrogsTadpole()));
        //System.out.println(String.format("CurrentRepRate: %f.", this.getCurrentRepRate()));
        
        // Calculate Frog reproduction rate
        this.calculateReproductionRate();

        // timestep every frog
        for (Frog frog : this.frogs) {
            frog.timestep();
        }
    
        // add one week to current frogs
        this.addOneWeek();

        // create new frogs
        for (int i=0; i < this.newbornQueue; i++) {
            frogs.add(new Frog(this));
        }
        this.newbornQueue = 0;

        // add zoospores from zoosporeQueue
        this.addZoospores(this.getZoosporesQueue());
        this.zoosporeQueue = 0;

        // simulate reservoir zoospore deaths
        this.simulateReservoirZoospores();

        // delete all dead frogs
        for (int i=0; i < this.frogs.size(); i++) {
            // If frog is NOT alive, remove it
            if (!this.frogs.get(i).getAlive()) {frogs.remove(i);}
        }
        
        // Update number of tadpoles
        this.updateAccompTadpoles();
    }

    public void simulateReservoirZoospores() {
        // Simulate zoospore death on reservoir
        this.setZoospores((int)(this.getZoospores() * (1 - this.ZoosporeDeathRate)));
    }

    public void updateAccompTadpoles() {
        // Count how many frogs are alive and toads
        this.accompTadpoles = 0;
        for (Frog frog : this.frogs) {
            if (frog.getAlive() && !frog.getAdult()) {this.accompTadpoles++;}  
        }
    }

    public void calculateReproductionRate() {
        // Calculate reproduction rate of frogs, according to documentation
        final double pluv = (0.5)*(2*this.plMax-(this.plMax-this.plMin)*(1-Math.cos((2*Math.PI/52)*((this.getWeek())-(0.5))+Math.PI)));  // formula for pluviosity

        // Formula according to documentation
        double hT = fMax - ((this.fMax-this.fMin)/(this.est-this.plMin))*(pluv-this.plMin);
        
        this.currentRepRate = Math.max(0, ((float)hT));  // returns ft. ft is equal to the new tadpoles created. inimum value is 0. converted to int. 
    }

    public void addOneWeek() {
        this.week += 1;
    }
    public void addNewbownQueue(float n) {
        this.newbornQueue += n;
    }
    public void addZoosporesQueue(int n) {
        if ((this.zoosporeQueue + n) >= Integer.MAX_VALUE || (this.zoosporeQueue + n) < 0) {   // overflow: too many zoospores to count
            this.zoosporeQueue = Integer.MAX_VALUE;
        } else {
        this.zoosporeQueue += n;
        }
    }
    public void subtZoosporeQueue(int n) {
        this.zoosporeQueue -= n;
        if (this.zoosporeQueue < 0) {this.zoosporeQueue = 0;} 
    }
    
    public void addZoospores(int n) {
        if ((this.zoospores + n) >= Integer.MAX_VALUE || (this.zoospores + n) < 0) {   // overflow: too many zoospores to count
            this.zoospores = Integer.MAX_VALUE;
        } else {
        this.zoospores += n;
        }
    }
    public void subtZoospores(int n) {
        this.zoospores -= n;
        if (this.zoospores < 0) {this.zoospores = 0;} 
    }
    
}