package annotations;

public class TestedClass {
    @AfterSuite
    public void afterTests() {
        System.out.println("After tests!");
    }

    @BeforeSuite
    public void beforeTests() {
        System.out.println("Before tests!");
    }

    @Test(3)
    public void lastTest() {
        System.out.println("I am last test");
    }

    @Test(10)
    public void firstTest() {
        System.out.println("I am first test!");
    }

    @Test(9)
    public void ninthTest() {
        System.out.println("I am after first test!");
    }

    @Test
    public void middleTest() {
        System.out.println("I am in the middle");
    }

    public void notATest() {
        System.out.println("Dont look at me, plz");
    }
}
