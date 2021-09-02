package org.example;

import org.example.data.DataProcessor;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Main {
    public static void main(String[] args) throws Exception {
        DataProcessor dataProcessor = new DataProcessor();
        long start = System.currentTimeMillis();
        dataProcessor.processByBitmap();
        long cp1 = System.currentTimeMillis();
        System.out.println("第一步运行时间: "+(cp1-start)+"ms");
        dataProcessor.distinctFromFiles();
        long cp2 = System.currentTimeMillis();
        System.out.println("第二部运行时间： "+(cp2-cp1)+"ms");
    }
}
