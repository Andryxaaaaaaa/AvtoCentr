package com.example.avtocentr;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.maps.android.PolyUtil;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class map extends AppCompatActivity {
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int UPDATE_INTERVAL = 600000; // 10 секунд
    private static final String TAG = "MapActivity";
    private LatLng lastMarkerLatLng;
    private WebView webView;
    private TextView distanceTextView;
    private AutoCompleteTextView autoCompleteTextView;
    private FirebaseFirestore db;
    private Handler handler = new Handler();
    double currentLatitude;
    double currentLongitude;
    private ProgressBar progressBar;
    private TextView textView;

    private ArrayAdapter<String> adapter;
    private List<String> addressList = new ArrayList<>();
    private Map<String, LatLng> addressToLatLngMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Log.d(TAG, "onCreate: Activity started");
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.textview);
        webView = findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        progressBar.setVisibility(View.VISIBLE);
        textView.setVisibility(View.VISIBLE);
        distanceTextView = findViewById(R.id.distanceTextView);
        webView.setWebViewClient(new WebViewClient() {
            boolean markersInitialized = false;

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);

                if (!markersInitialized) {
                    webView.loadUrl("javascript:initMarkers();");
                    markersInitialized = true;
                }
            }
        });

        webView.addJavascriptInterface(new MyJavaScriptInterface(), "Android");

        db = FirebaseFirestore.getInstance();

        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, addressList);
        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Этот метод вызывается перед тем, как текст изменится
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                distanceTextView.setText("Distance to marker: km");
                searchLocation(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Этот метод вызывается после того, как текст изменится
            }
        });

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedAddress = (String) parent.getItemAtPosition(position);
                LatLng latLng = addressToLatLngMap.get(selectedAddress);
                if (latLng != null) {
                    // Устанавливаем маркер на карте
                    webView.evaluateJavascript(
                            "javascript:setMarker(" + latLng.latitude + "," + latLng.longitude + ");",
                            null
                    );
                    // Обновляем маршрут
                    updateRoute(latLng.latitude, latLng.longitude);
                }
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            getLocation();
        }
    }

    private void getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        try {
            Log.d(TAG, "getLocation: Requesting location updates");
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.d(TAG, "onLocationChanged: Location changed");
                    currentLatitude = location.getLatitude();
                    currentLongitude = location.getLongitude();

                    String currentAddressUrl = "https://nominatim.openstreetmap.org/reverse?lat=" +
                            currentLatitude + "&lon=" + currentLongitude + "&format=json";

                    JsonObjectRequest currentAddressRequest = new JsonObjectRequest(Request.Method.GET, currentAddressUrl, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        JSONObject addressDetails = response.getJSONObject("address");
                                        String userAddress = addressDetails.optString("city") + ", " +
                                                addressDetails.optString("road") + " " +
                                                addressDetails.optString("house_number");
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

                                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                                                new Response.Listener<JSONObject>() {
                                                    @Override
                                                    public void onResponse(JSONObject response) {
                                                        processRouteResponse(response, userAddress);
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        Log.e(TAG, "onErrorResponse: ", error);
                                                    }
                                                });

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
                                    Log.e(TAG, "onErrorResponse: ", error);
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
            JSONArray paths = response.getJSONArray("paths");
            if (paths.length() > 0) {
                JSONObject path = paths.getJSONObject(0);
                String points =                path.getString("points");
                List<LatLng> coordinates = parseCoordinates(points);
                double distanceMeters = path.getDouble("distance");
                double distanceKilometers = distanceMeters / 1000.0;
                DecimalFormat df = new DecimalFormat("#.##");
                String fullAddress = "Расстояние маршрута: " + df.format(distanceKilometers) + " км\n" +
                        "Адрес места назначения: " + destinationAddress;
                distanceTextView.setText(fullAddress);
                SharedPreferences sp = getSharedPreferences("Авторизация", Context.MODE_PRIVATE);
                String currentUserEmail = sp.getString("CurrentUserEmail", "");
                db.collection("map")
                        .whereEqualTo("user", currentUserEmail)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                String documentId = documentSnapshot.getId();
                                Map<String, Object> mapData = new HashMap<>();
                                mapData.put("mappoint", destinationAddress);
                                mapData.put("km", distanceKilometers);
                                mapData.put("latitude", currentLatitude);
                                mapData.put("longitude", currentLongitude);
                                mapData.put("user", currentUserEmail);
                                db.collection("map").document(documentId).update(mapData)
                                        .addOnSuccessListener(aVoid -> Toast.makeText(map.this, "Данные успешно обновлены", Toast.LENGTH_LONG).show())
                                        .addOnFailureListener(e -> Toast.makeText(map.this, "Не удалось обновить данные, попробуйте еще раз", Toast.LENGTH_LONG).show());
                                break;
                            }
                        });
            } else {
                Log.e(TAG, "No paths found in the response");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "processRouteResponse: JSONException: " + e.getMessage());
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getLocation();
            }
        }, UPDATE_INTERVAL);
    }

    private void searchLocation(String searchText) {
        String searchUrl;
        try {
            searchUrl = "https://nominatim.openstreetmap.org/search?q=" + URLEncoder.encode(searchText, "UTF-8") + "&format=json";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            searchUrl = "https://nominatim.openstreetmap.org/search?q=" + searchText + "&format=json";
        }

        JsonArrayRequest searchRequest = new JsonArrayRequest(Request.Method.GET, searchUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        processSearchResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: ", error);
                    }
                });

        MySingleton.getInstance(this).addToRequestQueue(searchRequest);
    }



    private void processSearchResponse(JSONArray response) {
        addressList.clear();
        addressToLatLngMap.clear();
        try {
            // Регулярное выражение для извлечения города, улицы и номера дома
            Pattern pattern = Pattern.compile("([^,]+),\\s*([^,]+)\\s+(\\d+).*");
            for (int i = 0; i < response.length(); i++) {
                JSONObject location = response.getJSONObject(i);
                String displayName = location.getString("display_name");
                double lat = location.getDouble("lat");
                double lon = location.getDouble("lon");

                Matcher matcher = pattern.matcher(displayName);
                if (matcher.find()) {
                    // Извлечение города, улицы и номера дома
                    String part1 = matcher.group(1).trim();
                    String part2 = matcher.group(2).trim();
                    String houseNumber = matcher.group(3).trim();

                    String simplifiedDisplayName;
                    // Проверяем, что вторая часть - это улица, а не город
                    if (Character.isDigit(part2.charAt(0))) {
                        simplifiedDisplayName = part1 + ", " + part2;
                    } else {
                        simplifiedDisplayName = part2 + " " + houseNumber + ", " + part1;
                    }

                    addressList.add(simplifiedDisplayName);
                    addressToLatLngMap.put(simplifiedDisplayName, new LatLng(lat, lon));
                    Log.d(TAG, "Location: " + simplifiedDisplayName + " (Lat: " + lat + ", Lon: " + lon + ")");
                    distanceTextView.setText(simplifiedDisplayName);
                }
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private List<LatLng> parseCoordinates(String points) {
        List<LatLng> coordinates = new ArrayList<>();
        try {
            // Декодируем строку координат в набор LatLng
            List<LatLng> decodedCoordinates = PolyUtil.decode(points);

            // Добавляем все координаты из декодированного списка в итоговый список координат
            coordinates.addAll(decodedCoordinates);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "parseCoordinates: Exception: " + e.getMessage());
        }
        return coordinates;
    }

    private void updateRoute(double latitude, double longitude) {
        double destinationLatitude = latitude;
        double destinationLongitude = longitude;
        String apiKey = "1b5c7ff9-e26a-4bc3-8861-1f67387981fd";
        String url = "https://graphhopper.com/api/1/route?point=" +
                currentLatitude + "%2C" + currentLongitude + "&point=" +
                destinationLatitude + "%2C" + destinationLongitude +
                "&vehicle=car&locale=ru&key=" + apiKey;
        String url2 = "https://osmand.net/map/plan/?lat=" +
                currentLatitude + "&lon=" + currentLongitude + "&z=15";
        webView.loadUrl(url2);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject addressDetails = response.getJSONArray("paths").getJSONObject(0);
                            String destinationAddress = "Адрес: " + destinationLatitude + ", " + destinationLongitude;
                            processRouteResponse(response, destinationAddress);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: ", error);
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(map.this);
        requestQueue.add(jsonObjectRequest);
    }

    private class MyJavaScriptInterface {
        @JavascriptInterface
        public void onMarkerCoordinates(String coordinatesJSON) {
            try {
                JSONArray coordinatesArray = new JSONArray(coordinatesJSON);
                if (coordinatesArray.length() > 0) {
                    JSONObject lastMarker = coordinatesArray.getJSONObject(coordinatesArray.length() - 1);
                    if (!lastMarker.isNull("lat") && !lastMarker.isNull("lng")) {
                        double markerLat = lastMarker.getDouble("lat");
                        double markerLng = lastMarker.getDouble("lng");

                        float[] results = new float[1];
                        Location.distanceBetween(currentLatitude, currentLongitude, markerLat, markerLng, results);
                        double distance = results[0] / 1000; // Convert distance to kilometers

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                DecimalFormat df = new DecimalFormat("#.##");
                                String formattedDistance = df.format(distance);
                                distanceTextView.setText("Distance to marker: " + formattedDistance + " km");
                            }
                        });
                    } else {
                        Log.e(TAG, "Invalid or missing latitude or longitude values");
                    }
                } else {
                    Log.e(TAG, "Coordinates array is empty");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}