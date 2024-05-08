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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class singupactivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_singupactivity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        SharedPreferences sp = getSharedPreferences("Регистрация", Context.MODE_PRIVATE);
        TextView email = findViewById(R.id.editTextLogin);
        TextView password = findViewById(R.id.editTextPassword);
        Button button = findViewById(R.id.button);
        Button button2 = findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(singupactivity.this, Osnovnaja.class ));
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(new Intent(singupactivity.this, MainActivity.class ));

            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Проверьте поле email!", Toast.LENGTH_LONG).show();
                }
                else if (password.getText().toString().isEmpty() || password.getText().toString().length() < 4) {
                    Toast.makeText(getApplicationContext(), "Пароль должен быть больше 3 символов", Toast.LENGTH_LONG).show();
                }
                else {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    // Create a new user with a first and last name
                    Map<String, Object> user = new HashMap<>();
                    user.put("email", email.getText().toString());
                    user.put("password", password.getText().toString());


// Проверка наличия пользователя с таким email
                    db.collection("users")
                            .whereEqualTo("email", email.getText().toString())
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    if (!queryDocumentSnapshots.isEmpty()) {
                                        // Если найден пользователь с таким email, выводим сообщение об ошибке
                                        Toast.makeText(singupactivity.this, "Пользователь с таким email уже существует", Toast.LENGTH_LONG).show();
                                    } else {
                                        sp.edit().putString("Email", email.getText().toString()).commit();

                                        // Создаем новый документ в коллекции "map" с данными пользователя и пустыми значениями "km" и "mappoint"
                                        Map<String, Object> mapData = new HashMap<>();
                                        mapData.put("user", email.getText().toString()); // Используем ID документа пользователя в качестве идентификатора пользователя
                                        mapData.put("km", ""); // Пустое значение для km
                                        mapData.put("mappoint", ""); // Пустое значение для mappoint

                                        db.collection("map")
                                                .add(mapData)
                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                    @Override
                                                    public void onSuccess(DocumentReference documentReference) {
                                                        // После успешного создания документа в коллекции "map" переходим на главный экран
                                                        startActivity(new Intent(singupactivity.this, Osnovnaja.class));
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(singupactivity.this, "Не удалось создать документ в коллекции 'map'", Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                    }
                                }

                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Обработка ошибки запроса к базе данных
                                    Toast.makeText(singupactivity.this, "Ошибка при проверке email", Toast.LENGTH_LONG).show();
                                }
                            });
// Add a new document with a generated ID

                }
            }
        });
    }
}