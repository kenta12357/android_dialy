package com.example.user.gamearticlesmenu;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class ViewHolder extends RecyclerView.ViewHolder {
    public TextView titleView;
    public TextView urlView;

    public ViewHolder (View itemView) {
        super(itemView);
        titleView = (TextView) itemView.findViewById(R.id.list_item_title);
        urlView = (TextView) itemView.findViewById(R.id.list_item_url);
    }
}
