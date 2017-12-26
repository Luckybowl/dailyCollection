package com.primeton.devops.spm.component.service;

import java.util.ArrayList;
import java.util.Iterator;

public class Test1 {
    public static void main(String[] args){
        ArrayList<String> list = new ArrayList<>();
        String a = "a";
        String b = "b";
        String c = "c";
        list.add(a);
        list.add(b);
        list.add(a);
        boolean flag = false;
        System.out.println(list.contains("c") ? !flag : flag);
        System.out.println(list.size());
        Iterator<String> iter = list.iterator();
        while(iter.hasNext()){
        	String x = iter.next();
        	if(!a.equals(x)){
        		iter.remove(); //若用list.remove(x),则会报ConcurrentModificationException，因为元素在使用的时候发生了并发的修改，导致异常抛出。
//        		list.remove(x);
        		
        	}
        	
        }
        System.out.println(list);
    }
}