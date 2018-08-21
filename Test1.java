
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
/**********update:2018-3-29 09:22:50 不推荐在迭代器中删除，可以遍历符合条件的对象，然后塞入一个新的list。java 8中可以使用filter方法。************************************ */
        	}
        	
        }
        System.out.println(list);
    }
}