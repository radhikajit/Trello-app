package com.example.project;
import androidx.annotation.NonNull;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.DialogFragment;
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

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import androidx.core.content.ContextCompat;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class Todo extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    private List<MyList2> myLists;
    private MyAdapter2 adapter;
    private FloatingActionButton newadd1;
    RecyclerView recyclerView;
    private String mText = "";
    private Button buttonTimePicker;
    private TextView name;
    FirebaseFirestore db;
    DocumentReference ref;
    DocumentReference ref1;
    String email;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        Bundle bundle=getIntent().getExtras();
        email=bundle.getString("email");
        db=FirebaseFirestore.getInstance();
        ref=db.collection("Users").document(email).collection("To-do List")
                .document("To-do List");
        ref1=db.collection("Users").document(email);
        ref1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot data1=task.getResult();
                Map<String,String> dash=(Map<String,String>)data1.get("Dashboard");
                for(Map.Entry<String,String>entry:dash.entrySet())
                {
                    if(entry.getKey().equalsIgnoreCase("To-do List"))
                    {
                        if(!entry.getValue().equalsIgnoreCase("0"))
                        {
                            ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful())
                                    {DocumentSnapshot data = task.getResult();
                                        if (data != null) {
                                            String hour= (String) data.get("hour");
                                            String minute=(String)data.get("minute");
                                            int h=Integer.parseInt(hour);
                                            int min=Integer.parseInt(minute);
                                            Calendar c = Calendar.getInstance();
                                            c.set(Calendar.HOUR_OF_DAY, h-1);
                                            c.set(Calendar.MINUTE, min);
                                            c.set(Calendar.SECOND, 0);
                                            updateTimeText(c);
                                            startAlarm(c);
                                            Map<String,String> alarms=(Map<String,String>)data.get("alarms");
                                            for(String comp:alarms.keySet())
                                            {
                                                myLists.add(new MyList2(comp));
                                            }
                                            adapter = new MyAdapter2(myLists, getApplicationContext());
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
        name =(TextView) findViewById(R.id.textView3);
        buttonTimePicker =(Button)findViewById(R.id.button_timepicker);
        buttonTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.rec2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        newadd1 = (FloatingActionButton) findViewById(R.id.newadd1);
        myLists = new ArrayList<>();
        newadd1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Todo.this);
                builder.setTitle("Name of the activity");

                View viewInflated = LayoutInflater.from(Todo.this).inflate(R.layout.dialogbox, null);
// Set up the input
                final EditText input = (EditText) viewInflated.findViewById(R.id.inputname);

// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                builder.setView(viewInflated);
// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mText = input.getText().toString();
                        myLists.add(new MyList2(mText));
                        adapter = new MyAdapter2(myLists, getApplicationContext());
                        recyclerView.setAdapter(adapter);
                        ref.update("alarms."+mText,mText);
                        ref1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot data = task.getResult();
                                Map<String,String>dashboard= (Map<String,String>) data.get("Dashboard");
                                for(Map.Entry<String,String> entry:dashboard.entrySet())
                                {
                                    if(entry.getKey().equalsIgnoreCase("To-do List"))
                                    {  int s= Integer.parseInt(entry.getValue());
                                        s++;
                                        ref1.update("Dashboard.To-do List",String.valueOf(s));
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
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay-1);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        updateTimeText(c);
        startAlarm(c);
        ref.update("hour",String.valueOf(hourOfDay));
        ref.update("minute",String.valueOf(minute));
    }
    private void updateTimeText(Calendar c) {
        String timeText = "Alarm set: ";
        timeText += DateFormat.getTimeInstance(DateFormat.SHORT).format(c.getTime());
        name.setText(timeText);
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(Todo.this, AlertReceiver.class);
        intent.putExtra("alarm_message","Complete today's work");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(Todo.this, 1, intent, 0);
        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }
    String deleted = null;
    MyList2 del;
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
                    del = myLists.get(position);
                    myLists.remove(position);
                    adapter.notifyDataSetChanged();
                    ref.update("alarms."+deleted, FieldValue.delete());
                    ref1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot data = task.getResult();
                            Map<String,String>dashboard= (Map<String,String>) data.get("Dashboard");
                            for(Map.Entry<String,String> entry:dashboard.entrySet())
                            {
                                if(entry.getKey().equalsIgnoreCase("To-do List"))
                                {  int s= Integer.parseInt(entry.getValue());
                                    s--;
                                    ref1.update("Dashboard.To-do List",String.valueOf(s));
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
                                    ref.update("alarms."+deleted,deleted);
                                    ref1.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            DocumentSnapshot data = task.getResult();
                                            Map<String,String>dashboard= (Map<String,String>) data.get("Dashboard");
                                            for(Map.Entry<String,String> entry:dashboard.entrySet())
                                            {
                                                if(entry.getKey().equalsIgnoreCase("To-do List"))
                                                {  int s= Integer.parseInt(entry.getValue());
                                                    s--;
                                                    ref1.update("Dashboard.To-do List",String.valueOf(s));
                                                    break;
                                                }
                                            }}
                                    });
                                }
                            }).show();
                    break;
                case ItemTouchHelper.RIGHT:
                    String shareable=myLists.get(position).getText();
                    MyList2 temp=myLists.get(position);
                    String message=shareable;
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
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(Todo.this,R.color.red))
                    .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(Todo.this,R.color.purple_200))
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
}