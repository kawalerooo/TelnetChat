package Server;

import io.reactivex.subjects.PublishSubject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

class ClientHandler extends Thread {
    private Socket socket;
    private PublishSubject<String> messagesSubject;
    private List<PrintWriter> writers;

    ClientHandler(Socket socket, PublishSubject<String> messagesSubject, List<PrintWriter> writers) {
        this.socket = socket;
        this.messagesSubject = messagesSubject;
        this.writers = writers;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            writers.add(writer);

            String name = reader.readLine();
            messagesSubject.onNext(name + " joined the chat");

            String message;
            while ((message = reader.readLine()) != null) {
                messagesSubject.onNext(name + ": " + message);
            }
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        } finally {
            writers.remove(socket);
        }
    }
}
