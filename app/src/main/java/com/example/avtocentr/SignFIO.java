package com.example.avtocentr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
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
import java.util.regex.Pattern;

public class SignFIO extends AppCompatActivity {

    // Шаблоны для проверки ввода
    private static final Pattern NAME_PATTERN = Pattern.compile("^[А-Яа-яЁё]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^7\\d{10}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_user); // Установка макета
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
        editTextNomer.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
        Button saveButton = findViewById(R.id.button);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SharedPreferences sp = getSharedPreferences("Регистрация", Context.MODE_PRIVATE);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentUserEmail = sp.getString("CurrentUserEmail", ""); // Получение email пользователя из SharedPreferences
                String familiya = capitalizeFirstLetter(editTextFamiliya.getText().toString());
                String imya = capitalizeFirstLetter(editTextImya.getText().toString());
                String otchestvo = capitalizeFirstLetter(editTextOtchestvo.getText().toString());
                String nomer = editTextNomer.getText().toString();

                if (familiya.isEmpty() || imya.isEmpty() || otchestvo.isEmpty() || nomer.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Проверьте поля!", Toast.LENGTH_LONG).show();
                } else if (familiya.length() < 2 || imya.length() < 2 || otchestvo.length() < 2) {
                    Toast.makeText(getApplicationContext(), "Фамилия, Имя и Отчество должны содержать минимум 2 буквы", Toast.LENGTH_LONG).show();
                } else if (!NAME_PATTERN.matcher(familiya).matches() || !NAME_PATTERN.matcher(imya).matches() || !NAME_PATTERN.matcher(otchestvo).matches()) {
                    Toast.makeText(getApplicationContext(), "Фамилия, Имя и Отчество должны содержать только кириллицу", Toast.LENGTH_LONG).show();
                } else if (!PHONE_PATTERN.matcher(nomer).matches()) {
                    Toast.makeText(getApplicationContext(), "Введите действительный номер телефона, начинающийся с 7", Toast.LENGTH_LONG).show();
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
                                            userData.put("familia", familiya);
                                            userData.put("imya", imya);
                                            userData.put("otchestvo", otchestvo);
                                            userData.put("number", nomer);
                                            // Use the current user's email to update the document
                                            userData.put("email", currentUserEmail);
                                            // Обновление данных пользователя в базе данных
                                            db.collection("userinfo").document(documentId).update(userData)
                                                    .addOnSuccessListener(aVoid -> {
                                                        Toast.makeText(SignFIO.this, "Данные успешно обновлены", Toast.LENGTH_LONG).show();
                                                        startActivity(new Intent(SignFIO.this, Login.class));
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

        // Метод для преобразования первой буквы в верхний регистр
    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }
}
