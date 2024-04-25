package com.example.avtocentr;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class map extends FragmentActivity  {
    // Код запроса для обратного вызова
    private static final int REQUEST_PICK_LOCATION = 1;
    // Код запроса для разрешения доступа к местоположению
    private static final int REQUEST_LOCATION_PERMISSION = 2;

    // Координаты стартовой точки
    private double startLatitude = 51.742124445022874;
    private double startLongitude = 55.09555714401241;

    // Ссылка на TextView
    private TextView distanceTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        SharedPreferences sp = getSharedPreferences("TY", Context.MODE_PRIVATE);

        // Get references to EditText and Button
        EditText editTextLocation = findViewById(R.id.editTextLocation);
        EditText editTextDistance = findViewById(R.id.editTextDistance);
        Button saveButton = findViewById(R.id.button6);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentUserEmail = sp.getString("email", ""); // Получение текущей электронной почты пользователя из SharedPreferences
                if (editTextLocation.getText().toString().isEmpty() || editTextDistance.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Проверьте поля!", Toast.LENGTH_LONG).show();
                } else {
                    // Check if the user exists
                    db.collection("map").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                // If the document exists, update it
                                DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
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
                                        Toast.makeText(map.this, "Данные успешно обновлены", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(map.this, Glavnaya.class));
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(map.this, "Не удалось обновить данные, попробуйте еще раз", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                // If the document doesn't exist, create a new one
                                Map<String, Object> mapData = new HashMap<>();
                                mapData.put("user", currentUserEmail); // Use UID as user identifier
                                mapData.put("mappoint", editTextLocation.getText().toString());
                                mapData.put("km", editTextDistance.getText().toString());
                                // Add the data to Firestore
                                db.collection("map").add(mapData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Toast.makeText(map.this, "Данные успешно сохранены", Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(map.this, Glavnaya.class));
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(map.this, "Не удалось сохранить данные, попробуйте еще раз", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });



        // Проверяем наличие разрешения на доступ к местоположению
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Если разрешение не было предоставлено, запрашиваем его у пользователя
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            // Если разрешение уже было предоставлено, открываем карту
            openMap();
        }
    }

    private void openMap() {
        // Получаем местоположение пользователя, если разрешение на доступ к местоположению было предоставлено
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
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
                Uri mapUri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=" +
                        startLatitude + "," + startLongitude + "&destination=" + endLatitude + "," + endLongitude);

                // Создание интента для открытия карты
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);

                // Установка флага для обязательного использования приложения Google Maps
                mapIntent.setPackage("com.google.android.apps.maps");

                // Добавление флага для закрытия всех активностей, связанных с картой, после ее открытия
                mapIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                // Проверка наличия устройства для обработки интента
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    // Запуск активности для открытия карты
                    startActivity(mapIntent);
                } else {
                    // Обработка случая, когда не найдено приложение для открытия карты
                    Toast.makeText(this, "Приложение для карт не найдено", Toast.LENGTH_SHORT).show();
                }

                // Вывод второй точки в logcat
                Log.d("SecondPoint", "Latitude: " + endLatitude + ", Longitude: " + endLongitude);
            } else {
                // Обработка случая, когда местоположение пользователя не доступно
                Toast.makeText(this, "Местоположение пользователя не доступно", Toast.LENGTH_SHORT).show();
            }
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
    }
}
