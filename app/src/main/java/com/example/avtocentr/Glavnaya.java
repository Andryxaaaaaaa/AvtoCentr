package com.example.avtocentr;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Glavnaya extends AppCompatActivity {

    private LinearLayout imageContainer;

    private Button addButton;


    private ListView listView;
    private FirebaseFirestore db;
    // Добавьте переменную для хранения списка пользователей
    private List<String> userNames = new ArrayList<>();
    private List<String> userEmails = new ArrayList<>();
    List<String> additionalDataFromMap = new ArrayList<>();
    List<String> additionalDataFromUser = new ArrayList<>();
    List<String> additionalDataFromZayavka = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glavnaya);
        listView = findViewById(R.id.ListView);
        db = FirebaseFirestore.getInstance();
        EditText editText = findViewById(R.id.editText);
        EditText editTextBig = findViewById(R.id.editTextBig);
        addButton = findViewById(R.id.addButton);

        // Добавьте слушатель событий к ListView
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedUserName = userNames.get(position);
            String selectedUserEmail = userEmails.get(position); // Получаем email выбранного пользователя
            onItemClick(selectedUserEmail); // Переход к активности с подробной информацией о выбранном пользователе
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> info = new HashMap<>();
                info.put("bigtext", editTextBig.getText().toString());
                info.put("text", editText.getText().toString());
                info.put("timestamp", FieldValue.serverTimestamp()); // Добавляем текущую дату и время

                if (editText.getText().toString().isEmpty() || editTextBig.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Поля не должны быть пустыми!", Toast.LENGTH_LONG).show();
                } else {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("info")
                            .add(info)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    // Document created successfully
                                    Log.d("info", "Успешно загружено" + documentReference.getId());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Failed to create document
                                    Toast.makeText(Glavnaya.this, "Не удалось загрузить", Toast.LENGTH_LONG).show();
                                    Log.e("info", "Error adding document", e);
                                }
                            });
                }
            }
        });

        getUsersList();
    }
    public void getUsersList() {
        CollectionReference usersRef = db.collection("zayavka");
        usersRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                userNames.clear(); // Очищаем список перед заполнением новыми данными
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Log.d("UserList", "Document: " + documentSnapshot.getId());
                    String typezayavka= documentSnapshot.getString("typezayavka");
                    String email = documentSnapshot.getString("user"); // Извлекаем email пользователя
                    String zayavka = "Поступила заявка на " + typezayavka;

                    // Добавляем ФИО пользователя и его email в список
                    userNames.add(zayavka);
                    userEmails.add(email);
                }
                // Создаем адаптер
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userNames);
                // Устанавливаем адаптер для ListView
                listView.setAdapter(adapter);
                // Уведомляем адаптер о том, что данные изменились
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(e -> {
            Log.e("Glavnaya", "Ошибка при загрузке пользователей", e);
        });
    }
    private void getUserAdditionalData(String userEmail) {
        List<Task<QuerySnapshot>> tasks = new ArrayList<>();

        Task<QuerySnapshot> mapTask = db.collection("map").whereEqualTo("email", userEmail).get();
        Task<QuerySnapshot> userTask = db.collection("user").whereEqualTo("email", userEmail).get();
        Task<QuerySnapshot> zayavkaTask = db.collection("zayavka").whereEqualTo("email", userEmail).get();

        tasks.add(mapTask);
        tasks.add(userTask);
        tasks.add(zayavkaTask);

        Tasks.whenAllSuccess(tasks).addOnSuccessListener(results -> {
            additionalDataFromMap.clear();
            additionalDataFromUser.clear();
            additionalDataFromZayavka.clear();

            for (int i = 0; i < results.size(); i++) {
                QuerySnapshot result = (QuerySnapshot) results.get(i); // Явное приведение к QuerySnapshot
                for (DocumentSnapshot document : result.getDocuments()) {
                    if (i == 0) { // map collection
                        String km = document.getString("km");
                        String mappoint = document.getString("mappoint");
                        additionalDataFromMap.add(mappoint + " " + km);
                    } else if (i == 1) { // user collection
                        String email = document.getString("email");
                        String password = document.getString("password");
                        additionalDataFromUser.add(email + " " + password);
                    } else if (i == 2) { // zayavka collection
                        String Tip = document.getString("typezayavka");
                        additionalDataFromZayavka.add(Tip);
                    }
                }
            }
        }).addOnFailureListener(e -> {
            Log.e("Glavnaya", "Ошибка при загрузке дополнительных данных", e);
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Обновление списка пользователей при возвращении к этой активности
        getUsersList();
    }

    public void onItemClick(String selectedUserEmail) {
        // Получаем email выбранного пользователя по его позиции в списке
        // Передаем email выбранного пользователя в UserListActivity
        Intent intent = new Intent(this, UserListActivity.class);
        intent.putExtra("selectedUserEmail", selectedUserEmail);
        startActivity(intent);

        // Получаем дополнительные данные о пользователе
        getUserAdditionalData(selectedUserEmail);
    }

}
