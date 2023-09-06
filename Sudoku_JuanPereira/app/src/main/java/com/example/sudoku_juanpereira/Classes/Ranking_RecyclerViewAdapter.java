package com.example.sudoku_juanpereira.Classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sudoku_juanpereira.R;

import java.util.ArrayList;

public class Ranking_RecyclerViewAdapter extends RecyclerView.Adapter<Ranking_RecyclerViewAdapter.MyViewHolder> {

    Context context;
    ArrayList<Ranking_User> users;

    public Ranking_RecyclerViewAdapter(Context context, ArrayList<Ranking_User> users){
        this.context = context;
        this.users = users;
    }
    @NonNull
    @Override
    public Ranking_RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_ranking,parent,false);
        return new Ranking_RecyclerViewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Ranking_RecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.tv_user.setText(users.get(position).getUser());
        holder.tv_points.setText(String.valueOf(users.get(position).getPoints()));
        if(position<3){
            switch(position){
                case 0:
                    holder.iv_medal.setImageResource(R.drawable.goldmedal);
                    break;
                case 1:
                    holder.iv_medal.setImageResource(R.drawable.silvermedal);
                    break;
                case 2:
                    holder.iv_medal.setImageResource(R.drawable.bronzemedal);
                    break;

            }
            holder.tv_position.setText(null);
        }
        else{
            holder.tv_position.setText(String.valueOf(position+1));
            holder.iv_medal.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_position;
        TextView tv_user;
        TextView tv_points;
        ImageView iv_medal;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_position = itemView.findViewById(R.id.tv_position);
            tv_user = itemView.findViewById(R.id.tv_name);
            tv_points = itemView.findViewById(R.id.tv_points);
            iv_medal = itemView.findViewById(R.id.iv_medal);
        }
    }
}
