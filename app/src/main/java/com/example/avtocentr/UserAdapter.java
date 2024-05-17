package com.example.avtocentr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private Context context;
    private List<String> userData;

    public UserAdapter(Context context, List<String> userData) {
        this.context = context;
        this.userData = userData;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        String userInfo = userData.get(position);
        holder.bind(userInfo);
    }

    @Override
    public int getItemCount() {
        return userData.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewUserInfo;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewUserInfo = itemView.findViewById(R.id.textViewUserInfo);
        }

        public void bind(String userInfo) {
            textViewUserInfo.setText(userInfo);
        }
    }
}
