package com.example.project;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Homemaker extends AppCompatActivity {
    private List<MyList>myLists;
    private MyAdapter adapter;
    private FloatingActionButton add;
    private MyAdapter.RecyclerViewClickListener listener;
    RecyclerView recyclerView;
    private String mText="";
    FirebaseFirestore db;
    String email,name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homemaker);
        Bundle bundle=getIntent().getExtras();
        email=bundle.getString("email");
        name=bundle.getString("name");
        myLists=new ArrayList<>();
        recyclerView=(RecyclerView)findViewById(R.id.rec);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        db=FirebaseFirestore.getInstance();
        DocumentReference ref=db.collection("Users").document(email);
        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot data = task.getResult();
                Map<String,String>dashboard= (Map<String,String>) data.get("Dashboard");
                name=data.getString("username");
                for(String comp:dashboard.keySet())
                {
                    if(comp.equalsIgnoreCase("Groceries"))
                    {
                        myLists.add(new MyList(R.mipmap.groceries_foreground,comp));
                    }
                    else if(comp.equalsIgnoreCase("To-do List"))
                    {
                        myLists.add(new MyList(R.mipmap.todo_foreground,comp));
                    }
                    else if(comp.equalsIgnoreCase("Meetings"))
                    {
                        myLists.add(new MyList(R.mipmap.call_foreground,comp));
                    }
                    else if(comp.equalsIgnoreCase("Furniture"))
                        myLists.add(new MyList(R.mipmap.furniture_foreground,comp));
                    else if(comp.equalsIgnoreCase("Kitchen Appliances"))
                        myLists.add(new MyList(R.mipmap.kitchen_foreground,comp));
                    else if(comp.equalsIgnoreCase("Tasks"))
                        myLists.add(new MyList(R.mipmap.task_foreground,comp));
                    else
                        myLists.add(new MyList(R.mipmap.call_foreground,comp));
                }
                adapter=new MyAdapter(myLists,getApplicationContext(),listener);
                recyclerView.setAdapter(adapter);
            }
        });
        add=(FloatingActionButton)findViewById(R.id.add) ;
        setOnClickListener();
        add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                AlertDialog.Builder builder = new AlertDialog.Builder(Homemaker.this);
                builder.setTitle("Name of the component");

                View viewInflated = LayoutInflater.from(Homemaker.this).inflate(R.layout.dialogbox, null);
// Set up the input
                final EditText input = (EditText) viewInflated.findViewById(R.id.inputname);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                builder.setView(viewInflated);
// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mText = input.getText().toString();
                        if(mText.equalsIgnoreCase("Furniture"))
                            myLists.add(new MyList(R.mipmap.furniture_foreground,mText));
                        else if(mText.equalsIgnoreCase("Kitchen Appliances"))
                            myLists.add(new MyList(R.mipmap.kitchen_foreground,mText));
                        else
                            myLists.add(new MyList(R.mipmap.call_foreground,mText));
                        adapter=new MyAdapter(myLists,getApplicationContext(),listener);
                        recyclerView.setAdapter(adapter);
                        ref.update("Dashboard."+mText,"0");
                        Map<String,String>Components=new HashMap<>();
                        db.collection("Users").document(email).collection(mText).document(mText).set(Components);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert=builder.create();
                alert.show();
                Button b=alert.getButton(DialogInterface.BUTTON_POSITIVE);
                b.setTextColor(Color.BLUE);
                Button b1=alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                b1.setTextColor(Color.BLUE);
            }});
    }

    private void setOnClickListener() {
        listener=new MyAdapter.RecyclerViewClickListener() {
            @Override
            public void OnClick(View v, int position) {
                if(myLists.get(position).getDesc().equalsIgnoreCase("Groceries"))
                {
                    Intent intent=new Intent(getApplicationContext(),General_list.class);
                    intent.putExtra("des",myLists.get(position).getDesc());
                    intent.putExtra("email",email);
                    startActivity(intent);
                }
                else if(myLists.get(position).getDesc().equalsIgnoreCase("To-do List"))
                {
                    Intent intent=new Intent(getApplicationContext(),Todo.class);
                    intent.putExtra("email",email);
                    startActivity(intent);
                }
                else if(myLists.get(position).getDesc().equalsIgnoreCase("Meetings"))
                {
                    Intent intent=new Intent(getApplicationContext(),DashBoardActivity.class);
                    intent.putExtra("name",name);
                    startActivity(intent);
                }
                else if(myLists.get(position).getDesc().equalsIgnoreCase("Furniture"))
                {
                    Intent intent=new Intent(getApplicationContext(),General_list.class);
                    intent.putExtra("des",myLists.get(position).getDesc());
                    intent.putExtra("email",email);
                    startActivity(intent);
                }
                else if(myLists.get(position).getDesc().equalsIgnoreCase("Kitchen Appliances"))
                {
                    Intent intent=new Intent(getApplicationContext(),General_list.class);
                    intent.putExtra("des",myLists.get(position).getDesc());
                    intent.putExtra("email",email);
                    startActivity(intent);
                }
                else if(myLists.get(position).getDesc().equalsIgnoreCase("Tasks"))
                {
                    Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                    intent.putExtra("name",name);
                    startActivity(intent);
                }
                else
                {
                    Intent intent=new Intent(getApplicationContext(),General_list.class);
                    intent.putExtra("des",myLists.get(position).getDesc());
                    intent.putExtra("email",email);
                    startActivity(intent);
                }
            }
        };
    }

}