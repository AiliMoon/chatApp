package com.example.android.chatapp.contacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.chatapp.R;
import com.example.android.chatapp.models.UserModel;
import com.example.android.chatapp.interfaces.OnItemClickListener;

import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {

    private List<UserModel> list;
    private LayoutInflater inflater;
    private OnItemClickListener onItemClickListener;

    public ContactsAdapter(Context context, List<UserModel> list) {
        inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = inflater.inflate(R.layout.list_user, parent, false);
       return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(getAdapterPosition()));
            textView = itemView.findViewById(R.id.textViewUserContact);
        }

       public void bind(UserModel userModel) {
            textView.setText(userModel.getName());
       }
    }
}
