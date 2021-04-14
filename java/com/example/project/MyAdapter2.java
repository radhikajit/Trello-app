package com.example.project;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
public class MyAdapter2  extends RecyclerView.Adapter<MyAdapter2.ViewHolder2> implements Filterable {
    private List<MyList2>myLists;
    private Context context;
    private List<MyList2>myListsAll;
    public MyAdapter2(List<MyList2> myLists, Context context) {
        this.myLists = myLists;
        this.context = context;
        this.myListsAll=new ArrayList<>(myLists);
    }

    @NonNull
    @Override
    public MyAdapter2.ViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_todo,parent,false);
        return new MyAdapter2.ViewHolder2(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder2 holder, int position) {
        MyList2 myList=myLists.get(position);
        holder.alarm.setText(myList.getText());
    }

    @Override
    public int getItemCount() {
        return myLists.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
    Filter filter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<MyList2> filteredList= new ArrayList<>();
            if(constraint.toString().isEmpty()){
                filteredList.addAll(myListsAll);
            }
            else{
                for(MyList2 temp:myListsAll){
                    if(temp.getText().toLowerCase().contains(constraint.toString().toLowerCase()))
                        filteredList.add(temp);
                }
            }
            FilterResults filterResults= new FilterResults();
            filterResults.values=filteredList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            myLists.clear();
            myLists.addAll((Collection<? extends MyList2>)results.values);
            notifyDataSetChanged();
        }
    };
    public class ViewHolder2 extends RecyclerView.ViewHolder{
        private TextView alarm;
        public ViewHolder2(@NonNull View itemView) {
            super(itemView);
            alarm = (TextView) itemView.findViewById(R.id.alarm);
        }
    }
}