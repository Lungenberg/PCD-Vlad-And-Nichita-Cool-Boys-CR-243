import java.util.function.Consumer;

public class StudentB implements Runnable {
    private final int[] array;
    private final Consumer<String> output;
    private long sum;

    public StudentB(int[] array, Consumer<String> output) {
        this.array = array;
        this.output = output;
    }

    @Override
    public void run() {
        long result = 0;

        int start = array.length - 1;
        if (start % 2 == 0) start--;

        for (int i = start; i >= 3; i -= 8) {
            if (Thread.currentThread().isInterrupted()) return;

            long product1 = (long) array[i] * array[i - 2];
            result += product1;

            if (i >= 7) {
                long product2 = (long) array[i - 4] * array[i - 6];
                result += product2;

                output.accept(Thread.currentThread().getName()
                        + ": [" + i + "] " + array[i]
                        + " * [" + (i - 2) + "] " + array[i - 2]
                        + " + [" + (i - 4) + "] " + array[i - 4]
                        + " * [" + (i - 6) + "] " + array[i - 6]
                        + " = " + (product1 + product2));
            } else {
                output.accept(Thread.currentThread().getName()
                        + ": [" + i + "] " + array[i]
                        + " * [" + (i - 2) + "] " + array[i - 2]
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
