package components;

import java.util.LinkedList;

public class Conveyor extends BasicEquipment {
    
    public Conveyor(int startRate, int lowerRateLimit, int upperRateLimit, int startTemp, int lowerTempLimit, int upperTempLimit) {
        super(startRate, lowerRateLimit, upperRateLimit, startTemp, lowerTempLimit, upperTempLimit);
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
