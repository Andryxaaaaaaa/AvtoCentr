package com.example.avtocentr;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Admin extends AppCompatActivity {

    private LinearLayout imageContainer;
    private Button addButton;
    private ListView listView;
    private WebView webView;
    private FirebaseFirestore db;
    private List<String> userNames = new ArrayList<>();
    private List<String> userEmails = new ArrayList<>();
    List<String> additionalDataFromMap = new ArrayList<>();
    List<String> additionalDataFromUser = new ArrayList<>();
    List<String> additionalDataFromZayavka = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        listView = findViewById(R.id.ListView);
        db = FirebaseFirestore.getInstance();
        EditText editText = findViewById(R.id.editText);
        EditText editTextBig = findViewById(R.id.editTextBig);
        addButton = findViewById(R.id.addButton);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedUserName = userNames.get(position);
            String selectedUserEmail = userEmails.get(position);
            onItemClick(selectedUserEmail);
        });

        addButton.setOnClickListener(v -> {
                    Map<String, Object> info = new HashMap<>();
                    info.put("bigtext", editTextBig.getText().toString());
                    info.put("text", editText.getText().toString());
                    info.put("timestamp", FieldValue.serverTimestamp());

                    if (editText.getText().toString().isEmpty() || editTextBig.getText().toString().isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Fields should not be empty!", Toast.LENGTH_LONG).show();
                    } else {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("info")
                                .add(info)
                                .addOnSuccessListener(documentReference -> Log.d("info", "Successfully uploaded " + documentReference.getId()))
                                .addOnFailureListener(e -> {
                                    Toast.makeText(Admin.this, "Failed to upload", Toast.LENGTH_LONG).show();
                                    Log.e("info", "Error adding document", e);
                                });
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage("Заявка отправлена. Ожидайте!")
                                .setPositiveButton("OK", (dialog, which) -> {
                                    // Очистка текстовых полей
                                    editText.setText("");
                                    editTextBig.setText("");
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                    ;
                });
        getUsersList();
    }


    public void getUsersList() {
        CollectionReference usersRef = db.collection("zayavka");
        usersRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                userNames.clear();
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    Log.d("UserList", "Document: " + documentSnapshot.getId());
                    String typezayavka = documentSnapshot.getString("typezayavka");
                    String email = documentSnapshot.getString("user");
                    String Модель_техники = documentSnapshot.getString("Модель техники");
                    String zayavka = "Поступила заявка на " + typezayavka + " " + Модель_техники;

                    userNames.add(zayavka);
                    userEmails.add(email);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userNames);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(e -> Log.e("Glavnaya", "Ошибка при загрузке пользователей", e));
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
                QuerySnapshot result = (QuerySnapshot) results.get(i);
                for (DocumentSnapshot document : result.getDocuments()) {
                    if (i == 0) {
                        String km = document.getString("km");
                        String mappoint = document.getString("mappoint");
                        additionalDataFromMap.add(mappoint + " " + km);
                    } else if (i == 1) {
                        String email = document.getString("email");
                        String password = document.getString("password");
                        additionalDataFromUser.add(email + " " + password);
                    } else if (i == 2) {
                        String tip = document.getString("typezayavka");
                        additionalDataFromZayavka.add(tip);
                    }
                }
            }
        }).addOnFailureListener(e -> Log.e("Glavnaya", "Ошибка при загрузке дополнительных данных", e));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUsersList();
    }

    public void onItemClick(String selectedUserEmail) {
        Intent intent = new Intent(this, UserList.class);
        intent.putExtra("selectedUserEmail", selectedUserEmail);
        startActivity(intent);

        getUserAdditionalData(selectedUserEmail);
    }

    private void addDetailsToLayout(DocumentSnapshot document) {
        Timestamp timestamp = document.getTimestamp("timestamp");
        if (timestamp != null) {
            Date date = timestamp.toDate();
            // ... use the date object ...
        } else {
            Log.e("Admin", "Timestamp is null for document: " + document.getId());
        }
    }

    private void loadDetailsFromFirestore() {
        db.collection("info").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot document : queryDocumentSnapshots) {
                addDetailsToLayout(document);
            }
        }).addOnFailureListener(e -> Log.e("Glavnaya", "Error loading details from Firestore", e));
    }
}
