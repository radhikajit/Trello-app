package com.example.project;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import androidx.appcompat.widget.AppCompatButton;
import java.util.concurrent.TimeUnit;
public class otpgen extends AppCompatActivity{
    private AppCompatButton appCompatButtonSendOtp;
    private AppCompatButton appCompatButtonVerify;
    private RelativeLayout rel;
    private TextInputLayout textInputLayoutPhone;
    private TextInputLayout textInputLayoutOtp;
    private TextInputEditText textInputEditTextPhone;
    private TextInputEditText textInputEditTextOtp;

    String phoneNumber, otp;

    FirebaseAuth auth;

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private String verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpgen);
        findViews();

        StartFirebaseLogin();
        appCompatButtonSendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber=textInputEditTextPhone.getText().toString();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber,                     // Phone number to verify
                        60,                           // Timeout duration
                        TimeUnit.SECONDS,                // Unit of timeout
                        otpgen.this,        // Activity (for callback binding)
                        mCallback);                      // OnVerificationStateChangedCallbacks
            }
        });

        appCompatButtonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otp=textInputEditTextOtp.getText().toString();

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, otp);

                SigninWithPhone(credential);
            }
        });
    }
    private void SigninWithPhone(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent =new Intent(otpgen.this,Register.class);
                            intent.putExtra("phone",phoneNumber);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(otpgen.this,"Incorrect OTP",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void findViews() {
        rel = (RelativeLayout) findViewById(R.id.rel);
        textInputLayoutPhone = (TextInputLayout) findViewById(R.id.textInputLayoutPhone);
        textInputLayoutOtp = (TextInputLayout) findViewById(R.id.textInputLayoutOtp);
        appCompatButtonSendOtp=findViewById(R.id.appCompatButtonSendOtp);
        appCompatButtonVerify=findViewById(R.id.appCompatButtonVerify);
        textInputEditTextPhone=findViewById(R.id.textInputEditTextPhone);
        textInputEditTextOtp=findViewById(R.id.textInputEditTextOtp);
    }

    private void StartFirebaseLogin() {

        auth = FirebaseAuth.getInstance();
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(otpgen.this,"verification completed",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(otpgen.this,"verification failed",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String s,  PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationCode = s;
                Toast.makeText(otpgen.this,"Code sent",Toast.LENGTH_SHORT).show();
            }
        };
    }


}