package lesson9;

import java.math.BigDecimal;

public class Stats<T extends Number> {
    private T[] nums;


    public Stats(T... nums) { //varargs
        this.nums = nums;
    }

    public double avg() {
        double sum = 0.0;
        for (int i = 0; i < nums.length; i++) {
            sum = sum +  nums[i].doubleValue();
        }
        return sum / nums.length;
    }

    public boolean isSaveAvg(Stats<? extends Number> stats) {
        return Math.abs(this.avg() - stats.avg()) < 0.00001;
    }
}
