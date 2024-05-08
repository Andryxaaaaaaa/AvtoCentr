package com.example.avtocentr;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class PDF extends AppCompatActivity {

    private static final String TAG = "PDF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        LinearLayout layoutMaintenance = findViewById(R.id.layoutMaintenance);
        LinearLayout layoutRepair = findViewById(R.id.layoutRepair);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioMaintenance) {
                    // Показать раздел "Техобслуживание", скрыть раздел "Ремонт"
                    layoutMaintenance.setVisibility(View.VISIBLE);
                    layoutRepair.setVisibility(View.GONE);
                } else if (checkedId == R.id.radioRepair) {
                    // Показать раздел "Ремонт", скрыть раздел "Техобслуживание"
                    layoutMaintenance.setVisibility(View.GONE);
                    layoutRepair.setVisibility(View.VISIBLE);
                }
            }
        });
        Button button = findViewById(R.id.button4);
        String tag = "";

        // Создаем новый поток для сетевой операции
        new Thread(() -> {
            // Настройки POP3-сервера
            String host = "pop.mail.ru";
            String port = "995"; // Порт POP3-сервера
            String username = "santa5435@mail.ru";
            String password = "TdXP5DNNc9pTFrdAmNqW";

            // Настройка свойств соединения
            Properties props = new Properties();
            props.put("mail.pop3.host", host);
            props.put("mail.pop3.port", port);
            props.put("mail.pop3.ssl.enable", "true");

            // Получение сессии
            Session session = Session.getDefaultInstance(props);

            try {
                Log.d(TAG, "Trying to connect to the mail server...");
                // Подключение к почтовому серверу
                Store store = session.getStore("pop3s");
                store.connect(host, username, password);
                Log.d(TAG, "Successfully connected to the mail server");

                // Получение папки входящих сообщений
                Folder inbox = store.getFolder("INBOX");
                inbox.open(Folder.READ_ONLY);
                Log.d(TAG, "Opened INBOX folder");

                // Получение количества непрочитанных сообщений
                int messageCount = inbox.getUnreadMessageCount();
                Log.d(TAG, "Total unread messages: " + messageCount);

                // Закрытие соединения
                inbox.close(false);
                store.close();

                // Выводим результат на экран с использованием UI-потока

                button.setOnClickListener(v -> {



                    String message = "Сообщение прошу произвести мне " + "\n" +
                            "ФИО: "  + "\n" +
                            "Адрес:--- " + "\n" +
                            "Километры:--- ";

                    new Thread(() -> sendMail("kaltaevaangelina@mail.ru" ,  message)).start();
                });


            } catch (Exception e) {
                e.printStackTrace();
                // Выводим сообщение об ошибке на экран с использованием UI-потока
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(PDF.this, "Ошибка при чтении почты", Toast.LENGTH_LONG).show());
            }
        }).start();
    }
    private void sendMail(String recipient,  String body) {
        final String username = "santa5435@mail.ru"; // Ваша почта
        final String password = "TdXP5DNNc9pTFrdAmNqW"; // Пароль от почты

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.mail.ru");; // SMTP-сервер, замените на нужный вам
        props.put("mail.smtp.port", "2525"); // Порт SMTP-сервера, может отличаться в зависимости от почтового провайдера
        props.put("mail.smtp.ssl.trust", "*");
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Log.d(TAG, "Trying to send email...");
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));

            message.setText(body);

            Transport.send(message);

            Log.d(TAG, "Email sent successfully");

        } catch (MessagingException e) {
            Log.e(TAG, "Failed to send email", e);
            throw new RuntimeException(e);
        } finally {
            Log.d(TAG, "End of sendMail()");
        }
    }
}

