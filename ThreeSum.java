import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.*;

public class ThreeSum {
    public List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> list = new ArrayList<>();
        Set<List<Integer>> set = new HashSet<>();
        int target = 0;
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < nums.length; i++) {
            map.put(nums[i], i);
        }
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            Map<Integer, Integer> map1 = new HashMap<>();
            for (int j = i + 1; j < nums.length; j++) {
                map1.put(nums[j], j);
            }
            for (int j = i + 1; j < nums.length; j++) {
                int complement1 = complement - nums[j];
                List<Integer> a = new ArrayList<>();
                if (map1.containsKey(complement1) && map1.get(complement1) > j) {
                    a.add(nums[i]);
                    a.add(nums[j]);
                    a.add(complement1);
                    // return new int[] { nums[i], nums[j], complement1 };
                }
                if (a.size() > 0) {
                    boolean flag = false;
                    for (List b : list) {
                        if (b.containsAll(a)) {
                            if(!(a.get(0)== 0 && a.get(1)== 0 && a.get(2)== 0)){
                                // a = null;
                                flag = true;
                                break;
                            }
                        }
                    }
                    // if (a != null) {
                        if(!flag && set.add(a)){
                        list.add(a);
                    }
                }
            }

        }
        return list;
    }

    public static void main(String[] args) {
        ThreeSum threeSum = new ThreeSum();
        List<List<Integer>> list = new ArrayList<>();
        int[] nums = {0,0,0,0};
        list = threeSum.threeSum(nums);
        System.out.print(list);
    }
}
