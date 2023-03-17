package components;

import java.util.Random;
import java.util.LinkedList;

/**
 * Class for all equipment that do not consume resources. E.g., conveyor belts.
 */
public class BasicEquipment implements Equipment {

    Random random;
    boolean alarming;
    boolean rootCause; //true if this machine is the root cause of a failure
    String alarmMessage;
    int temp;
    int rate;
    int[] validTempRange; //validTempRange[0] is the lower limit, validTempRange[1] is the upper
    int[] validRateRange; //as above
    LinkedList<Equipment> dependencies; //machines impacted by this one's failure

    public BasicEquipment(int startRate, int lowerRateLimit, int upperRateLimit, int startTemp, int lowerTempLimit, int upperTempLimit) {
        random = new Random();
        alarming = false;
        rootCause = false;
        rate = startRate;
        validRateRange = new int[]{lowerRateLimit, upperRateLimit};
        dependencies = new LinkedList<Equipment>();
        temp = startTemp;
        validTempRange = new int[]{lowerTempLimit, upperTempLimit};
    }

    public void addDependency(Equipment dependency) {
        dependencies.add(dependency);
    }

    public void setDependencies(LinkedList<Equipment> newDependencies) {
        dependencies = newDependencies;
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

    /**
     * @param fail
     * If true, force the machine to fail. Otherwise the machine will run as normal.
     * 
     * @return All machines that will fail as a result of this update (null if this machine does not fail)
     */
    public LinkedList<Equipment> update(boolean fail) {
        if (fail) {
           return updateFail();
        } else {
            updateSuccess();
            return null;
        }
    }

    public void updateSuccess() {
        rate = random.nextInt(validRateRange[0], validRateRange[1] + 1);
        temp = random.nextInt(validTempRange[0], validTempRange[1] + 1);
    }

    /**
     * Sets the machine's rate and temp to be either too high or low, with an applicable error message
     * for either scenario.
     * 
     * @return All machines that will fail as a result of this update
     */
    public LinkedList<Equipment> updateFail() {
        if (random.nextBoolean()) {
            rate = random.nextInt(validRateRange[1], validRateRange[1] * 2);
            temp = random.nextInt(validTempRange[1], validTempRange[1] * 2);
            alarmMessage = "Machine operating too fast. Safe rate and temperature exceeded.";
        } else {
            rate = random.nextInt(validRateRange[0]);
            temp = random.nextInt(validTempRange[0], validTempRange[1] + 1);
            alarmMessage = "Machine operating too slow.";
        }
        rootCause = true;
        alarming = true;
        return dependencies;
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
    public LinkedList<Equipment> fail(boolean overloaded) {
        if (!alarming) {
            if (overloaded) {
                alarmMessage = "Machine receiving too much input.";
                rate = validRateRange[1];
            } else {
                rate = validRateRange[0];
                alarmMessage = "Machine not receiving enough input.";
            }
            alarming = true;
            return dependencies;
        }
        return null;
    }

    public LinkedList<Equipment> fixRootCause() {
        if (rootCause && alarming) {
            updateSuccess();
            rootCause = false;
            alarming = false;
            return dependencies;
        } else {
            throw new Error("This machine either isn't the root cause of the error or isn't alarming.");
        }
    }

    public LinkedList<Equipment> fix() {
        if (alarming) {
            updateSuccess();
            alarming = false;
            return dependencies;
        } else {
            throw new Error("This machine isn't alarming.");
        }
    }

    /* public void changeBehavior(ChangeBehavior newBehavior) {
        newBehavior.changeBehavior();
    } */
}