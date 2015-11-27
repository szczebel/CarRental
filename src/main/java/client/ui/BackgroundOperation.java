package client.ui;

import javax.swing.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class BackgroundOperation {
    public static <Result> void execute(final Callable<Result> backgroundTask, final Consumer<Result> successHandler) {
        execute( backgroundTask, successHandler, BackgroundOperation::onException);
    }

    private static void onException(Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error occured");
    }

    public static <Result> void execute(
            Callable<Result> task,
            Consumer<Result> successHandler,
            Consumer<Exception> failureHandler) {

        new SwingWorker<Result, Object>(){
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
        }.execute();
    }
}
