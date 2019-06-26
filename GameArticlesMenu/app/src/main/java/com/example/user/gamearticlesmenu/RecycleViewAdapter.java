package com.example.user.gamearticlesmenu;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;

import java.util.List;

public class RecycleViewAdapter extends RecyclerView.Adapter<ViewHolder>{

    private List<Articles> list;
    private Context context;
    private SharedPreferences preferences;

    public RecycleViewAdapter(List<Articles> list, Context c) {
        this.list = list;
        context = c;
    }

    protected void onItemClick (int position) {}

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.articles_list, parent,false);
        final ViewHolder vh = new ViewHolder(inflate);

        vh.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View view) {
                onItemClick (vh.getAdapterPosition());
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CardView cardView = holder.itemView.findViewById(R.id.cardViewId);
        holder.titleView.setText(list.get(position).title);
        if (list.get(position).url.contains("http://jin115.com/"))
            holder.urlView.setText("オレ的ゲーム速報＠刃");
        else if (list.get(position).url.contains("http://openworldnews.net/"))
            holder.urlView.setText("PS4速報");
        else if (list.get(position).url.contains("http://xn--eckybzahmsm43ab5g5336c9iug.com/"))
            holder.urlView.setText("SWITCH速報");
        else if (list.get(position).url.contains("https://jp.automaton.am/"))
            holder.urlView.setText("AUTOMATON");

        if (!list.get(position).isNew){
            cardView.setCardBackgroundColor(Color.parseColor("#E0E0E0"));
        } else {
            cardView.setCardBackgroundColor(Color.WHITE);
        }

        if (list.get(position).isRead) {
            holder.titleView.setTextColor(Color.parseColor("#FF757575"));
        } else {
            holder.titleView.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    /*
    public void remove(int position){
        list.remove(position);
        notifyItemRemoved(position);
    }
    */
}
