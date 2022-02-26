package fisch.com.tello;

public class Messages {
    // General
    private static String arg_assemble(String command, String[] args){
        String output = command;
        for (int i = 0; i < args.length; i++) output += " " + args[i];

        return output;
    }
    private static String arg_assemble(String command, String arg0){
        return command + " " + arg0;
    }

    // Takeoff and Landing
    public static String takeoff(){
        return "takeoff";
    }
    public static String land(){
        return "land";
    }

    // Emergency Stop
    public static String emergency(){
        return "emergency";
    }
    
    // Video Stream
    public static String videoon(){
        return "streamon";
    }
    public static String videooff(){
        return "streamoff";
    }
    
    // Movement
    public static String up(int distance){
        return arg_assemble("up",Integer.toString(distance));
    }
    public static String down(int distance){
        return arg_assemble("down",Integer.toString(distance));
    }
    public static String left(int distance){
        return arg_assemble("left",Integer.toString(distance));
    }
    public static String right(int distance){
        return arg_assemble("right",Integer.toString(distance));
    }
    public static String forward(int distance){
        return arg_assemble("forward",Integer.toString(distance));
    }
    public static String back(int distance){
        return arg_assemble("back",Integer.toString(distance));
    }
    public static String stop(){
        return "stop";
    }
    // Complex movement
    public static String flip(String direction){
        return arg_assemble("flip",direction);
    }
    public static String go(int x, int y, int z, int speed){
        String[] args = {
            Integer.toString(x),
            Integer.toString(y),
            Integer.toString(z),
            Integer.toString(speed)
        };
        return arg_assemble("go",args);
    }
    public static String go(int x, int y, int z, int speed, String mid){
        String[] args = {
            Integer.toString(x),
            Integer.toString(y),
            Integer.toString(z),
            Integer.toString(speed),
            mid
        };
        return arg_assemble("go",args);
    }
    public static String curve(int x1, int y1, int z1, int x2, int y2, int z2, int speed){
        String[] args = {
            Integer.toString(x1), Integer.toString(y1), Integer.toString(z1),
            Integer.toString(x2), Integer.toString(y2), Integer.toString(z2),
            Integer.toString(speed)
        };
        return arg_assemble("curve",args);
    }
    public static String curve(int x1, int y1, int z1, int x2, int y2, int z2, int speed, String mid){
        String[] args = {
            Integer.toString(x1), Integer.toString(y1), Integer.toString(z1),
            Integer.toString(x2), Integer.toString(y2), Integer.toString(z2),
            Integer.toString(speed),
            mid
        };
        return arg_assemble("curve",args);
    }
    public static String jump(int x, int y, int z, int speed, int yaw, String mid1, String mid2){
        String[] args = {
            Integer.toString(x), Integer.toString(y), Integer.toString(z),
            Integer.toString(speed), Integer.toString(yaw),
            mid1, mid2
        };
        return arg_assemble("jump",args);
    }
    
    // Rotation
    public static String clockwise(int x){
        return arg_assemble("cw",Integer.toString(x));
    }
    public static String counter_clockwise(int x){
        return arg_assemble("ccw",Integer.toString(x));
    }

    // Others
    public static String speed(int x){
        return arg_assemble("speed",Integer.toString(x));
    }
    
    public static String remote_controller(int left_right, int forward_backward, int up_down, int yaw){
        String[] args = {
            Integer.toString(left_right), 
            Integer.toString(forward_backward),
            Integer.toString(up_down),
            Integer.toString(yaw)
        };
        return arg_assemble("rc",args);
    }
    public static String wifi(String ssid, String password){
        String[] args = {ssid, password};
        return arg_assemble("wifi",args);
    }
    public static String access_point(String ssid, String password){
        String[] args = {ssid, password};
        return arg_assemble("ap",args);
    }

    public static String mission_pad_on(){
        return "mon";
    }
    public static String mission_pad_off(){
        return "moff";
    }
    public static String mission_pad_detect_direction(int direction){
        return arg_assemble("mdirection",Integer.toString(direction));
    }

    public static String initialize_sdk(){
        return "command";
    }

    public static String get_speed(){
        return "speed?";
    }
    public static String get_battery(){
        return "battery?";
    }
    public static String get_flight_time(){
        return "time?";
    }
    public static String get_wifi_strength(){
        return "wifi?";
    }
    public static String get_sdk_version(){
        return "sdk?";
    }
    public static String get_serial_number(){ // Hehe... Cereal number
        return "sn?";
    }
}
