package fisch.com.tello;

import fisch.com.tello.exceptions.*;

import java.io.IOException;
import java.net.*;

public class TelloController {
    private final static int default_buffer_size = 256;
    private final static int default_timeout     = 15;  // You could set this to zero and risk your program stopping in the middle of execution


    private DatagramSocket socket;
    private InetAddress address;
    private int port;

    private boolean retry_policy;

    private TelloStateServer state_server;
    public TelloState state;

    // Constructors
    public TelloController(String pAddress, int pPort) throws SocketException, UnknownAddress{
        this(pAddress,pPort,default_timeout, false);
    }
    public TelloController(String pAddress, int pPort, boolean pRetryPolicy) throws SocketException, UnknownAddress{
        this(pAddress,pPort,default_timeout, pRetryPolicy);
    }

    public TelloController(String pAddress, int pPort, int packet_timeout, boolean pRetryPolicy) throws SocketException, UnknownAddress{
        retry_policy = pRetryPolicy;
        
        socket = new DatagramSocket();
        socket.setSoTimeout(packet_timeout);
        
        try{address = InetAddress.getByName(pAddress);}
        catch(UnknownHostException e){
            throw new UnknownAddress(pAddress); // Throw exception if IP Address not valid 
        }
        port = pPort;

        boolean sdk_inited = false;
        //while (!sdk_inited) sdk_inited = (send_fixed(Messages.initialize_sdk()).equals("ok")); // Force SDK to be enabled
        send_fixed(Messages.initialize_sdk());

        try{ state_server = new TelloStateServer();}
        catch (UnknownHostException e){} // This won't occur anyway. Like, it actually can't...
        state_server.start();

        state = state_server.state;
    }

    // Private Methods
    private String send_fixed(String message){
        // Keep retrying until the command has worked
        while (true){
            try{ 
                return send_raw(message);
            } catch (CommandTimedOut e){} // Ignore command time out
        }
    }

    private String send_raw(String message) throws CommandTimedOut{
        return send_raw(message.getBytes());
    }

    private String send_raw(String message, int buffer_size) throws CommandTimedOut{
        return send_raw(message.getBytes(), buffer_size);
    }

    private String send_raw(byte[] message) throws CommandTimedOut{
        return send_raw(message, default_buffer_size);
    }

    private String send_raw(byte[] message,int buffer_size) throws CommandTimedOut{
        DatagramPacket send_packet = new DatagramPacket(message,message.length,address,port);
        try{socket.send(send_packet);}
        catch (IOException e){ // Now that's something to write into the documentation. I didn't bother throwing an IOException because this scenario is really unlikely
            return "";
        }

        byte[] buffer = new byte[buffer_size];
        DatagramPacket recv_packet = new DatagramPacket(buffer,buffer_size);
        try{socket.receive(recv_packet);}
        catch (SocketTimeoutException e){
            throw new CommandTimedOut();
        }
        catch (IOException e){ // Again, hilariously unlikely. Even the documentation doesn't know when this should occur
            return "";
        }

        return new String(buffer,0,recv_packet.getLength());
    }

    private boolean control_command_base(String message, boolean retry){
        String response;
        if (retry) response=send_fixed(message);
        else{
            try{
                response = send_raw(message);
            } catch(CommandTimedOut e){
                response = "error"; // This isn't neccessarily true, but it's close
            }
        }

        return response == "ok";
    }
    private String read_command_base(String message, boolean retry) throws CommandTimedOut{
        String response;
        if (retry) response=send_fixed(message);
        else       response=send_raw  (message);

        return response;
    }

    private static int limit_distance(int distance){
        if      (distance < 20 ) distance = 20 ;
        else if (distance > 500) distance = 500;

        return distance;
    }
    private static int limit_rotation(int degrees){
        if (degrees == 360 || degrees == -360) return degrees; // If you ever want to do a 360 ;) (Also, didn't bother to import Math.abs)
        else degrees = degrees % 360;
        if (degrees < 0){
            degrees = 360 + degrees;
        }

        return degrees;
    }

    private static int limit_space(int position){
        if      (position < -500) position = -500;
        else if (position >  500) position =  500;

        return position;
    }

    private static int limit_speed(int speed){
        if      (speed > 100) speed = 100;
        else if (speed <  10) speed =  10;

        return speed;
    }
    private static int limit_speed_weak(int speed){
        if      (speed > 60) speed = 60;
        else if (speed < 10) speed = 10;

        return speed;
    }

    private static int limit_controller(int val){
        if      (val > 100)  val =  100;
        else if (val < -100) val = -100;

        return val;
    }

    // Public methods
    public boolean takeoff(){
        return takeoff(retry_policy);
    }
    public boolean takeoff(boolean retry){
        return control_command_base(Messages.takeoff(), retry);
    }

    public boolean land(){
        return land(retry_policy);
    }
    public boolean land(boolean retry){
        return control_command_base(Messages.land(), retry);
    }

    public boolean emergency_shutoff(){
        return control_command_base(Messages.emergency(), true); // It's an emergency! We don't want it to just lose the package and not shut off!
    }

    // This is going to be a lot of duplicate code... But we don't have factory methods in Java! (At least I think so)
    // Basic movement
    public boolean up(int distance){
        return up(distance,retry_policy);
    }
    public boolean up(int distance, boolean retry){
        distance = limit_distance(distance);

        return control_command_base(Messages.up(distance), retry);
    }
    
    public boolean down(int distance){
        return down(distance,retry_policy);
    }
    public boolean down(int distance, boolean retry){
        distance = limit_distance(distance);

        return control_command_base(Messages.down(distance), retry);
    }

    public boolean left(int distance){
        return left(distance,retry_policy);
    }
    public boolean left(int distance, boolean retry){
        distance = limit_distance(distance);

        return control_command_base(Messages.left(distance), retry);
    }

    public boolean right(int distance){
        return right(distance,retry_policy);
    }
    public boolean right(int distance, boolean retry){
        distance = limit_distance(distance);

        return control_command_base(Messages.right(distance), retry);
    }

    public boolean forward(int distance){
        return forward(distance,retry_policy);
    }
    public boolean forward(int distance, boolean retry){
        distance = limit_distance(distance);

        return control_command_base(Messages.forward(distance), retry);
    }

    public boolean backward(int distance){
        return backward(distance,retry_policy);
    }
    public boolean backward(int distance, boolean retry){
        distance = limit_distance(distance);

        return control_command_base(Messages.back(distance), retry);
    }

    public boolean stop(){
        return stop(retry_policy);
    }
    public boolean stop(boolean retry){
        return control_command_base(Messages.stop(),retry);
    }

    // Rotation
    public boolean rotate_clockwise(int degrees){
        return rotate_clockwise(degrees, retry_policy);
    }
    public boolean rotate_clockwise(int degrees, boolean retry){
        degrees = limit_rotation(degrees);

        return control_command_base(Messages.clockwise(degrees), retry);
    }

    public boolean rotate_counter_clockwise(int degrees){
        return rotate_counter_clockwise(degrees, retry_policy);
    }
    public boolean rotate_counter_clockwise(int degrees, boolean retry){
        degrees = limit_rotation(degrees);

        return control_command_base(Messages.counter_clockwise(degrees), retry);
    }

    public boolean rotate(int degrees){
        return rotate(degrees,retry_policy);
    }
    public boolean rotate(int degrees, boolean retry){
        if (degrees < 0){
            return rotate_counter_clockwise(degrees,retry);
        } else{
            return rotate_clockwise(degrees, retry);
        }
    }

    // Flips
    public boolean flip(String direction){
        return flip(direction,retry_policy);
    }
    public boolean flip(String direction, boolean retry){
        return control_command_base(Messages.flip(direction), retry);
    }

    public boolean frontflip(){
        return frontflip(retry_policy);
    }
    public boolean frontflip(boolean retry){
        return flip("f",retry);
    }

    public boolean backflip(){
        return backflip(retry_policy);
    }
    public boolean backflip(boolean retry){
        return flip("b",retry);
    }

    public boolean leftflip(){
        return leftflip(retry_policy);
    }
    public boolean leftflip(boolean retry){
        return flip("l",retry);
    }

    public boolean rightflip(){
        return rightflip(retry_policy);
    }
    public boolean rightflip(boolean retry){
        return flip("r",retry);
    }

    // Other maneuvers
    public boolean arccurve(int x1, int y1, int z1, int x2, int y2, int z2, int speed){
        return arccurve(x1,y1,z1,x2,y2,z2,speed);
    }
    public boolean arccurve(int x1, int y1, int z1, int x2, int y2, int z2, int speed, boolean retry){
        x1 = limit_space(x1);
        y1 = limit_space(y1);
        z1 = limit_space(z1);

        x2 = limit_space(x2);
        y2 = limit_space(y2);
        z2 = limit_space(z2);

        speed = limit_speed_weak(speed);


        return control_command_base(Messages.curve(x1,y1,z1,x2,y2,z2,speed), retry);
    }

    public boolean go(int x, int y, int z, int speed){
        x = limit_space(x);
        y = limit_space(y);
        z = limit_space(z);

        speed = limit_speed(speed);
        

        return go(x,y,z,speed,retry_policy);
    }
    public boolean go(int x, int y, int z, int speed, boolean retry){
        return control_command_base(Messages.go(x,y,z,speed),retry);
    }
    // Now all of that with mission pads! (Yay!)
    public boolean arccurve(int x1, int y1, int z1, int x2, int y2, int z2, int speed, String mission_pad_id){
        return arccurve(x1,y1,z1,x2,y2,z2,speed,mission_pad_id,retry_policy);
    }
    public boolean arccurve(int x1, int y1, int z1, int x2, int y2, int z2, int speed, String mission_pad_id, boolean retry){
        x1 = limit_space(x1);
        y1 = limit_space(y1);
        z1 = limit_space(z1);

        x2 = limit_space(x2);
        y2 = limit_space(y2);
        z2 = limit_space(z2);

        speed = limit_speed_weak(speed);


        return control_command_base(Messages.curve(x1,y1,z1,x2,y2,z2,speed,mission_pad_id), retry);
    }

    public boolean go(int x, int y, int z, int speed, String mission_pad_id){
        return go(x,y,z,speed,retry_policy);
    }
    public boolean go(int x, int y, int z, int speed, String mission_pad_id, boolean retry){
        x = limit_space(x);
        y = limit_space(y);
        z = limit_space(z);

        speed = limit_speed(speed);


        return control_command_base(Messages.go(x,y,z,speed,mission_pad_id),retry);
    }

    public boolean jump(int x, int y, int z, int speed, int yaw, String mission_pad_id_1, String mission_pad_id_2){
        return jump(x,y,z,speed,yaw,mission_pad_id_1,mission_pad_id_2,retry_policy);
    }
    public boolean jump(int x, int y, int z, int speed, int yaw, String mission_pad_id_1, String mission_pad_id_2, boolean retry){
        x = limit_space(x);
        y = limit_space(y);
        z = limit_space(z);

        speed = limit_speed(speed);
        yaw   = limit_rotation(yaw);


        return control_command_base(Messages.jump(x,y,z,speed,yaw,mission_pad_id_1,mission_pad_id_2),retry);
    }

    // Configuration
    public boolean speed(int pSpeed){
        return speed(pSpeed,retry_policy);
    }
    public boolean speed(int pSpeed, boolean retry){
        return control_command_base(Messages.speed(pSpeed),retry);
    }

    public boolean remote_controller(int left_right, int forward_backward, int up_down, int yaw){
        return remote_controller(left_right,forward_backward,up_down,yaw, retry_policy);
    }
    public boolean remote_controller(int left_right, int forward_backward, int up_down, int yaw, boolean retry){
        left_right       = limit_controller(left_right      );
        forward_backward = limit_controller(forward_backward);
        up_down          = limit_controller(up_down         );
        yaw              = limit_controller(yaw             );

        return control_command_base(Messages.remote_controller(left_right, forward_backward, up_down, yaw),retry);
    }

    public boolean set_wifi(String ssid, String password){
        return set_wifi(ssid,password,retry_policy);
    }
    public boolean set_wifi(String ssid, String password, boolean retry){
        return control_command_base(Messages.wifi(ssid, password),retry);
    }
    
    public boolean mission_pad_detection_on(){
        return mission_pad_detection_on(retry_policy);
    }
    public boolean mission_pad_detection_on(boolean retry){
        return control_command_base(Messages.mission_pad_on(),retry);
    }

    public boolean mission_pad_detection_off(){
        return mission_pad_detection_on(retry_policy);
    }
    public boolean mission_pad_detection_off(boolean retry){
        return control_command_base(Messages.mission_pad_off(),retry);
    }

    public boolean mission_pad_detect_direction(boolean downward, boolean forward){
        return mission_pad_detect_direction(downward,forward,retry_policy);
    }
    public boolean mission_pad_detect_direction(boolean downward, boolean forward, boolean retry){
        int direction = (((downward) ? 1 : 0)*1 + ((forward) ? 1 : 0)*2) - 1; // This is some math stuff to figure out what number I should send the drone

        return control_command_base(Messages.mission_pad_detect_direction(direction),retry);
    }
    
    public boolean set_access_point(String ssid, String password){
        return set_access_point(ssid,password);
    }
    public boolean set_access_point(String ssid, String password, boolean retry){
        return control_command_base(Messages.access_point(ssid, password), retry);
    }

    // Read Commands (The information giving ones)
    public int get_speed() throws CommandTimedOut{
        return get_speed(retry_policy);
    }
    public int get_speed(boolean retry) throws CommandTimedOut{
        return Integer.parseInt(
            read_command_base(Messages.get_speed(),retry)
        );
    }

    public int get_battery() throws CommandTimedOut{
        return get_battery(retry_policy);
    }
    public int get_battery(boolean retry) throws CommandTimedOut{
        return Integer.parseInt(
            read_command_base(Messages.get_battery(),retry)
        );
    }

    public String get_flight_time() throws CommandTimedOut{
        return get_flight_time(retry_policy);
    }
    public String get_flight_time(boolean retry) throws CommandTimedOut{
        return read_command_base(Messages.get_flight_time(), retry);
    }

    public String get_wifi_strength() throws CommandTimedOut{
        return get_wifi_strength(retry_policy);
    }
    public String get_wifi_strength(boolean retry) throws CommandTimedOut{
        return read_command_base(Messages.get_wifi_strength(), retry);
    }

    public String get_sdk_version() throws CommandTimedOut{
        return get_sdk_version(retry_policy);
    }
    public String get_sdk_version(boolean retry) throws CommandTimedOut{
        return read_command_base(Messages.get_sdk_version(), retry);
    }

    public String get_serial_number() throws CommandTimedOut{
        return get_serial_number(retry_policy);
    }
    public String get_serial_number(boolean retry) throws CommandTimedOut{
        return read_command_base(Messages.get_serial_number(), retry);
    }

    // In the end I'm gonna find out that there are factory methods in Java and all of this could have been done in 1/10 of the effort and code length

    public void close(){
        state_server.stop_server();
        socket.close();
    }
}
