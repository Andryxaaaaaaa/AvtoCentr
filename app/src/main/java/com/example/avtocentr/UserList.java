package com.example.avtocentr;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class UserList extends AppCompatActivity {

    private List<String> additionalUserData = new ArrayList<>();
    private List<String> additionalUserDataMap = new ArrayList<>();
    private List<String> additionalUserDataUserInfo = new ArrayList<>();
    private List<String> additionalUserDataZayavka = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private static final String TAG = "UserListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        ListView userDetailListView = findViewById(R.id.userDetailListView);

        // Создаем адаптер для отображения дополнительных данных
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, additionalUserData);

        // Устанавливаем адаптер для ListView
        userDetailListView.setAdapter(adapter);

        userDetailListView.setOnItemLongClickListener((parent, view, position, id) -> {
            // Получаем данные элемента списка, который был долговременно нажат
            String selectedItem = adapter.getItem(position);

            // Показываем диалоговое окно для подтверждения удаления
            showDeleteConfirmationDialog(selectedItem, position);

            // Возвращаем true, чтобы указать, что событие было обработано
            return true;
        });
        // Получаем email выбранного пользователя из Intent
        String selectedUserEmail = getIntent().getStringExtra("selectedUserEmail");

        // Получаем дополнительные данные пользователя по его email и отображаем их
        getUserAdditionalData(selectedUserEmail);
    }

    // Метод для получения дополнительных данных пользователя из разных коллекций
    private void getUserAdditionalData(String userEmail) {
        // Получаем ссылку на коллекции в Firestore
        CollectionReference usersRef = FirebaseFirestore.getInstance().collection("users");
        CollectionReference usersRef2 = FirebaseFirestore.getInstance().collection("map");
        CollectionReference usersRef3 = FirebaseFirestore.getInstance().collection("userinfo");
        CollectionReference usersRef4 = FirebaseFirestore.getInstance().collection("zayavka");

        // Счетчик для отслеживания завершения всех запросов
        AtomicInteger queryCounter = new AtomicInteger(4);

        // Запрос на получение дополнительных данных пользователя по его email из первой коллекции
        usersRef.whereEqualTo("email", userEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Очищаем список перед добавлением новых данных
                    additionalUserData.clear();
                    // Обрабатываем каждый документ с данными пользователя
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        // Получаем данные пользователя из документа
                        String email = documentSnapshot.getString("email");
                        String password = documentSnapshot.getString("password");
                        // Формируем строку с данными пользователя
                        String userData = "email: " + email + ", password: " + password;
                        // Добавляем данные в список
                        additionalUserData.add(userData);
                    }
                    // Уведомляем адаптер об изменениях
                    adapter.notifyDataSetChanged();
                    // Проверяем, завершились ли все запросы

                })
                .addOnFailureListener(e -> {
                    // Обработка ошибки
                    Log.e("UserListActivity", "Error getting user additional data", e);
                    // Проверяем, завершились ли все запросы
                    checkQueryCompletion(queryCounter);
                });

        // Запрос на получение дополнительных данных из коллекции "map"
        usersRef2.whereEqualTo("user", userEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Очищаем список перед добавлением новых данных
                    additionalUserDataMap.clear();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String mappoint = documentSnapshot.getString("mappoint");
                        Object kmValue = documentSnapshot.get("km");
                        if (kmValue instanceof Long) {
                            Long kmLong = (Long) kmValue;
                            // обработка числового значения
                        } else if (kmValue instanceof String) {
                            String kmString = (String) kmValue;
                            // обработка строкового значения
                        } else {
                            // обработка других типов данных (если это возможно)
                        }
                        String userData = "Адрес: " + mappoint + ", Расстояние: " + kmValue;
                        additionalUserDataMap.add(userData);
                    }
                    // Уведомляем адаптер об изменениях
                    adapter.notifyDataSetChanged();
                    // Проверяем, завершились ли все запросы
                    checkQueryCompletion(queryCounter);
                })
                .addOnFailureListener(e -> {
                    // Обработка ошибки
                    Log.e("UserListActivity", "Error getting user additional data from 'map' collection", e);
                    // Проверяем, завершились ли все запросы
                    checkQueryCompletion(queryCounter);
                });

// Запрос на получение дополнительных данных из коллекции "userinfo"
        usersRef3.whereEqualTo("email", userEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Очищаем список перед добавлением новых данных
                    additionalUserDataUserInfo.clear();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String familia = documentSnapshot.getString("familia");
                        String imya = documentSnapshot.getString("imya");
                        String otchestvo = documentSnapshot.getString("otchestvo");
                        String number = documentSnapshot.getString("number");
                        String userData = familia + " " + imya + " " + otchestvo + "\nНомер телефона: " + number;
                        additionalUserDataUserInfo.add(userData);
                    }
                    // Уведомляем адаптер об изменениях
                    adapter.notifyDataSetChanged();
                    // Проверяем, завершились ли все запросы
                    checkQueryCompletion(queryCounter);
                })
                .addOnFailureListener(e -> {
                    // Обработка ошибки
                    Log.e("UserListActivity", "Error getting user additional data from 'userinfo' collection", e);
                    // Проверяем, завершились ли все запросы
                    checkQueryCompletion(queryCounter);
                });
        // Запрос на получение дополнительных данных из коллекции "zayavka"
        usersRef4.whereEqualTo("user", userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    // Уменьшаем счетчик запросов
                    int count = queryCounter.decrementAndGet();
                    if (task.isSuccessful()) {
                        // Очищаем список перед добавлением новых данных
                        additionalUserDataZayavka.clear();
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
                                    "\nАдрес места эксплуатации: " + adres  +
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

                            additionalUserDataZayavka.add(userData);
                        }
                        // Уведомляем адаптер об изменениях
                        adapter.notifyDataSetChanged();
                    } else {
                        // Обработка ошибки
                        Log.e("UserListActivity", "Error getting user additional data from 'zayavka' collection", task.getException());
                    }
                    // Проверяем, завершились ли все запросы
                    checkQueryCompletion(queryCounter);
                });

    }


    // Метод для проверки завершения всех запросов и объединения данных
    // Метод для проверки завершения всех запросов и объединения данных
    // Метод для проверки завершения всех запросов и объединения данных
    private void checkQueryCompletion(AtomicInteger queryCounter) {
        // Уменьшаем счетчик запросов
        int count = queryCounter.decrementAndGet();
        // Если все запросы завершились
        if (count == 0) {
            // Объединяем данные из всех списков перед обновлением адаптера
            List<String> combinedData = new ArrayList<>();
            combinedData.addAll(additionalUserData);
            combinedData.addAll(additionalUserDataMap);
            combinedData.addAll(additionalUserDataUserInfo);
            combinedData.addAll(additionalUserDataZayavka);
            // Очищаем список перед добавлением объединенных данных
            additionalUserData.clear();
            // Добавляем объединенные данные в список
            additionalUserData.addAll(combinedData);
            // Добавляем объединенные данные в адаптер и обновляем список на экране
            adapter.clear();
            adapter.addAll(combinedData);
            adapter.notifyDataSetChanged();
        }
    }
    private void showDeleteConfirmationDialog(String selectedItem, int position) {
        // Создаем список для объединенных данных
        List<String> combinedData = new ArrayList<>();
        combinedData.addAll(additionalUserData);
        combinedData.addAll(additionalUserDataMap);
        combinedData.addAll(additionalUserDataUserInfo);
        combinedData.addAll(additionalUserDataZayavka);
        new AlertDialog.Builder(this)
                .setTitle("Закрытие заявки")
                .setMessage("Вы собираетесь закрыть выполнение заявки,\nВы уверены что хотите удалить эту заявку?")
                .setPositiveButton("Да", (dialog, which) -> {
                    if (position >= 0 && position < additionalUserDataZayavka.size()) {
                        additionalUserDataZayavka.remove(position);
                        combinedData.remove(position + additionalUserData.size() + additionalUserDataMap.size() + additionalUserDataUserInfo.size() + 2);
                        adapter.notifyDataSetChanged();
                        deleteZayavkaDocument(selectedItem);
                    } else {
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }
    private void deleteZayavkaDocument(String selectedItem) {
        CollectionReference zayavkaRef = FirebaseFirestore.getInstance().collection("zayavka");
        zayavkaRef.whereEqualTo("typezayavka", selectedItem)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            // Получаем уникальный идентификатор документа
                            String documentId = document.getId();

                            // Удаляем документ по его уникальному идентификатору
                            zayavkaRef.document(documentId).delete()
                                    .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully deleted"))
                                    .addOnFailureListener(e -> Log.w(TAG, "Error deleting document", e));
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }



}
