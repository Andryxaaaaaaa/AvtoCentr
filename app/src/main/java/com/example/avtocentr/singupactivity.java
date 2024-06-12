package com.example.avtocentr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class singupactivity extends AppCompatActivity {

    // Шаблон для проверки пароля
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{7,}$"
    );

    private String code; // Переменная для хранения сгенерированного кода

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singupactivity);

        SharedPreferences sp = getSharedPreferences("Регистрация", Context.MODE_PRIVATE);
        EditText email = findViewById(R.id.editTextLogin);
        EditText password = findViewById(R.id.editTextPassword);
        Button button = findViewById(R.id.button);
        Button buttonstart = findViewById(R.id.buttonstart);
        ImageButton button2 = findViewById(R.id.button2);
        EditText editTextCode = findViewById(R.id.editTextCode);
        TextView TextViewcode = findViewById(R.id.textViewcode);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(singupactivity.this, MainActivity.class));
            }
        });

        buttonstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredCode = editTextCode.getText().toString(); // Получаем введенный пользователем код
                String emailText = email.getText().toString();
                String passwordText = password.getText().toString();
                if (!enteredCode.equals(code)) { // Сравниваем введенный пользователем код с сгенерированным
                    Toast.makeText(getApplicationContext(), "Неверный одноразовый код!", Toast.LENGTH_LONG).show();
                } else {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    db.collection("users")
                            .whereEqualTo("email", emailText)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if (!queryDocumentSnapshots.isEmpty()) {
                                        Toast.makeText(singupactivity.this, "Пользователь с таким email уже существует", Toast.LENGTH_LONG).show();
                                    } else {
                                        Map<String, Object> user = new HashMap<>();
                                        user.put("email", emailText);
                                        user.put("password", passwordText);

                                        db.collection("users")
                                                .add(user)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        Map<String, Object> mapData = new HashMap<>();
                                                        mapData.put("user", emailText);
                                                        mapData.put("km", "");
                                                        mapData.put("mappoint", "");

                                                        db.collection("map")
                                                                .add(mapData)
                                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                    @Override
                                                                    public void onSuccess(DocumentReference documentReference) {
                                                                        sp.edit().putString("CurrentUserEmail", emailText).apply();
                                                                        startActivity(new Intent(singupactivity.this, SignFIO.class));
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(Exception e) {
                                                                        Toast.makeText(singupactivity.this, "Не удалось создать документ в коллекции 'map'", Toast.LENGTH_LONG).show();
                                                                    }
                                                                });

                                                        Map<String, Object> userInfo = new HashMap<>();
                                                        userInfo.put("email", emailText);
                                                        userInfo.put("familia", "");
                                                        userInfo.put("imya", "");
                                                        userInfo.put("otchestvo", "");
                                                        userInfo.put("number", "");

                                                        db.collection("userinfo")
                                                                .add(userInfo)
                                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                    @Override
                                                                    public void onSuccess(DocumentReference documentReference) {
                                                                        startActivity(new Intent(singupactivity.this, SignFIO.class));
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(Exception e) {
                                                                        Toast.makeText(singupactivity.this, "Failed to create document in 'userinfo' collection", Toast.LENGTH_LONG).show();
                                                                    }
                                                                });
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(Exception e) {
                                                        Toast.makeText(singupactivity.this, "Не удалось создать пользователя", Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(Exception e) {
                                    Toast.makeText(singupactivity.this, "Ошибка при проверке email", Toast.LENGTH_LONG).show();
                                }
                            });
                    startActivity(new Intent(singupactivity.this, SignFIO.class));
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // По нажатию этой кнопки выводим buttonstart и editTextCode (остальное убираем)

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                String emailText = email.getText().toString();
                String passwordText = password.getText().toString();
                if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                    Toast.makeText(getApplicationContext(), "Неверный формат email!", Toast.LENGTH_LONG).show();
                } else if (!PASSWORD_PATTERN.matcher(passwordText).matches()) {
                    Toast.makeText(getApplicationContext(), "Пароль должен содержать большую букву, цифры и не менее 8 символов", Toast.LENGTH_LONG).show();
                }
                else
                    {
                        db.collection("users")
                                .whereEqualTo("email", emailText)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        if (!queryDocumentSnapshots.isEmpty()) {
                                            Toast.makeText(singupactivity.this, "Пользователь с таким email уже существует", Toast.LENGTH_LONG).show();
                                        } else {
                                            email.setVisibility(View.GONE);
                                            password.setVisibility(View.GONE);
                                            button.setVisibility(View.GONE);
                                            button2.setVisibility(View.GONE);
                                            buttonstart.setVisibility(View.VISIBLE);
                                            editTextCode.setVisibility(View.VISIBLE);
                                            TextViewcode.setVisibility(View.VISIBLE);


                                        }

                                    }
                                });
                    }

                // Генерируем код при создании активити и отправляем его на почту
                sendMailInBackground(email.getText().toString(), generateCode());

            }
        });
    }

    private String generateCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000; // Генерируем случайное число от 100000 до 999999
        this.code = String.valueOf(code); // Сохраняем сгенерированный код в переменной класса
        return this.code;
    }
    private void sendMailInBackground(String recipient, String code) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                // Весь код отправки электронного письма должен быть здесь
                final String username = "santa5435@mail.ru"; // Ваша почта
                final String password = "TdXP5DNNc9pTFrdAmNqW"; // Пароль от почты

                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", "smtp.mail.ru"); // SMTP-сервер, замените на нужный вам
                props.put("mail.smtp.port", "2525"); // Порт SMTP-сервера, может отличаться в зависимости от почтового провайдера
                props.put("mail.smtp.ssl.trust", "*");
                Session session = Session.getInstance(props,
                        new javax.mail.Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(username, password);
                            }
                        });

                try {
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(username));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
                    message.setSubject("Автоцентр - одноразовый код для регистрации в приложении Автоцентр");
                    message.setText("Ваш одноразовый код: " + code);
                    Transport.send(message);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }
}