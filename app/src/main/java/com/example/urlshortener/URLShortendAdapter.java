package com.example.urlshortener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.List;

public class URLShortendAdapter extends RecyclerView.Adapter<URLShortendAdapter.ViewHolder> {
    private Context context;
    public static final String TAG = OverviewActivity.class.getSimpleName();
    private List<URLShortend> list;
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(URLShortend urlShortend);
    }

    public URLShortendAdapter(Context context, List<URLShortend> list, OnItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public URLShortendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.single_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull URLShortendAdapter.ViewHolder holder, int position) {
        URLShortend urlShortend = list.get(position);

        holder.bind(urlShortend, listener);



    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    // this nested class offers the access to the views of the list item
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView shortIdentifier, redirectURL;
        public ImageButton editButton, deleteButton;
        private View frontLayout, backLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            shortIdentifier = itemView.findViewById(R.id.shortIdentifier);
            redirectURL = itemView.findViewById(R.id.redirectURL);
            deleteButton = itemView.findViewById(R.id.delete_button);
            editButton = itemView.findViewById(R.id.edit_button);
            frontLayout = itemView.findViewById(R.id.front_layout);
            backLayout = itemView.findViewById(R.id.back_layout);
        }

        public void bind(URLShortend urlShortend, OnItemClickListener listener){

            shortIdentifier.setText(urlShortend.getShortIdentifier());
            redirectURL.setText(urlShortend.getRedirectURL());

            frontLayout.setOnClickListener(v -> {
                String url = URLs.ROOT_URL + urlShortend.getShortIdentifier();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            });

            editButton.setOnClickListener(v -> {
                Intent i = new Intent(context, EditURLShortendActivity.class);
                i.putExtra("ID", urlShortend.getId());
                context.startActivity(i);
            });

            deleteButton.setOnClickListener(v -> listener.onItemClick(urlShortend));
        }
    }
}
