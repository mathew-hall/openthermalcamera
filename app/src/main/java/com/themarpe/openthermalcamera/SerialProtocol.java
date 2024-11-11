package com.themarpe.openthermalcamera;

import java.util.ArrayList;

public class SerialProtocol extends Protocol{

    private static final String TAG = "SerialProtocol";

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
        rsp.responseCode = RSP_SERIAL_FRAME;
        rsp.data = raw;
        responseQueue.add(rsp);
        Log.d(TAG, "Added packet to response queue, size " + responseQueue.size());
        if(responseListener != null) responseListener.onResponse(responseQueue);
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
