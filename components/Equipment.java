package components;

import java.util.LinkedList;

interface Equipment {

    static final int failureRate = 2; //% chance of failure

    void addDependency(Equipment dependency);
    void setDependencies(LinkedList<Equipment> newDependencies);
    String getAlarmMessage();
    int getRate();
    int getTemp();
    //void changeBehavior(ChangeBehavior newBehavior);
    LinkedList<Equipment> update(boolean fail);
    //void updateSuccess();
    //LinkedList<Equipment> updateFail();
    LinkedList<Equipment> fail(boolean overloaded);
    LinkedList<Equipment> fixRootCause();
    LinkedList<Equipment> fix();
}