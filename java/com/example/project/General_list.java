package com.example.project;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.core.content.ContextCompat;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class General_list extends AppCompatActivity {
    private List<MyList1> myLists;
    private MyAdapter1 adapter;
    private MyAdapter1.RecyclerViewClickListener listener;
    private FloatingActionButton newadd;
    RecyclerView recyclerView;
    private String mText = "";
    FirebaseFirestore db;
    int no1;
    String email,des;
    DocumentReference ref;
    DocumentReference ref1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_list);
        recyclerView = (RecyclerView)findViewById(R.id.rec1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(General_list.this));
        newadd = (FloatingActionButton) findViewById(R.id.newadd);
        myLists = new ArrayList<>();
        Bundle bundle=getIntent().getExtras();
        email=bundle.getString("email");
        des=bundle.getString("des");
        myLists=new ArrayList<>();
        db=FirebaseFirestore.getInstance();
        ref=db.collection("Users").document(email).collection(des).document(des);
        ref1=db.collection("Users").document(email);
        final int[] flag = {0};
        ref1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot data1=task.getResult();
                Map<String,String> dash=(Map<String,String>)data1.get("Dashboard");
                for(Map.Entry<String,String>entry:dash.entrySet())
                {
                    if(entry.getKey().equalsIgnoreCase(des))
                    {
                        if(!entry.getValue().equalsIgnoreCase("0"))
                        {
                            ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful())
                                    {DocumentSnapshot data = task.getResult();
                                        if (data != null) {
                                            Map<String, String> comp = (Map<String, String>) data.get("Components");
                                            for (Map.Entry<String, String> entry : comp.entrySet()) {
                                                myLists.add(new MyList1(entry.getKey(), entry.getValue()));
                                            }
                                            adapter = new MyAdapter1(myLists, getApplicationContext(), email, des,listener);
                                            recyclerView.setAdapter(adapter);
                                        }
                                    }}
                            });
                        }
                        break;
                    }
                }
            }
        });
        setOnClickListener();
        newadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(General_list.this);
                builder.setTitle("Name of the component");

                View viewInflated = LayoutInflater.from(General_list.this).inflate(R.layout.dialogbox, null);
// Set up the input
                final EditText input = (EditText) viewInflated.findViewById(R.id.inputname);

// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                builder.setView(viewInflated);
// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mText = input.getText().toString();
                        myLists.add(new MyList1(mText));
                        adapter = new MyAdapter1(myLists, getApplicationContext(),email,des,listener);
                        recyclerView.setAdapter(adapter);
                        ref.update("Components."+mText,"0");
                        ref1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot data = task.getResult();
                                Map<String,String>dashboard= (Map<String,String>) data.get("Dashboard");
                                for(Map.Entry<String,String> entry:dashboard.entrySet())
                                {
                                    if(entry.getKey().equalsIgnoreCase(des))
                                    {    int s= Integer.parseInt(entry.getValue());
                                    s++;
                                            ref1.update("Dashboard."+des,String.valueOf(s));
                                        break;
                                    }
                                }}
                        });
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                Button b = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                b.setTextColor(Color.MAGENTA);
                Button b1 = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                b1.setTextColor(Color.MAGENTA);
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }
    String deleted = null;
    String no;
    MyList1 del;
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            switch(direction){
                case ItemTouchHelper.LEFT:
                    deleted = myLists.get(position).getText();
                    no=myLists.get(position).getNo();
                    del = myLists.get(position);
                    myLists.remove(position);
                    adapter.notifyDataSetChanged();
                    ref.update("Components."+deleted, FieldValue.delete());
                    ref1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot data = task.getResult();
                            Map<String,String>dashboard= (Map<String,String>) data.get("Dashboard");
                            for(Map.Entry<String,String> entry:dashboard.entrySet())
                            {
                                if(entry.getKey().equalsIgnoreCase(des))
                                {  int s= Integer.parseInt(entry.getValue());
                                    s--;
                                    ref1.update("Dashboard."+des,String.valueOf(s));
                                    break;
                                }
                            }}
                    });
                    Snackbar.make(recyclerView, deleted, Snackbar.LENGTH_LONG)
                            .setAction("Undo", new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    myLists.add(position, del);
                                    adapter.notifyItemInserted(position);
                                    ref.update("Components."+deleted, no);
                                    ref1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            DocumentSnapshot data = task.getResult();
                                            Map<String,String>dashboard= (Map<String,String>) data.get("Dashboard");
                                            for(Map.Entry<String,String> entry:dashboard.entrySet())
                                            {
                                                if(entry.getKey().equalsIgnoreCase(des))
                                                { int s= Integer.parseInt(entry.getValue());
                                                    s++;
                                                    ref1.update("Dashboard."+des,String.valueOf(s));
                                                    break;
                                                }
                                            }}
                                    });
                                }
                            }).show();
                    break;
                case ItemTouchHelper.RIGHT:
                    String shareable=myLists.get(position).getText();
                    String no=myLists.get(position).getNo();
                    MyList1 temp=myLists.get(position);
                    String message=shareable+" - "+no;
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, message);
                    sendIntent.setType("message/rfc822");

                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                    startActivity(shareIntent);
                    myLists.remove(position);
                    adapter.notifyDataSetChanged();
                    myLists.add(position,temp);
                    adapter.notifyItemInserted(position);
                    break;
            }

        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(General_list.this,R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(General_list.this,R.color.purple_200))
                    .addSwipeRightActionIcon(R.drawable.ic_baseline_share_24)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem item=menu.findItem(R.id.action_search);
        SearchView searchView=(SearchView)item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    private void setOnClickListener() {
        listener=new MyAdapter1.RecyclerViewClickListener() {
            @Override
            public void OnClick(View v, int position) {
                if(des.equalsIgnoreCase("Groceries"))
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.amazon.in/" +
                            "Gourmet-Specialty-Foods/b/?ie=UTF8&node=2454178031&ref_=topnav_storetab_topnav_storetab_gourmet"));
                    startActivity(intent);
                }
                else if(des.equalsIgnoreCase("Furniture"))
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.amazon.in/gp/browse.html?node=1380441031&" +
                            "ref_=nav_em_sbc_hk_furniture_0_2_12_5"));
                    startActivity(intent);
                }
                else if(des.equalsIgnoreCase("Kitchen Appliances"))
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.amazon.in/gp/browse.html?node=4951860031&" +
                            "ref_=nav_em_sbc_tvelec_kitchen_appliances_0_2_9_18"));
                    startActivity(intent);
                }
                else
                {
                    Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://www.amazon.in/gp/" +
                            "goldbox?ref_=nav_cs_gb"));
                    startActivity(intent);
                }
            }
        };
    }
}