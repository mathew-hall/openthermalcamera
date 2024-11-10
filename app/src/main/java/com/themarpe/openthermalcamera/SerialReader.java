package com.themarpe.openthermalcamera;

import java.util.function.Consumer;

public class SerialReader {

    public static final String TAG = "SERIAL";

    enum State {
        LOOK_FOR_START,
        READ_LENGTH,
        READ_FRAME,
        READ_LAST,
        READ_PARITY
    }

    private int frameIndex =0 ;
    private float[] frame;

    private Byte previousByte = null;

    private Consumer<float[]> frameConsumer;

    private State state = State.LOOK_FOR_START;
    int length;
    public SerialReader(Consumer<float[]> frameConsumer){
        this.frameConsumer = frameConsumer;

    }

    public void handleNewData(byte[] data){
        int startOffset = 0;
        if (previousByte != null) {
            startOffset = handleSequence(previousByte, data[0]) -1;
        }

        int i = startOffset;
        while(i < data.length-1){
            i += handleSequence(data[i], data[i+1]);
        }

        if((data.length - i) == 1) {
            previousByte = data[data.length - 1];
        }else {
            previousByte = null;
        }
    }

    private int handleSequence(byte b1, byte b2){
        switch(state){
            case LOOK_FOR_START:
                if(b1 == b2 && b1 == 0x5a){
                    Log.d(TAG, "Found start sequence");
                    state = State.READ_LENGTH;
                    return 2;
                }
                return 1;
            case READ_LENGTH:
                length = (b2<<8) & 0xff00 | b1 & 0xff;
                state = State.READ_FRAME;
                frame = new float[length];
                frameIndex = 0;
                Log.d(TAG, "Reading frame of length " + length + " bytes " + String.format("%02x %02x %02x %02x", b2, b1, b2<<8, b1));
                return 2;
            case READ_FRAME:
                frame[frameIndex++] = (b2<<8 & 0xff00 |  b1 & 0xff)/100f;
                length--;
                if(length == 0){
                    Log.d(TAG, "Finished reading frame");
                    state = State.READ_LAST;
                }
                return 2;
            case READ_LAST:
                state = State.READ_PARITY;
                return 2;
            case READ_PARITY:
                frameConsumer.accept(frame);
                state = State.LOOK_FOR_START;
                return 2;
        }
        return 1;
    }

    public float[] getFrame(){
        if(state == State.READ_FRAME){
            throw new IllegalStateException("Cannot get frame part-way through reading");
        }
        return frame;
    }

    public State getState(){
        return state;
    }

    public int getLength(){
        return length;
    }

}

class Log {
    public static void d(String tag, String message){
        System.out.println(tag + ": " + message);
    }
}