package Client;

import io.reactivex.subjects.PublishSubject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

public class ChatClientListener extends Thread {
    private BufferedReader reader;
    private PublishSubject<String> messagesSubject;
    private String senderName;

    public ChatClientListener(String name, BufferedReader reader, PublishSubject<String> messagesSubject) {
        this.senderName = name;
        this.reader = reader;
        this.messagesSubject = messagesSubject;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String message = reader.readLine();
                if (message != null) {
                    messagesSubject.onNext(message.startsWith(senderName + ": ") ?
                            message.substring(senderName.length() + 2) : message);
                } else {
                    messagesSubject.onComplete();
                    break;
                }
            } catch (IOException ex) {
                messagesSubject.onError(ex);
                break;
            }
        }
    }
}
