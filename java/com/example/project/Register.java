package com.example.project;
import android.content.Intent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import android.widget.ArrayAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import android.widget.AdapterView;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private final AppCompatActivity activity = Register.this;
    private static final String TAG = "gmailverify";
    private RelativeLayout relative;
    private TextInputLayout textInputLayoutName;
    private TextInputEditText textInputEditTextName;
    private Spinner profession;
    private TextInputLayout emailtemp;
    private TextInputEditText email;
    private AppCompatButton appCompatButtonRegister;
    private TextInputLayout textInputLayoutPassword1;
    private TextInputEditText password;
    private AppCompatTextView appCompatTextViewLoginLink;
    private static int c=0;
    String phone;
    FirebaseFirestore db ;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    String[] list = { "Businessman", "Homemaker", "Teacher"};
    private String temp;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Bundle bundle=getIntent().getExtras();
        phone=bundle.getString("phone");
        db=FirebaseFirestore.getInstance();
        relative = (RelativeLayout) findViewById(R.id.relative);
        textInputLayoutName=(TextInputLayout) findViewById(R.id.textInputLayoutName) ;
        textInputEditTextName=(TextInputEditText)findViewById(R.id.textInputEditTextName);
        profession=(Spinner)findViewById(R.id.profession);
        profession.setOnItemSelectedListener(this);
        emailtemp = (TextInputLayout) findViewById(R.id.emailtemp);
        email = (TextInputEditText) findViewById(R.id.email);
        textInputLayoutPassword1=(TextInputLayout)findViewById(R.id.textInputLayoutPassword1) ;
        password=(TextInputEditText)findViewById(R.id.password);
        appCompatButtonRegister = (AppCompatButton) findViewById(R.id.appCompatButtonRegister);
        appCompatTextViewLoginLink = (AppCompatTextView) findViewById(R.id.appCompatTextViewLoginLink);
        appCompatTextViewLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRegister = new Intent(activity, LoginActivity.class);
                startActivity(intentRegister);
            }
        });
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        profession.setAdapter(dataAdapter);

        firebaseAuth=FirebaseAuth.getInstance();
        appCompatButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(),
                        password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            firebaseAuth.getCurrentUser().sendEmailVerification().
                                    addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(Register.this,"Email Verification sent",Toast.LENGTH_LONG).show();
                                                Map<String, String> dashboard= new HashMap<>();
                                                dashboard.put("Groceries","0");
                                                dashboard.put("To-do List","0");
                                                dashboard.put("Meetings","0");
                                                dashboard.put("Tasks","0");
                                                User user = new User(textInputEditTextName.getText().toString(),email.getText().toString(),
                                                        password.getText().toString(), temp,phone);
                                                db.collection("Users").document(email.getText().toString()).set(user);
                                                DocumentReference ref=db.collection("Users").document(email.getText().toString());
                                                ref.update("Dashboard",dashboard);
                                                Map<String,String>Components=new HashMap<>();
                                                db.collection("Users").document(email.getText().toString()).
                                                        collection("Groceries").document("Groceries").set(Components);
                                                Map<String,String>alarm=new HashMap<>();
                                                db.collection("Users").document(email.getText().toString()).
                                                        collection("To-do List").document("To-do List").set(alarm);
                                                db.collection("Users").document(email.getText().toString()).collection("Meetings")
                                                        .document("Meetings").set(alarm);
                                            }
                                            else{
                                                Toast.makeText(Register.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                        }
                    }
                });
            }

        });
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        temp=list[position];
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
        temp="Businessman";
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}