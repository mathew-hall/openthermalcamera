package com.themarpe.openthermalcamera;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

public class SerialReaderTest {

    @Test
    public void testReadBasicFrame(){

        SerialReader serialReader = new SerialReader((frame) -> {});

        serialReader.handleNewData(new byte[]{0x12, 0x32, 0x5a});

        assertEquals(SerialReader.State.LOOK_FOR_START, serialReader.getState());

        serialReader.handleNewData(new byte[]{0x5a});

        assertEquals(SerialReader.State.READ_LENGTH, serialReader.getState());

        serialReader.handleNewData(new byte[]{0x01,0x0});

        assertEquals(SerialReader.State.READ_FRAME, serialReader.getState());

        assertEquals(1, serialReader.getLength());

        float value = 213.4F;
        int v = (int) (value * 100);
        byte[] packed = new byte[]{
                (byte) (v),
                (byte) (v >> 8),
        };

        serialReader.handleNewData(new byte[]{packed[0],packed[1], 0xa,0xa,0x12,0x23});

        assertEquals(value, serialReader.getFrame()[0], 0.0001f);

    }

    @Test
    public void testReadLargeFrame(){

        SerialReader serialReader = new SerialReader((frame) -> {});

        serialReader.handleNewData(new byte[]{0x12, 0x32, 0x5a});

        assertEquals(SerialReader.State.LOOK_FOR_START, serialReader.getState());

        serialReader.handleNewData(new byte[]{0x5a});

        assertEquals(SerialReader.State.READ_LENGTH, serialReader.getState());

        float[] frame = new float[1000];
        for(int i = 0; i < frame.length; i++){
            frame[i] = i/10f;
        }


        serialReader.handleNewData(new byte[]{(byte)frame.length,(byte)(frame.length >> 8)});

        assertEquals(SerialReader.State.READ_FRAME, serialReader.getState());

        assertEquals(frame.length, serialReader.getLength());

        byte[] packed = new byte[frame.length * 2];
        for(int i = 0; i < frame.length; i++){
            int v = (int) (frame[i] * 100f) ;
            packed[i*2] = (byte) (v & 0xff);
            packed[i*2+1] = (byte) (v >> 8 & 0xff);
        }

        serialReader.handleNewData(packed);

        serialReader.handleNewData(new byte[]{0xa,0xa,0x12,0x23});

        assertArrayEquals(frame, serialReader.getFrame(), 0.01f);
    }

}