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

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
public class MyAdapter1  extends RecyclerView.Adapter<MyAdapter1.ViewHolder1> implements Filterable {
    private List<MyList1>myLists;
    private Context context;
    private List<MyList1>myListsAll;
    private RecyclerViewClickListener listener;
    DocumentReference ref;
    String email,des;
    FirebaseFirestore db;
    public MyAdapter1(List<MyList1> myLists, Context context, String email, String des,
                      RecyclerViewClickListener listener) {
        this.myLists = myLists;
        this.context = context;
        this.email=email;
        this.des=des;
        this.listener=listener;
        this.myListsAll=new ArrayList<>(myLists);
    }
    @NonNull
    @Override
    public MyAdapter1.ViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_general_list,parent,false);
        return new MyAdapter1.ViewHolder1(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder1 holder, int position) {
        MyList1 myList=myLists.get(position);
        holder.text.setText(myList.getText());
        holder.no.setText(myList.getNo());
        db=FirebaseFirestore.getInstance();
        ref=db.collection("Users").document(email).collection(des).document(des);
        holder.decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int n=Integer.parseInt(myList.getNo());
                n--;
                myList.setNo(String.valueOf(n));
                holder.no.setText(myList.getNo());
                ref.update("Components."+myList.getText(),myList.getNo());
            }
        });
        holder.increase.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                int n=Integer.parseInt(myList.getNo());
                n++;
                myList.setNo(String.valueOf(n));
                holder.no.setText(myList.getNo());
                ref.update("Components."+myList.getText(),myList.getNo());
            }
        });
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
            List<MyList1> filteredList= new ArrayList<>();
            if(constraint.toString().isEmpty()){
                filteredList.addAll(myListsAll);
            }
            else{
                for(MyList1 temp:myListsAll){
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
            myLists.addAll((Collection<? extends MyList1>)results.values);
            notifyDataSetChanged();
        }
    };
    public class ViewHolder1 extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView text;
        private TextView no;
        private Button decrease;
        private Button increase;
        private Button buynow;
        public ViewHolder1(@NonNull View itemView) {
            super(itemView);
            text=(TextView)itemView.findViewById(R.id.text);
            no=(TextView) itemView.findViewById(R.id.integer_number);
            decrease=(Button)itemView.findViewById(R.id.decrease);
            increase=(Button)itemView.findViewById(R.id.increase);
            buynow=(Button)itemView.findViewById(R.id.buynow);
            buynow.setOnClickListener(this);
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
