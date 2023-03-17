package components;

public class CookieLine {
    int startRate = 6;
    int lowerRateLimit = 3;
    int upperRateLimit = 10;
    int startTemp = 30;
    int lowerTempLimit = 15;
    int upperTempLimit = 50;

    String[] resourceLabelsBoxers = {"Boxes", "Tape"};
    String[] resourceLabelsCookieFormer = {"Dough"};
    String[] resourceLabelsLabeler = {"Labels"};
    String[] resourceLabelsWrapper = {"Plastic Wrap"};

    int[] resources1 = {120};
    int[] resources2 = {120, 120};

    BasicEquipment conveyor1 = 
        new BasicEquipment(startRate, lowerRateLimit, upperRateLimit, startTemp, lowerTempLimit, upperTempLimit);
    BasicEquipment conveyor2 = 
        new BasicEquipment(startRate, lowerRateLimit, upperRateLimit, startTemp, lowerTempLimit, upperTempLimit);
    BasicEquipment conveyor3 = 
        new BasicEquipment(startRate, lowerRateLimit, upperRateLimit, startTemp, lowerTempLimit, upperTempLimit);

    BasicEquipment freezingTunnel =
        new BasicEquipment(startRate, lowerRateLimit, upperRateLimit, -20, -40, -10);

    EquipmentWithResources boxSealer = 
        new EquipmentWithResources(startRate, lowerRateLimit, upperRateLimit, startTemp, lowerTempLimit, upperTempLimit, resourceLabelsBoxers, resources2);

    EquipmentWithResources cookieFormer = 
        new EquipmentWithResources(startRate, lowerRateLimit, upperRateLimit, startTemp, lowerTempLimit, upperTempLimit, resourceLabelsCookieFormer, resources1);

    EquipmentWithResources boxErector = 
        new EquipmentWithResources(startRate, lowerRateLimit, upperRateLimit, startTemp, lowerTempLimit, upperTempLimit, resourceLabelsBoxers, resources2)

    EquipmentWithResources labelingBelt =
        new EquipmentWithResources(startRate, lowerRateLimit, upperRateLimit, startTemp, lowerTempLimit, upperTempLimit, resourceLabelsLabeler, resources1);
    
    EquipmentWithResources plasticLiner =
        new EquipmentWithResources(startRate, lowerRateLimit, upperRateLimit, startTemp, lowerTempLimit, upperTempLimit, resourceLabelsWrapper, resources1);
}
