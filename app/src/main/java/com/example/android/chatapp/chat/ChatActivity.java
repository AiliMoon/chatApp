package com.example.android.chatapp.chat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.android.chatapp.R;
import com.example.android.chatapp.models.UserModel;
import com.example.android.chatapp.models.Chat;
import com.example.android.chatapp.models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private EditText editTextMessage;
    private UserModel user;
    private Chat chat;

    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        editTextMessage = findViewById(R.id.input_message);
        recyclerView = findViewById(R.id.recyclerViewMessages);
        user = (UserModel) getIntent().getSerializableExtra("user");
        chat = (Chat) getIntent().getSerializableExtra("chat");
        if (chat == null){
            chat = new Chat();
//        chat.setId();
            ArrayList<String> userIds = new ArrayList<>();
            userIds.add(user.getId());
            userIds.add(FirebaseAuth.getInstance().getUid());
            chat.setUserIds(userIds);
        } else {
            initList();
            getMessages();
        }
    }

    private void getMessages() {
        FirebaseFirestore.getInstance().collection("chats")
                .document(chat.getId())
                .collection("messages").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                assert value != null;
                for (DocumentChange change : value.getDocumentChanges()) {
                    switch (change.getType()) {
                        case ADDED:
                            list.add(change.getDocument().toObject(Message.class));
                            break;
                        case MODIFIED:
                            break;
                        case REMOVED:
                            break;
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void initList() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new MessageAdapter(this, list);
        recyclerView.setAdapter(adapter);
    }

    public void onClickSendMessage(View view) {
        String text = editTextMessage.getText().toString().trim();
        if (chat.getId() != null) {
            sendMessage(text);
        } else {
            createChat(text);
        }
    }

    private void createChat(final String text) {
        FirebaseFirestore
                .getInstance()
                .collection("chats")
                .add(chat).addOnSuccessListener(documentReference -> {
                    chat.setId(documentReference.getId());
                    sendMessage(text);
                });
    }

    private void sendMessage(String text) {
        Map<String, Object> map = new HashMap<>();
        map.put("text", text);
        FirebaseFirestore
                .getInstance()
                .collection("chats")
                .document(chat.getId())
                .collection("messages")
                .add(map);
    }
}