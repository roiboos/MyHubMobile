package smarthome.petersen.com.myhub.datamodel;

/**
 * Created by mull12 on 05.02.2018.
 */

public class Sensor
{
    public final static String SENSOR_TYPE_OPENCLOSE = "openclose";
    public final static String SENSOR_TYPE_MOTION = "motion";

    public SensorState state;

    public String name;

    public String id;

    public String type;
}
