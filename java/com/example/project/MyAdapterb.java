package com.example.project;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import android.database.DataSetObserver;
public class MyAdapterb extends RecyclerView.Adapter<MyAdapterb.ViewHolder> {
    private List<MyList>myLists;
    private Context context;
    private RecyclerViewClickListener listener;

    public MyAdapterb(List<MyList> myLists, Context context,RecyclerViewClickListener listener) {
        this.myLists = myLists;
        this.context = context;
        this.listener=listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_business,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MyList myList=myLists.get(position);
        holder.btn.setText(myList.getDesc());
        holder.img.setImageDrawable(context.getResources().getDrawable(myList.getImage()));
    }

    @Override
    public int getItemCount() {
        return myLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView img;
        private TextView btn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img=(ImageView)itemView.findViewById(R.id.image);
            btn=(TextView) itemView.findViewById(R.id.desc);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.OnClick(v,getAdapterPosition());
        }
    }
    public interface RecyclerViewClickListener{
        void OnClick(View v,int position);
    }
}