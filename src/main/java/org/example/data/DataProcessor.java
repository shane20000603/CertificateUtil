package org.example.data;

import org.apache.poi.ss.formula.functions.T;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class DataProcessor {
    private final MultiBitSet multiBitSet = MultiBitSet.getInstance();
    private final Set<Object> set = new HashSet<>();
    //output
    private final File fileContainsPins = new File("src\\pins");
    //private FileWriter fileWriter;
    private final int[] seeds = {0,1};

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
    public void processByBitmap() throws Exception {
        for (int i = 0; i < seeds.length; i++) {
            //分部处理，创建分批文件
            File thisFile = new File("src/first/first"+i+".txt");
            if(!thisFile.exists()) thisFile.createNewFile();
            File midFile = new File("src/second/second"+i+".txt");
            if(!midFile.exists()) midFile.createNewFile();
            FileWriter fileWriter = new FileWriter(thisFile);
            //获取第i个重复pin集合
            Set<Object> duplicateSet = getDuplicateSet(seeds[i]);
            for(Object o:duplicateSet) fileWriter.write((String)o+'\n');
            //清空，方便后续的加入
            multiBitSet.clear();
            fileWriter.flush();
            fileWriter.close();
        }
    }

    public void distinctFromFiles() throws IOException {
        File file = new File("src/first");
        int count=0;
        for(File f: Objects.requireNonNull(file.listFiles())){
            FileWriter fileWriter = new FileWriter("src/second/second"+count+".txt");
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(f))) {
                for (; ; ) {
                    //逐行读入
                    String s = bufferedReader.readLine();
                    if (s == null) break;
                    set.add(s.substring(s.indexOf(',')+1));
                }
            }
            for(Object o:set) fileWriter.write((String)o+'\n');
            count++;
            set.clear();
            fileWriter.flush();
            fileWriter.close();
        }


        //进行重新筛选：
        file = new File("src/second");
        Set<Object> set1 = new HashSet<>();
        Set<Object> set2 = new HashSet<>();
        List<Set<Object>> list = new ArrayList<>();
        list.add(set1);
        list.add(set2);
        count = 0;
        FileWriter fileWriter = new FileWriter("src/out/out.txt");
        fileWriter.write("重复部分："+'\n');
        for(File f: Objects.requireNonNull(file.listFiles())){
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(f))) {
                for (; ; ) {
                    //逐行读入
                    String s = bufferedReader.readLine();
                    if (s == null) break;
                    list.get(count).add(s);
                }
            }
            count++;
        }
        set.clear();
        set.addAll(set1);
        set.retainAll(set2);
        for(Object o:set) fileWriter.write((String)o+'\n');
        fileWriter.flush();
        fileWriter.close();
    }

    public Set<Object> getDuplicateSet(int seed) throws IOException, NoSuchAlgorithmException, InterruptedException {
        //遍历文件夹
        for (File f : Objects.requireNonNull(fileContainsPins.listFiles())) {
            //筛选出txt
            if (f.toString().endsWith(".txt")) {
                //bufferedReader读入
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(f))) {
                            for (; ; ) {
                                //逐行读入
                                String s = bufferedReader.readLine();
                                if (s == null) break;
                                s = s.trim();
                                multiBitSet.process(s, s.indexOf(",") + 1,seed);
                            }
                        } catch (NoSuchAlgorithmException | IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }
        Thread.sleep(30000);
        return multiBitSet.getSet();
    }



//    @Deprecated
//    public void processByPartition() throws IOException {
//        //初始化
//        StringBuilder sb = new StringBuilder();
//        File[] files = fileContainsPins.listFiles();
//        assert files != null;
//        //遍历文件,找到txt
////        long fileCreateStart = System.currentTimeMillis();
//        for (File f : files) {
//            if (f.toString().endsWith(".txt")) {
//                try(BufferedReader bufferedReader = new BufferedReader(new FileReader(f))){
//                    for(;;){
//                        //逐行读入
//                        String s = bufferedReader.readLine();
//                        if (s == null) break;
//                        //hash后对1024进行取余分布得到文件，该方法处理后的每个文件里的pin其hash值相同
//                        int num = MultiBitSet.hash(s.substring(s.indexOf(",")+1)) & (1023);
//                        File file = new File("src\\smallFiles\\" + num + ".txt");
//                        if (!file.exists()) file.createNewFile();
//                        fileWriter = new FileWriter(file, true);
//                        fileWriter.write(s + "\n");
//                        fileWriter.flush();
//                        fileWriter.close();
//                    }
//                }
//            }
//        }
////        long fileCreateEnd = System.currentTimeMillis();
////
////        long fileOutputStart = System.currentTimeMillis();
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
////        long fileOutputEnd = System.currentTimeMillis();
////        System.out.println("创建文件时间及写入时间: "+(fileCreateEnd-fileCreateStart)+"ms");
////        System.out.println("distinct时间: "+(fileOutputEnd-fileOutputStart)+"ms");
//    }

//    //区别出已有文件
//    private void distinct(String path) throws IOException {
//        String thisLine;
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
//        while ((thisLine = bufferedReader.readLine()) != null) {
//            thisLine = thisLine.substring(thisLine.indexOf(",")+1);
//            if (set.contains(thisLine)) fileWriter.write(thisLine + "\n");
//            else set.add(thisLine);
//        }
//        set.clear();
//    }

}
