package com.themarpe.openthermalcamera;


import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SerialReaderTest {

    @Test
    public void testReadBasicFrame(){

        SerialReader serialReader = new SerialReader();

        serialReader.handleNewData(new byte[]{0x12, 0x32, 0x5a});

        assertEquals(SerialReader.State.LOOK_FOR_START, serialReader.getState());

        serialReader.handleNewData(new byte[]{0x5a});

        assertEquals(SerialReader.State.READ_LENGTH, serialReader.getState());

        serialReader.handleNewData(new byte[]{0x00,0x1});

        assertEquals(SerialReader.State.READ_FRAME, serialReader.getState());

        float value = 213.4F;
        int v = (int) (value * 100);
        byte[] packed = new byte[]{
                (byte) (v),
                (byte) (v >> 8),
        };

        serialReader.handleNewData(new byte[]{packed[0],packed[1], 0xa,0xa,0x12,0x23});

        assertEquals(new float[]{value}, serialReader.getFrame());

    }

}