package org.example.data;


import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class MultiBitSet {
    //最大性能位两者相加等于33
    private static final int DEFAULT_CAPACITY = 1<<30;
    private static final int DEFAULT_SET_NUMBER = 1<<5;
    //初始化，维护一个n维bitset
    private final BitSet[] bitSets;
    //使用set来装重复的元素
    private final Set<Object> set = new HashSet<>();

    public MultiBitSet(){
        //默认值构造
        bitSets = new BitSet[DEFAULT_SET_NUMBER];
        for(int i=0;i<bitSets.length;i++) bitSets[i] = new BitSet(DEFAULT_CAPACITY);
    }

    void process(Object key){
        //先对key进行合适处理将他放到合适的组，减少hash冲突
        int mark = myHash(key.toString()) & DEFAULT_SET_NUMBER-1;
        //在对应的hash中再次放入相应的位置
        int pos = pos(hash(key));
        System.out.println("Group: "+mark + " Pos: "+pos);
        //若之前没有被置1，则置1，若被置1，则说明该key已经存在，加入重复set中
        if (bitSets[mark].get(pos)) set.add(key);
        else bitSets[mark].set(pos);
    }
    void process(String key, int start, FileWriter fileWriter) throws IOException {
        String realKey = key.substring(start);
        //先对key进行合适处理将他放到合适的组，减少hash冲突
        int mark = myHash(realKey) & DEFAULT_SET_NUMBER-1;
        //在对应的hash中再次放入相应的位置
        int pos = pos(realKey.hashCode());
//        System.out.println("key: "+key);
//        System.out.println("realKey: "+realKey);
//        System.out.println("Group: "+mark + " Pos: "+pos);
        fileWriter.write("realKey: "+realKey+" Group: "+mark+" Pos: "+pos+"\n");
        //若之前没有被置1，则置1，若被置1，则说明该key已经存在，加入重复set中
        if (bitSets[mark].get(pos)) set.add(key);
        else bitSets[mark].set(pos);
    }

    public void reset(){
        for(BitSet bitSet:bitSets) bitSet.clear();
        set.clear();
    }

    public Set<Object> getSet(){
        return set;
    }

    //扰动函数
    static int hash(Object key){
        int h;
        return  (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    @Deprecated
    static int mHash(Object key,int seed){
        int h;
        return key == null ? 0 : (seed * (DEFAULT_CAPACITY - 1) & ((h = key.hashCode()) ^ (h >>> 16)));
    }

    //找到在每个set的位置
    private int pos(int hash){
        return hash & (DEFAULT_CAPACITY-1);
    }

    public BitSet[] getBitSets(){
        return bitSets;
    }

    private int myHash(String str){
        char[] chars = str.toCharArray();
        int sum = 0;
        for (int i = 0; i < chars.length; i++) {
            sum+=i*chars[i];
        }
        return sum;
    }
}
