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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;


public class Osnovnaja extends AppCompatActivity {

    // Код запроса для обратного вызова
    private static final int REQUEST_PICK_LOCATION = 1;
    // Код запроса для разрешения доступа к местоположению
    static final int REQUEST_LOCATION_PERMISSION = 2;

    // Координаты стартовой точки
    private double startLatitude = 51.742124445022874;
    private double startLongitude = 55.09555714401241;

    // Ссылка на TextView
    private TextView distanceTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_osnovnaja);
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        RadioGroup radioGroup2 = findViewById(R.id.radioGroup2);
        LinearLayout layoutMaintenance = findViewById(R.id.layoutMaintenance);
        LinearLayout ZajavkaLayout = findViewById(R.id.ZajavkaLayout);
        LinearLayout MapLayout = findViewById(R.id.MapLayout);
        ImageButton buttonHome = findViewById(R.id.buttonHome);
        ImageButton buttonZayavka = findViewById(R.id.buttonZayavka);
        ImageButton buttonMap = findViewById(R.id.buttonMap);
        // Получаем ссылку на ImageView
         // Устанавливаем цвет фильтра
        LinearLayout layoutRepair = findViewById(R.id.layoutRepair);
        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SharedPreferences sp = getSharedPreferences("Авторизация", Context.MODE_PRIVATE);
        ((SharedPreferences.Editor) sp.edit()).putString("Авторизация","9").commit();
        // Get references to EditText and Button
        EditText editTextLocation = findViewById(R.id.editTextLocation);
        EditText editTextDistance = findViewById(R.id.editTextDistance);
        Button saveButton = findViewById(R.id.button6);
        String currentUserEmail = sp.getString("CurrentUserEmail", "");
        RadioButton radioButtonHome = findViewById(R.id.Glavnaja);
        RadioButton radioButtonZayavka = findViewById(R.id.Zajavka);
        RadioButton radioButtonMap = findViewById(R.id.Karta);


// Получаем информацию о пользователе из Firestore
        // Получаем информацию о пользователе из Firestore
        // Получаем информацию о пользователе из Firestore
        TextView textViewCurrentUserEmail = findViewById(R.id.textViewCurrentUserEmail);
        ImageButton imagebutton = findViewById(R.id.buttonimage);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        textViewCurrentUserEmail.setVisibility(View.GONE);
        textViewCurrentUserEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // Переходим на активити "Вход"
                Intent intent = new Intent(Osnovnaja.this, Profile.class);
                startActivity(intent);

            }
        });
        imagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Osnovnaja.this, Profile.class);
                startActivity(intent);
            }
        });
        buttonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButtonHome.setChecked(true);
                radioButtonZayavka.setChecked(false);
                radioButtonMap.setChecked(false);
            }
        });

        buttonZayavka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButtonHome.setChecked(false);
                radioButtonZayavka.setChecked(true);
                radioButtonMap.setChecked(false);
            }
        });

        buttonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioButtonHome.setChecked(false);
                radioButtonZayavka.setChecked(false);
                radioButtonMap.setChecked(true);
            }
        });
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
                            // Устанавливаем email пользователя в TextView


                        } else {
                            // Обработка ошибки или ситуации, когда не найден пользователь
                        }
                    }
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
                            textViewCurrentUserEmail.setText(imya);
                            progressBar.setVisibility(View.GONE);
                            textViewCurrentUserEmail.setVisibility(View.VISIBLE); //

                        }
                    } else {
                        textViewCurrentUserEmail.setText("ФИО не найдено");
                    }
                })
                .addOnFailureListener(e -> {
                    // Обработка ошибки
                    textViewCurrentUserEmail.setText("Ошибка загрузки ФИО: " + e.getMessage());
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
        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.Zajavka) {
                    // Показать раздел Заявки,
                    ZajavkaLayout.setVisibility(View.VISIBLE);
                    MapLayout.setVisibility(View.GONE);


                } else if (checkedId == R.id.Karta) {
                    MapLayout.setVisibility(View.VISIBLE);
                    ZajavkaLayout.setVisibility(View.GONE);

                }
                else if (checkedId == R.id.Glavnaja) {


                }
            }
        });



        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentUserEmail = sp.getString("CurrentUserEmail", ""); // Получение email пользователя из SharedPreferences
                if (editTextLocation.getText().toString().isEmpty() || editTextDistance.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Проверьте поля!", Toast.LENGTH_LONG).show();
                } else {
                    // Check if the user exists
                    db.collection("map")
                            .whereEqualTo("user", currentUserEmail)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                        String documentId = documentSnapshot.getId();
                                        // Update the existing document
                                        Map<String, Object> mapData = new HashMap<>();
                                        mapData.put("mappoint", editTextLocation.getText().toString());
                                        mapData.put("km", editTextDistance.getText().toString());
                                        // Use the current user's email to update the document
                                        mapData.put("user", currentUserEmail);
                                        db.collection("map").document(documentId).update(mapData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(Osnovnaja.this, "Данные успешно обновлены", Toast.LENGTH_LONG).show();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(Osnovnaja.this, "Не удалось обновить данные, попробуйте еще раз", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                        // Если найден документ, соответствующий текущему пользователю, выходим из цикла
                                        break;
                                    }
                                }
                            });
                }
            }
        });



        // Проверяем наличие разрешения на доступ к местоположению
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Если разрешение не было предоставлено, запрашиваем его у пользователя
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            // Если разрешение уже было предоставлено, открываем карту
            openMap();
        }
    }

    private void openMap() {
        // Получаем местоположение пользователя, если разрешение на доступ к местоположению было предоставлено
        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Получаем доступ к провайдеру местоположения
            // В реальном приложении следует также проверить наличие провайдера и обработать ситуацию, когда местоположение не доступно
            Location userLocation = getCurrentLocation();

            if (userLocation != null) {
                // Если местоположение пользователя доступно, получаем его координаты
                double endLatitude = userLocation.getLatitude();
                double endLongitude = userLocation.getLongitude();

                // Расчет расстояния между точками
                double distance = calculateDistance(startLatitude, startLongitude, endLatitude, endLongitude);

                // Создание URI для запроса открытия карты с указанием стартовой и конечной точек маршрута
                //Uri mapUri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=" +
                        //startLatitude + "," + startLongitude + "&destination=" + endLatitude + "," + endLongitude);

                // Создание интента для открытия карты
                //Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);

                // Установка флага для обязательного использования приложения Google Maps
                //mapIntent.setPackage("com.google.android.apps.maps");

                // Добавление флага для закрытия всех активностей, связанных с картой, после ее открытия
                //mapIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                // Проверка наличия устройства для обработки интента
               // if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    // Запуск активности для открытия карты
                   // startActivity(mapIntent);
               // } else {
                    // Обработка случая, когда не найдено приложение для открытия карты
                    //Toast.makeText(this, "Приложение для карт не найдено", Toast.LENGTH_SHORT).show();
                }

                // Вывод второй точки в logcat
               // Log.d("SecondPoint", "Latitude: " + endLatitude + ", Longitude: " + endLongitude);
           // } else {
                // Обработка случая, когда местоположение пользователя не доступно
             //   Toast.makeText(this, "Местоположение пользователя не доступно", Toast.LENGTH_SHORT).show();
            }
        }


    // Получение текущего местоположения пользователя
    private Location getCurrentLocation() {
        // Здесь следует использовать ваш механизм получения текущего местоположения пользователя
        // Например, с использованием Fused Location Provider API или LocationManager
        // В данном примере возвращается просто случайное местоположение
        Location location = new Location("dummyProvider");
        location.setLatitude(startLatitude);
        location.setLongitude(startLongitude);
        return location;
    }

    // Обработка результата запроса разрешения на доступ к местоположению
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Если разрешение было предоставлено, открываем карту
                openMap();
            } else {
                // Если разрешение не было предоставлено, вы можете обработать это событие здесь
                // Например, показать диалог с объяснением, почему доступ к местоположению важен для вашего приложения
                // или предложить пользователю предоставить разрешение повторно в настройках приложения
                Toast.makeText(this, "Доступ к местоположению не предоставлен", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Метод для расчета расстояния между двумя точками по широте и долготе
    private double calculateDistance(double startLatitude, double startLongitude, double endLatitude, double endLongitude) {
        // Радиус Земли в километрах
        final double RADIUS_EARTH = 6371;

        // Преобразование градусов в радианы
        double startLatRadians = Math.toRadians(startLatitude);
        double endLatRadians = Math.toRadians(endLatitude);
        double deltaLatRadians = Math.toRadians(endLatitude - startLatitude);
        double deltaLonRadians = Math.toRadians(endLongitude - startLongitude);

        // Расчет гаверсинуса расстояния
        double a = Math.sin(deltaLatRadians / 2) * Math.sin(deltaLatRadians / 2) +
                Math.cos(startLatRadians) * Math.cos(endLatRadians) *
                        Math.sin(deltaLonRadians / 2) * Math.sin(deltaLonRadians / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // Расчет расстояния
        double distance = RADIUS_EARTH * c;

        return distance;
    }*/
}
}




