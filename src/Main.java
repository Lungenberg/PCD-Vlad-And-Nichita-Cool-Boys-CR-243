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

    private final Button startButton =
            new Button("Запустить 4 потока");

    private final TextArea output = new TextArea();

    private final Label status =
            new Label("Готов к запуску");

    private final Label results =
            new Label("Результаты появятся после вычислений");

    private final Label authors = new Label();

    private Thread[] threads = new Thread[0];
    private Thread coordinator;
    private Timeline typing;

    @Override
    public void start(Stage stage) {

        Label title = new Label(
                "PCD — Лабораторная работа №1"
        );

        title.setStyle(
                "-fx-font-size: 20px;" + "-fx-font-weight: bold;"
        );

        output.setEditable(false);
        output.setWrapText(true);
        output.setStyle("-fx-font-family: monospace;");

        results.setWrapText(true);
        results.setStyle("-fx-font-size: 14px;");

        authors.setStyle("-fx-font-size: 14px;");

        startButton.setOnAction(event -> runTasks());

        VBox root = new VBox(
                12,
                title,
                startButton,
                status,
                new Label("Журнал выполнения:"),
                output,
                new Label("Результаты:"),
                results,
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

        // блокируем повторный запуск
        startButton.setDisable(true);

        output.clear();
        results.setText("Вычисление...");
        authors.setText("");
        status.setText("Потоки выполняются...");

        int[] mas = new int[100];
        Random random = new Random();

        for (int i = 0; i < mas.length; i++) {
            mas[i] = random.nextInt(100) + 1;
        }

        output.appendText(
                "Массив:\n" + Arrays.toString(mas) + "\n\n"
        );

        // вывод в JavaFX
        Consumer<String> log = message ->
                Platform.runLater(() ->
                        output.appendText(message + "\n")
                );

        StudentA a = new StudentA(mas);
        StudentB b = new StudentB(mas);

        // передача единого вывода
        a.setOutput(log);
        b.setOutput(log);

        threads = new Thread[] {
                new Thread(a.forwardTask(), "A-Th1"),
                new Thread(a.backwardTask(), "A-Th2"),
                new Thread(b.forwardTask(), "B-Th1"),
                new Thread(b.backwardTask(), "B-Th2")

        };

        // координатор для запуска и ожидания завершения потоков
        Thread[] workers = threads;

        coordinator = new Thread(() -> {

            for (Thread thread : workers) {
                thread.start();
            }

            try {
                for (Thread thread : workers) {
                    thread.join();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

                for (Thread thread : workers) {
                    thread.interrupt();
                }

                return;
            }

            // обновление JavaFX после завершения
            Platform.runLater(() -> {

                output.appendText(
                        "\nВсе четыре потока завершены.\n"
                );

                results.setText(
                        "Student A:\n" +
                                "  Th1 (Forward): " + a.getSumForward() + "\n" +
                                "  Th2 (Backward): " + a.getSumBackward() + "\n\n" +
                                "Student B:\n" +
                                "  Th1 (Forward): " + b.getSumForward() + "\n" +
                                "  Th2 (Backward): " + b.getSumBackward()
                );

                status.setText("Вычисления завершены");

                showAuthors();
            });

        }, "Coordinator");

        coordinator.setDaemon(true);
        coordinator.start();
    }

    private void showAuthors() {

        String text =
                "Nichita Negru\n" +
                        "Vlad Osadciuc\n" +
                        "Группа: CR-243";

        int[] index = {0};

        typing = new Timeline(
                new KeyFrame(Duration.millis(100), event -> {
                    authors.setText(
                            authors.getText() +
                                    text.charAt(index[0]++)
                    );
                })
        );

        typing.setCycleCount(text.length());

        typing.setOnFinished(event -> {
            status.setText("Работа завершена");
            startButton.setDisable(false);
        });

        typing.play();
    }

    @Override
    public void stop() {

        if (typing != null) {
            typing.stop();
        }

        for (Thread thread : threads) {
            thread.interrupt();
        }

        if (coordinator != null) {
            coordinator.interrupt();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
