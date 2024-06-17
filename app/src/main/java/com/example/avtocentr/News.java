package com.example.avtocentr;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class News extends AppCompatActivity {

    private LinearLayout detailsContainer;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        detailsContainer = findViewById(R.id.detailsContainer);
        db = FirebaseFirestore.getInstance();

        // Получаем данные из Firestore и отображаем их
        loadDetailsFromFirestore();
    }

    private void loadDetailsFromFirestore() {
        db.collection("info")
                .orderBy("timestamp", Query.Direction.DESCENDING) // Сортируем по времени в обратном порядке
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        // Обработка ошибки
                        return;
                    }

                    for (DocumentChange dc : Objects.requireNonNull(value).getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            DocumentSnapshot document = dc.getDocument();
                            addDetailsToLayout(document);
                        }
                    }
                });
    }

    private void addDetailsToLayout(DocumentSnapshot document) {
        // Создаем новый блок для каждого документа
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(0, 0, 0, 16); // Добавляем отступы между блоками
        cardView.setLayoutParams(cardParams);
        cardView.setRadius(16); // Устанавливаем закругление углов
        cardView.setCardElevation(35);

         // Устанавливаем цвет обводки // Устанавливаем цвет обводки// Устанавливаем тень для блока

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        LinearLayout layout = new LinearLayout(this);
        layout.setLayoutParams(params);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Создаем TextView для даты
        TextView dateTextView = new TextView(this);
        dateTextView.setLayoutParams(params);
        dateTextView.setText(formatDate(document.getTimestamp("timestamp").toDate()));
        dateTextView.setPadding(50, 0, 0, 0);
        layout.addView(dateTextView);

        // Создаем TextView для заголовка
        TextView titleTextView = new TextView(this);
        titleTextView.setLayoutParams(params);
        titleTextView.setText(document.getString("bigtext")); // Получаем заголовок из документа
        titleTextView.setTextColor(getResources().getColor(R.color.black));
        titleTextView.setPadding(50, 0, 0, 0);// Устанавливаем черный цвет текста
        titleTextView.setTextSize(18);
         // Устанавливаем цвет обводки// Устанавливаем размер текста
        layout.addView(titleTextView);

        // Создаем TextView для текста
        TextView textTextView = new TextView(this);
        textTextView.setLayoutParams(params);
        textTextView.setText(document.getString("text")); // Получаем текст из документа
        textTextView.setTextColor(getResources().getColor(R.color.black));
        titleTextView.setPadding(50, 0, 0, 0);// Устанавливаем черный цвет текста
        layout.addView(textTextView);

        // Добавляем блок с данными в контейнер
        cardView.addView(layout);
        detailsContainer.addView(cardView);
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", new Locale("ru", "RU"));
        return sdf.format(date);
    }
}
