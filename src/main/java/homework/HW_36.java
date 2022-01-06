package homework;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class HW_36 {
    public static void main(String[] args) {
        List<Integer> firstArray = Arrays.asList(1,2,4,4,2,3,4,1,7);
        Logger logger = LogManager.getLogger(HW_36.class);
    }

    public static List<Integer> transformArray(List<Integer> array) {
        if (!array.contains(4)) {
            return List.of();
        }
        List<Integer> newArray = new ArrayList<>(array);
        Collections.reverse(newArray);
        List<Integer> reversedArray = newArray.stream().takeWhile(element-> element != 4).collect(Collectors.toList());
        Collections.reverse(reversedArray);
        return reversedArray;
    }

    public static boolean findDigits(List<Integer> array) {
        int firstDigitToCheck = 1;
        int secondDigitToCheck = 4;
        int countFirst = 0;
        int countSecond = 0;
        for (Integer digit : array) {
            if (digit == firstDigitToCheck) {
                countFirst++;
            } else if (digit == secondDigitToCheck) {
                countSecond++;
            }
            if (countFirst > 0 && countSecond > 0) {
                return true;
            }
        }
        return false;
    }
}
