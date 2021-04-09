package com.example.android.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class PhoneActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText editPhone, inputVerificationCode;
    private Button verifyCodeButton, verifyNumberButton;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    CountryCodePicker ccp;
    private ProgressDialog loadingBar;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        ccp = findViewById(R.id.ccp);
        editPhone = findViewById(R.id.editPhone);
        verifyNumberButton = findViewById(R.id.button);
        inputVerificationCode = findViewById(R.id.codeInput);
        verifyCodeButton = findViewById(R.id.verifyButton);

        loadingBar = new ProgressDialog(this);


        ccp.registerCarrierNumberEditText(editPhone);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.e("Phone", "onVerificationCompleted");
                signIn(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.e("Phone", "onVerificationFailed");
                loadingBar.dismiss();
                Toast.makeText(PhoneActivity.this, "Подтвердить номер не удалось. Введите верный номер", Toast.LENGTH_SHORT).show();
                editPhone.setVisibility(View.VISIBLE);
                verifyNumberButton.setVisibility(View.VISIBLE);

                verifyCodeButton.setVisibility(View.INVISIBLE);
                inputVerificationCode.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Log.e("Phone", "onCodeSent");
                // Save verification ID and resending token so we can use them later
                mVerificationId = s;
                mResendToken = forceResendingToken;

                verifyNumberButton.setVisibility(View.INVISIBLE);
                editPhone.setVisibility(View.INVISIBLE);

                loadingBar.dismiss();

                Toast.makeText(PhoneActivity.this, "Введите код проверки", Toast.LENGTH_SHORT).show();

                verifyCodeButton.setVisibility(View.VISIBLE);
                inputVerificationCode.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Log.e("Phone", "onCodeAutoRetrievalTimeOut");
            }
        };
    }

    public void onClick(View view) {
        String phone = ccp.getFullNumberWithPlus();

        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(PhoneActivity.this, "Пожалуйста введите номер...", Toast.LENGTH_SHORT).show();
        }
        else {

            loadingBar.setTitle("Подтверждение номера...");
            loadingBar.setMessage("Пожалуйста подождите.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            PhoneAuthOptions options =
                    PhoneAuthOptions.newBuilder(mAuth)
                            .setPhoneNumber(phone)       // Phone number to verify
                            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                            .setActivity(this)                 // Activity (for callback binding)
                            .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                            .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
        }
    }

    private void signIn(PhoneAuthCredential phoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                loadingBar.dismiss();
                startActivity(new Intent(PhoneActivity.this, ProfileActivity.class));
                finish();
            } else {
                Toast.makeText(PhoneActivity.this, "Ошибка авторизации" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onClickVerifyCode(View view) {
        verifyNumberButton.setVisibility(View.INVISIBLE);
        editPhone.setVisibility(View.INVISIBLE);

        String verificationCode = inputVerificationCode.getText().toString();

        if (TextUtils.isEmpty(verificationCode))
        {
            Toast.makeText(PhoneActivity.this, "Введите код проверки", Toast.LENGTH_SHORT).show();
        }  else {
            loadingBar.setTitle("Подтверждение кода проверки");
            loadingBar.setMessage("Пожалуйста подождите");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
            signIn(credential);
        }

    }
}