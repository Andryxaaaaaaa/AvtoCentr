package com.example.avtocentr;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class PDF extends AppCompatActivity {

    private static final String TAG = "PDF";

    private EditText t1;
    private EditText t2;
    private EditText t3;
    private EditText t4;
    private EditText t5;
    private EditText t6;
    private EditText t7;
    private EditText t8;
    private EditText t9;
    private EditText t10;
    private EditText t11;
    private EditText t12;
    private EditText t13;
    private EditText t14;
    private EditText t15;
    private EditText t16;
    private EditText t17;
    private EditText t18;
    private EditText t19;
    private EditText t20;
    private EditText t21;
    private EditText t22;
    private EditText d1;
    private EditText d2;
    private EditText d3;
    private EditText d4;
    private EditText d5;
    private EditText d6;
    private EditText d7;
    private EditText d8;
    private EditText d9;
    private EditText d10;
    private EditText d11;
    private EditText d12;
    private EditText d13;
    private EditText d14;
    private EditText d15;
    private EditText d16;
    private EditText d17;
    private EditText d18;
    private EditText d19;
    private EditText d20;
    private EditText d21;
    private EditText d22;
    String currentUserFIO;
    String currentUserEmail;
    String km;
    String mappoint;

    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        LinearLayout layoutMaintenance = findViewById(R.id.layoutMaintenance);
        LinearLayout layoutRepair = findViewById(R.id.layoutRepair);
        t1 = findViewById(R.id.editText111);
        t2 = findViewById(R.id.editText112);
        t3 = findViewById(R.id.editText113);
        t4 = findViewById(R.id.editText114);
        t5 = findViewById(R.id.editText115);
        t6 = findViewById(R.id.editText116);
        t7 = findViewById(R.id.editText117);
        t8 = findViewById(R.id.editText118);
        t9 = findViewById(R.id.editText119);
        t10 = findViewById(R.id.editText120);
        t11 = findViewById(R.id.editText121);
        t12 = findViewById(R.id.editText122);
        t13 = findViewById(R.id.editText123);
        t14 = findViewById(R.id.editText124);
        t15 = findViewById(R.id.editText125);
        t16 = findViewById(R.id.editText126);
        t17 = findViewById(R.id.editText127);
        t18 = findViewById(R.id.editText128);
        t19 = findViewById(R.id.editText129);
        t20 = findViewById(R.id.editText130);
        t21 = findViewById(R.id.editText131);
        t22 = findViewById(R.id.editText132);
        d1 = findViewById(R.id.editText222);
        d2 = findViewById(R.id.editText222);
        d3 = findViewById(R.id.editText223);
        d4 = findViewById(R.id.editText224);
        d5 = findViewById(R.id.editText225);
        d6 = findViewById(R.id.editText226);
        d7 = findViewById(R.id.editText227);
        d8 = findViewById(R.id.editText228);
        d9 = findViewById(R.id.editText229);
        d10 = findViewById(R.id.editText230);
        d11 = findViewById(R.id.editText231);
        d12 = findViewById(R.id.editText232);
        d13 = findViewById(R.id.editText233);
        d14 = findViewById(R.id.editText234);
        d15 = findViewById(R.id.editText235);
        d16 = findViewById(R.id.editText236);
        d17 = findViewById(R.id.editText237);
        d18 = findViewById(R.id.editText238);
        d19 = findViewById(R.id.editText239);
        d20 = findViewById(R.id.editText240);
        d21 = findViewById(R.id.editText241);
        d22 = findViewById(R.id.editText242);
        db = FirebaseFirestore.getInstance();
        SharedPreferences sp = getSharedPreferences("Авторизация", Context.MODE_PRIVATE);
        currentUserEmail = sp.getString("CurrentUserEmail", "");
        // Получение ФИО текущего пользователя из базы данных
        db.collection("userinfo")
                .whereEqualTo("email", currentUserEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String familia = documentSnapshot.getString("familia");
                            String imya = documentSnapshot.getString("imya");
                            String otchestvo = documentSnapshot.getString("otchestvo");
                            String number = documentSnapshot.getString("number");
                            currentUserFIO = familia + " " + imya + " " + otchestvo;





                        }
                    } else {

                    }
                })
                .addOnFailureListener(e -> {
                    // Обработка ошибки

                });
        db.collection("map")
                .whereEqualTo("email", currentUserEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String mappoint = documentSnapshot.getString("mappoint");
                            String km = documentSnapshot.getString("km");
                        }
                    } else {

                    }
                })
                .addOnFailureListener(e -> {
                    // Обработка ошибки

                });
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
        Button button2 = findViewById(R.id.button5);
        String tag = "";



        // Создаем новый поток для сетевой операции
        new Thread(() -> {

            try {

                // Выводим результат на экран с использованием UI-потока

                button.setOnClickListener(v -> {
                    String message = "Сообщение\n Заявка на Техническое Обслуживание " + "\n" +
                            "ФИО: " + currentUserFIO + "\n" +
                            "Адрес: "+ mappoint + "\n" +
                            "Километры: "+ km + "\n" +
                            "Наша организация " +                                                                   d1.getText().toString() + "\n" +
                            "Наименование изделия " +                                                               d2 .getText().toString()+ "\n" +
                            "Дата приобретения " +                                                                  d3.getText().toString() + "\n" +
                            "№ товарной накладной " +                                                               d4.getText().toString() + "\n" +
                            "Модель техники (оборудования) " +                                                      d5.getText().toString() + "\n" +
                            "Серийный номер техники (оборудования) " +                                              d6.getText().toString() + "\n" +
                            "Дата ввода в эксплуатацию " +                                                          d7.getText().toString() + "\n" +
                            "Кол-во отработанных часов " +                                                          d8.getText().toString() + "\n" +
                            "Модель двигателя " +                                                                   d9.getText().toString() + "\n" +
                            "Серийный номер двигателя " +                                                           d10.getText().toString() + "\n" +
                            "Дата реализации техники (оборудования) " +                                             d11.getText().toString() + "\n" +
                            "Владелец техники (об-ния) " +                                                          d12.getText().toString() + "\n" +
                            "Адрес владельца " +                                                                    d13.getText().toString() + "\n" +
                            "Область владельца " +                                                                  d14.getText().toString() + "\n" +
                            "Район владельца " +                                                                    d15.getText().toString() + "\n" +
                            "Адрес места эксплуатации / места проведения ремонта " +                                d16.getText().toString() + "\n" +
                            "Область места эксплуатации " +                                                         d17.getText().toString() + "\n" +
                            "Район места эксплуатации " +                                                           d18.getText().toString() + "\n" +
                            "Расстояние от технического центра до места проведения ремонта (туда и обратно), км " + d19.getText().toString() + "\n" +
                            "Наименование дефектного изделия " +                                                    d20.getText().toString() + "\n" +
                            "Предварительная причина поломки (выявленный недостаток) " +                            d21.getText().toString() + "\n";


                    new Thread(() -> sendMail("kaltaevaangelina@mail.ru" ,  message)).start();
                    new Thread(() -> sendTO()).start();
                });
                button2.setOnClickListener(v -> {
                    String message = "Сообщение\n Заявка на Ремонт " + "\n" +
                            "ФИО: " + currentUserFIO + "\n" +
                            "Адрес: "+ mappoint + "\n" +
                            "Километры: "+ km + "\n" +
                            "Наша организация " +                                                                   t1.getText().toString() + "\n" +
                            "Наименование изделия " +                                                               t2.getText().toString() + "\n" +
                            "Дата приобретения " +                                                                  t3.getText().toString() + "\n" +
                            "№ товарной накладной " +                                                               t4.getText().toString() + "\n" +
                            "Модель техники (оборудования) " +                                                      t5.getText().toString() + "\n" +
                            "Серийный номер техники (оборудования) " +                                              t6.getText().toString() + "\n" +
                            "Дата ввода в эксплуатацию " +                                                          t7.getText().toString() + "\n" +
                            "Кол-во отработанных часов " +                                                          t8.getText().toString() + "\n" +
                            "Модель двигателя " +                                                                   t9.getText().toString() + "\n" +
                            "Серийный номер двигателя " +                                                           t11.getText().toString() + "\n" +
                            "Дата реализации техники (оборудования) " +                                             t12.getText().toString() + "\n" +
                            "Владелец техники (об-ния) " +                                                          t13.getText().toString() + "\n" +
                            "Адрес владельца " +                                                                    t14.getText().toString() + "\n" +
                            "Область владельца " +                                                                  t15.getText().toString() + "\n" +
                            "Район владельца " +                                                                    t16.getText().toString() + "\n" +
                            "Адрес места эксплуатации / места проведения ремонта " +                                t17.getText().toString() + "\n" +
                            "Область места эксплуатации " +                                                         t18.getText().toString() + "\n" +
                            "Район места эксплуатации " +                                                           t19.getText().toString() + "\n" +
                            "Расстояние от технического центра до места проведения ремонта (туда и обратно), км " + t20.getText().toString() + "\n" +
                            "Наименование дефектного изделия " +                                                    t21.getText().toString() + "\n" +
                            "Предварительная причина поломки (выявленный недостаток) " +                            t22.getText().toString() + "\n";

                    new Thread(() -> sendMail("kaltaevaangelina@mail.ru" ,  message)).start();
                    new Thread(() -> sendRemont()).start();
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
    private void sendRemont() {
        Map<String, Object> zayavkaData = new HashMap<>();
        zayavkaData.put("Наша организация" ,  d1.getText().toString());
        zayavkaData.put("Наименование изделия",  d2.getText().toString());
        zayavkaData.put("Дата приобретения" ,  d3.getText().toString());
        zayavkaData.put("№ товарной накладной",  d4.getText().toString());
        zayavkaData.put("Модель техники (оборудования)",  d5.getText().toString());
        zayavkaData.put("Серийный номер техники (оборудования)",  d6.getText().toString());
        zayavkaData.put("Дата ввода в эксплуатацию",  d7.getText().toString());
        zayavkaData.put("Кол-во отработанных часов",  d8.getText().toString());
        zayavkaData.put("Модель двигателя ",  d9.getText().toString());
        zayavkaData.put("Владелец компании", d10.getText().toString());
        zayavkaData.put("Серийный номер двигателя ", d11.getText().toString());
        zayavkaData.put("Дата реализации техники (оборудования)", d12.getText().toString());
        zayavkaData.put("Владелец техники (об-ния) ", d13.getText().toString());
        zayavkaData.put("Адрес владельца ", d14.getText().toString());
        zayavkaData.put("Область владельца", d15.getText().toString());
        zayavkaData.put("Район владельца ", d16.getText().toString());
        zayavkaData.put("Адрес места эксплуатации / места проведения ремонта ", d17.getText().toString());
        zayavkaData.put("Область места эксплуатации ", d18.getText().toString());
        zayavkaData.put("Район места эксплуатации ", d19.getText().toString());
        zayavkaData.put("Расстояние от технического центра до места проведения ремонта (туда и обратно), км ", d20.getText().toString());
        zayavkaData.put("Наименование дефектного изделия " , d21.getText().toString());
        zayavkaData.put("Предварительная причина поломки (выявленный недостаток) ", d22.getText().toString());
        zayavkaData.put("typezayavka",  "Ремонт");
        zayavkaData.put("user", currentUserEmail);
        db.collection("zayavka")
                .add(zayavkaData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Document created successfully
                        Log.d("zayavka", "DocumentSnapshot written with ID: " + documentReference.getId());

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to create document
                        Toast.makeText(PDF.this, "Failed to create document in 'userinfo' collection", Toast.LENGTH_LONG).show();
                        Log.e("zayavka", "Error adding document", e);
                    }
                });
    }
    private void sendTO() {
        Map<String, Object> zayavkaData = new HashMap<>();
        zayavkaData.put("Наша организация" ,  t1.getText().toString());
        zayavkaData.put("Наименование изделия",  t2.getText().toString());
        zayavkaData.put("Дата приобретения" ,  t3.getText().toString());
        zayavkaData.put("№ товарной накладной",  t4.getText().toString());
        zayavkaData.put("Модель техники (оборудования)",  t5.getText().toString());
        zayavkaData.put("Серийный номер техники (оборудования)", t6.getText().toString());
        zayavkaData.put("Дата ввода в эксплуатацию", t7.getText().toString());
        zayavkaData.put("Кол-во отработанных часов",  t8.getText().toString());
        zayavkaData.put("Модель двигателя ",  t9.getText().toString());
        zayavkaData.put("Владелец компании", t10.getText().toString());
        zayavkaData.put("Серийный номер двигателя ", t11.getText().toString());
        zayavkaData.put("Дата реализации техники (оборудования)", t12.getText().toString());
        zayavkaData.put("Владелец техники (об-ния) ",t13.getText().toString());
        zayavkaData.put("Адрес владельца ",t14.getText().toString());
        zayavkaData.put("Область владельца", t15.getText().toString());
        zayavkaData.put("Район владельца ", t16.getText().toString());
        zayavkaData.put("Адрес места эксплуатации / места проведения ремонта ", t17.getText().toString());
        zayavkaData.put("Область места эксплуатации ", t18.getText().toString());
        zayavkaData.put("Район места эксплуатации ", t19.getText().toString());
        zayavkaData.put("Расстояние от технического центра до места проведения ремонта (туда и обратно), км ", d20.getText().toString());
        zayavkaData.put("Наименование дефектного изделия " , t21.getText().toString());
        zayavkaData.put("Предварительная причина поломки (выявленный недостаток) ", t22.getText().toString());;
        zayavkaData.put("typezayavka",  "Техническое Обслуживание");
        zayavkaData.put("user", currentUserEmail);
        db.collection("zayavka")
                .add(zayavkaData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Document created successfully
                        Log.d("zayavka", "DocumentSnapshot written with ID: " + documentReference.getId());

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to create document
                        Toast.makeText(PDF.this, "Failed to create document in 'userinfo' collection", Toast.LENGTH_LONG).show();
                        Log.e("zayavka", "Error adding document", e);
                    }
                });
    }
}

