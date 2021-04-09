package com.example.android.chatapp.contacts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.android.chatapp.R;
import com.example.android.chatapp.models.UserModel;
import com.example.android.chatapp.chat.ChatActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ContactsAdapter adapter;
    private List<UserModel> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        recyclerView = findViewById(R.id.recyclerViewContacts);
        initList();
        getContacts();
    }

    private void getContacts() {
        FirebaseFirestore.getInstance().collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                    UserModel user = snapshot.toObject(UserModel.class);
                    if (user != null) {
                        user.setId(snapshot.getId());
                    }
                    list.add(user);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void initList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new ContactsAdapter(this, list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(ContactsActivity.this, ChatActivity.class);
            intent.putExtra("user", list.get(position));
            startActivity(intent);
        });
    }
}