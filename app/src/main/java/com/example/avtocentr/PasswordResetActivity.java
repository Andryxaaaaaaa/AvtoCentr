package com.example.avtocentr;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Properties;
import java.util.Random;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class PasswordResetActivity extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextNewPassword;
    private EditText editTextConfirmPassword;
    private Button resetPasswordButton;
    private Button confirmPasswordButton;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    Button buttonstart;
    EditText editTextCode;
    TextView TextViewcode;
    private String userId;
    private String code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);
        buttonstart = findViewById(R.id.buttonstart);
        editTextCode = findViewById(R.id.editTextCode);
        TextViewcode = findViewById(R.id.textViewcode);
        confirmPasswordButton = findViewById(R.id.confirmPasswordButton);
        progressBar = findViewById(R.id.progressBar);

        db = FirebaseFirestore.getInstance();

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                if (email.isEmpty()) {
                    Toast.makeText(PasswordResetActivity.this, "Введите email", Toast.LENGTH_SHORT).show();
                    return;
                }
                editTextEmail.setVisibility(View.GONE);
                resetPasswordButton.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);

                db.collection("users")
                        .whereEqualTo("email", email)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                progressBar.setVisibility(View.GONE);

                                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                    DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                    userId = documentSnapshot.getId();
                                    showNewPasswordFields();
                                    // Генерируем код при создании активити и отправляем его на почту
                                    sendMailInBackground(editTextEmail.getText().toString(), generateCode());
                                } else {
                                    editTextEmail.setVisibility(View.VISIBLE);
                                    resetPasswordButton.setVisibility(View.VISIBLE);
                                    Toast.makeText(PasswordResetActivity.this, "Пользователь с таким email не найден", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        confirmPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = editTextNewPassword.getText().toString().trim();
                String confirmPassword = editTextConfirmPassword.getText().toString().trim();
                progressBar.setVisibility(View.VISIBLE);
                editTextNewPassword.setVisibility(View.GONE);
                editTextConfirmPassword.setVisibility(View.GONE);
                confirmPasswordButton.setVisibility(View.GONE);
                if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(PasswordResetActivity.this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                    editTextNewPassword.setVisibility(View.VISIBLE);
                    editTextConfirmPassword.setVisibility(View.VISIBLE);
                    confirmPasswordButton.setVisibility(View.VISIBLE);
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(PasswordResetActivity.this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
                    editTextNewPassword.setVisibility(View.VISIBLE);
                    editTextConfirmPassword.setVisibility(View.VISIBLE);
                    confirmPasswordButton.setVisibility(View.VISIBLE);
                    return;
                }

                updatePassword(newPassword);
            }
        });

        buttonstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredCode = editTextCode.getText().toString(); // Переместите эту строку внутрь onClick
                if (!enteredCode.equals(code)) { // Проверьте корректность кода в момент нажатия на кнопку
                    Toast.makeText(getApplicationContext(), "Неверный одноразовый код!", Toast.LENGTH_LONG).show();
                }
                else {
                    buttonstart.setVisibility(View.GONE);
                    editTextCode.setVisibility(View.GONE);
                    TextViewcode.setVisibility(View.GONE);
                    editTextNewPassword.setVisibility(View.VISIBLE);
                    editTextConfirmPassword.setVisibility(View.VISIBLE);
                    confirmPasswordButton.setVisibility(View.VISIBLE);
                }
            }
        });
    }

        private void showNewPasswordFields () {
            editTextEmail.setVisibility(View.GONE);
            resetPasswordButton.setVisibility(View.GONE);
            buttonstart.setVisibility(View.VISIBLE);
            editTextCode.setVisibility(View.VISIBLE);
            TextViewcode.setVisibility(View.VISIBLE);

        }

    private void updatePassword(String newPassword) {
        progressBar.setVisibility(View.VISIBLE);

        db.collection("users").document(userId)
                .update("password", newPassword)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(PasswordResetActivity.this, "Пароль успешно обновлен", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(PasswordResetActivity.this, "Ошибка при обновлении пароля", Toast.LENGTH_SHORT).show();
                            editTextNewPassword.setVisibility(View.VISIBLE);
                            editTextConfirmPassword.setVisibility(View.VISIBLE);
                            confirmPasswordButton.setVisibility(View.VISIBLE);
                        }
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


