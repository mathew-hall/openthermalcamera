package com.themarpe.openthermalcamera;

public class SerialReader {

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

    private State state = State.LOOK_FOR_START;
    int length;
    public SerialReader(){

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
        }
    }

    private int handleSequence(byte b1, byte b2){
        switch(state){
            case LOOK_FOR_START:
                if(b1 == b2 && b1 == 0x5a){
                    state = State.READ_LENGTH;
                    return 2;
                }
                return 1;
            case READ_LENGTH:
                length = b2 *256 + b1;
                state = State.READ_FRAME;
                frame = new float[length];
                frameIndex = 0;
                return 2;
            case READ_FRAME:
                frame[frameIndex++] = (b2 *256 + b1)/100f;
                length--;
                if(length == 0){
                    state = State.READ_LAST;
                    notifyAll();
                }
                return 2;
            case READ_LAST:
                state = State.READ_PARITY;
                return 2;
            case READ_PARITY:
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

    public float[] waitForFrame() throws InterruptedException {
        wait();
        return getFrame();
    }

    public State getState(){
        return state;
    }

}
