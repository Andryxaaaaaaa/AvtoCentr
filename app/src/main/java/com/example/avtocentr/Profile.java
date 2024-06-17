package com.example.avtocentr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Profile extends AppCompatActivity {
    // Ссылка на TextView, в который будут загружены данные
    private TextView textViewAdres;

    private TextView textViewZayavkiCount;
    private int zayavkiCount = 0;
    private TextView textViewFIO;
    private TextView textViewNomer;
    public String currentUserEmail;

    // Ссылка на SharedPreferences
    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        textViewZayavkiCount = findViewById(R.id.textViewZayavki);
        ImageButton backButton = findViewById(R.id.buttonНазад);

        TextView textViewLogout = findViewById(R.id.textViewВыйтиИзАккаунта);
        TextView textViewNomer = findViewById(R.id.textViewNomer);

        textViewLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Очищаем SharedPreferences
                SharedPreferences sp = getSharedPreferences("Авторизация", Context.MODE_PRIVATE);
                sp.edit().clear().apply();

                // Переходим на активити "Вход"
                Intent intent = new Intent(Profile.this, Login.class);
                startActivity(intent);
                finish();

            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Profile.this, Osnovnaya.class));
                finish();
            }

        });
        // Связывание TextView из макета с переменной
        textViewFIO = findViewById(R.id.textViewFIO);

        // Инициализация SharedPreferences
        // Инициализация SharedPreferences
        sp = getSharedPreferences("Авторизация", Context.MODE_PRIVATE);

// Получение email текущего пользователя из SharedPreferences
        currentUserEmail = sp.getString("CurrentUserEmail", "");

        //Связывание TextView из макета с переменной
        textViewAdres = findViewById(R.id.textViewAdres);
        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("zayavka")
                .whereEqualTo("user", currentUserEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    zayavkiCount = queryDocumentSnapshots.size(); // Получение количества заявок текущего пользователя
                    textViewZayavkiCount.setText("Ваши заявки: " + zayavkiCount);
                })
                .addOnFailureListener(e -> {
                    // Обработка ошибки при получении данных
                    Toast.makeText(Profile.this, "Не удалось загрузить заявки: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });


    // Метод для открытия активити с данными о заявках

        // Получение данных текущего пользователя из Firestore и установка их в TextView
        db.collection("map")
                .whereEqualTo("user", currentUserEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            // Получение данных из документа
                            String mappoint = documentSnapshot.getString("mappoint");


                            // Установка данных в TextView
                            textViewAdres.setText(mappoint);
                        }
                    } else {
                        textViewAdres.setText("Данные не найдены");
                    }
                })
                .addOnFailureListener(e -> {
                    // Обработка ошибки, если загрузка данных не удалась
                    textViewAdres.setText("Ошибка загрузки данных");
                    Toast.makeText(Profile.this, "Ошибка загрузки данных: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
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
                            String currentUserFIO = familia + " " + imya + " " + otchestvo;


                            // Установка ФИО пользователя в TextView
                            textViewFIO.setText(currentUserFIO);
                            textViewNomer.setText(number);
                        }
                    } else {
                        textViewFIO.setText("ФИО не найдено");
                    }
                })
                .addOnFailureListener(e -> {
                    // Обработка ошибки
                    textViewFIO.setText("Ошибка загрузки ФИО: " + e.getMessage());
                });
    }
    public void showZayavkiDetails(View view) {
        // Логика для открытия активити с данными о заявках
        Intent intent = new Intent(Profile.this, ZayavkiListActivity.class);
        intent.putExtra("userEmail", currentUserEmail); // Передача email текущего пользователя
        startActivity(intent);
    }
}