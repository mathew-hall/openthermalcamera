package com.themarpe.openthermalcamera;

import java.util.ArrayList;

public class SerialProtocol extends Protocol{


    private final SerialReader reader;

    public SerialProtocol(ISender sender, IResponseListener responseListener) {
        super(sender, responseListener);
        reader = new SerialReader();
    }

    @Override
    public void handleNewData(byte[] data) {
        reader.handleNewData(data);
    }



    @Override
    public void sendCommand(CmdStruct cmd) {
        //Not implemented!
    }
}
