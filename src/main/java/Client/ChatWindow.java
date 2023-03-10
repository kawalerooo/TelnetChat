package Client;


//import com.itextpdf.kernel.pdf.PdfWriter;
//import com.itextpdf.layout.element.Paragraph;
import io.reactivex.Observable;
import org.w3c.dom.Document;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileOutputStream;

public class ChatWindow extends JFrame {
    private ChatClient client;
    private JScrollPane scrollPane;
    private JTextArea textArea;
    private JPanel topPanelSec;
    private JPanel topPanel;
    private JPanel bottomPanel;
    private JLabel statusLabel;
    private JTextField nameFieldPort;
    private JTextField nameFieldHost;
    private JTextField nameField;
    private JButton connectHostPortButton;
    private JButton connectButton;
    private JTextField messageField;
    private JButton sendButton;
    private JButton saveButton;
    private String host;
    private String port;
    public ChatWindow(){
        this.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e){
                System.exit(1);
            }
        });

        this.setTitle("Chat");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());


        statusLabel = new JLabel("Nazwa użytkownika:");
        statusLabel.setForeground(Color.WHITE);
        nameField = new JTextField(30);
        connectButton = new JButton("Połącz");
        connectButton.setBackground(new Color(29, 185, 84));
        connectButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        connectButton.setPreferredSize(new Dimension(200, 40));


        nameFieldPort = new JTextField(30);
        nameFieldHost = new JTextField(30);
        connectHostPortButton = new JButton("Połącz");
        topPanelSec = new JPanel();
        topPanelSec.add(nameFieldPort);
        topPanelSec.add(nameFieldHost);
        topPanelSec.add(connectHostPortButton);

        topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        topPanel.add(statusLabel);
        topPanel.add(nameField);
        topPanel.add(connectButton);
        topPanel.setBackground(new Color(33, 33, 33));

        messageField = new JTextField(30);
        sendButton = new JButton("Wyślij");
        statusLabel.setForeground(Color.WHITE);
        sendButton.setBackground(new Color(29, 185, 84));
        sendButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        sendButton.setPreferredSize(new Dimension(200, 40));

        bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setBackground(new Color(30, 30, 30));
        statusLabel.setForeground(Color.WHITE);
        bottomPanel.add(messageField);
        bottomPanel.add(sendButton);

        saveButton = new JButton("Zapis do pliku");
        saveButton.setBackground(new Color(29, 185, 84));
        saveButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        saveButton.setPreferredSize(new Dimension(200, 40));
        bottomPanel.add(saveButton);

        textArea = new JTextArea();
        textArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
        textArea.setEditable(false);
        textArea.setBackground(new Color(30, 30, 30));
        textArea.setForeground(Color.WHITE);

        scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 400));

        JButton clearButton = new JButton("Wyczyść czat");
        clearButton.setBackground(new Color(29, 185, 84));
        clearButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        clearButton.setPreferredSize(new Dimension(200, 40));
        bottomPanel.add(clearButton);

        JPanel genderPanel = new JPanel();
        genderPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        genderPanel.setBackground(new Color(33, 33, 33));
        JLabel genderLabel = new JLabel("Płeć:");
        genderLabel.setForeground(Color.WHITE);
        genderPanel.add(genderLabel);
        JRadioButton maleButton = new JRadioButton("Mężczyzna");
        JRadioButton femaleButton = new JRadioButton("Kobieta");
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleButton);
        genderGroup.add(femaleButton);
        genderPanel.add(maleButton);
        genderPanel.add(femaleButton);
        topPanel.add(genderPanel, 0);

        topPanel.setVisible(false);
        topPanelSec.setVisible(true);

        final String[] selectedGender = {""};
        ActionListener genderListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JRadioButton selectedRadio = (JRadioButton) e.getSource();
                selectedGender[0] = selectedRadio.getText().toLowerCase();
                nameField.setText(selectedGender[0] + "_" + nameField.getText());
            }
        };
        maleButton.addActionListener(genderListener);
        femaleButton.addActionListener(genderListener);

        this.add(topPanelSec, BorderLayout.NORTH);
        this.add(bottomPanel, BorderLayout.SOUTH);
        this.add(scrollPane, BorderLayout.CENTER);
        this.pack();
        this.setVisible(true);

        Observable<ActionEvent> connectHostPortButtonObservable = Observable.create(emitter -> connectHostPortButton.addActionListener(emitter::onNext));
        connectHostPortButtonObservable.subscribe(e -> {
            port = nameFieldPort.getText();
            host = nameFieldHost.getText();

            this.add(topPanel, BorderLayout.NORTH);
            this.remove(topPanelSec);
            topPanel.setVisible(true);
        });


        Observable<ActionEvent> clearButtonObservable = Observable.create(emitter -> clearButton.addActionListener(emitter::onNext));
        clearButtonObservable.subscribe(e -> textArea.setText(""));


        Observable<ActionEvent> saveButtonObservable = Observable.create(emitter -> saveButton.addActionListener(emitter::onNext));
        saveButtonObservable.subscribe(e -> {
            try {
                FileOutputStream fileOut = new FileOutputStream("conversation.txt");
                fileOut.write(textArea.getText().getBytes());
                fileOut.close();
                JOptionPane.showMessageDialog(this, "Konwersacja zapisana do pliku conversation.txt");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Observable<ActionEvent> connectButtonObservable = Observable.create(emitter -> connectButton.addActionListener(emitter::onNext));
        connectButtonObservable.subscribe(e -> {

            if (nameField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Wprowadź nazwę użytkownika", "Ostrzeżenie", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!maleButton.isSelected() && !femaleButton.isSelected()) {
                JOptionPane.showMessageDialog(this, "Wybierz płeć", "Błąd", JOptionPane.ERROR_MESSAGE);
                return;
            }
            client = new ChatClient(port, host ,nameField.getText(), this.textArea);
            statusLabel.setVisible(false);
            nameField.setVisible(false);
            connectButton.setVisible(false);
            this.remove(topPanel);
        });

        Observable<ActionEvent> sendButtonObservable = Observable.create(emitter -> sendButton.addActionListener(emitter::onNext));
        sendButtonObservable.subscribe(e -> {
            if (messageField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Wprowadź wiadomość", "Ostrzeżenie", JOptionPane.WARNING_MESSAGE);
                return;
            }
            client.sendMessage(messageField.getText());
            messageField.setText("");
        });
    }

    public static void main(String[] args) {
        new ChatWindow();
    }
}