package com.example.avtocentr;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class Glavnaya extends AppCompatActivity {

    private LinearLayout imageContainer;

    private Button addButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glavnaya);


        EditText editText = findViewById(R.id.editText);
        EditText editTextBig = findViewById(R.id.editTextBig);
        addButton = findViewById(R.id.addButton);



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

    }

}
