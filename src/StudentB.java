import java.util.function.Consumer;

public class StudentB {
    private final int[] array;
    private long sumForward;
    private long sumBackward;

    private Consumer<String> output = System.out::println;

    public StudentB(int[] array) {
        this.array = array;
    }

    public void setOutput(Consumer<String> output) {
        this.output = output;
    }

    public Runnable forwardTask() {
        return () -> {
            long sum = 0;

            for (int i = 0; i + 2 < array.length; i += 4) {
                long product = (long) array[i] * array[i + 2];
                sum += product;

                output.accept(Thread.currentThread().getName()
                        + ": [" + i + "] " + array[i]
                        + " * [" + (i + 2) + "] " + array[i + 2]
                        + " = " + product);

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            sumForward = sum;
        };
    }

    public Runnable backwardTask() {
        return () -> {
            long sum = 0;

            int start = array.length - 1;
            if (start % 2 != 0) start--;

            for (int i = start; i >= 2; i -= 4) {
                long product = (long) array[i] * array[i - 2];
                sum += product;

                output.accept(Thread.currentThread().getName()
                        + ": [" + i + "] " + array[i]
                        + " * [" + (i - 2) + "] " + array[i - 2]
                        + " = " + product);

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            sumBackward = sum;
        };
    }

    public long getSumForward() {
        return sumForward;
    }

    public long getSumBackward() {
        return sumBackward;
    }
}