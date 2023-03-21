package components;

import java.util.Scanner;
import java.util.LinkedList;

enum status {
    FAILING,
    FIXING,
    NORMAL,
}

public class CookieLine {
    int startRate = 6;
    int lowerRateLimit = 3;
    int upperRateLimit = 10;
    int startTemp = 30;
    int lowerTempLimit = 15;
    int upperTempLimit = 50;

    int errorCode = 0;
    status state = status.NORMAL;

    String[] resourceLabelsBoxers = {"Boxes", "Tape"};
    String[] resourceLabelsCookieFormer = {"Dough"};
    String[] resourceLabelsLabeler = {"Labels"};
    String[] resourceLabelsWrapper = {"Plastic Wrap"};

    int[] resources1 = {120};
    int[] resources2 = {120, 120};

    BasicEquipment conveyor1, conveyor2, conveyor3, freezingTunnel;
    EquipmentWithResources cookieFormer, boxErector, labelingBelt, plasticLiner, boxSealer;

    Equipment[] everything;

    LinkedList<Equipment> toFailOrFix = new LinkedList<Equipment>();

    public CookieLine() {
        conveyor1 = 
            new BasicEquipment("conveyor1", startRate, lowerRateLimit, upperRateLimit, startTemp, lowerTempLimit, upperTempLimit);
        conveyor2 = 
            new BasicEquipment("conveyor2", startRate, lowerRateLimit, upperRateLimit, startTemp, lowerTempLimit, upperTempLimit);
        conveyor3 = 
            new BasicEquipment("conveyor3", startRate, lowerRateLimit, upperRateLimit, startTemp, lowerTempLimit, upperTempLimit);

        freezingTunnel =
            new BasicEquipment("freezingTunnel", startRate, lowerRateLimit, upperRateLimit, -20, -40, -10);

        boxSealer = 
            new EquipmentWithResources("boxSealer", startRate, lowerRateLimit, upperRateLimit, startTemp, lowerTempLimit, upperTempLimit, resourceLabelsBoxers, resources2);

        cookieFormer = 
            new EquipmentWithResources("cookieFormer", startRate, lowerRateLimit, upperRateLimit, startTemp, lowerTempLimit, upperTempLimit, resourceLabelsCookieFormer, resources1);

        boxErector = 
            new EquipmentWithResources("boxErector", startRate, lowerRateLimit, upperRateLimit, startTemp, lowerTempLimit, upperTempLimit, resourceLabelsBoxers, resources2);

        labelingBelt =
            new EquipmentWithResources("labelingBelt", startRate, lowerRateLimit, upperRateLimit, startTemp, lowerTempLimit, upperTempLimit, resourceLabelsLabeler, resources1);
        
        plasticLiner =
            new EquipmentWithResources("plasticLiner", startRate, lowerRateLimit, upperRateLimit, startTemp, lowerTempLimit, upperTempLimit, resourceLabelsWrapper, resources1);

        cookieFormer.addDependency(conveyor1);
        conveyor1.addDependency(freezingTunnel);
        freezingTunnel.addDependency(conveyor3);
        conveyor3.addDependency(boxSealer);

        boxErector.addDependency(conveyor2);
        conveyor2.addDependency(plasticLiner);
        plasticLiner.addDependency(labelingBelt);
        labelingBelt.addDependency(boxSealer);

        conveyor1.addProvider(cookieFormer);
        freezingTunnel.addProvider(conveyor1);
        conveyor3.addProvider(freezingTunnel);
        boxSealer.addProvider(conveyor3);

        conveyor2.addProvider(boxErector);
        plasticLiner.addProvider(conveyor2);
        labelingBelt.addProvider(plasticLiner);
        boxSealer.addProvider(labelingBelt);
        everything = new Equipment[]{cookieFormer, conveyor1, freezingTunnel, conveyor3, boxErector, conveyor2, plasticLiner, labelingBelt, boxSealer};
    }

    public void setState(status newState) {
        state = newState;
    }

    public void displayState() {
        for (Equipment e: everything) {
            if (e.isAlarming()) {
                System.out.println(e.getName() + " is alarming with status: " + e.getAlarmMessage());
            } else {
                System.out.println(e.getName() + " operating normally.");
                System.out.println("Rate: " + e.getRate());
                System.out.println("Temp: " + e.getTemp());
                if (e.hasResources()) {
                    EquipmentWithResources temp = (EquipmentWithResources) e;
                    for (String label: temp.getResources().keySet()) {
                        System.out.println("Resource: " + label + " | Quantity: " + temp.getResources().get(label));
                    }
                }
            }
            System.out.println("");
        }
    }

    public void setErrorCode (int errorCode) {
        this.errorCode = errorCode;
    }

    public void beginFailOrFix(LinkedList<Equipment> relevant) {
        toFailOrFix = relevant;
    }

    public void updateAll() {
        LinkedList<Equipment> nextDependencies = new LinkedList<Equipment>();
        
        for (Equipment e: toFailOrFix) {
            switch (state) {
                case FAILING:
                    if (!e.isAlarming()) {
                        e.fail(errorCode);
                        nextDependencies.addAll(e.getNeighbors());
                    }
                    /*
                    boolean overload;
                    switch (overloading) {
                        case 1: overload = false;
                                break;
                        case 2: overload = true;
                            break;
                        default:
                            throw new Error("This machine isn't alarming.");
                    }
                    nextDependencies.addAll(e.fail(overload));
                    */
                    break;
                case FIXING: 
                    nextDependencies.addAll(e.fix());
                    break;
                default:
                    throw new Error("Nothing to do.");
            }
        }
        toFailOrFix = nextDependencies;
        if (state == status.FIXING && nextDependencies.isEmpty()) setState(status.NORMAL);
        for (Equipment e: everything) {
            if (!e.isAlarming()) {
                e.update(false);
                /*
                if (!errors.isEmpty()) {
                    setState(status.FAILING);
                    toFailOrFix.addAll(errors);
                }
                */
            }
        }
    }

    public void getJSON() {
        for (Equipment e: everything) {
            System.out.println(e.exportAsJSON().toJSONString());
        }
    }


    public static void main(String[] args) {
        CookieLine line = new CookieLine();
        Scanner input = new Scanner(System.in);
        loop: while(true){
        System.out.println("Enter command: ");
        String command = input.nextLine();
        first: switch(command) {
            case "status":
                line.displayState();
                break;
            case "next":
                line.updateAll();
                break;
            case "JSON":
                line.getJSON();
                break;
            case "exit":
                break loop;
            default:
                String[] commands = command.split(" ");
                Equipment target;
                if (commands.length < 2) {
                    System.out.println("Command not recognized");
                    break first;
                }
                switch (commands[1]) {
                    case "conveyor1":
                        target = line.conveyor1;
                        break;
                    case "conveyor2":
                        target = line.conveyor2;
                        break;
                    case "conveyor3":
                        target = line.conveyor3;
                        break;
                    case "cookieFormer":
                        target = line.cookieFormer;
                        break;
                    case "freezingTunnel":
                        target = line.freezingTunnel;
                        break;
                    case "boxErector":
                        target = line.boxErector;
                        break;
                    case "plasticLiner":
                        target = line.plasticLiner;
                        break;
                    case "labelingBelt":
                        target = line.labelingBelt;
                        break;
                    case "boxSealer":
                        target = line.boxSealer;
                        break;
                    default:
                        System.out.println("Command not recognized");
                        break first;
                }
                switch (commands[0]) {
                    case "fix":
                        target.fixRootCause();
                        line.setState(status.FIXING);
                        break;
                    case "fail":
                        target.update(true);
                        line.setState(status.FAILING);
                        line.errorCode = 1;
                        break;
                    default: 
                        System.out.println("Command not recognized");
                        break first;
                }
                line.beginFailOrFix(target.getNeighbors());
                break;
            }
        }
        input.close();
    }
}
