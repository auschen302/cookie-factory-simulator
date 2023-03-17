package components;

import java.util.LinkedList;

/**
 * Class for equipment that consume resources. E.g., a wrapping machine consumes packaging materials.
 * Consumes resources at rate per update.
 */
public class EquipmentWithResources extends BasicEquipment {

    String[] resourceLabels;
    int[] resources;
    boolean outOfResource;
    int outOfResourceIndex;

    public EquipmentWithResources(int startRate, int lowerRateLimit, int upperRateLimit, int startTemp, int lowerTempLimit, int upperTempLimit, String[] labels, int[] resourceStartingVals) {
        super(startRate, lowerRateLimit, upperRateLimit, startTemp, lowerTempLimit, upperTempLimit);
        resourceLabels = labels;
        resources = resourceStartingVals;
        outOfResource = false;
        outOfResourceIndex = -1;
    }

    public int checkStock(int index) {
        return resources[index];
    }

    public void restock(int index, int numRestocked) {
        resources[index] += numRestocked;
    }

    /**
     * Identical to the method in the super class with one exception. This
     */
    public LinkedList<Equipment> update(boolean fail) {
        if (fail) {
            return updateFail();
        } else {
            super.updateSuccess();
            for (int i = 0; i < resources.length; i++) {
                if (resources[i] < rate) {
                    outOfResource = true;
                    outOfResourceIndex = i;
                    return fail(false);
                } else {
                    resources[i] -= rate;
                }
            }
        }
        return null;
    }

    public LinkedList<Equipment> fail(boolean overloaded) {
        if (!alarming) {
            if (outOfResource) {
                alarmMessage = "Machine out of resource: " + resourceLabels[outOfResourceIndex];
                rate = 0;
                rootCause = true;
            } else {
                if (overloaded) {
                    alarmMessage = "Machine receiving too much input.";
                    rate = validRateRange[1];
                } else {
                    rate = validRateRange[0];
                    alarmMessage = "Machine not receiving enough input.";
                }
            }
            alarming = true;
            return dependencies;
        }
        return null;
    }

    public LinkedList<Equipment> fixRootCause() {
        if (rootCause && alarming) {
            super.fixRootCause();
            if (outOfResource) {
                resources[outOfResourceIndex] += rate * 10;
                outOfResource = false;
                outOfResourceIndex = -1;
            }
            return dependencies;
        } else {
            throw new Error("This machine either isn't the root cause of the error or isn't alarming.");
        }
        
    }

    public LinkedList<Equipment> fix() {
        if (alarming) {
            if (outOfResource) {
                throw new Error("Machine must be restocked before fixing");
            }
            return super.fix();
        } else {
            throw new Error("This machine isn't alarming.");
        }
    }
}
