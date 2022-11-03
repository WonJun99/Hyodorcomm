package com.example.myapplication.Activity;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.myapplication.BulletinBoard.NewActivity;
import com.example.myapplication.BulletinBoard.ReturnActivity;
import com.example.myapplication.BulletinBoard.TransferActivity;
import com.example.myapplication.R;
import com.example.myapplication.Writeinfo;
import com.example.myapplication.adapter.MainAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends BasicActivity {
    private static final String TAG = "MainActivity";

    String user_id = null;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setToolbarTitle(getResources().getString(R.string.app_name));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user == null){
            startActivity(RegisterActivity.class);
        } else {
            //회원가입 or 로그인
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d(TAG, "No such document");
                                startActivity(MemberinitActivity.class);
                            }
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });



        }

        mFirebaseAuth = FirebaseAuth.getInstance();
        //FirebaseUser user = mFirebaseAuth.getCurrentUser();

        Button btn_logout = findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //로그아웃하기
                mFirebaseAuth.signOut();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }


        });

        //탈퇴 처리
        Button btn_delete = findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                mFirebaseAuth.getCurrentUser().delete();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button btn_new = findViewById(R.id.btn_new);
        btn_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, NewActivity.class);
                startActivity(intent);
            }

        });


        Button btn_return = findViewById(R.id.btn_return);
        btn_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, ReturnActivity.class);
                startActivity(intent);
            }
        });

        Button btn_transfer = findViewById(R.id.btn_transfer);
        btn_transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this, TransferActivity.class);
                startActivity(intent);
            }
        });
    }

    private void startActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivity(intent);
    }


}