package com.example.clashofcounters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HighscoresAdapter extends RecyclerView.Adapter<HighscoresAdapter.ViewHolder> {
    private List<UserHighscore> highscoresList;

    public HighscoresAdapter(List<UserHighscore> highscoresList) {
        this.highscoresList = highscoresList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_highscore, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserHighscore userHighscore = highscoresList.get(position);

        String username = extractUsername(userHighscore.getUserEmail());

        holder.textUserEmail.setText(username);
        holder.textHighscore.setText(String.valueOf(userHighscore.getHighscore()));
    }

    @Override
    public int getItemCount() {
        return highscoresList != null ? highscoresList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textUserEmail;
        TextView textHighscore;

        ViewHolder(View itemView) {
            super(itemView);
            textUserEmail = itemView.findViewById(R.id.textUserEmail);
            textHighscore = itemView.findViewById(R.id.textHighscore);
        }
    }

    private String extractUsername(String email) {
        int atIndex = email.indexOf('@');
        return (atIndex != -1) ? email.substring(0, atIndex) : email;
    }
}
