package fisch.com.tello.exceptions;

import java.net.UnknownHostException;
public class UnknownAddress extends UnknownHostException{
    public UnknownAddress(String address){
        super(address + " could not be interpreted as a valid address");
    }
}
