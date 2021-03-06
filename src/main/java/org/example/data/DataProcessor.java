package org.example.data;

import org.apache.poi.ss.formula.functions.T;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class DataProcessor {
    private final MultiBitSet multiBitSet = MultiBitSet.getInstance();
    private final Set<Object> set = new HashSet<>();
    //output 输入目标文件夹
    private File fileContainsPins = new File("D:\\pins\\test_data");
    //算法种子
    private final int[] seeds = {MultiBitSet.SHA1_SEED,MultiBitSet.MD5_SEED};
    //多线程完成标记
    private final AtomicInteger countFlag = new AtomicInteger(0);

    public DataProcessor(){}
    public DataProcessor(String path){
        this.fileContainsPins = new File(path);
    }

    //使用bitset进行处理
    public void processByBitmap() throws IOException {
        File dir = new File("src/out");
        if (!dir.exists()) dir.mkdir();
        for (int i = 0; i < seeds.length; i++) {
            //初始化
            File file = new File("src/out/result" + i + ".txt");
            if (!file.exists()) file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);

            //获取第i个重复pin集合
            getDuplicateSet(seeds[i]);

            //阻塞等待操作完成
            int fileNum = Objects.requireNonNull(fileContainsPins.listFiles()).length;
            for (; ; ) {
                if (countFlag.get() == fileNum) break;
            }
            //重新置位
            countFlag.set(0);
            //保存该哈希算法下认为重叠的pin
            for (Object o : multiBitSet.getSet()) fileWriter.write((String) o + '\n');
            //清空，方便后续的加入
            multiBitSet.clear();
            fileWriter.flush();
            fileWriter.close();
        }
        //关闭线程池
        pool.shutdown();
    }


    //线程池处理，增加运行效率
    private final ThreadPoolExecutor pool = new ThreadPoolExecutor(
            4,8, 1,TimeUnit.SECONDS,new LinkedBlockingDeque<>());

    /**获取不同哈希算法对应的重复pin集，seed代表使用的算法
     *
     * @param seed 算法种子
     */
    public void getDuplicateSet(int seed) {
        //遍历文件夹
        for (File f : Objects.requireNonNull(fileContainsPins.listFiles())) {
            //bufferedReader读入
            Runnable task = () -> {
                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(f))) {
                    for (; ; ) {
                        //逐行读入
                        String s = bufferedReader.readLine();
                        if (s == null) break;
                        multiBitSet.process(s.substring(s.indexOf(',') + 1).trim(), seed);
                    }
                } catch (NoSuchAlgorithmException e) {
                    System.out.println("输入的算法种子有误！");
                    e.printStackTrace();
                } catch (IOException exception) {
                    System.out.println("IO错误！");
                    exception.printStackTrace();
                }
                //每个文件运行完毕将flag加1，当全部运行结束以后结束阻塞
                countFlag.getAndAdd(1);
                System.out.println(f.getName()+"已完成");
            };
            pool.execute(task);
        }
    }


    /**
     * 当使用多种哈希算法对原始数据进行去重以后,依然会有一部分的重复数据,对其进行取交集
     *
     * @throws IOException
     */
    public void getCross() throws IOException {
        File dir = new File("src/final");
        if (!dir.exists()) dir.mkdir();
        HashSet<Object> comparedSet = new HashSet<>();
        File file = new File("src/out");
        File result = new File("src/final/raw_result.txt");
        if (!result.exists()) result.createNewFile();
        FileWriter fileWriter = new FileWriter(result);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        //计数器
        int count = 0;
        for (File f : Objects.requireNonNull(file.listFiles())) {
            if (f.getPath().endsWith(".txt")) {
                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(f))) {
                    for (; ; ) {
                        String s = bufferedReader.readLine();
                        if (s == null) break;
                        if (count == 0) {
                            if (!set.add(s)) {
                                bufferedWriter.write(s + '\n');
                            }
                        } else {
                            if (!comparedSet.add(s)) {
                                bufferedWriter.write(s + '\n');
                            }
                        }
                    }
                }
            }
            if (count != 0) {
                set.retainAll(comparedSet);
                comparedSet.clear();
            }
            count++;
        }
        for (Object o : set) bufferedWriter.write((String) o + '\n');
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    /**
     * 作最后一次检查，与整个数据集线性比较
     *
     * @throws IOException
     */
    public void recheck() throws IOException {
        //初始化
        File rawResult = new File("src/final/raw_result.txt");
        Set<Integer> rawSet_hash = new HashSet<>();
        Set<String> rawSet_String = new HashSet<>();
        Set<String> tempSet = new HashSet<>();
        File finalResult = new File("src/final/final_result.txt");
        if (!finalResult.exists()) finalResult.createNewFile();
        FileWriter fileWriter = new FileWriter(finalResult);

        //读取数据，将交集中key的本身和hashcode都进行储存
        try (BufferedReader reader = new BufferedReader(new FileReader(rawResult))) {
            for (; ; ) {
                String s = reader.readLine();
                if (s == null) break;
                rawSet_hash.add(s.hashCode());
                rawSet_String.add(s);
            }
        }

        if (rawSet_hash.size() == rawSet_String.size()) fileWriter.write("每个字符串的Hash不同" + '\n');
        else fileWriter.write("有Hash相同的不同字符串" + '\n');
        fileWriter.write("重复部分: " + '\n');
        for (File f : Objects.requireNonNull(fileContainsPins.listFiles())) {
            //筛选出txt
            if (f.toString().endsWith(".txt")) {
                //bufferedReader读入
                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(f))) {
                    for (; ; ) {
                        //逐行读入
                        String s = bufferedReader.readLine();
                        if (s == null) break;
                        String realKey = s.substring(s.indexOf(',') + 1).trim();
                        if (rawSet_hash.contains(realKey.hashCode()) && rawSet_String.contains(realKey) && !tempSet.add(realKey))
                            fileWriter.write(s+'\n');
                    }

                }
            }
        }
        fileWriter.flush();
        fileWriter.close();

    }

    /**
     * 分治法，将大文件按hash分成许多小文件，使内存可以操作他，这样相同的key一定在同一个文件中
     *
     * @throws IOException
     */
    public void processByPartition() throws IOException {
        //初始化
        FileWriter fileWriter;
        File[] files = fileContainsPins.listFiles();
        assert files != null;
        //遍历文件,找到txt
        for (File f : files) {
            if (f.toString().endsWith(".txt")) {
                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(f))) {
                    for (; ; ) {
                        //逐行读入
                        String s = bufferedReader.readLine();
                        if (s == null) break;
                        //hash后对1024进行取余分布得到文件，该方法处理后的每个文件里的pin其hash值相同
                        int num = MultiBitSet.hash(s.substring(s.indexOf(",") + 1)) & (255);
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
        if (!fileOut.exists()) fileOut.createNewFile();
        FileWriter fileWriter = new FileWriter("src/out/dup.txt");
        //start
        for (File f : Objects.requireNonNull(file.listFiles())) {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(f))) {
                for (; ; ) {
                    String s = bufferedReader.readLine();
                    if (s == null) break;
                    if (!hashSet.add(s.substring(s.indexOf(",") + 1).trim())) {
                        fileWriter.write(s + '\n');
                    }
                }
            }
            hashSet.clear();
        }
        fileWriter.flush();
        fileWriter.close();
    }
}
