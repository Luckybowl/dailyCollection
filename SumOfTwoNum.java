import java.util.Map;
import java.util.HashMap;
public class SumOfTwoNum{
        static int temp;
        public static void main(String[] args){
            int[] nums ={2, 7, 11, 15,3,8,5};
            int target = 15;
            nums = twoSum1(nums,target);
            System.out.print(nums);
        }
        public static int[] twoSum(int[] nums, int target) {
            int[] result = new int[2];
            nums = BubbleSort(nums);
            nums = arraycopy(nums,target);
            for(int i = 0 ; i < nums.length; i ++){
                for(int j = i+1 ; j < nums.length ; j++ ){
                    if(nums[i] + nums[j] == target){
                        result[0] = nums[i];
                        result[1] = nums[j];
                        return result;
                    }
                }
            }
            return null;
        }

        public static int[] BubbleSort(int[] nums){

            for(int i = 0 ; i < nums.length ; i ++){
                for(int j = i+1 ; j < nums.length; j++ ){
                    if(nums[i] > nums[j]){
                        temp = nums[i];
                        nums[i] = nums[j];
                        nums[j] = temp;
                    }
                }
            }
            return nums;
        }

        public static int[] arraycopy(int[] nums,int target){
            int x = 0;
            int[] b = new int[nums.length];
            for(int i = 0 ; i < nums.length; i ++){
                if(nums[i] >= target){
                    x = i;
                    break;
                }
            }
            System.arraycopy(nums,0,b,0,x);
            return b;
        }

        public static int[] twoSum1(int[] nums, int target) {
            Map<Integer, Integer> map = new HashMap<>();
            for(int i = 0; i < nums.length; i++){
                map.put(nums[i], i);
            }
            for(int i = 0; i < nums.length; i++){
                int complement = target - nums[i];
                if (map.containsKey(complement) && map.get(complement) != i) {
                    return new int[] {i, map.get(complement)};
                }
            }
            throw new IllegalArgumentException("No two sum solution");
        }
    
}