package homework;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class HW_36Test {

    @ParameterizedTest
    @MethodSource("dataForTransformArrayTest")
    public void transformArrayTest(List<Integer> arrayIn, List<Integer> arrayOut) {
        Assertions.assertEquals(arrayOut, HW_36.transformArray(arrayIn));
    }

    public static Stream dataForTransformArrayTest() {
        List<Arguments> out = new ArrayList<>();
            out.add(Arguments.arguments(Arrays.asList(1,2,4,4,2,3,4,1,7), Arrays.asList(1,7)));
            out.add(Arguments.arguments(Arrays.asList(1,2,4,4,2,3,4,1,4), List.of()));
            out.add(Arguments.arguments(Arrays.asList(4,4,4,4,4,4,4,4,4), List.of()));
            //А тут мы упали внезапно :)
            out.add(Arguments.arguments(Arrays.asList(0,0,0,0,0,0,0,0,0), List.of()));
            out.add(Arguments.arguments(Arrays.asList(4,0,0,0,0,0,0,0,0), Arrays.asList(0,0,0,0,0,0,0,0)));
        return out.stream();
    }

    @ParameterizedTest
    @MethodSource("dataForFindDigits")
    public void findDigitsTest(List<Integer> arrayIn, boolean result) {
        Assertions.assertEquals(result, HW_36.findDigits(arrayIn));
    }

    public static Stream dataForFindDigits() {
        List<Arguments> out = new ArrayList<>();
        out.add(Arguments.arguments(Arrays.asList(1,2,4,4,2,3,4,1,7), true));
        out.add(Arguments.arguments(Arrays.asList(1,1,1,1,1,1,1,1,1), false));
        out.add(Arguments.arguments(Arrays.asList(4,4,4,4,4), false));
        out.add(Arguments.arguments(Arrays.asList(4,1,4,1,4,1,4), true));
        out.add(Arguments.arguments(Arrays.asList(1,4), true));
        out.add(Arguments.arguments(List.of(), false));
        return out.stream();
    }
}
