package org.example.data;

import org.apache.poi.ss.formula.functions.T;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class DataProcessor {
    private final MultiBitSet multiBitSet = MultiBitSet.getInstance();
    private final Set<Object> set = new HashSet<>();
    //output
    private final File fileContainsPins = new File("D:\\Java\\CertificateUtil\\src\\pins");
    //算法种子
    private final int[] seeds = {0,1};

    //
    public void processByBitmap() throws Exception {
        for (int i = 0; i < seeds.length; i++) {
            //分部处理，创建分批文件
            File file = new File("src/out/result"+i+".txt");
            if(!file.exists()) file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            //获取第i个重复pin集合
            getDuplicateSet(seeds[i]);
            //保存该哈希算法下认为重叠的pin
            for(Object o:multiBitSet.getSet()) fileWriter.write((String)o+'\n');
            //清空，方便后续的加入
            multiBitSet.clear();
            fileWriter.flush();
            fileWriter.close();
        }
    }


    //获取不同哈希算法对应的重复pin集，seed代表使用的算法
    public void getDuplicateSet(int seed) throws Exception {
        //遍历文件夹
        for (File f : Objects.requireNonNull(fileContainsPins.listFiles())) {
            //筛选出txt
            if (f.toString().endsWith(".txt")) {
                //bufferedReader读入
                        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(f))) {
                            for (; ; ) {
                                //逐行读入
                                String s = bufferedReader.readLine();
                                if (s == null) break;
                                multiBitSet.process(s.substring(s.indexOf(',')+1).trim(),seed);
                            }
                        } catch (NoSuchAlgorithmException | IOException e) {
                            e.printStackTrace();
                        }
            }
        }
    }

    public void getCross() throws Exception {
        HashSet<Object> comparedSet = new HashSet<>();
        File file = new File("src/out");
        File result = new File("src/final/final_result.txt");
        if(!result.exists()) result.createNewFile();
        FileWriter fileWriter = new FileWriter(result);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        //计数器
        int count = 0;
        for(File f: Objects.requireNonNull(file.listFiles())){
            if(f.getPath().endsWith(".txt")){
                try(BufferedReader bufferedReader = new BufferedReader(new FileReader(f))){
                    for(;;){
                        String s = bufferedReader.readLine();
                        if(s == null) break;
                        if(count == 0){
                            if (!set.add(s)) {
                                bufferedWriter.write(s+'\n');
                            }
                        }else{
                            if (!comparedSet.add(s)) {
                                bufferedWriter.write(s+'\n');
                            }
                        }
                    }
                }
            }
            if(count!=0){
                set.retainAll(comparedSet);
                comparedSet.clear();
            }
            count++;
        }
        for(Object o:set) bufferedWriter.write((String)o+'\n');
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    public void processByPartition() throws IOException {
        //初始化
        FileWriter fileWriter;
        File[] files = fileContainsPins.listFiles();
        assert files != null;
        //遍历文件,找到txt
        for (File f : files) {
            if (f.toString().endsWith(".txt")) {
                try(BufferedReader bufferedReader = new BufferedReader(new FileReader(f))){
                    for(;;){
                        //逐行读入
                        String s = bufferedReader.readLine();
                        if (s == null) break;
                        //hash后对1024进行取余分布得到文件，该方法处理后的每个文件里的pin其hash值相同
                        int num = MultiBitSet.hash(s.substring(s.indexOf(",")+1)) & (255);
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
    }

    public void finalProcess() throws Exception {
        //initialise
        File file = new File("src/smallFiles");
        HashSet<Object> hashSet = new HashSet<>();
        File fileOut = new File("src/out/dup.txt");
        if(!fileOut.exists()) fileOut.createNewFile();
        FileWriter fileWriter = new FileWriter("src/out/dup.txt");
        //start
        for(File f: Objects.requireNonNull(file.listFiles())){
            try(BufferedReader bufferedReader = new BufferedReader(new FileReader(f))){
                for(;;){
                    String s = bufferedReader.readLine();
                    if(s == null) break;
                    if (!hashSet.add(s.substring(s.indexOf(",")+1).trim())) {
                        fileWriter.write(s+'\n');
                    }
                }
            }
            hashSet.clear();
        }
        fileWriter.flush();
        fileWriter.close();
    }
}
