package com.example.avtocentr;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.maps.android.PolyUtil;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Osnovnaja extends AppCompatActivity {

    // Код запроса для обратного вызова
    private static final int REQUEST_PICK_LOCATION = 1;
    // Код запроса для разрешения доступа к местоположению
    static final int REQUEST_LOCATION_PERMISSION = 1;

    private static final int UPDATE_INTERVAL = 600000; // 10 seconds
    private static final String TAG = "MapActivity";
    private WebView webView;
    private TextView distanceTextView;
    private AutoCompleteTextView autoCompleteTextView;
    private FirebaseFirestore db;
    private Handler handler = new Handler();
    double currentLatitude;
    double currentLongitude;
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

    String currentUserFIO;
    String currentUserEmail;
    String adres;

    // Ссылка на TextView


    // Firestore объект

    // Контейнер для дополнительной информации
    private LinearLayout detailsContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_osnovnaja);

        // Инициализация Firestore
        db = FirebaseFirestore.getInstance();

        // Получение ссылок на элементы интерфейса
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        RadioGroup radioGroup2 = findViewById(R.id.radioGroup2);
        LinearLayout layoutMaintenance = findViewById(R.id.layoutMaintenance);
        ConstraintLayout ZajavkaLayout = findViewById(R.id.ZajavkaLayout);
        ConstraintLayout MapLayout = findViewById(R.id.MapLayout);
        ConstraintLayout HomeLayout = findViewById(R.id.HomeLayout);
        ImageButton buttonHome = findViewById(R.id.buttonHome);
        ImageButton buttonZayavka = findViewById(R.id.buttonZayavka);
        ImageButton buttonMap = findViewById(R.id.buttonMap);
        LinearLayout layoutRepair = findViewById(R.id.layoutRepair);
        webView = findViewById(R.id.webView);
        distanceTextView = findViewById(R.id.distanceTextView);
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        RadioButton radioButtonHome = findViewById(R.id.Glavnaja);
        RadioButton radioButtonZayavka = findViewById(R.id.Zajavka);
        RadioButton radioButtonMap = findViewById(R.id.Karta);
        TextView textViewCurrentUserEmail = findViewById(R.id.textViewCurrentUserEmail);
        ImageButton imagebutton = findViewById(R.id.buttonimage);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        detailsContainer = findViewById(R.id.detailsContainer);
        RadioButton radioMaintenance = findViewById(R.id.radioMaintenance);
        RadioButton radioRepair = findViewById(R.id.radioRepair);

        t2 = findViewById(R.id.editText111);
        t3 = findViewById(R.id.editText113);
        t4 = findViewById(R.id.editText114);
        t5 = findViewById(R.id.editText115);
        t6 = findViewById(R.id.editText116);
        t7 = findViewById(R.id.editText117);
        t8 = findViewById(R.id.editText118);
        t9 = findViewById(R.id.editText119);
        t10 =findViewById(R.id.editText120);

        t12 =findViewById(R.id.editText131);

        d2 = findViewById(R.id.editText211);
        d3 = findViewById(R.id.editText213);
        d4 = findViewById(R.id.editText214);
        d5 = findViewById(R.id.editText215);
        d6 = findViewById(R.id.editText216);
        d7 = findViewById(R.id.editText217);
        d8 = findViewById(R.id.editText218);
        d9 = findViewById(R.id.editText219);
        d10 = findViewById(R.id.editText220);
        d11 = findViewById(R.id.editText226);
        d12 = findViewById(R.id.editText231);


        Button button = findViewById(R.id.button4);
        Button button2 = findViewById(R.id.button5);

    

        // Отображение прогресса и скрытие текста о текущем пользователе
        progressBar.setVisibility(View.VISIBLE);
        textViewCurrentUserEmail.setVisibility(View.GONE);

        // Обработчик клика по тексту текущего пользователя
        textViewCurrentUserEmail.setOnClickListener(v -> {
            Intent intent = new Intent(Osnovnaja.this, Profile.class);
            startActivity(intent);
        });

        // Обработчик клика по кнопке перехода на профиль
        imagebutton.setOnClickListener(v -> {
            Intent intent = new Intent(Osnovnaja.this, Profile.class);
            startActivity(intent);
        });
        buttonHome.setColorFilter(Color.parseColor("#0097ED"));
        buttonZayavka.setColorFilter(Color.parseColor("#656565"));
        buttonMap.setColorFilter(Color.parseColor("#656565"));
        radioButtonHome.setChecked(true);
        // Обработчики клика по кнопкам навигации
        buttonHome.setOnClickListener(v -> {
            radioButtonHome.setChecked(true);
            radioButtonZayavka.setChecked(false);
            radioButtonMap.setChecked(false);
            buttonHome.setColorFilter(Color.parseColor("#0097ED"));
            buttonZayavka.setColorFilter(Color.parseColor("#656565"));
            buttonMap.setColorFilter(Color.parseColor("#656565"));
        });

        buttonZayavka.setOnClickListener(v -> {
            radioMaintenance.setChecked(true);
            radioButtonHome.setChecked(false);
            radioButtonZayavka.setChecked(true);
            radioButtonMap.setChecked(false);
            buttonZayavka.setColorFilter(Color.parseColor("#0097ED"));
            buttonMap.setColorFilter(Color.parseColor("#656565"));
            buttonHome.setColorFilter(Color.parseColor("#656565"));
        });

        buttonMap.setOnClickListener(v -> {
            radioButtonHome.setChecked(false);
            radioButtonZayavka.setChecked(false);
            radioButtonMap.setChecked(true);
            buttonMap.setColorFilter(Color.parseColor("#0097ED"));
            buttonZayavka.setColorFilter(Color.parseColor("#656565"));
            buttonHome.setColorFilter(Color.parseColor("#656565"));

        });

        // Получение текущего пользователя из SharedPreferences
        SharedPreferences sp = getSharedPreferences("Авторизация", Context.MODE_PRIVATE);
        String currentUserEmail = sp.getString("CurrentUserEmail", "");

        // Получение информации о пользователе из Firestore
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

                            // Отображение ФИО текущего пользователя
                            textViewCurrentUserEmail.setText(imya);
                            progressBar.setVisibility(View.GONE);
                            textViewCurrentUserEmail.setVisibility(View.VISIBLE);
                        }
                    } else {
                        textViewCurrentUserEmail.setText("ФИО не найдено");
                    }
                })
                .addOnFailureListener(e -> {
                    // Обработка ошибки
                    textViewCurrentUserEmail.setText("Ошибка загрузки ФИО: " + e.getMessage());
                });

        // Обработчик выбора в RadioGroup 1
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioMaintenance) {
                layoutMaintenance.setVisibility(View.VISIBLE);
                layoutRepair.setVisibility(View.GONE);
            } else if (checkedId == R.id.radioRepair) {
                layoutMaintenance.setVisibility(View.GONE);
                layoutRepair.setVisibility(View.VISIBLE);
            }
        });

        // Обработчик выбора в RadioGroup 2
        radioGroup2.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.Zajavka) {
                // Устанавливаем радиокнопку Техобслуживание в состояние "нажато"
                radioMaintenance.setChecked(true);

                ZajavkaLayout.setVisibility(View.VISIBLE);
                MapLayout.setVisibility(View.GONE);
                HomeLayout.setVisibility(View.GONE);
                buttonZayavka.setColorFilter(Color.parseColor("#0097ED"));
                buttonMap.setColorFilter(Color.parseColor("#656565"));
                buttonHome.setColorFilter(Color.parseColor("#656565"));
            } else if (checkedId == R.id.Karta) {
                MapLayout.setVisibility(View.VISIBLE);
                ZajavkaLayout.setVisibility(View.GONE);
                HomeLayout.setVisibility(View.GONE);
                buttonMap.setColorFilter(Color.parseColor("#0097ED"));
                buttonZayavka.setColorFilter(Color.parseColor("#656565"));
                buttonHome.setColorFilter(Color.parseColor("#656565"));
            } else if (checkedId == R.id.Glavnaja) {
                MapLayout.setVisibility(View.GONE);
                ZajavkaLayout.setVisibility(View.GONE);
                HomeLayout.setVisibility(View.VISIBLE);
                buttonHome.setColorFilter(Color.parseColor("#0097ED"));
                buttonZayavka.setColorFilter(Color.parseColor("#656565"));
                buttonMap.setColorFilter(Color.parseColor("#656565"));
            }
        });


        // Проверка наличия разрешения на доступ к местоположению
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Запрос разрешения у пользователя
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            getLocation();

        }

        // Загрузка информации из Firestore
        loadDetailsFromFirestore();
        webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        db = FirebaseFirestore.getInstance();
        distanceTextView = findViewById(R.id.distanceTextView);
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Этот метод вызывается перед тем, как текст изменится
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Этот метод вызывается при изменении текста
                // Отправляем запрос на поиск местоположений при изменении текста
                searchLocation(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Этот метод вызывается после того, как текст изменится
            }
        });
        // Проверяем разрешение на доступ к геолокации
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Запрашиваем разрешение на доступ к геолокации
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            // Разрешение уже получено, получаем текущее местоположение пользователя
            getLocation();
        }
        // Создаем новый поток для сетевой операции
        new Thread(() -> {

            try {

                // Выводим результат на экран с использованием UI-потока

                button2.setOnClickListener(v -> {

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
                                        adres = mappoint + " Маршрут составляет: " + km;
                                    }
                                } else {

                                }
                            })
                            .addOnFailureListener(e -> {
                                // Обработка ошибки

                            });

                    String message = "Сообщение\n Заявка на Ремонт " + "\n" +
                            "ФИО: " + currentUserFIO + "\n" +
                            "Адрес: " + adres + "\n" +
                            "Ваша организация " + d2.getText().toString() + "\n" +
                            "Дата приобретения " + d3.getText().toString() + "\n" +
                            "№ товарной накладной " + d4.getText().toString() + "\n" +
                            "Модель техники  " + d5.getText().toString() + "\n" +
                            "Серийный номер техники (VIN) " + d6.getText().toString() + "\n" +
                            "Дата ввода в эксплуатацию " + d7.getText().toString() + "\n" +
                            "Кол-во отработанных часов " + d8.getText().toString() + "\n" +
                            "Модель двигателя " + d9.getText().toString() + "\n" +
                            "Серийный номер двигателя " + d10.getText().toString() + "\n" +
                            "Адрес места эксплуатации / места проведения ремонта " + d11.getText().toString() + "\n" +
                            "Описание неисправностей (При наличии) " + d12.getText().toString() + "\n" ;
                    d11.setText(adres);
                    new Thread(() -> sendTO()).start();
                    new Thread(() -> sendMail("avtocentr056@mail.ru", message)).start();
                    // Создание и отображение диалогового окна
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Заявка отправлена. Ожидайте!")
                            .setPositiveButton("OK", (dialog, which) -> {
                                // Очистка текстовых полей

                                d2.setText("");
                                d3.setText("");
                                d4.setText("");
                                d5.setText("");
                                d6.setText("");
                                d7.setText("");
                                d8.setText("");
                                d9.setText("");
                                d10.setText("");
                                d11.setText("");
                                d12.setText("");

                                radioMaintenance.setChecked(false);
                                radioRepair.setChecked(false);
                                layoutRepair.setVisibility(View.GONE);
                                layoutMaintenance.setVisibility(View.GONE);
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                });

                button.setOnClickListener(v -> {
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
                                        adres = mappoint + " Маршрут составляет: " + km;
                                    }
                                } else {

                                }
                            })
                            .addOnFailureListener(e -> {
                                // Обработка ошибки

                            });


                    String message = "Сообщение\n Заявка на Техническое обслуживание " + "\n" +
                            "ФИО: " + currentUserFIO + "\n" +
                            "Ваша организация " + d2.getText().toString() + "\n" +
                            "Дата приобретения " + d3.getText().toString() + "\n" +
                            "№ товарной накладной " + d4.getText().toString() + "\n" +
                            "Модель техники  " + d5.getText().toString() + "\n" +
                            "Серийный номер техники (VIN) " + d6.getText().toString() + "\n" +
                            "Дата ввода в эксплуатацию " + d7.getText().toString() + "\n" +
                            "Кол-во отработанных часов " + d8.getText().toString() + "\n" +
                            "Модель двигателя " + d9.getText().toString() + "\n" +
                            "Серийный номер двигателя " + d10.getText().toString() + "\n" +
                            "Адрес места эксплуатации / места проведения ремонта " + d11.getText().toString() + "\n" +
                            "Описание неисправностей (При наличии) " + d12.getText().toString() + "\n" ;
                    new Thread(() -> sendRemont()).start();
                    new Thread(() -> sendMail("avtocentr056@mail.ru", message)).start();



                    // Создание и отображение диалогового окна
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Заявка отправлена. Ожидайте!")
                            .setPositiveButton("OK", (dialog, which) -> {
                                // Очистка текстовых полей

                                t2.setText("");
                                t3.setText("");
                                t4.setText("");
                                t5.setText("");
                                t6.setText("");
                                t7.setText("");
                                t8.setText("");
                                t9.setText("");
                                t11.setText("");
                                t12.setText("");

                                radioMaintenance.setChecked(false);
                                radioRepair.setChecked(false);
                                layoutRepair.setVisibility(View.GONE);
                                layoutMaintenance.setVisibility(View.GONE);
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                });



            } catch (Exception e) {
                e.printStackTrace();
                // Выводим сообщение об ошибке на экран с использованием UI-потока
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(Osnovnaja.this, "Ошибка при чтении почты", Toast.LENGTH_LONG).show());
            }
        }).start();

    }
    private void sendMail(String recipient,  String body) {
        final String username = "avtocentr056@mail.ru"; // Ваша почта
        final String password = "QkJzjx3JYDzAV9bwSsEp"; // Пароль от почты

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
        SharedPreferences sp = getSharedPreferences("Авторизация", Context.MODE_PRIVATE);
        String currentUserEmail = sp.getString("CurrentUserEmail", "");
        Map<String, Object> zayavkaData = new HashMap<>();
        zayavkaData.put("Ваша организация",                     d2.getText().toString());
        zayavkaData.put("Дата приобретения" ,                       d3.getText().toString());
        zayavkaData.put("№ товарной накладной",                     d4.getText().toString());
        zayavkaData.put("Модель техники",            d5.getText().toString());
        zayavkaData.put("Серийный номер техники (VIN)",    d6.getText().toString());
        zayavkaData.put("Дата ввода в эксплуатацию",                d7.getText().toString());
        zayavkaData.put("Кол-во отработанных часов",                d8.getText().toString());
        zayavkaData.put("Модель двигателя ",                        d9.getText().toString());
        zayavkaData.put("Серийный номер двигателя",                        d10.getText().toString());
        zayavkaData.put("Адрес места эксплуатации / места проведения ремонта",                d11.getText().toString());
        zayavkaData.put("Описание неисправностей (При наличии)",   d12.getText().toString());
        zayavkaData.put("typezayavka",  "Техническое обслуживание");
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
                        Toast.makeText(Osnovnaja.this, "Failed to create document in 'userinfo' collection", Toast.LENGTH_LONG).show();
                        Log.e("zayavka", "Error adding document", e);
                    }
                });
    }
    private void sendTO() {
        db.collection("map")
                .whereEqualTo("user", currentUserEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            // Получение данных из документа
                            String mappoint = documentSnapshot.getString("mappoint");
                            t11 = findViewById(R.id.editText126);
                            t11.setText(mappoint);


                        }
                    } else {

                    }
                })
                .addOnFailureListener(e -> {

                });

        SharedPreferences sp = getSharedPreferences("Авторизация", Context.MODE_PRIVATE);
        String currentUserEmail = sp.getString("CurrentUserEmail", "");
        Map<String, Object> zayavkaData = new HashMap<>();
        zayavkaData.put("Ваша организация",                     t2.getText().toString());
        zayavkaData.put("Дата приобретения" ,                       t3.getText().toString());
        zayavkaData.put("№ товарной накладной",                     t4.getText().toString());
        zayavkaData.put("Модель техники",            t5.getText().toString());
        zayavkaData.put("Серийный номер техники (VIN)",    t6.getText().toString());
        zayavkaData.put("Дата ввода в эксплуатацию",                t7.getText().toString());
        zayavkaData.put("Кол-во отработанных часов",                t8.getText().toString());
        zayavkaData.put("Модель двигателя ",                        t9.getText().toString());
        zayavkaData.put("Серийный номер двигателя",                        t10.getText().toString());
        zayavkaData.put("Адрес места эксплуатации / места проведения ремонта",               t11.getText().toString());
        zayavkaData.put("Описание неисправностей (При наличии)",   t12.getText().toString());
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
                        Toast.makeText(Osnovnaja.this, "Failed to create document in 'userinfo' collection", Toast.LENGTH_LONG).show();
                        Log.e("zayavka", "Error adding document", e);
                    }
                });
    }
    private void getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            Log.d(TAG, "getLocation: Requesting location updates");
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
                // В методе onLocationChanged
                @Override
                public void onLocationChanged(Location location) {
                    Log.d(TAG, "onLocationChanged: Location changed");
                    currentLatitude = location.getLatitude();
                    currentLongitude = location.getLongitude();

                    // Получение полного адреса текущего местоположения
                    String currentAddressUrl = "https://nominatim.openstreetmap.org/reverse?lat=" +
                            currentLatitude + "&lon=" + currentLongitude + "&format=json";

                    JsonObjectRequest currentAddressRequest = new JsonObjectRequest(Request.Method.GET, currentAddressUrl, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        JSONObject addressDetails = response.getJSONObject("address");
                                        // Получение полного адреса
                                        String userAddress = addressDetails.optString("city") + ", " +
                                                addressDetails.optString("road") + " " +
                                                addressDetails.optString("house_number");
                                        // Построение URL для отображения маршрута
                                        double destinationLatitude = 51.74184;
                                        double destinationLongitude = 55.09565;
                                        String apiKey = "1b5c7ff9-e26a-4bc3-8861-1f67387981fd";
                                        String url = "https://graphhopper.com/api/1/route?point=" +
                                                currentLatitude + "%2C" + currentLongitude + "&point=" +
                                                destinationLatitude + "%2C" + destinationLongitude +
                                                "&vehicle=car&locale=ru&key=" + apiKey;
                                        String url2 = "https://www.openstreetmap.org/directions?engine=graphhopper_car&route=" +
                                                currentLatitude + "%2C" + currentLongitude + "%3B" +
                                                destinationLatitude + "%2C" + destinationLongitude;
                                        webView.loadUrl(url2);
                                        // Отправка запроса к GraphHopper API и получение ответа
                                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                                                new Response.Listener<JSONObject>() {
                                                    @Override
                                                    public void onResponse(JSONObject response) {
                                                        // Обработка ответа от GraphHopper API
                                                        processRouteResponse(response, userAddress);
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        // Обработка ошибки
                                                    }
                                                });

                                        // Добавление запроса в очередь запросов
                                        RequestQueue requestQueue = Volley.newRequestQueue(Osnovnaja.this);
                                        requestQueue.add(jsonObjectRequest);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // Обработка ошибки
                                }
                            });
                    RequestQueue requestQueue = Volley.newRequestQueue(Osnovnaja.this);
                    requestQueue.add(currentAddressRequest);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}

                @Override
                public void onProviderEnabled(String provider) {}

                @Override
                public void onProviderDisabled(String provider) {
                    Toast.makeText(Osnovnaja.this, "Please enable GPS", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (SecurityException e) {
            e.printStackTrace();
            Log.e(TAG, "getLocation: SecurityException: " + e.getMessage());
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void processRouteResponse(JSONObject response, String destinationAddress) {
        try {
            // Получение массива сегментов маршрута
            JSONArray paths = response.getJSONArray("paths");
            JSONObject path = paths.getJSONObject(0); // Берем первый сегмент
            Log.d(TAG, "processRouteResponse: Processing route response");
            // Получение геометрии маршрута (список координат)
            String points = path.getString("points");
            List<LatLng> coordinates = parseCoordinates(points);

            // Получение длины маршрута в метрах
            double distanceMeters = path.getDouble("distance");

            // Конвертация метров в километры
            double distanceKilometers = distanceMeters / 1000.0;

            // Отображение расстояния и адреса места назначения в TextView
            DecimalFormat df = new DecimalFormat("#.##");

            String fullAddress = "Расстояние маршрута: " + df.format(distanceKilometers) + " км\n" +
                    "Адрес места назначения: " + destinationAddress;
            distanceTextView.setText(fullAddress);
            SharedPreferences sp = getSharedPreferences("Авторизация", Context.MODE_PRIVATE);
            String currentUserEmail = sp.getString("CurrentUserEmail", "");
            // Проверка наличия документа с данными пользователя
            db.collection("map")
                    .whereEqualTo("user", currentUserEmail)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String documentId = documentSnapshot.getId();
                            // Обновление существующего документа
                            Map<String, Object> mapData = new HashMap<>();
                            mapData.put("mappoint", destinationAddress);
                            mapData.put("km", distanceKilometers);
                            mapData.put("latitude", currentLatitude);
                            mapData.put("longitude", currentLongitude);
                            mapData.put("user", currentUserEmail);
                            db.collection("map").document(documentId).update(mapData)
                                    .addOnFailureListener(e -> Toast.makeText(Osnovnaja.this, "Не удалось обновить данные, попробуйте еще раз", Toast.LENGTH_LONG).show());
                            // Если найден документ, соответствующий текущему пользователю, выходим из цикла
                            break;
                        }
                    });

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "processRouteResponse: JSONException: " + e.getMessage());
        }
        // Планирование обновления геолокации через 10 секунд
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getLocation();
            }
        }, UPDATE_INTERVAL);
    }

    private void searchLocation(String searchText) {
        // Формируем URL для поиска местоположений

        String searchUrl;
        try {
            searchUrl = "https://nominatim.openstreetmap.org/search?q=" + URLEncoder.encode(searchText, "UTF-8") + "&format=json";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            // Ваш обработчик исключения
            searchUrl = "https://nominatim.openstreetmap.org/search?q=" + searchText + "&format=json";
        }
        // Отправляем запрос
        JsonObjectRequest searchRequest = new JsonObjectRequest(Request.Method.GET, searchUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Обработка ответа с результатами поиска
                        processSearchResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Обработка ошибки
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(searchRequest);
    }
    private void processSearchResponse(JSONObject response) {
        try {
            // Получаем массив результатов поиска
            JSONArray results = response.getJSONArray("results");

            // Создаем список для отображения результатов пользователю
            List<String> searchResults = new ArrayList<>();

            // Проходим по всем результатам и добавляем их в список
            for (int i = 0; i < results.length(); i++) {
                JSONObject result = results.getJSONObject(i);
                String displayName = result.optString("display_name");
                searchResults.add(displayName);
            }

            // Отображаем результаты пользователю, например, в выпадающем списке или списке подсказок
            // Например, можно использовать AutoCompleteTextView для автодополнения
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, searchResults);
            autoCompleteTextView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private List<LatLng> parseCoordinates(String points) {
        List<LatLng> coordinates = new ArrayList<>();
        try {
            // Декодируем строку координат в набор LatLng
            List<LatLng> decodedCoordinates = PolyUtil.decode(points);

            // Добавляем все координаты из декодированного списка в итоговый список координат
            coordinates.addAll(decodedCoordinates);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "parseCoordinates: Exception: " + e.getMessage());
        }
        return coordinates;
    }


    // Метод открытия карты


    // Метод загрузки дополнительной информации из Firestore
    private void loadDetailsFromFirestore() {
        db.collection("info")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        // Обработка ошибки
                        return;
                    }

                    for (DocumentChange dc : Objects.requireNonNull(value).getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            DocumentSnapshot document = dc.getDocument();
                            addDetailsToLayout(document);
                        }
                    }
                });
    }

    // Метод добавления информации в макет
    private void addDetailsToLayout(DocumentSnapshot document) {
        // Создание нового блока для каждого документа
        // Загрузка шрифта из файла assets

        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(50, 25, 50, 25);
        cardView.setLayoutParams(cardParams);


        // Устанавливаем ширину обводки
        cardView.setBackground(ContextCompat.getDrawable(this, R.drawable.progress_circular));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        LinearLayout layout = new LinearLayout(this);
        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView dateTextView = new TextView(this);
        dateTextView.setLayoutParams(params);
        dateTextView.setText(formatDate(document.getTimestamp("timestamp").toDate()));
        dateTextView.setPadding(50, 25, 50, 0);
        dateTextView.setTypeface(Typeface.SANS_SERIF);
        layout.addView(dateTextView);

        TextView titleTextView = new TextView(this);
        titleTextView.setLayoutParams(params);
        titleTextView.setText(document.getString("bigtext"));
        titleTextView.setTextColor(getResources().getColor(R.color.black));
        titleTextView.setTypeface(Typeface.DEFAULT_BOLD);
        titleTextView.setPadding(50, 15, 50, 25);
        titleTextView.setTextSize(16);
        layout.addView(titleTextView);

        TextView textTextView = new TextView(this);
        textTextView.setLayoutParams(params);
        textTextView.setText(document.getString("text"));
        textTextView.setTextColor(getResources().getColor(R.color.black));
        textTextView.setPadding(50, 25, 50, 0);
        layout.addView(textTextView);
        textTextView.setTypeface(Typeface.SANS_SERIF);
        cardView.addView(layout);
        detailsContainer.addView(cardView);
    }

    // Метод форматирования даты
    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("ru", "RU"));
        return sdf.format(date);
    }
}
