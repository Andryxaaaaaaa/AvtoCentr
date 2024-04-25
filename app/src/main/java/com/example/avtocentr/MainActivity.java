package com.example.avtocentr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        SharedPreferences sp = getSharedPreferences("TY", Context.MODE_PRIVATE);
        // Проверяем, вошел ли пользователь ранее
        if (sp.contains("CurrentUserEmail")) {
            // Если пользователь уже вошел, перенаправляем его на экран Glavnaya
            startActivity(new Intent(MainActivity.this, Glavnaya.class));
            finish(); // Закрываем текущую активити, чтобы пользователь не мог вернуться назад
        }
        // Находим кнопку по ее идентификатору
        Button button = findViewById(R.id.button);
        // Устанавливаем слушатель кликов для кнопки
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
         TextView email = findViewById(R.id.editTextLogin);
         TextView password = findViewById(R.id.editTextPassword);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("users")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String userEmail = document.getString("email");
                                        String userPassword = document.getString("password");
                                        if (userEmail.equals(email.getText().toString()) && userPassword.equals(password.getText().toString())) {
                                            // Сохраняем состояние входа пользователя в SharedPreferences
                                            sp.edit().putString("CurrentUserEmail", userEmail).apply();
                                            // Переходим на активити "Главная"
                                            Intent intent = new Intent(MainActivity.this, Glavnaya.class);

                                            startActivity(intent);
                                            return; // Выход из цикла, если найдены правильные учетные данные
                                        }
                                    }
                                    // Если не найдены правильные учетные данные
                                    Toast.makeText(MainActivity.this, "Неверный email или пароль", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "Ошибка при получении данных", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });


    }
}