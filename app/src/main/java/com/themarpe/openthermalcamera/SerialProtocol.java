package com.themarpe.openthermalcamera;

import java.util.ArrayList;

public class SerialProtocol extends Protocol{


    private final SerialReader reader;

    public SerialProtocol(ISender sender, IResponseListener responseListener) {
        super(sender, responseListener);
        reader = new SerialReader(this::handleFrame);
    }

    public void handleFrame(float[] frame){
        ArrayList<Integer> raw = new ArrayList<>();
        for (float v : frame) {
            raw.add((int) (v * 100));
        }

        RspStruct rsp = new RspStruct();
        rsp.responseCode = RSP_GET_FRAME_DATA;
        rsp.data = raw;
        responseQueue.add(rsp);
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
