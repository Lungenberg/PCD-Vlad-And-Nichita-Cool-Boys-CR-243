import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Consumer;

public class Main extends Application {
    private final Button startButton = new Button("Запустить 4 потока");
    private final TextArea output = new TextArea();
    private final Label status = new Label("Готов к запуску");
    private final Label authors = new Label();

    private Thread[] threads = new Thread[0];
    private Thread coordinator;
    private Timeline typing;

    @Override
    public void start(Stage stage) {
        Label title = new Label("PCD — Лабораторная работа №1");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        output.setEditable(false);
        output.setWrapText(true);
        output.setStyle("-fx-font-family: monospace;");
        authors.setStyle("-fx-font-size: 14px;");
        startButton.setOnAction(event -> runTasks());

        VBox root = new VBox(
                12,
                title,
                startButton,
                status,
                new Label("Журнал выполнения:"),
                output,
                new Label("Авторы:"),
                authors
        );

        root.setPadding(new Insets(16));
        VBox.setVgrow(output, Priority.ALWAYS);

        stage.setTitle("PCD — Concurrent Threads");
        stage.setScene(new Scene(root, 900, 650));
        stage.show();
    }

    private void runTasks() {
        startButton.setDisable(true);
        output.clear();
        authors.setText("");
        status.setText("Потоки выполняются...");

        int[] mas = new int[100];
        Random random = new Random();
        for (int i = 0; i < mas.length; i++) {
            mas[i] = random.nextInt(100) + 1;
        }
        output.appendText("Массив:\n" + Arrays.toString(mas) + "\n\n");

        Consumer<String> log = message -> Platform.runLater(
                () -> output.appendText(message + "\n"));

        StudentA a1 = new StudentA(mas, log);
        StudentA a2 = new StudentA(mas, log);
        StudentB b1 = new StudentB(mas, log);
        StudentB b2 = new StudentB(mas, log);

        threads = new Thread[] {
                new Thread(a1, "A-Th1"),
                new Thread(a2, "A-Th2"),
                new Thread(b1, "B-Th1"),
                new Thread(b2, "B-Th2")
        };

        // запускаем все четыре вычислительных потока.
        for (Thread thread : threads) {
            thread.start();
        }

        // вспомогательный поток ожидает завершения, чтобы окно не зависало.
        coordinator = new Thread(() -> {
            try {
                for (Thread thread : threads) {
                    thread.join();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                for (Thread thread : threads) thread.interrupt();
                return;
            }

            Platform.runLater(() -> {
                output.appendText(
                        "\nВсе четыре вычислительных потока завершены.\n"
                );

                status.setText("Вычисления завершены");
                showAuthors();
            });

        }, "Coordinator");
        coordinator.start();
    }
    private void showAuthors() {
        String text = "Nichita Negru\nVlad Osadciuc\nГруппа: CR-243";
        int[] index = {0};

        typing = new Timeline(new KeyFrame(Duration.millis(100), event -> {
            authors.setText(authors.getText() + text.charAt(index[0]++));
        }));
        typing.setCycleCount(text.length());
        typing.setOnFinished(event -> {
            status.setText("Работа завершена");
            startButton.setDisable(false);
        });
        typing.play();
    }

    @Override
    public void stop() {
        if (typing != null) typing.stop();
        if (coordinator != null) coordinator.interrupt();
        for (Thread thread : threads) thread.interrupt();
    }

    public static void main(String[] args) throws InterruptedException {
        launch(args);

        String text = "Nichita Negru\nVlad Osadciuc\nГруппа: CR-243";
        for (int i = 0; i < text.length(); i++) {
            System.out.print(text.charAt(i));
            Thread.sleep(100);
        }
        System.out.println();
    }
}
