package components;

import java.util.Random;
import java.util.jar.JarEntry;

import org.json.simple.JSONObject;

import java.util.LinkedList;

/**
 * Class for all equipment that do not consume resources. E.g., conveyor belts.
 */
public class BasicEquipment implements Equipment {

    Random random;
    String name;
    boolean alarming;
    boolean rootCause; //true if this machine is the root cause of a failure
    String alarmMessage;
    int temp;
    int rate;
    int[] validTempRange; //validTempRange[0] is the lower limit, validTempRange[1] is the upper
    int[] validRateRange; //as above
    LinkedList<Equipment> dependencies; //machines directly downstream of this one
    LinkedList<Equipment> providers; //machines directly upstream of this one
    LinkedList<Equipment> neighbors; //all machines that directly interface with this one

    public BasicEquipment(String name, int startRate, int lowerRateLimit, int upperRateLimit, int startTemp, int lowerTempLimit, int upperTempLimit) {
        random = new Random();
        this.name = name;
        alarming = false;
        rootCause = false;
        rate = startRate;
        validRateRange = new int[]{lowerRateLimit, upperRateLimit};
        dependencies = new LinkedList<Equipment>();
        providers = new LinkedList<Equipment>();
        neighbors = new LinkedList<Equipment>();
        temp = startTemp;
        validTempRange = new int[]{lowerTempLimit, upperTempLimit};
    }

    public void addDependency(Equipment dependency) {
        dependencies.add(dependency);
        neighbors.add(dependency);
    }

    public void setDependencies(LinkedList<Equipment> newDependencies) {
        dependencies = newDependencies;
        neighbors.addAll(newDependencies);
    }

    public void addProvider(Equipment provider) {
        providers.add(provider);
        neighbors.add(provider);
    }

    public void setProviders(LinkedList<Equipment> newProviders) {
        providers = newProviders;
        neighbors.addAll(newProviders);
    }


    public String getName() {
        return name;
    }

    public boolean hasResources() {
        return false;
    }

    public boolean isAlarming() {
        return alarming;
    }

    public int isOverloaded() {
        if (rate > validRateRange[1]) {
            return 2;
        } else {
            if (rate < validRateRange[0]) {
                return 1;
            }
        }
        return 0;
    }

    public String getAlarmMessage() {
        return alarmMessage;
    }

    public int getRate() {
        return rate;
    }

    public int getTemp() {
        return temp;
    }

    public LinkedList<Equipment> getNeighbors() {
        return neighbors;
    }

    /**
     * @param fail
     * If true, force the machine to fail. Otherwise the machine will run as normal.
     * 
     * @return All machines that will fail as a result of this update (null if this machine does not fail)
     */
    public int update(boolean fail) {
        if (fail) {
           return updateFail();
        } else {
            updateSuccess();
            return 0;
        }
    }

    protected void updateSuccess() {
        rate = random.nextInt(validRateRange[0], validRateRange[1] + 1);
        temp = random.nextInt(validTempRange[0], validTempRange[1] + 1);
    }

    /**
     * Sets the machine's rate and temp to be either too high or low, with an applicable error message
     * for either scenario.
     * 
     * @return All machines that will fail as a result of this update
     */
    protected int updateFail() {
        rootCause = true;
        alarming = true;
        rate = random.nextInt(validRateRange[0]);
        temp = random.nextInt(validTempRange[0], validTempRange[1] + 1);
        alarmMessage = "Machine operating too slow.";
        return 1;

        /*
        if (random.nextBoolean()) {
            rate = random.nextInt(validRateRange[1], validRateRange[1] + 30);
            temp = random.nextInt(validTempRange[1], validTempRange[1] + 30);
            alarmMessage = "Machine operating too fast. Safe rate and temperature exceeded.";
            return dependencies;
        } else {
            rate = random.nextInt(validRateRange[0]);
            temp = random.nextInt(validTempRange[0], validTempRange[1] + 1);
            alarmMessage = "Machine operating too slow.";
            return dependencies;
        }
        */
    }
    /**
     * Force the machine to enter an alarm state. Used when this machine is being
     * forced to alarm by an upstream piece of equipment. Sets the operation rate of
     * this machine to the max or min, depending on the cause of failure.
     * 
     * @param overloaded
     * True if a machine upstream is running too quickly, false otherwise.
     * 
     * @return All machines that will fail as a result of this update
     */

    /*
    public LinkedList<Equipment> fail(boolean overloaded) {
        if (!alarming) {
            alarming = true;
            if (overloaded) {
                alarmMessage = "Machine receiving too much input.";
                rate = validRateRange[1];
                return dependencies;
            } else {
                rate = validRateRange[0];
                alarmMessage = "Machine creating too much output.";
                return providers;
            }
            //alarming = true;
            //return neighbors;
        }
        return new LinkedList<Equipment>();
    }
    */

    public int fail(int errorCode) {
        if (!alarming) {
            switch (errorCode) {
                case 0:
                    throw new Error("Pipeline has no active alarms");
                case 1:
                    alarmMessage = "Slowdown on pipeline";
                    alarming = true;
                    rate = validRateRange[0];
                    return 1;
                default:
                    throw new Error("Error code not recognized");
            }
        }
        return -1;
    }

    public LinkedList<Equipment> fixRootCause() {
        if (rootCause && alarming) {
            updateSuccess();
            rootCause = false;
            alarming = false;
            return neighbors;
        } else {
            if (alarming) {
                throw new Error("This machine isn't the root cause.");
            } else {
                throw new Error("This machine isn't alarming.");
            }
        }
    }

    public LinkedList<Equipment> fix() {
        if (alarming) {
            updateSuccess();
            alarming = false;
            return neighbors;
        }
        return new LinkedList<Equipment>();
    }

    public JSONObject exportAsJSON() {
        JSONObject toRet = new JSONObject();
        toRet.put("Name", name);
        toRet.put("Alarming", alarming);
        toRet.put("Alarm Message", alarmMessage);
        toRet.put("Temperature", temp);
        toRet.put("Speed", rate);
        return toRet;
    }

    /* public void changeBehavior(ChangeBehavior newBehavior) {
        newBehavior.changeBehavior();
    } */
}