package components;

import java.util.LinkedList;

import org.json.simple.JSONObject;

import java.util.HashMap;

/**
 * Class for equipment that consume resources. E.g., a wrapping machine consumes packaging materials.
 * Consumes resources at rate per update.
 */
public class EquipmentWithResources extends BasicEquipment {

    String[] resourceLabels;
    //int[] resources;
    HashMap<String, Integer> resources;
    boolean outOfResource;
    String outOfResourceLabel;

    public EquipmentWithResources(String name, int startRate, int lowerRateLimit, int upperRateLimit, int startTemp, int lowerTempLimit, int upperTempLimit, String[] labels, int[] resourceStartingVals) {
        super(name, startRate, lowerRateLimit, upperRateLimit, startTemp, lowerTempLimit, upperTempLimit);
        resources = new HashMap<String, Integer>();
        for (int i = 0; i < labels.length; i++) {
            resources.put(labels[i], resourceStartingVals[i]);
        }
        //resourceLabels = labels;
        //resources = resourceStartingVals;
        outOfResource = false;
        outOfResourceLabel = "";
    }

    public boolean hasResources() {
        return true;
    }

    public int checkStock(String label) {
        return resources.get(label);
    }

    public HashMap<String, Integer> getResources() {
        return resources;
    }

    public void modifyResource(String label, int diff) {
        resources.put(label, resources.get(label) + diff);
    }

    /*
    public LinkedList<Equipment> update(boolean fail) {
        if (fail) {
            return updateFail();
        } else {
            super.updateSuccess();
            for (String label: resources.keySet()) {
                if (resources.get(label) < rate) {
                    
                    outOfResource = true;
                    outOfResourceIndex = i;
                    return fail(false);
                    
                    this.modifyResource(label, rate * 10);
                } else {
                    this.modifyResource(label, -rate);
                }
            }
        }
        return new LinkedList<Equipment>();
    }
    */

    public int update(boolean fail) {
        if (fail) {
            return updateFail();
        } else {
            super.updateSuccess();
            for (String label: resources.keySet()) {
                if (resources.get(label) < rate) {
                    this.modifyResource(label, rate * 10);
                } else {
                    this.modifyResource(label, -rate);
                }
            }
        }
        return 0;
    }

    /*
    public LinkedList<Equipment> updateFail() {
        switch (random.nextInt(3)) {
            case 0:
            case 1:
                return super.updateFail();
            case 2:
                int depletedResource = random.nextInt(resources.size());
                outOfResourceLabel = (String) resources.keySet().toArray()[depletedResource];
                resources.put(outOfResourceLabel, 0);
                return providers;
            default:
                return new LinkedList<Equipment>();
        }
    }
    */

    public int updateFail() {
        if (random.nextBoolean()) {
            return super.updateFail();
        } else {
            int depletedResource = random.nextInt(resources.size());
            outOfResourceLabel = (String) resources.keySet().toArray()[depletedResource];
            resources.put(outOfResourceLabel, 0);
            return 1;
        }
    }

    public LinkedList<Equipment> fail(boolean overloaded) {
        if (!alarming) {
            alarming = true;
            if (outOfResource) {
                alarmMessage = "Machine out of resource: " + outOfResourceLabel;
                rate = 0;
                rootCause = true;
                return providers;
            } else {
                if (overloaded) {
                    alarmMessage = "Machine receiving too much input.";
                    rate = validRateRange[1];
                    return dependencies;
                } else {
                    rate = validRateRange[0];
                    alarmMessage = "Machine creating too much output.";
                    return providers;
                }
            }
            //alarming = true;
            //return neighbors;
        }
        return new LinkedList<Equipment>();
    }

    public LinkedList<Equipment> fixRootCause() {
        if (rootCause && alarming) {
            super.fixRootCause();
            if (outOfResource) {
                this.modifyResource(outOfResourceLabel, rate * 10);
                outOfResource = false;
                outOfResourceLabel = "";
            }
            return neighbors;
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
        }
        return new LinkedList<Equipment>();
    }

    public JSONObject exportAsJSON() {
        JSONObject toRet = super.exportAsJSON();
        toRet.put("Resources", resources);
        return toRet;
    }
}
