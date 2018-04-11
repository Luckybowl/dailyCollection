public class TestForVM {
    public static void main(String[] args){
        /********intern是个native方法，作用是：如果字符串常量池中已经包含一个等于此String对象的字符串，
         * 则返回代表池中这个字符串的String对象；否则，将此String对象包含的字符串添加到常量池中，并且返回此String对象的引用。
         * jdk1.6以及之前，由于常量池分配在永久带。。intern方法会把首次遇到的字符串复制到永久带，返回的则是永久带的引用，儿StringBuilder
         * 创建的字符串实例是在堆上，必然是false；
         * jdk1.7的intern不再复制实例，只是在常量池中记录首次出现的实例引用，因此intern返回的引用和StringBuilder创建的实例实际是同一个。************************************ */
        String st1 = new StringBuilder("计算机").append("软件").toString();
        System.out.println(st1.intern() == st1);
        String st2 = new StringBuilder("ja").append("va").toString();/******因为java已经出现过，所以不符合intern“首次出现”的规则********************** */
        System.out.println(st2.intern() == st2);
        String st3 = new StringBuilder("jaasdasda").append("vaasdasd").toString();
        System.out.println(st3.intern() == st3);
    }

}