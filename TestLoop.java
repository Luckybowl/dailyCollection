package com.primeton.arturo.core.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.primeton.arturo.specs.model.report.MemoryMeanPoint;
import com.primeton.arturo.specs.model.report.MemoryTimePoint;

public class TestLoop {
	public static void main(String[] args){
		List<MemoryTimePoint> memTimeList = new ArrayList<>();
		List<MemoryMeanPoint> memMeanList = new ArrayList<>();
		MemoryMeanPoint test = new MemoryMeanPoint();
		MemoryTimePoint a = new MemoryTimePoint();
		a.setTime("123");
		a.setValue(123.0);
		MemoryTimePoint b = new MemoryTimePoint();
		b.setTime("321");
		b.setValue(321.0);
		memTimeList.add(a);
		memTimeList.add(b);
		for(MemoryTimePoint mem : memTimeList){
			/**********对象得放在for循环中创建，不然每次set后改变的都是同一个对象，最后放到list里面的都是一个对象*********************************/
			test.setTime(mem.getTime());
			test.setValue(mem.getValue());
			memMeanList.add(test);
		}
		System.out.println(memMeanList);
	}

}
