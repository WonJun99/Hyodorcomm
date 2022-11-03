package com.example.myapplication.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.myapplication.R;
import com.example.myapplication.Reportinfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class LightbulbActivity extends BasicActivity {
    private static final String TAG = "LightbulbActivity";

    EditText title, contents;
    Button btn_bulbupload;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lightbulb);

        setToolbarTitle("문의 접수");

        title = findViewById(R.id.bulbEditText);
        contents = findViewById(R.id.bulbEditText2);
        btn_bulbupload = findViewById(R.id.btn_bulbupload);


        btn_bulbupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_title = title.getText().toString();
                String txt_contents = contents.getText().toString();
                if (TextUtils.isEmpty(txt_title) || TextUtils.isEmpty(txt_contents)){
                    Toast.makeText(LightbulbActivity.this,"내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else {
                    profileUpdate(txt_title, txt_contents);
                }
            }
        });
    }


    public void profileUpdate(String txtTitle, String txt_contents) {
        final String title = ((EditText) findViewById(R.id.bulbEditText)).getText().toString();
        final String write = ((EditText) findViewById(R.id.bulbEditText2)).getText().toString();

        if (title.length() > 0 && write.length() > 0) {
            Toast.makeText(LightbulbActivity.this, "접수 완료", Toast.LENGTH_SHORT).show();
            user = FirebaseAuth.getInstance().getCurrentUser();
            Reportinfo reportinfo = new Reportinfo(user.getUid(), title, write);
            uploader(reportinfo);
            finish();

        } else {
            startToast("접수 실패");
            return;
        }
    }

    private void uploader(Reportinfo reportinfo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("bulb").add(reportinfo)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID" + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG,"Error",e);
                    }
                });
    }

    private void startToast(String msg)
    {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();}

}