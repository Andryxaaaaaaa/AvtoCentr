package com.example.avtocentr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class SignFIO extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_fio); // Установка макета
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Инициализация элементов интерфейса после установки макета
        EditText editTextFamiliya = findViewById(R.id.editTextFamiliya);
        EditText editTextImya = findViewById(R.id.editTextImya);
        EditText editTextOtchestvo = findViewById(R.id.editTextOtchestvo);
        EditText editTextNomer = findViewById(R.id.editTextNomer);
        Button saveButton = findViewById(R.id.button);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SharedPreferences sp = getSharedPreferences("Регистрация", Context.MODE_PRIVATE);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentUserEmail = sp.getString("CurrentUserEmail", ""); // Получение email пользователя из SharedPreferences
                if (editTextFamiliya.getText().toString().isEmpty() ||
                        editTextImya.getText().toString().isEmpty() ||
                        editTextOtchestvo.getText().toString().isEmpty() ||
                        editTextNomer.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Проверьте поля!", Toast.LENGTH_LONG).show();
                } else {
                    // Check if the user exists
                    db.collection("userinfo")
                            .whereEqualTo("email", currentUserEmail)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    // Проверка наличия документов в коллекции
                                    if (!queryDocumentSnapshots.isEmpty()) {
                                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                            String documentId = documentSnapshot.getId();
                                            // Update the existing document
                                            Map<String, Object> userData = new HashMap<>();
                                            userData.put("familia", editTextFamiliya.getText().toString());
                                            userData.put("imya", editTextImya.getText().toString());
                                            userData.put("otchestvo", editTextOtchestvo.getText().toString());
                                            userData.put("number", editTextNomer.getText().toString());
                                            // Use the current user's email to update the document
                                            userData.put("email", currentUserEmail);
                                            // Обновление данных пользователя в базе данных
                                            db.collection("userinfo").document(documentId).update(userData)
                                                    .addOnSuccessListener(aVoid -> {
                                                        Toast.makeText(SignFIO.this, "Данные успешно обновлены", Toast.LENGTH_LONG).show();
                                                        startActivity(new Intent(SignFIO.this, MainActivity.class));
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Toast.makeText(SignFIO.this, "Не удалось обновить данные, попробуйте еще раз", Toast.LENGTH_LONG).show();
                                                    });
                                            // Если найден документ, соответствующий текущему пользователю, выходим из цикла
                                            break;
                                        }
                                    } else {
                                        // Если документы не найдены, выведите сообщение об ошибке
                                        Toast.makeText(SignFIO.this, "Нет данных для обновления", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                }
            }
        });
    }
}
