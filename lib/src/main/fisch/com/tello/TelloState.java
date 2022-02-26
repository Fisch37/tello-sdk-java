package fisch.com.tello;

// This is so much useless code... In Python, I'd just use setattr to do all of this in a for-loop
public class TelloState {
    private int mission_pad_id, x, y, z, pitch, roll, yaw, x_velocity, y_velocity, z_velocity, lowest_temperature, highest_temperature, flight_distance, height, battery_state, flight_time;
    private double barometer, x_acceleration, y_acceleration, z_acceleration;

    public void __update_state__(
        int pMission_pad_id,                            // "mid"
        int pX,                                         // "x"
        int pY,                                         // "y"
        int pZ,                                         // "z"
        int[] pMpry,                                    // "mpry"
        int pPitch,                                     // "pitch"
        int pRoll,                                      // "roll"
        int pYaw,                                       // "yaw"
        int pX_velocity,                                // "vgx"
        int pY_velocity,                                // "vgy"
        int pZ_velocity,                                // "vgz"
        int pLowest_temperature,                        // "templ"
        int pHighest_temperature,                       // "temph"
        int pFlight_distance,                           // "tof"
        int pHeight,                                    // "h"
        int pBattery_state,                             // "bat"
        double pBarometer,                              // "baro"
        int pFlight_time,                               // "time"
        double pX_acceleration,                         // "agx"
        double pY_acceleration,                         // "agy"
        double pZ_acceleration                          // "agz"
    ){
        mission_pad_id = pMission_pad_id;
        x = pX;
        y = pY;
        z = pZ;
        pitch = pPitch;
        roll = pRoll;
        yaw = pYaw;
        x_velocity = pX_velocity;
        y_velocity = pY_velocity;
        z_velocity = pZ_velocity;
        lowest_temperature = pLowest_temperature;
        highest_temperature = pHighest_temperature;
        flight_distance = pFlight_distance;
        height = pHeight;
        battery_state = pBattery_state;
        barometer = pBarometer;
        flight_time = pFlight_time;
        x_acceleration = pX_acceleration;
        y_acceleration = pY_acceleration;
        z_acceleration = pZ_acceleration;
    }

    public int[] get_velocity(){
        int[] velocity_arr = {x_velocity, y_velocity, z_velocity};
        return velocity_arr;
    }
    public double[] get_acceleration(){
        double[] acceleration_arr = {x_acceleration, y_acceleration, z_acceleration};
        return acceleration_arr;
    }
    public int[] get_orientation(){
        int[] orientation = {pitch, roll, yaw};
        return orientation;
    }
    public int get_height(){
        return height;
    }

    public int get_mission_pad_id(){
        return mission_pad_id;
    }
    public int[] get_mp_position(){
        int[] position = {x,y,z};
        return position;
    }

    public double get_pressure(){
        return barometer;
    }

    public int get_lowest_recorded_temperature(){
        return lowest_temperature;
    }
    public int get_highest_recorded_temperature(){
        return highest_temperature;
    }

    public int get_battery_state(){
        return battery_state;
    }

    public int get_flight_distance(){
        return flight_distance;
    }
    public int get_flight_time(){
        return flight_time;
    }
}
