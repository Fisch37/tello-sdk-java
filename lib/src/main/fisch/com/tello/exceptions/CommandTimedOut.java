package fisch.com.tello.exceptions;

import java.net.SocketTimeoutException;	
public class CommandTimedOut extends SocketTimeoutException{
    public CommandTimedOut(){
        super("The called command timed out");
    }
}
