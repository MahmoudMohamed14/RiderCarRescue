package com.example.riderremake;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.riderremake.Services.UserUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth auth;
    private  FirebaseAuth.AuthStateListener listener;

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    @Override
    protected void onStart() {

        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent=new Intent(MainActivity.this,RiderHomeActivity.class);
            startActivity(intent);
            finish();
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    Log.d("token",instanceIdResult.getToken());
                    UserUtils.updateToken(MainActivity.this,instanceIdResult.getToken());

                }
            });



        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference(Common.RIDER__INFO);
        auth=FirebaseAuth.getInstance();
        showloginlayout();
    }
    private void ResetPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.forget_password, null);
        final EditText resetemail = view.findViewById(R.id.email_reset);
        Button resetpassword = view.findViewById(R.id.reset_password);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
        resetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final String email = resetemail.getText().toString().trim();
                if (email.isEmpty()) {
                    resetemail.setError("email is requried");
                    resetemail.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    resetemail.setError("please inter valid email");
                    resetemail.requestFocus();
                    return;
                }
                auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(v.getContext(), " check yor email", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(v.getContext(), " some thing wrong happend!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }


        });

    }
    private void showregistyerlayout() {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        View view= LayoutInflater.from(this).inflate(R.layout.regester,null);
        final EditText firstName = view.findViewById(R.id.firstName);
        final EditText  lastName = view.findViewById(R.id.lastName);
        final EditText useremail = view.findViewById(R.id.email);
        final EditText userphone = view.findViewById(R.id.phone);
        final EditText  userpassword = view.findViewById(R.id.password);
        final EditText confirmPassword =view.findViewById(R.id.confirmPassword);

        Button  signUp = view.findViewById(R.id.signUp);


        builder.setView(view);
        final AlertDialog dialog=builder.create();
        dialog.show();
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = useremail.getText().toString().trim();
                final String password = userpassword.getText().toString().trim();
                final String cpassword = confirmPassword.getText().toString().trim();
                final String fname = firstName.getText().toString().trim();
                final String lname = lastName.getText().toString().trim();
                final String phone = userphone.getText().toString().trim();


                if (fname.isEmpty()) {
                    firstName.setError("corent password");
                    firstName.requestFocus();
                    return;
                }
                if (lname.isEmpty()) {
                    lastName.setError("corent password");
                    lastName.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    useremail.setError("enter vaild email");
                    useremail.requestFocus();
                    return;
                }
                if (email.isEmpty()) {
                    useremail.setError("Requer");
                    useremail.requestFocus();
                    return;
                }
                if (password.isEmpty()) {
                    userpassword.setError("Requred");
                    userpassword.requestFocus();
                    return;
                }
                if (!cpassword.equals(password)) {
                    userpassword.setError("Not Match");
                    userpassword.requestFocus();
                    return;
                }
                if (password.length() < 6) {
                    userpassword.setError("length must 6 Number");
                    userpassword.requestFocus();
                    return;
                }
                if (phone.isEmpty()) {
                    userphone.setError("Requred");
                    userphone.requestFocus();
                    return;
                }
                /*
                if(phone.length()<11){
                   userphone .setError("invalid phone");
                   userphone.requestFocus();
                    return;
                }

                 */
                if (!Patterns.PHONE.matcher(phone).matches()) {
                    userphone.setError("invalid phone");
                    userphone.requestFocus();
                    return;
                }
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    String id = auth.getCurrentUser().getUid();
                                   // String token = FirebaseInstanceId.getInstance().getToken();
                                   RiderInfo driverInfo = new RiderInfo(  /*name,email,phone,id,password,"0.0"   */);
                                    HashMap<String,Object> hashMap=new HashMap<>();
                                    hashMap.put("name",fname +" "+lname);
                                    hashMap.put("id",id);
                                    hashMap.put("email",email);
                                    hashMap.put("password",password);
                                    hashMap.put("phone",phone);

                                    hashMap.put("image","default");
                                    //hashMap.put("token",token);


                                    databaseReference.child(auth.getCurrentUser().getUid()).setValue(hashMap);
                                    dialog.dismiss();

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(MainActivity.this, "Sign Up Failed Failed", Toast.LENGTH_SHORT).show();
                                }

                                // ...
                            }
                        });


                /*



                    databaseReference.child(auth.getCurrentUser().getUid()).setValue(driverInfo).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            dialog.dismiss();
                            Toast.makeText(SplashActivity.this, "faild!", Toast.LENGTH_SHORT).show();

                        }

                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(SplashActivity.this, "Register Succesfully! ", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            GotoHomeActivity(driverInfo);
                        }
                    });

                }
                */
            }
        });




    }
    private void showloginlayout() {


        final EditText username= findViewById(R.id.user_name);
        final EditText passwordd=findViewById(R.id.pass_word);
        TextView forgetpassword=findViewById(R.id.forget_password);
        Button sinin= findViewById(R.id.sin_in);
        TextView signUp = findViewById(R.id.sin_up_text);
        forgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResetPassword();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 showregistyerlayout();
            }
        });
        sinin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String email = username.getText().toString().trim();
                String password = passwordd.getText().toString().trim();

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    username.setError("enter vaild email");
                    username.requestFocus();
                    return;
                }
                if(email.isEmpty()){
                    username .setError("Requer");
                    username.requestFocus();
                    return;
                }
                if(password.isEmpty()){
                    passwordd .setError("Requred");
                    passwordd.requestFocus();
                    return;
                }

                if(password.length()<6){
                    passwordd .setError("length must 6 Number");
                    passwordd.requestFocus();
                    return;
                }

                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                           Intent intent=new Intent(MainActivity.this,RiderHomeActivity.class);
                           startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(v.getContext(), "failed password", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });



    }
}