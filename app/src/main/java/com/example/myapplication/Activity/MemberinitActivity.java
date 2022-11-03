package com.example.myapplication.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.myapplication.Memberinfo;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import static com.example.myapplication.Util.INTENT_PATH;
import static com.example.myapplication.Util.showToast;

public class MemberinitActivity extends BasicActivity {
    private static final String TAG = "MemberinitActivity";
    private FirebaseUser user;
    private ImageView profileImageVIew;
    private String profilePath;
    private DatabaseReference mDatabaseRef; // 실시간 데이터베이스
    private EditText mEtEmail, mEtPwd; // 로그인 입력필드

    //뒤로가기
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0: {
                if (requestCode == Activity.RESULT_OK) {
                    profilePath = data.getStringExtra(INTENT_PATH);
                    Glide.with(this).load(profilePath).centerCrop().override(500).into(profileImageVIew);
                }
                break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_init);
        setToolbarTitle("회원정보 입력");


        mDatabaseRef = FirebaseDatabase.getInstance().getReference("MyApplication");

        mEtEmail = findViewById(R.id.et_email);
        mEtPwd = findViewById(R.id.et_pwd);

        Button btn_gallery = findViewById(R.id.btn_gallery);
        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MemberinitActivity.this, GalleryActivity.class);
                startActivity(intent);
            }
        });

        Button btn_ok = findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 로그인 요청
                String name = ((EditText)findViewById(R.id.nameeditText)).getText().toString();
                String PhoneNumber = ((EditText)findViewById(R.id.PhoneNumbereditText)).getText().toString();
                String Date = ((EditText)findViewById(R.id.DateeditText)).getText().toString();
                String Address = ((EditText)findViewById(R.id.Address)).getText().toString();

                if(name.length() > 0 && PhoneNumber.length() > 9 && Date.length() > 5 && Address.length() > 0) {

                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    Memberinfo memberinfo = new Memberinfo(name, PhoneNumber, Date, Address);

                    if (user != null) {
                        db.collection("users").document(user.getUid()).set(memberinfo)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        showToast(MemberinitActivity.this,"회원정보 등록을 성공하였습니다.");
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        showToast(MemberinitActivity.this,"회원정보 등록에 실패하였습니다.");
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });
                    }
                }

            }

        });
    }

}
