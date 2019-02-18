package com.ctrip.tour.wireless.resourcegateway.service.business;

/**
 * @author pchenc
 * Created on 2019/2/18
 */

/**
 * String类型的日期也可以用compareTo比较
 */
public class DateTest {
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        String str1 = "2015-02-08";
        String str2 = "2015-03-08";
        int res = str1.compareTo(str2);
        if (res > 0)
            System.out.println("str1>str2");
        else if (res == 0)
            System.out.println("str1=str2");
        else
            System.out.println("str1<str2");
    }
}
