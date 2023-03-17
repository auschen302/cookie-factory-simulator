package components;

import java.util.LinkedList;

public class BoxSealer extends BasicEquipment {

    public BoxSealer(int startRate, int lowerRateLimit, int upperRateLimit, int startTemp, int lowerTempLimit, int upperTempLimit, int[] resourceStartingVals) {
        super(startRate, lowerRateLimit, upperRateLimit, startTemp, lowerTempLimit, upperTempLimit, resourceStartingVals);
    }

    public LinkedList<Equipment> updateRandom() {
        if (random.nextInt(100) < failureRate) {
            //fail and update dependencies
            return dependencies;
        } else {
            //random number in proper range
            return null;
        }
    }

    public LinkedList<Equipment> fail() {
        //fail and update dependencies
        return (alarming ? dependencies : null);
    }

}
