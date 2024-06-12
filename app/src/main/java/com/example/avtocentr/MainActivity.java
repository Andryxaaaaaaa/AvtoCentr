package com.example.avtocentr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


public class MainActivity extends AppCompatActivity {
    private boolean isPasswordVisible = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        EditText passwordEditText = findViewById(R.id.editTextPassword);
        ImageView eyeIcon = findViewById(R.id.eyeIcon);
        TextView forgotPassword = findViewById(R.id.forgotPassword);
        eyeIcon.setOnClickListener(v -> {
            if (isPasswordVisible) {
                // Скрыть пароль
                passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                eyeIcon.setImageResource(R.drawable.ic_eye); // замените на вашу иконку закрытого глаза
            } else {
                // Показать пароль
                passwordEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                eyeIcon.setImageResource(R.drawable.ic_eye_off); // замените на вашу иконку открытого глаза
            }
            isPasswordVisible = !isPasswordVisible;
            passwordEditText.setSelection(passwordEditText.length()); // чтобы курсор оставался в конце текста
        });
        SharedPreferences sp = getSharedPreferences("Авторизация", Context.MODE_PRIVATE);
        // Проверяем, вошел ли пользователь ранее
        if (sp.contains("CurrentUserEmail")) {
            // Если пользователь уже вошел, перенаправляем его на экран Glavnaya
            startActivity(new Intent(MainActivity.this, Osnovnaja.class));
            finish(); // Закрываем текущую активити, чтобы пользователь не мог вернуться назад
        }
        // Находим кнопку по ее идентификатору
        Button button = findViewById(R.id.button);
        Button button2 = findViewById(R.id.button2);
        // Устанавливаем слушатель кликов для кнопки
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
         TextView email = findViewById(R.id.editTextLogin);
         TextView password = findViewById(R.id.editTextPassword);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, singupactivity.class));
            }
        });
        // Обработчик клика для "Забыли пароль?"

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, PasswordResetActivity.class));
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = email.getText().toString();
                String userPassword = password.getText().toString();

                // Проверяем, являются ли введенные email и пароль администраторскими
                if (userEmail.equals("admin") && userPassword.equals("admin")) {
                    // Перенаправляем на главный экран
                    startActivity(new Intent(MainActivity.this, Glavnaya.class));
                    finish(); // Закрываем текущую активити, чтобы пользователь не мог вернуться назад
                    return; // Завершаем выполнение метода, чтобы не продолжать проверку в Firestore
                }

                // Если введены не администраторские данные, выполняем проверку в Firestore
                db.collection("users")
                        .whereEqualTo("email", userEmail)
                        .whereEqualTo("password", userPassword)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (!task.getResult().isEmpty()) {
                                        // Пользователь найден
                                        // Сохраняем состояние входа пользователя в SharedPreferences
                                        sp.edit().putString("CurrentUserEmail", userEmail).apply();
                                        // Перенаправляем на главный экран
                                        startActivity(new Intent(MainActivity.this, Osnovnaja.class));
                                        finish(); // Закрываем текущую активити, чтобы пользователь не мог вернуться назад
                                    } else {
                                        // Пользователь не найден
                                        Toast.makeText(MainActivity.this, "Неверный email или пароль", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    // Ошибка при получении данных из Firestore
                                    Toast.makeText(MainActivity.this, "Ошибка при получении данных", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });


    }
}