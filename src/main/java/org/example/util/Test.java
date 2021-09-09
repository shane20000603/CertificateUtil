package org.example.util;

public class Test {
    public void quickSort(int[] x,int left,int right){
        if(left>=right) return;
        int pivot = x[left];
        int first = left,last = right;
        int temp;
        while(first < last){
            while(first<last&&x[last]>=pivot) last--;
            x[first] = x[last];
            while(first<last&&x[first]<=pivot) first++;
        }
    }
}
