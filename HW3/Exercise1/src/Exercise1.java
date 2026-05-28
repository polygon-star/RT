import java.util.*;

public class Exercise1 {

    public static void main(String[] args) {

        // Exercise 1
        char[] chars = {'a', 'b', 'b', 'c', 'c', 'c'};
        List<Character> thirdHighest = findThirdHighestFrequency(chars);
        System.out.println("Exercise 1: Third highest frequency char(s)");
        System.out.println("Input: " + Arrays.toString(chars));
        System.out.println("Output: " + thirdHighest);
        System.out.println();

        // Exercise 2
        String str = "abc";
        String reversed = reverseString(str);
        System.out.println("Exercise 2: Reverse a string");
        System.out.println("Input: " + str);
        System.out.println("Output: " + reversed);
        System.out.println();

        // Exercise 3
        int[] nums = {1, 2, 3, 4};
        int target = 5;
        List<List<Integer>> pairs = findPairs(nums, target);
        System.out.println("Exercise 3: Pairs sum to target");
        System.out.println("Input: " + Arrays.toString(nums));
        System.out.println("Target: " + target);
        System.out.println("Output: " + pairs);
    }

    public static List<Character> findThirdHighestFrequency(char[] chars) {
        Map<Character, Integer> frequencyMap = new HashMap<>();

        for (char c : chars) {
            frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
        }

        List<Map.Entry<Character, Integer>> list =
                new ArrayList<>(frequencyMap.entrySet());

        list.sort((a, b) -> b.getValue() - a.getValue());

        List<Character> result = new ArrayList<>();

        if (list.size() < 3) {
            return result;
        }

        int thirdHighestFrequency = list.get(2).getValue();

        for (Map.Entry<Character, Integer> entry : list) {
            if (entry.getValue() == thirdHighestFrequency) {
                result.add(entry.getKey());
            }
        }

        return result;
    }

    public static String reverseString(String str) {
        StringBuilder sb = new StringBuilder(str);
        return sb.reverse().toString();
    }

    public static List<List<Integer>> findPairs(int[] nums, int target) {
        List<List<Integer>> result = new ArrayList<>();
        Set<Integer> used = new HashSet<>();
        Map<Integer, Integer> map = new HashMap<>();

        for (int num : nums) {
            int complement = target - num;

            if (map.containsKey(complement)
                    && !used.contains(num)
                    && !used.contains(complement)) {

                result.add(Arrays.asList(complement, num));

                used.add(num);
                used.add(complement);
            } else {
                map.put(num, map.getOrDefault(num, 0) + 1);
            }
        }

        return result;
    }
}
