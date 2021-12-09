public class HomeWork {
    final Object lock = new Object();
    volatile char currentLetter = 'a';

    public static void main(String[] args) {
        HomeWork hw = new HomeWork();
        Thread thread1 = new Thread(()->{
            hw.print('a', 'b');
        });
        Thread thread2 = new Thread(()->{
            hw.print('b', 'C');
        });
        Thread thread3 = new Thread(()->{
            hw.print('C', 'a');
        });
        thread1.start();
        thread2.start();
        thread3.start();
    }

    void print(char symbol, char nextSymbol) {
        synchronized (lock) {
            for (int i = 0; i < 5; i++) {
                while (currentLetter != symbol) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                System.out.println(currentLetter);
                currentLetter = nextSymbol;
                lock.notifyAll();
            }
        }
    }
}
