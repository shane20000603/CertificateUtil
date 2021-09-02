package org.example;

import org.example.data.DataProcessor;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        DataProcessor dataProcessor = new DataProcessor();
        long start = System.currentTimeMillis();
        try {
            dataProcessor.processByBitmap();
        } catch (IOException exception) {
            System.out.println("IOException");
            exception.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("运行时间: "+(end-start)+"ms");
    }
}
