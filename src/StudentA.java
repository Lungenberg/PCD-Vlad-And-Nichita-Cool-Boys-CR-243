import java.util.function.Consumer;

public class StudentA implements Runnable {
    private final int[] array;
    private final Consumer<String> output;
    private long sum;

    public StudentA(int[] array, Consumer<String> output) {
        this.array = array;
        this.output = output;
    }


    @Override
    public void run() {
        long result = 0;

        for (int i = 1; i + 2 < array.length; i += 8) {
            if (Thread.currentThread().isInterrupted()) return;

            long product1 = (long) array[i] * array[i + 2];
            result += product1;

            if (i + 6 < array.length) {
                long product2 = (long) array[i + 4] * array[i + 6];
                result += product2;

                output.accept(Thread.currentThread().getName()
                        + ": [" + i + "] " + array[i]
                        + " * [" + (i + 2) + "] " + array[i + 2]
                        + " + [" + (i + 4) + "] " + array[i + 4]
                        + " * [" + (i + 6) + "] " + array[i + 6]
                        + " = " + (product1 + product2));
            } else {
                output.accept(Thread.currentThread().getName()
                        + ": [" + i + "] " + array[i]
                        + " * [" + (i + 2) + "] " + array[i + 2]
                        + " = " + product1);
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }

        sum = result;
    }


    public long getSum() {
        return sum;
    }
}
