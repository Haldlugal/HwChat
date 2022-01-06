package annotations;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Tester {
    public static void main(String[] args) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        start(TestedClass.class);
    }
    public static void start(Class testedClass) throws InvocationTargetException, IllegalAccessException, RuntimeException, NoSuchMethodException, InstantiationException {
        Object testClassInstance = testedClass.getDeclaredConstructor().newInstance();
        Method[] methods = testedClass.getDeclaredMethods();
        List<Method> beforeSuiteMethods = Arrays.stream(methods).filter(method-> {
            return method.getAnnotation(BeforeSuite.class) != null;
        }).collect(Collectors.toList());

        List<Method> afterSuiteMethods = Arrays.stream(methods).filter(method-> {
            return method.getAnnotation(AfterSuite.class) != null;
        }).collect(Collectors.toList());



        if (beforeSuiteMethods.size() > 1 ) {
            throw new RuntimeException();
        }
        if (afterSuiteMethods.size() > 1) {
            throw new RuntimeException();
        }

        if (beforeSuiteMethods.size() > 0) {
            beforeSuiteMethods.get(0).invoke(testClassInstance);
        }
        if (afterSuiteMethods.size() > 0) {
            afterSuiteMethods.get(0).invoke(testClassInstance);
        }

        Arrays.stream(methods).filter(method-> {
            return method.getAnnotation(Test.class) != null;
        }).sorted(Comparator.comparingInt((Method method) -> - method.getAnnotation(Test.class).value())).forEach((Method method) -> {
            try {
                method.invoke(testClassInstance);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        });


    }
}
