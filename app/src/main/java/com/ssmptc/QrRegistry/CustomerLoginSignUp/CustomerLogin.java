package com.ssmptc.QrRegistry.CustomerLoginSignUp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ssmptc.QrRegistry.DataBase.SessionManagerCustomer;
import com.ssmptc.QrRegistry.R;

public class CustomerLogin extends AppCompatActivity {

    SessionManagerCustomer managerCustomer;

    EditText phoneNumber, password;
    Button b2,b3;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_login);

        phoneNumber = findViewById(R.id.log_phone);
        password = findViewById(R.id.log_password);

        b2 = findViewById(R.id.btn_login);

        b3 = findViewById(R.id.btn_callSignUp);
        //Create a Session
        managerCustomer = new SessionManagerCustomer(getApplicationContext());



        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),CustomerSignup.class);

                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View,String>(findViewById(R.id.btn_callSignUp),"transition_signUp");

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(CustomerLogin.this,pairs);
                    startActivity(intent,options.toBundle());
                }
                else{
                    finish();
                }
            }
        });
    }

    public void login(View view) {

        //Initialize ProgressDialog
        progressDialog = new ProgressDialog(CustomerLogin.this);

        progressDialog.show();

        progressDialog.setContentView(R.layout.progress_dialog);

       progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        String _phoneNumber = phoneNumber.getText().toString().trim();
        String _password = password.getText().toString().trim();

        if (_phoneNumber.charAt(0) == '0') {

            _phoneNumber = _phoneNumber.substring(1);
        }

        String _completePhoneNumber = "+91" + _phoneNumber;

        //Database
        Query checkUser = FirebaseDatabase.getInstance().getReference("Users").orderByChild("phoneNo").equalTo(_completePhoneNumber);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    phoneNumber.setError(null);
                    String systemPassword = snapshot.child(_completePhoneNumber).child("password").getValue(String.class);

                    if (systemPassword.equals(_password)) {
                        password.setError(null);

                        String _name = snapshot.child(_completePhoneNumber).child("name").getValue(String.class);
                        String _email = snapshot.child(_completePhoneNumber).child("email").getValue(String.class);
                        String _phoneNo = snapshot.child(_completePhoneNumber).child("phoneNo").getValue(String.class);
                        String _password = snapshot.child(_completePhoneNumber).child("password").getValue(String.class);


                        managerCustomer.setCustomerLogin(true);

                        managerCustomer.setDetails(_name, _email, _phoneNo, _password);

                        startActivity(new Intent(getApplicationContext(), CustomerDashBoard.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();


                        //   Toast.makeText(CustomerLogin.this, "Hai", Toast.LENGTH_SHORT).show();
                        //  Intent intent = new Intent(CustomerLogin.this, QRCodeGeneration.class);
                        //  intent.putExtra("name",_name);
                        // intent.putExtra("email",_email);
                        //  intent.putExtra("phoneNo",_phoneNo);
                        // startActivity(intent);

                    } else {
                        Toast.makeText(CustomerLogin.this, "Password Doesn't Match!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CustomerLogin.this, "User Does Not Exist!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(CustomerLogin.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}

