package com.example.android.chatapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.android.chatapp.chat.ChatActivity;
import com.example.android.chatapp.chat.ChatAdapter;
import com.example.android.chatapp.contacts.ContactsActivity;
import com.example.android.chatapp.models.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private List<Chat> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, PhoneActivity.class));
            return;
        }
        recyclerView = findViewById(R.id.recyclerViewAllChats);
        initList();
        getChats();
    }

    public void onClickContacts(View view) {
        startActivity(new Intent(this, ContactsActivity.class));
    }

    private void initList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new ChatAdapter(this, list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(position -> {
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            intent.putExtra("chat", list.get(position));
            startActivity(intent);
        });
    }

    private void getChats() {
        String myUserId = FirebaseAuth.getInstance().getUid();
        FirebaseFirestore.getInstance().collection("chats")
                .whereArrayContains("userIds", myUserId)
                .addSnapshotListener((value, error) -> {
                    for (DocumentChange change : value.getDocumentChanges()) {
                        switch (change.getType()) {
                            case ADDED:
                                Chat chat = change.getDocument().toObject(Chat.class);
                                chat.setId(change.getDocument().getId());
                                list.add(chat);
                                break;
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}