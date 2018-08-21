import java.util.ArrayList;
import java.util.*;
public class TestInt{
    public static void main(String[] args){
        List<Integer> a = new ArrayList<>();
        a.add(0);
        a.add(0);
        a.add(0);
        System.out.println(a.indexOf(0)== 0);
        System.out.println(a.indexOf(1)== 0 );
        System.out.println(a.indexOf(2)== 0);
        a.get(0);
    }
}