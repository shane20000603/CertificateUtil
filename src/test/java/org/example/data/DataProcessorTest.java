package org.example.data;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class DataProcessorTest {

    @Test
    public void loadFile() {
    }

    @Test
    public void processByBitmap() throws IOException {
        long start = System.currentTimeMillis();
        DataProcessor dataProcessor = new DataProcessor();
        dataProcessor.processByBitmap();
        long end = System.currentTimeMillis();
        System.out.println("总时间: "+(end-start));
    }

    @Test
    public void processByPartition() throws IOException {
        long start = System.currentTimeMillis();
        new DataProcessor().processByPartition();
        long end = System.currentTimeMillis();
        System.out.println("总时间: "+(end-start));
    }

    @Test
    public void test1() throws IOException {
        DataProcessor dataProcessor = new DataProcessor();
        dataProcessor.test();
    }
}