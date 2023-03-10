package Client;

import javax.swing.*;
import java.io.*;
import java.net.Socket;


import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import org.w3c.dom.Document;

public class ChatClient {
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private PublishSubject<String> messagesSubject = PublishSubject.create();
    private Observable<String> messagesObservable;

    private String name;

    public ChatClient(String port, String host ,String name, JTextArea textArea) {
        try {
            this.name = name;
            socket = new Socket(host, Integer.valueOf(port));
            writer = new PrintWriter(socket.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            messagesObservable = messagesSubject.share();

            messagesObservable.subscribe(message -> {textArea.append(message.concat("\n"));});
            sendMessage(name);

            Thread clientThread = new ChatClientListener(name, reader, messagesSubject);
            clientThread.start();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public void sendMessage(String message) {
        writer.println(message);
        writer.flush();
    }
}
