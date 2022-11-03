package com.example.myapplication.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class ReportActivity extends BasicActivity {
    private static final String TAG = "ReportActivity";

    EditText title, contents;
    Button btn_reportupload;
    TextView forbid_behaviour;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        setToolbarTitle("신고 접수");


        title = findViewById(R.id.reportEditText);
        contents = findViewById(R.id.reportEditText2);
        btn_reportupload = findViewById(R.id.btn_reportupload);
        forbid_behaviour = findViewById(R.id.forbid_behaviour);

        forbid_behaviour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReportActivity.this, ForbidBehaviourActivity.class));
            }
        });

        btn_reportupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_title = title.getText().toString();
                String txt_contents = contents.getText().toString();
                if (TextUtils.isEmpty(txt_title) || TextUtils.isEmpty(txt_contents)){
                    Toast.makeText(ReportActivity.this,"내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else {
                    profileUpdate(txt_title, txt_contents);
                }
            }
        });
    }

    public void profileUpdate(String txtTitle, String txt_contents) {
        final String title = ((EditText) findViewById(R.id.reportEditText)).getText().toString();
        final String write = ((EditText) findViewById(R.id.reportEditText2)).getText().toString();

        if (title.length() > 0 && write.length() > 0) {
            Toast.makeText(ReportActivity.this, "접수 완료", Toast.LENGTH_SHORT).show();
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
        db.collection("report").add(reportinfo)
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