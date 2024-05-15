package com.example.avtocentr;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class map extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int UPDATE_INTERVAL = 600000; // 10 seconds
    private static final String TAG = "MapActivity";
    private WebView webView;
    private TextView distanceTextView;
    private AutoCompleteTextView autoCompleteTextView;
    private FirebaseFirestore db;
    private Handler handler = new Handler();
    double currentLatitude;
    double currentLongitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Log.d(TAG, "onCreate: Activity started");
        webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        db = FirebaseFirestore.getInstance();
        webView = findViewById(R.id.webView);
        distanceTextView = findViewById(R.id.distanceTextView);
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Этот метод вызывается перед тем, как текст изменится
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Этот метод вызывается при изменении текста
                // Отправляем запрос на поиск местоположений при изменении текста
                searchLocation(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Этот метод вызывается после того, как текст изменится
            }
        });
        // Проверяем разрешение на доступ к геолокации
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Запрашиваем разрешение на доступ к геолокации
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            // Разрешение уже получено, получаем текущее местоположение пользователя
            getLocation();
        }
    }

    private void getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {
            Log.d(TAG, "getLocation: Requesting location updates");
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
                // В методе onLocationChanged
                @Override
                public void onLocationChanged(Location location) {
                    Log.d(TAG, "onLocationChanged: Location changed");
                    currentLatitude = location.getLatitude();
                    currentLongitude = location.getLongitude();

                    // Получение полного адреса текущего местоположения
                    String currentAddressUrl = "https://nominatim.openstreetmap.org/reverse?lat=" +
                            currentLatitude + "&lon=" + currentLongitude + "&format=json";

                    JsonObjectRequest currentAddressRequest = new JsonObjectRequest(Request.Method.GET, currentAddressUrl, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        JSONObject addressDetails = response.getJSONObject("address");
                                        // Получение полного адреса
                                        String userAddress = addressDetails.optString("city") + ", " +
                                                addressDetails.optString("road") + " " +
                                                addressDetails.optString("house_number");
                                        // Построение URL для отображения маршрута
                                        double destinationLatitude = 51.74184;
                                        double destinationLongitude = 55.09565;
                                        String apiKey = "1b5c7ff9-e26a-4bc3-8861-1f67387981fd";
                                        String url = "https://graphhopper.com/api/1/route?point=" +
                                                currentLatitude + "%2C" + currentLongitude + "&point=" +
                                                destinationLatitude + "%2C" + destinationLongitude +
                                                "&vehicle=car&locale=ru&key=" + apiKey;
                                        String url2 = "https://www.openstreetmap.org/directions?engine=graphhopper_car&route=" +
                                                currentLatitude + "%2C" + currentLongitude + "%3B" +
                                                destinationLatitude + "%2C" + destinationLongitude;
                                        webView.loadUrl(url2);
                                        // Отправка запроса к GraphHopper API и получение ответа
                                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                                                new Response.Listener<JSONObject>() {
                                                    @Override
                                                    public void onResponse(JSONObject response) {
                                                        // Обработка ответа от GraphHopper API
                                                        processRouteResponse(response, userAddress);
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        // Обработка ошибки
                                                    }
                                                });

                                        // Добавление запроса в очередь запросов
                                        RequestQueue requestQueue = Volley.newRequestQueue(map.this);
                                        requestQueue.add(jsonObjectRequest);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // Обработка ошибки
                                }
                            });
                    RequestQueue requestQueue = Volley.newRequestQueue(map.this);
                    requestQueue.add(currentAddressRequest);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {}

                @Override
                public void onProviderEnabled(String provider) {}

                @Override
                public void onProviderDisabled(String provider) {
                    Toast.makeText(map.this, "Please enable GPS", Toast.LENGTH_SHORT).show();
                }
            }, null);
        } catch (SecurityException e) {
            e.printStackTrace();
            Log.e(TAG, "getLocation: SecurityException: " + e.getMessage());
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void processRouteResponse(JSONObject response, String destinationAddress) {
        try {
            // Получение массива сегментов маршрута
            JSONArray paths = response.getJSONArray("paths");
            JSONObject path = paths.getJSONObject(0); // Берем первый сегмент
            Log.d(TAG, "processRouteResponse: Processing route response");
            // Получение геометрии маршрута (список координат)
            String points = path.getString("points");
            List<LatLng> coordinates = parseCoordinates(points);

            // Получение длины маршрута в метрах
            double distanceMeters = path.getDouble("distance");

            // Конвертация метров в километры
            double distanceKilometers = distanceMeters / 1000.0;

            // Отображение расстояния и адреса места назначения в TextView
            DecimalFormat df = new DecimalFormat("#.##");

            String fullAddress = "Расстояние маршрута: " + df.format(distanceKilometers) + " км\n" +
                    "Адрес места назначения: " + destinationAddress;
            distanceTextView.setText(fullAddress);
            SharedPreferences sp = getSharedPreferences("Авторизация", Context.MODE_PRIVATE);
            String currentUserEmail = sp.getString("CurrentUserEmail", "");
            // Проверка наличия документа с данными пользователя
            db.collection("map")
                    .whereEqualTo("user", currentUserEmail)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            String documentId = documentSnapshot.getId();
                            // Обновление существующего документа
                            Map<String, Object> mapData = new HashMap<>();
                            mapData.put("mappoint", destinationAddress);
                            mapData.put("km", distanceKilometers);
                            mapData.put("latitude", currentLatitude);
                            mapData.put("longitude", currentLongitude);
                            mapData.put("user", currentUserEmail);
                            db.collection("map").document(documentId).update(mapData)
                                    .addOnSuccessListener(aVoid -> Toast.makeText(map.this, "Данные успешно обновлены", Toast.LENGTH_LONG).show())
                                    .addOnFailureListener(e -> Toast.makeText(map.this, "Не удалось обновить данные, попробуйте еще раз", Toast.LENGTH_LONG).show());
                            // Если найден документ, соответствующий текущему пользователю, выходим из цикла
                            break;
                        }
                    });

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "processRouteResponse: JSONException: " + e.getMessage());
        }
        // Планирование обновления геолокации через 10 секунд
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getLocation();
            }
        }, UPDATE_INTERVAL);
    }

    private void searchLocation(String searchText) {
        // Формируем URL для поиска местоположений

        String searchUrl;
        try {
            searchUrl = "https://nominatim.openstreetmap.org/search?q=" + URLEncoder.encode(searchText, "UTF-8") + "&format=json";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            // Ваш обработчик исключения
            searchUrl = "https://nominatim.openstreetmap.org/search?q=" + searchText + "&format=json";
        }
        // Отправляем запрос
        JsonObjectRequest searchRequest = new JsonObjectRequest(Request.Method.GET, searchUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Обработка ответа с результатами поиска
                        processSearchResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Обработка ошибки
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(searchRequest);
    }
    private void processSearchResponse(JSONObject response) {
        try {
            // Получаем массив результатов поиска
            JSONArray results = response.getJSONArray("results");

            // Создаем список для отображения результатов пользователю
            List<String> searchResults = new ArrayList<>();

            // Проходим по всем результатам и добавляем их в список
            for (int i = 0; i < results.length(); i++) {
                JSONObject result = results.getJSONObject(i);
                String displayName = result.optString("display_name");
                searchResults.add(displayName);
            }

            // Отображаем результаты пользователю, например, в выпадающем списке или списке подсказок
            // Например, можно использовать AutoCompleteTextView для автодополнения
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, searchResults);
            autoCompleteTextView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private List<LatLng> parseCoordinates(String points) {
        List<LatLng> coordinates = new ArrayList<>();
        try {
            // Разделим строку на отдельные координаты
            String[] pointsArray = points.split(";");
            for (String point : pointsArray) {
                // Разделим координаты широты и долготы
                String[] latLng = point.split(",");
                // Проверим, что полученные значения представляют числа
                if (latLng.length == 2) {
                    double latitude = Double.parseDouble(latLng[0]);
                    double longitude = Double.parseDouble(latLng[1]);
                    // Создаем объект LatLng и добавляем его в список
                    coordinates.add(new LatLng(latitude, longitude));
                } else {
                    Log.e(TAG, "parseCoordinates: Invalid coordinates format: " + point);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            Log.e(TAG, "parseCoordinates: NumberFormatException: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "parseCoordinates: Exception: " + e.getMessage());
        }
        return coordinates;
    }
}