package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

public class ChatServer {
    private List<PrintWriter> writers = new ArrayList<>();
    private PublishSubject<String> messagesSubject = PublishSubject.create();

    public static void main(String[] args) {
        new ChatServer().start();
    }

    private void start() {
        messagesSubject.subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable disposable) {
            }

            @Override
            public void onNext(String message) {
                broadcastMessage(message);
            }

            @Override
            public void onError(Throwable throwable) {
                System.err.println(throwable.getMessage());
            }

            @Override
            public void onComplete() {
            }
        });

        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            while (true) {
                Socket socket = serverSocket.accept();
                new ClientHandler(socket, messagesSubject, writers).start();
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private void broadcastMessage(String message) {
        writers.forEach(writer -> writer.println(message));
        writers.forEach(PrintWriter::flush);
    }
}
