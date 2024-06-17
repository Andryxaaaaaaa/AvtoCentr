package com.example.avtocentr;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ZayavkiListActivity extends AppCompatActivity {

    private ListView zayavkiListView;
    private ArrayAdapter<String> adapter;
    public String userEmail;
    private List<String> zayavkiList;

    private static final String TAG = "ZayavkiListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zayavki_list);
        ImageButton backButton = findViewById(R.id.buttonНазад);
        userEmail = getIntent().getStringExtra("userEmail");
        zayavkiListView = findViewById(R.id.zayavkiListView);
        zayavkiList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, zayavkiList);
        zayavkiListView.setAdapter(adapter);

        // Получаем заявки из Firestore и отображаем их
        getZayavkiFromFirestore(userEmail);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(ZayavkiListActivity.this, Profile.class));
                finish();
            }

        });
    }

    private void getZayavkiFromFirestore(String userEmail) {
        CollectionReference zayavkaRef = FirebaseFirestore.getInstance().collection("zayavka");
        // Запрос на получение заявок конкретного пользователя
        zayavkaRef.whereEqualTo("user", userEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            zayavkiList.clear();

                            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                String documentId = documentSnapshot.getId();
                                String typezayavka = documentSnapshot.getString("typezayavka");
                                Object adresObj = documentSnapshot.get("Адрес места эксплуатации");
                                String adres = (adresObj != null) ? adresObj.toString() : "";
                                Object chasObj = documentSnapshot.get("Ваша организация");
                                String chas = (chasObj != null) ? chasObj.toString() : "";

                                Object chasObj2 = documentSnapshot.get("Дата ввода в эксплуатацию");
                                String chas2 = (chasObj2 != null) ? chasObj2.toString() : "";

                                Object chasObj3 = documentSnapshot.get("Дата приобретения");
                                String chas3 = (chasObj3 != null) ? chasObj3.toString() : "";

                                Object chasObj4 = documentSnapshot.get("Кол-во отработанных часов");
                                String chas4 = (chasObj4 != null) ? chasObj4.toString() : "";

                                Object modelObj = documentSnapshot.get("Модель двигателя ");
                                String model = (modelObj != null) ? modelObj.toString() : "";

                                Object nomerdvigObj = documentSnapshot.get("Модель техники");
                                String nomerdvig = (nomerdvigObj != null) ? nomerdvigObj.toString() : "";

                                Object nomerdvigObj2 = documentSnapshot.get("Описание неисправностей (При наличии)");
                                String nomerdvig2 = (nomerdvigObj2 != null) ? nomerdvigObj2.toString() : "";

                                Object nomerdvigObj3 = documentSnapshot.get("Серийный номер двигателя");
                                String nomerdvig3 = (nomerdvigObj3 != null) ? nomerdvigObj3.toString() : "";

                                Object nomerdvigObj4 = documentSnapshot.get("Серийный номер техники (VIN)");
                                String nomerdvig4 = (nomerdvigObj4 != null) ? nomerdvigObj4.toString() : "";

                                Object nomerdvigObj5 = documentSnapshot.get("№ товарной накладной");
                                String nomerdvig5 = (nomerdvigObj5 != null) ? nomerdvigObj5.toString() : "";

                                Object nomerdvigObj6 = documentSnapshot.get("Маршрут, км");
                                String nomerdvig6 = (nomerdvigObj6 != null) ? nomerdvigObj6.toString() : "";

                                String userData = "\nТип заявки: " + typezayavka +
                                        "\nАдрес места эксплуатации: " + adres +
                                        "\nОрганизация: " + chas + " " +
                                        "\nДата ввода в эксплуатацию: " + chas2
                                        +
                                        "\nДата приобретения: " + chas3
                                        +
                                        "\nКол-во отработанных часов: " + chas4
                                        +
                                        "\nМодель двигателя: " + model
                                        +
                                        "\nМодель техники: " + nomerdvig
                                        +
                                        "\nОписание неисправностей: " + nomerdvig2
                                        +
                                        "\nСерийный номер двигателя: " + nomerdvig3
                                        +
                                        "\nСерийный номер техники (VIN): " + nomerdvig4
                                        +
                                        "\n№ товарной накладной: " + nomerdvig5
                                        +
                                        "\nМаршрут, км: " + nomerdvig6;

                                zayavkiList.add(userData);
                            }

                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}
