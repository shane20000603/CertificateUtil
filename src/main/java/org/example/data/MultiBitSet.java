package org.example.data;


import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MultiBitSet{
    //最大性能位两者相加等于33
    private static final int DEFAULT_CAPACITY = 1<<29;
    private static final int DEFAULT_SET_NUMBER = 1<<3;
    //初始化，维护一个n维bitset
    private final BitSet[] bitSets;
    //使用set来装重复的元素
    private final Set<Object> set = new HashSet<>();
    private static volatile MultiBitSet instance;

    private MultiBitSet(){
        //默认值构造
        bitSets = new BitSet[DEFAULT_SET_NUMBER];
        for(int i=0;i<bitSets.length;i++) bitSets[i] = new BitSet(DEFAULT_CAPACITY);
    }

    private MultiBitSet(int cap,int rows){
        //默认值构造
        bitSets = new BitSet[rows];
        for(int i=0;i<bitSets.length;i++) bitSets[i] = new BitSet(cap);
    }


    public static MultiBitSet getInstance(){
        if(instance == null) {
            synchronized (MultiBitSet.class){
                if(instance == null) instance = new MultiBitSet();
            }
        }
        return instance;
    }

    public static MultiBitSet getInstance(int Capacity,int rows){
        if(instance == null) {
            synchronized (MultiBitSet.class){
                if(instance == null) instance = new MultiBitSet();
            }
        }
        return instance;
    }




    /**
    对输入的key进行插入
     */
    public void process(Object key){
        //先对key进行合适处理将他放到合适的组，减少hash冲突
        int mark = myHash(key.toString()) & DEFAULT_SET_NUMBER-1;
        //在对应的hash中再次放入相应的位置
        int pos = pos(key.hashCode());
        System.out.println("Group: "+mark + " Pos: "+pos);
        //若之前没有被置1，则置1，若被置1，则说明该key已经存在，加入重复set中
        if (bitSets[mark].get(pos)) set.add(key);
        else bitSets[mark].set(pos);
    }



    /**
    使用不同的算法对输入的key进行散列后插入
     */
    public void process(Object key, int seed) throws NoSuchAlgorithmException {
        String keyStr = (String)key;
        //先对key进行合适处理将他放到合适的组，减少hash冲突
        int mark = (keyStr.hashCode()) & (DEFAULT_SET_NUMBER-1);
        //在对应的hash中再次放入相应的位置
        int pos = 0;
        switch (seed){
            case 0:
                pos = pos(encrypt(keyStr,"SHA1").hashCode());break;
            case 1:
                pos = pos(encrypt(keyStr,"MD5").hashCode());break;
            case 2:
                pos = pos(encrypt(keyStr,"SHA384").hashCode());break;
            case 3:
                pos = pos(encrypt(keyStr,"MD2").hashCode());break;
        }
        //若之前没有被置1，则置1，若被置1，则说明该key已经存在，加入重复set中
        if (bitSets[mark].get(pos)) {
            set.add(keyStr);
        }
        else bitSets[mark].set(pos);
    }


    public void clear(){
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
            sum = sum*i+chars[i];
        }
        return sum;
    }

    public String encrypt(String key,String method) throws NoSuchAlgorithmException {
        byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
        MessageDigest md = MessageDigest.getInstance(method);
        StringBuilder sb = new StringBuilder();
        md.update(bytes);
        byte[] digest = md.digest();
        for (int i = 0; i < digest.length; i++) {
            sb.append(Integer.toHexString(0xff & digest[i]));
        }
        return sb.toString();
    }
}
