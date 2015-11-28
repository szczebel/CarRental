package client.ui;

import javax.swing.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class BackgroundOperation {

    public static void execute(final Runnable backgroundTask) {
        execute(backgroundTask, () -> {
            //noop
        });
    }

    public static void execute(final Runnable backgroundTask, final Runnable successHandler) {
        execute(backgroundTask, successHandler, BackgroundOperation::onException);
    }

    public static void execute(final Runnable backgroundTask, final Runnable successHandler, Consumer<Exception> failureHandler) {
        execute(
                () -> {
                    backgroundTask.run();
                    return null;
                },
                o -> successHandler.run(),
                failureHandler);
    }

    public static <Result> void execute(final Callable<Result> backgroundTask, final Consumer<Result> successHandler) {
        execute(backgroundTask, successHandler, BackgroundOperation::onException);
    }

    private static void onException(Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error occured : " + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()));
    }

    public static <Result> Cancellable execute(
            Callable<Result> task,
            Consumer<Result> successHandler,
            Consumer<Exception> failureHandler) {

        SwingWorker<Result, Object> swingWorker = new SwingWorker<Result, Object>() {
            @Override
            protected Result doInBackground() throws Exception {
                return task.call();
            }

            @Override
            protected void done() {
                try {
                    successHandler.accept(get());
                } catch (InterruptedException | ExecutionException e) {
                    failureHandler.accept(e);
                }
            }
        };
        swingWorker.execute();
        return () -> swingWorker.cancel(true);
    }

    public interface Cancellable {
        void cancel();
    }
}
