package org.example.data;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class DataProcessor {
    private final MultiBitSet multiBitSet = new MultiBitSet();
    private final Set<String> set = new HashSet<>();
    //output
    private final File file = new File("src\\pins");
    private FileWriter fileWriter;

    //处理输入数据，加载文件
    @Deprecated
    public static StringBuilder loadFile(String path) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (InputStream inputStream = new FileInputStream(path)) {
            for (; ; ) {
                int read = inputStream.read();
                if (read == -1) break;
                stringBuilder.append((char) read);
            }
        }
        return stringBuilder;
    }

    //
    public void processByBitmap() throws IOException {
        FileWriter fileWriter = new FileWriter("src/out/out.txt");
        //遍历文件夹
        File[] files = file.listFiles();
        assert files != null;
        for (File f : files) {
            //筛选出txt
            if (f.toString().endsWith(".txt")) {
                //bufferedReader读入
                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(f))) {
                    for (; ; ) {
                        //逐行读入
                        String s = bufferedReader.readLine();
                        if (s == null) break;
                        s = s.trim();
                        multiBitSet.process(s, s.indexOf(",") + 1,fileWriter);
                    }
                }
            }
        }
        Set<Object> set = multiBitSet.getSet();
        if (set.isEmpty()) System.out.println("无重复！");
        else fileWriter.write("重复部分： \n");
        for (Object o : set) {
            String pin = (String) o;
            fileWriter.write(pin+"\n");
        }
        fileWriter.flush();
        fileWriter.close();
    }

    public void processByPartition() throws IOException {
        //初始化
        StringBuilder sb = new StringBuilder();
        File[] files = file.listFiles();
        assert files != null;
        //遍历文件,找到txt
//        long fileCreateStart = System.currentTimeMillis();
        for (File f : files) {
            if (f.toString().endsWith(".txt")) {
                try(BufferedReader bufferedReader = new BufferedReader(new FileReader(f))){
                    for(;;){
                        //逐行读入
                        String s = bufferedReader.readLine();
                        if (s == null) break;
                        //hash后对1024进行取余分布得到文件，该方法处理后的每个文件里的pin其hash值相同
                        int num = MultiBitSet.hash(s.substring(s.indexOf(",")+1)) & (1023);
                        File file = new File("src\\smallFiles\\" + num + ".txt");
                        if (!file.exists()) file.createNewFile();
                        fileWriter = new FileWriter(file, true);
                        fileWriter.write(s + "\n");
                        fileWriter.flush();
                        fileWriter.close();
                    }
                }
            }
        }
//        long fileCreateEnd = System.currentTimeMillis();
//
//        long fileOutputStart = System.currentTimeMillis();
        //写出文件
        fileWriter = new FileWriter("src/out/out.txt", true);
        //中间文件
        File[] newFiles = new File("src/smallFiles").listFiles();
        for (File f : newFiles) {
            if (f.toString().endsWith(".txt")) {
                distinct(f.getPath());
                f.delete();
            }
        }
        fileWriter.flush();
        fileWriter.close();
//        long fileOutputEnd = System.currentTimeMillis();
//        System.out.println("创建文件时间及写入时间: "+(fileCreateEnd-fileCreateStart)+"ms");
//        System.out.println("distinct时间: "+(fileOutputEnd-fileOutputStart)+"ms");
    }

    //区别出已有文件
    private void distinct(String path) throws IOException {
        String thisLine;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
        while ((thisLine = bufferedReader.readLine()) != null) {
            thisLine = thisLine.substring(thisLine.indexOf(",")+1);
            if (set.contains(thisLine)) fileWriter.write(thisLine + "\n");
            else set.add(thisLine);
        }
        set.clear();
    }

    public void test() throws IOException {
//        //写出文件
//        fileWriter = new FileWriter("src/out/out.txt", true);
//        //中间文件
//        File[] newFiles = new File("src/smallFiles").listFiles();
//        for (File f : newFiles) {
//            if (f.toString().endsWith(".txt")) {
//                distinct(f.getPath());
//                f.delete();
//            }
//        }
//        fileWriter.flush();
//        fileWriter.close();
    }

}
