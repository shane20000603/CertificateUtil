package org.example.data;

import java.io.IOException;

public class DataProcessorProxy {

    private static final DataProcessor dataProcessor = new DataProcessor("D:\\pins\\test_data");
    static long checkPointStart;
    static long checkPointEnd;
    public static void process(){
        try {
            checkPointStart = System.currentTimeMillis();
            dataProcessor.processByBitmap();
            checkPointEnd = System.currentTimeMillis();
            System.out.println("第1步消耗时间："+(checkPointEnd-checkPointStart));

            checkPointStart = System.currentTimeMillis();
            dataProcessor.getCross();
            checkPointEnd = System.currentTimeMillis();
            System.out.println("第2步消耗时间："+(checkPointEnd-checkPointStart));

            checkPointStart = System.currentTimeMillis();
            dataProcessor.recheck();
            checkPointEnd = System.currentTimeMillis();
            System.out.println("第2步消耗时间："+(checkPointEnd-checkPointStart));

        } catch (IOException e){
            System.out.println("IO错误！");
            e.printStackTrace();
        }
    }

}
