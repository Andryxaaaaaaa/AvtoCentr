package com.example.avtocentr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Glavnaya extends AppCompatActivity {

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_glavnaya);

        SharedPreferences sp = (SharedPreferences) getSharedPreferences("PC", Context.MODE_PRIVATE);
        ((SharedPreferences.Editor) sp.edit()).putString("TY","9").commit();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });



        Button button = findViewById(R.id.button);
        Button button2 = findViewById(R.id.button2);
        Button button3 = findViewById(R.id.button3);
        Button button4 = findViewById(R.id.button4);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Очищаем SharedPreferences

                // Переходим на активити "Вход"
                Intent intent = new Intent(Glavnaya.this, MainActivity.class);

                startActivity(intent);
                // Показываем уведомление об успешном выходе из аккаунта
                Toast.makeText(Glavnaya.this, "Вы успешно вышли из аккаунта", Toast.LENGTH_LONG).show();
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Glavnaya.this, map.class);
                startActivity(intent);

            }
        });
        button4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Glavnaya.this, PDF.class);
                startActivity(intent);

            }
        });
        button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(new Intent(Glavnaya.this, singupactivity.class ));

            }
        });

        String currentUserEmail = sp.getString("email", "");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
// Получаем информацию о пользователе из Firestore
        db.collection("users")
                .whereEqualTo("email", currentUserEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            // Получаем данные пользователя
                            String email = document.getString("email");
                            // Используем полученные данные

                        } else {
                            // Обработка ошибки или ситуации, когда не найден пользователь
                        }
                    }
                });


    }
}