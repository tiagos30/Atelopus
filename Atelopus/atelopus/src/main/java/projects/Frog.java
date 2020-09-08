package projects;

public class Frog {
    Reservoir reservoir;
    boolean alive = true;
    boolean adult = false;  // False if tadpoles; True if frogs.
    byte age = 0; 
    int zoospore = 0;

    /* CONSTRUCTORS */ 
    public Frog(Reservoir reservoir) {
        // Constructor for default values
        this.reservoir = reservoir;
    }
    public Frog(Reservoir reservoir, boolean alive, boolean adult, int zoospore, byte age) {
        // Constructor for all custom values
        this.reservoir = reservoir;
        this.alive = alive;
        this.adult = adult;
        this.zoospore = zoospore;
        this.age = age;
    }
    
    /* SETTERS */
    public void setAlive(boolean alive) {
        this.alive = alive;
    }
    public void setAdult(boolean adult) {
        this.adult = adult;
    }
    public void setAge(byte age) {
        this.age = age;
    }
    public void setZoospore(int zoospore) {
        this.zoospore = zoospore;
    }

    /* GETTERS */ 
    public boolean getAlive() {
        return this.alive;
    }
    public boolean getAdult() {
        return this.adult;
    }
    public int getAge() {
        return this.age;
    }
    public int getZoospore() {
        return this.zoospore;
    }

    /* METHODS */ 
    public void timestep() {

        // if frog is dead, return
        if (!this.getAlive()) {return;}

        // FROGS AND TOADS DIE
        this.simulateDeath();

        // if frog is dead, return
        if (!this.getAlive()) {return;}

        // TOADS MATURE INTO FROGS
        this.simulateMaturity();

        // INFECTED FROGS RELEASE ZOOSPORES INTO RESERVOIR
        this.simulateZoosporeRelease();

        // INFECTED FROGS HAVE ZOOSPORES THAT REPRODUCE INTERNALLY
        this.simulateZoosporeGrowth();

        // FROGS INFECTED FROM RESERVOIR ZOOSPORES (AQUIRING THEM)
        this.simulateInfection();

        // ADULTS REPRODUCE
        // Note: maybe this one needs to go in Reservoir. We'll see. 
        this.simulateReproduction();

        // SURVIVORS AGE ++
        this.ageOneWeek();

    }
    public void mature() {
        this.adult = true;
        this.age = 0;
    }
    public void kill() {
        this.alive = false;
    }
    public void ageOneWeek() {
        this.age++;
    }
    public void addZoospore(int n) {
        this.zoospore += n;
    }
    public void subtZoospore(int n) {
        this.zoospore -= n;
    }

    public void simulateDeath() {
        // Constants from documentation. A is for adult. P is for Tadpole. 


        // Determine probability of death according to documentation
        double probDeath;
        if (this.getAdult()) {
            probDeath = this.reservoir.uA + (this.getZoospore()*this.reservoir.gA); // formula for adults
        } else {
            probDeath = this.reservoir.uP + (this.getZoospore()*this.reservoir.gP) + (this.reservoir.getAccompTadpole()*this.reservoir.uD);  // formula for tadpoles
        }
        
        // Generate random float value
        double random = this.reservoir.rand.nextDouble();

        // if condition is met, kill
        if (probDeath > random) {this.kill();}
    }

    public void simulateMaturity() {
        // if frog is already mature OR age =<8, return
        if (this.getAdult() || this.getAge() <= 8) {return;}

        // if already 12 weeks, mature and return
        if (this.getAge() >= 12) {this.mature(); return;} 

        // u(t) = q*e^(B*j)-r  -- where j is the age of the frog
        final double q = 3.12*10e-5;
        final double B = 0.8673;
        final double r = 0.0321;
        double probabilityMaturation = q * Math.exp(B*this.getAge()) - r;  // u(t)

        // Generate random float value
        double random = this.reservoir.rand.nextDouble();
        
        // if probability is higher than random number, mature
        if (probabilityMaturation > random) {this.mature();}
    }

    public void simulateInfection() {
        
        int acquired;
        if (this.getAdult()) {
            acquired = (int) (this.reservoir.getZoospores() * this.reservoir.AdultZoosporeAcqRate);  // of tadpole
        } else {
            acquired = (int) (this.reservoir.getZoospores() * this.reservoir.TadpoleZoosporeAcqRate);
        }
        
        // Subtract aquired zoospores from reservoir and add to this frog.
        this.reservoir.subtZoosporeQueue(acquired);
        this.addZoospore(acquired);
       
    }

    public void simulateZoosporeGrowth() {
        // Constants from documentation
        //final double bMax = 7;
        //final double bMin = 4;
        //final double tempMax = 28;
        //final double tempMin = 20;

        // The number of new zoospore per week, produced by a single zoospore
        // double growthRate = bMax - ((bMax-bMin)/(tempMax-tempMin)) * (tempMax-((tempMax-tempMin)/2)*(1-Math.cos(2*Math.PI/52)*(this.reservoir.getWeek()-10)+Math.PI)-tempMin);
        

        // multiply times number of zoospores in this frog
        int newZoospore = (this.reservoir.ZoosporeGrowthRate * this.getZoospore());

        this.setZoospore(newZoospore);
    }
    public void simulateZoosporeRelease() {
        // Constants from documentation
        // final float v = ; // percentage of zoospore released

        int release = (int) (this.zoospore * this.reservoir.ZoosporeReleaseRate);
        
        // subtract from this frog and add to reservoir
        this.subtZoospore(release);
        this.reservoir.addZoosporesQueue(release);
    }
    public void simulateReproduction() {
        // If adult
        if (this.getAdult()) {
            // Add tadpoles to reservoir's newborns queue. (queue to prevent infinite growth per week)
            this.reservoir.addNewbownQueue(this.reservoir.getCurrentRepRate());
        }

        
    }
} 
 