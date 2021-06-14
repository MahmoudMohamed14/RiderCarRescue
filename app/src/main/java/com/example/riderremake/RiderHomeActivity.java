package com.example.riderremake;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
@SuppressWarnings("unchecked")
public class RiderHomeActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUST = 3;
    private AppBarConfiguration mAppBarConfiguration;
    NavigationView navigationView;
    DrawerLayout drawer;
    NavController navController;
    CircleImageView image_profile;
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    private StorageReference mStorageRef;
    Uri uriprofile;
    AlertDialog waitdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_rider);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
         navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setDrawerLayout(drawer)
                .build();
         navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        init();
    }

    private void init() {
        mStorageRef= FirebaseStorage.getInstance().getReference();
        waitdialog = new AlertDialog.Builder(this).setCancelable(false)
                .setMessage("waiting...").create();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId()==R.id.nav_sign_out){
                    //sign out message
                    AlertDialog.Builder builder=new     AlertDialog.Builder(RiderHomeActivity.this);
                    builder.setTitle("Sign Out").setMessage("Do you really want to Sign out").setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setPositiveButton("SIGN OUT", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseAuth.getInstance().signOut();

                            Intent intent=new Intent(RiderHomeActivity.this,MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();

                        }
                    }).setCancelable(false);
                    final AlertDialog dialog=builder.create();
                    //control button
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface DialogInterface) {
                            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(RiderHomeActivity.this,android.R.color.holo_red_dark));
                            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(RiderHomeActivity.this,R.color.colorAccent));

                        }
                    });
                    dialog.show();

                }
                return true;
            }
        });
        View headerView=navigationView.getHeaderView(0);
        final TextView name=headerView.findViewById(R.id.name_profile);
        final TextView phone=headerView.findViewById(R.id.phone_profile);
        image_profile=headerView.findViewById(R.id.image_profile);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Riders");
        myRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               RiderInfo user=snapshot.getValue(RiderInfo.class);
                if(user!=null){
                    name.setText(user.getName());
                    phone.setText(user.getPhone());
                    Glide.with(RiderHomeActivity.this).load(user.getImage()).into(image_profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RiderHomeActivity.this, "some thing rong"+error, Toast.LENGTH_SHORT).show();

            }
        });



        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showimage();

            }
        });

    }
    private void showimage() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUST&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null){
            if(data!=null&&data.getData()!=null){
                uriprofile = data.getData();
                Glide.with(this).load(uriprofile).into(image_profile);
                uploadimage();
            }
            


            
                
        }
    }

    private void uploadimage() {
        AlertDialog.Builder builder=new     AlertDialog.Builder(RiderHomeActivity.this);
        builder.setTitle("Change Image")
                .setMessage("Do you really want to Change Image ")
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("UPLOAD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              if(uriprofile!=null){
                  waitdialog.setMessage("Uploading...");
                  waitdialog.show();
              }
              String unique_name=FirebaseAuth.getInstance().getCurrentUser().getUid();
              final StorageReference imagefolder=mStorageRef.child("image/"+unique_name);
              imagefolder.putFile(uriprofile).addOnFailureListener(new OnFailureListener() {
                  @Override
                  public void onFailure(@NonNull Exception e) {
                      waitdialog.dismiss();
                      Snackbar.make(drawer,e.getMessage(),Snackbar.LENGTH_LONG).show();
                  }
              }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                  @Override
                  public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                      if(task.isSuccessful()){
                          imagefolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                              @Override
                              public void onSuccess(Uri uri) {
                                  HashMap<String,Object> updatedata=new HashMap<>() ;
                                  updatedata.put("image",uri.toString());
                                  FirebaseDatabase.getInstance().getReference(Common.RIDER__INFO)
                                          .child(FirebaseAuth.getInstance().getCurrentUser()
                                          .getUid()).updateChildren(updatedata);
                              }
                          });
                      }
                      waitdialog.dismiss();
                  }
              }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                  @Override
                  public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                      double progress=(100.0*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                      waitdialog.setMessage(new StringBuilder("Uploading: ").append(progress).append("%"));
                  }
              });

            }
        }).setCancelable(false);
        final AlertDialog dialog=builder.create();
        //control button
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface DialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(RiderHomeActivity.this,android.R.color.holo_red_dark));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(RiderHomeActivity.this,R.color.colorAccent));

            }
        });
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}