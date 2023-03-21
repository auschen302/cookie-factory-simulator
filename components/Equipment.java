package components;

import java.util.LinkedList;
import org.json.simple.JSONObject;

interface Equipment {

    static final int failureRate = 2; //% chance of failure

    void addDependency(Equipment dependency);
    void setDependencies(LinkedList<Equipment> newDependencies);
    void addProvider(Equipment provider);
    void setProviders(LinkedList<Equipment> newProviders);
    String getName();
    boolean hasResources();
    boolean isAlarming();
    int isOverloaded();
    String getAlarmMessage();
    int getRate();
    int getTemp();
    LinkedList<Equipment> getNeighbors();
    //LinkedList<Equipment> update(boolean fail);
    //LinkedList<Equipment> fail(boolean overloaded);
    int update(boolean fail);
    int fail(int errorCode);
    LinkedList<Equipment> fixRootCause();
    LinkedList<Equipment> fix();
    JSONObject exportAsJSON();
}