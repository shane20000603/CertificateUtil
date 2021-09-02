package org.example.data;

import org.junit.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.*;

public class DataProcessorTest {

    @Test
    public void loadFile() {
    }

    @Test
    public void processByBitmap() throws Exception {
        long start = System.currentTimeMillis();
        DataProcessor dataProcessor = new DataProcessor();
        dataProcessor.processByBitmap();
        long end = System.currentTimeMillis();
        System.out.println("总时间: "+(end-start));
    }
}