/*******************************************************************************
 * Copyright (c) 2001-2017 Primeton Technologies, Ltd.
 * All rights reserved.
 * 
 * Created on 2018年1月9日
 *******************************************************************************/

package com.primeton.devops.spm.component.service;

public class Test2 {
	public volatile int inc = 0;

	public void increase() {
		inc++;
	}

	public static void main(String[] args) {
		final Test2 test = new Test2();
		for (int i = 0; i < 10; i++) {
			new Thread() {
				public void run() {
					for (int j = 0; j < 1000; j++)
						test.increase();
				};
			}.start();

		}

		while (Thread.activeCount() > 1)
			//保证前面的线程都执行完
			Thread.yield();
		System.out.println(test.inc);
	}
	//    按道理来说结果是10000，但是运行下很可能是个小于10000的值。
//	有人可能会说volatile不是保证了可见性啊，一个线程对inc的修改，另外一个线程应该立刻看到啊！
//	可是这里的操作inc++是个复合操作啊，包括读取inc的值，对其自增，然后再写回主存。
//	假设线程A，读取了inc的值为10，这时候被阻塞了，因为没有对变量进行修改，触发不了volatile规则。
//	线程B此时也读读inc的值，主存里inc的值依旧为10，做自增，然后立刻就被写回主存了，为11。
//	此时又轮到线程A执行，由于工作内存里保存的是10，所以继续做自增，再写回主存，11又被写了一遍。
//	所以虽然两个线程执行了两次increase()，结果却只加了一次。
//	Java面试官最爱的volatile关键字 - 刘成的文章 - 知乎
//	http://zhuanlan.zhihu.com/p/32709174
}