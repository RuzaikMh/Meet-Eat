package com.example.resturant;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import dmax.dialog.SpotsDialog;
import io.reactivex.disposables.CompositeDisposable;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.resturant.Common.Common;
import com.example.resturant.Model.UserModel;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static int APP_REQUEST_CODE = 7171;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;
    private AlertDialog dialog;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private DatabaseReference userRef;
    private List<AuthUI.IdpConfig> provider;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(listener);
    }

    @Override
    protected void onStop() {
        if(listener != null)
            firebaseAuth.removeAuthStateListener(listener);
        compositeDisposable.clear();
        super.onStop();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init(){
        provider = Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build());
        userRef = FirebaseDatabase.getInstance().getReference(Common.USER_REFERENCES);
        firebaseAuth = FirebaseAuth.getInstance();
        dialog = new SpotsDialog.Builder().setCancelable(false).setContext(this).build();
        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    CheckUserFromFirebase(user);
                }else {
                    phoneLogin();
                }
            }
        };
    }

    private void CheckUserFromFirebase(FirebaseUser user) {
        dialog.show();
        userRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Toast.makeText(MainActivity.this, "You already registered", Toast.LENGTH_SHORT).show();

                    UserModel userModel = snapshot.getValue(UserModel.class);
                    goToHomeActivity(userModel);
                }else {
                    showRegisterDialog(user);
                }

                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRegisterDialog(FirebaseUser user){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Register");
        builder.setMessage("Please fill Information");

        View itemview = LayoutInflater.from(this).inflate(R.layout.layout_register,null);
        EditText edit_name = itemview.findViewById(R.id.edit_name);
        EditText edit_address = itemview.findViewById(R.id.edit_address);
        EditText edit_phone = itemview.findViewById(R.id.edit_phone);

        edit_phone.setText(user.getPhoneNumber());

        builder.setView(itemview);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("Register", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(TextUtils.isEmpty(edit_name.getText().toString())){
                    Toast.makeText(MainActivity.this, "Please Enter Your name", Toast.LENGTH_SHORT).show();
                    return;
                }else if(TextUtils.isEmpty(edit_address.getText().toString())){
                    Toast.makeText(MainActivity.this, "Please Enter Your address", Toast.LENGTH_SHORT).show();
                    return;
                }

                UserModel userModel = new UserModel();
                userModel.setUid(user.getUid());
                userModel.setName(edit_name.getText().toString());
                userModel.setAddress(edit_address.getText().toString());
                userModel.setPhones(edit_phone.getText().toString());


                userRef.child(user.getUid()).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()){
                            dialogInterface.dismiss();
                            Toast.makeText(MainActivity.this, "Register Complete", Toast.LENGTH_SHORT).show();
                            goToHomeActivity(userModel);
                        }
                    }
                });
            }


        });

        builder.setView(itemview);

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void goToHomeActivity(UserModel userModel) {
        Common.currentUser = userModel;
        startActivity(new Intent(MainActivity.this,HomeActivity.class));
        finish();
    }

    private void phoneLogin() {

        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(provider).build(), APP_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == APP_REQUEST_CODE){
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if(resultCode == RESULT_OK){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            }else {
                Toast.makeText(this, "Failed to sign in!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}