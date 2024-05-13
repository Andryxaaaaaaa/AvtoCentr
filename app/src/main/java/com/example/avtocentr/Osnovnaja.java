package com.example.avtocentr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class Osnovnaja extends AppCompatActivity {

    // Код запроса для обратного вызова
    private static final int REQUEST_PICK_LOCATION = 1;
    // Код запроса для разрешения доступа к местоположению
    static final int REQUEST_LOCATION_PERMISSION = 2;

    // Ссылка на TextView
    private TextView distanceTextView;

    // Firestore объект
    private FirebaseFirestore db;

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
        EditText editTextLocation = findViewById(R.id.editTextLocation);
        EditText editTextDistance = findViewById(R.id.editTextDistance);
        Button saveButton = findViewById(R.id.button6);
        RadioButton radioButtonHome = findViewById(R.id.Glavnaja);
        RadioButton radioButtonZayavka = findViewById(R.id.Zajavka);
        RadioButton radioButtonMap = findViewById(R.id.Karta);
        TextView textViewCurrentUserEmail = findViewById(R.id.textViewCurrentUserEmail);
        ImageButton imagebutton = findViewById(R.id.buttonimage);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        detailsContainer = findViewById(R.id.detailsContainer);

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

        // Обработчики клика по кнопкам навигации
        buttonHome.setOnClickListener(v -> {
            radioButtonHome.setChecked(true);
            radioButtonZayavka.setChecked(false);
            radioButtonMap.setChecked(false);
        });

        buttonZayavka.setOnClickListener(v -> {
            radioButtonHome.setChecked(false);
            radioButtonZayavka.setChecked(true);
            radioButtonMap.setChecked(false);
        });

        buttonMap.setOnClickListener(v -> {
            radioButtonHome.setChecked(false);
            radioButtonZayavka.setChecked(false);
            radioButtonMap.setChecked(true);
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
                            String currentUserFIO = familia + " " + imya + " " + otchestvo;

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
                ZajavkaLayout.setVisibility(View.VISIBLE);
                MapLayout.setVisibility(View.GONE);
                HomeLayout.setVisibility(View.GONE);
            } else if (checkedId == R.id.Karta) {
                MapLayout.setVisibility(View.VISIBLE);
                ZajavkaLayout.setVisibility(View.GONE);
                HomeLayout.setVisibility(View.GONE);
            } else if (checkedId == R.id.Glavnaja) {
                MapLayout.setVisibility(View.GONE);
                ZajavkaLayout.setVisibility(View.GONE);
                HomeLayout.setVisibility(View.VISIBLE);
            }
        });

        // Обработчик нажатия кнопки сохранения
        saveButton.setOnClickListener(view -> {
            if (editTextLocation.getText().toString().isEmpty() || editTextDistance.getText().toString().isEmpty()) {
                Toast.makeText(getApplicationContext(), "Проверьте поля!", Toast.LENGTH_LONG).show();
            } else {
                // Проверка наличия документа с данными пользователя
                db.collection("map")
                        .whereEqualTo("user", currentUserEmail)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                String documentId = documentSnapshot.getId();
                                // Обновление существующего документа
                                Map<String, Object> mapData = new HashMap<>();
                                mapData.put("mappoint", editTextLocation.getText().toString());
                                mapData.put("km", editTextDistance.getText().toString());
                                mapData.put("user", currentUserEmail);
                                db.collection("map").document(documentId).update(mapData)
                                        .addOnSuccessListener(aVoid -> Toast.makeText(Osnovnaja.this, "Данные успешно обновлены", Toast.LENGTH_LONG).show())
                                        .addOnFailureListener(e -> Toast.makeText(Osnovnaja.this, "Не удалось обновить данные, попробуйте еще раз", Toast.LENGTH_LONG).show());
                                // Если найден документ, соответствующий текущему пользователю, выходим из цикла
                                break;
                            }
                        });
            }
        });

        // Проверка наличия разрешения на доступ к местоположению
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Запрос разрешения у пользователя
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            // Открытие карты, если разрешение уже предоставлено
            openMap();
        }

        // Загрузка информации из Firestore
        loadDetailsFromFirestore();
    }

    // Метод открытия карты
    private void openMap() {
        // TODO: Реализация открытия карты
    }

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
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 50);
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
        layout.addView(dateTextView);

        TextView titleTextView = new TextView(this);
        titleTextView.setLayoutParams(params);
        titleTextView.setText(document.getString("bigtext"));
        titleTextView.setTextColor(getResources().getColor(R.color.black));
        titleTextView.setTextSize(18);
        layout.addView(titleTextView);

        TextView textTextView = new TextView(this);
        textTextView.setLayoutParams(params);
        textTextView.setText(document.getString("text"));
        textTextView.setTextColor(getResources().getColor(R.color.black));
        layout.addView(textTextView);

        cardView.addView(layout);
        detailsContainer.addView(cardView);
    }

    // Метод форматирования даты
    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("ru", "RU"));
        return sdf.format(date);
    }
}
