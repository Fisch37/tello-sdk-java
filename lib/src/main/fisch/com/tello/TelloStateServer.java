package fisch.com.tello;

import java.net.*;
import java.io.IOException;
import java.lang.Thread;

public class TelloStateServer extends Thread{
    private final static int            port            = 8890;

    private final static int default_buffer_size = 512;
    private final static int timeout = 5000;

    private DatagramSocket server;
    private int buffer_size;
    private Thread listen_thread;
    private InetAddress    bound_address;
    private boolean stop_server;

    public TelloState state;

    TelloStateServer() throws SocketException, UnknownHostException{
        this(default_buffer_size);
    }

    TelloStateServer(int pBufferSize) throws SocketException, UnknownHostException{
        bound_address = InetAddress.getByName("0.0.0.0");
        server = new DatagramSocket(port,bound_address);
        
        buffer_size = pBufferSize;
        server.setSoTimeout(timeout);

        state = new TelloState();
    }

    private String[] decode_no_mp_msg(String message){
        String[] splitted = message.split(";");

        String[] values = new String[splitted.length-1];
        for(int i = 0; i < splitted.length-1; i++){
            String[] entry_split = splitted[i].split(":");
            String value = entry_split[1];

            values[i] = value;
        }

        return values;
    }
    
    private int[] decode_mpry(String mpry){
        String[] mpry_split = mpry.split(",");
        int[] converted = new int[3];
        for (int i = 0; i < 3; i++){
            converted[i] = Integer.valueOf(mpry_split[i]);
        }

        return converted;
    }

    public void run(){
        // Message example: mid:-1;x:-100;y:-100;z:-100;mpry:0,0,0;pitch:0;roll:1;yaw:23;vgx:0;vgy:0;vgz:0;templ:64;temph:65;tof:10;h:0;bat:81;baro:-94.44;time:0;agx:-16.00;agy:-16.00;agz:-1002.00;
        byte[] buffer = new byte[buffer_size];

        while(!stop_server){
            DatagramPacket state_msg = new DatagramPacket(buffer,buffer_size);
            try {
                server.receive(state_msg);
            } catch (IOException e){continue;} // This also handles a timeout error, which is neat. In case of a timeout, we just want to check whether we got a stop instruction

            String message = new String(buffer, 0, state_msg.getLength());
            String[] decoded = decode_no_mp_msg(message);

            // No! You simply can't use the complex representations of int and double! That would go against everything Java stands for! AHHHHHHHH
            state.__update_state__(
                (int)Integer.valueOf(decoded[0]),

                (int)Integer.valueOf(decoded[1]), 
                (int)Integer.valueOf(decoded[2]),
                (int)Integer.valueOf(decoded[3]),

                decode_mpry(decoded[4]),

                (int)Integer.valueOf(decoded[5]),
                (int)Integer.valueOf(decoded[6]),
                (int)Integer.valueOf(decoded[7]),

                (int)Integer.valueOf(decoded[8]),
                (int)Integer.valueOf(decoded[9]),
                (int)Integer.valueOf(decoded[10]),

                (int)Integer.valueOf(decoded[11]),
                (int)Integer.valueOf(decoded[12]),

                (int)Integer.valueOf(decoded[13]),

                (int)Integer.valueOf(decoded[14]),

                (int)Integer.valueOf(decoded[15]),

                (double)Double.valueOf(decoded[16]),

                (int)Integer.valueOf(decoded[17]),

                (double)Double.valueOf(decoded[18]),
                (double)Double.valueOf(decoded[19]),
                (double)Double.valueOf(decoded[20])
            );
        }
        server.close();
    }

    public void start(){
        if (listen_thread == null){
            listen_thread = new Thread(this,"Tello State Listener");
            listen_thread.setDaemon(true);
            listen_thread.start();
        }
    }

    public void stop_server(){
        stop_server = true;
        while(!server.isClosed()){}
    }
}
