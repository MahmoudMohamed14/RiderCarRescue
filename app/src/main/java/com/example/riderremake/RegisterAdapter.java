package com.example.riderremake;



import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.riderremake.Model.RiderInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class RegisterAdapter extends RecyclerView.Adapter<RegisterAdapter.RegisterHolder> {

    private  FirebaseAuth.AuthStateListener listener;
    Register registerActivity;

    public RegisterAdapter(Register registerActivity) {
        this.registerActivity = registerActivity;
    }


    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference databaseReference=database.getReference("Riders");
    FirebaseAuth auth=FirebaseAuth.getInstance();
    @NonNull
    @Override
    public RegisterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RegisterHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.register, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RegisterHolder holder, int position) {
      holder.btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isConnected(registerActivity)){
                    Toast.makeText(registerActivity, " Please check Internet Connection!", Toast.LENGTH_SHORT).show();

                }else {
                   String email = holder.userEmail.getText().toString().trim();
                    final String password =holder.userPassword.getText().toString().trim();
                    final String cpassword = holder.confirmPassword.getText().toString().trim();
                    final String fname = holder.firstName.getText().toString().trim();
                    final String lname = holder.lastName.getText().toString().trim();
                    final String phone = holder.userPhone.getText().toString().trim();


                    if (fname.isEmpty()) {
                        holder.firstName.setError("write name");
                        holder.firstName.requestFocus();
                        return;
                    }
                    if (lname.isEmpty()) {
                        holder.lastName.setError("write name");
                        holder.lastName.requestFocus();
                        return;
                    }

                    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        holder.userEmail.setError("enter vaild email");
                        holder.userEmail.requestFocus();
                        return;
                    }
                    if (email.isEmpty()) {
                        holder.userEmail.setError("write email");
                        holder.userEmail.requestFocus();
                        return;
                    }
                    if (password.isEmpty()) {
                        holder.userPassword.setError("Requred");
                        holder.userPassword.requestFocus();
                        return;
                    }
                    if (!cpassword.equals(password)) {
                        holder.confirmPassword.setError("Not Match");
                        holder.confirmPassword.requestFocus();
                        return;
                    }
                    if (password.length() < 6) {
                        holder.userPassword.setError("length must 6 Number");
                        holder.userPassword.requestFocus();
                        return;
                    }
                    if (phone.isEmpty()) {
                       holder.userPhone.setError("Requred");
                      holder.userPhone.requestFocus();
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
                        holder.userPhone.setError("invalid phone");
                        holder.userPhone.requestFocus();
                        return;
                    }
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(registerActivity, new OnCompleteListener<AuthResult>() {
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


                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(registerActivity, "Sign Up Failed Failed", Toast.LENGTH_SHORT).show();
                                    }

                                    // ...
                                }
                            });

                }




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
      holder.btn_signIn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {


              registerActivity.startActivity(new Intent(registerActivity,MainActivity.class));


          }
      });


    }
    private boolean isConnected(Register mainActivity) {
        ConnectivityManager connectivityManager= (ConnectivityManager) mainActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiConn=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if((wifiConn!=null&&wifiConn.isConnected())||(mobileConn!=null&&mobileConn.isConnected())){
            return true;
        }else {
            return false;
        }

    }



    @Override
    public int getItemCount() {
        return 1;
    }

    public class RegisterHolder extends RecyclerView.ViewHolder {
        EditText firstName, lastName, userEmail, userPhone, userPassword, confirmPassword;
        Button btn_signUp, btn_signIn;




        public RegisterHolder(@NonNull View itemView) {
            super(itemView);
            lastName = itemView.findViewById(R.id.lastName);
            firstName = itemView.findViewById(R.id.firstName);

            userEmail = itemView.findViewById(R.id.email);
            userPhone = itemView.findViewById(R.id.phone);
            userPassword = itemView.findViewById(R.id.password);
            confirmPassword = itemView.findViewById(R.id.confirmPassword);
            btn_signIn = itemView.findViewById(R.id.btn_sign_in);
            btn_signUp = itemView.findViewById(R.id.signUp);

        }
    }

}
