package com.example.myapplication.BulletinBoard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.myapplication.Activity.BasicActivity;
import com.example.myapplication.R;
import com.example.myapplication.Writeinfo;
import com.example.myapplication.adapter.MainAdapter;
import com.example.myapplication.listener.OnPostListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;

import static com.example.myapplication.Util.isStorageUrl;
import static com.example.myapplication.Util.showToast;
import static com.example.myapplication.Util.storageUrlToName;

public class TransferActivity extends BasicActivity {
    private static final String TAG = "TransferActivity";
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageRef;
    private MainAdapter mainAdapter;
    private ArrayList<Writeinfo> postList;
    private int successCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        postList = new ArrayList<>();
        mainAdapter = new MainAdapter(TransferActivity.this, postList);
        mainAdapter.setOnPostListener(onPostListener);

        RecyclerView recyclerView = findViewById(R.id.recyclerView4);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(TransferActivity.this));
        recyclerView.setAdapter(mainAdapter);

        FloatingActionButton btn_plus3 = findViewById(R.id.btn_plus3);
        btn_plus3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TransferActivity.this, TransferWriteActivity.class);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        postUpdate();
    }


   OnPostListener onPostListener = new OnPostListener() {
       /* @Override*/
        public void onDelete(int position) {
            final String id = postList.get(position).getId();

            ArrayList<String> contentsList = postList.get(position).getContents();
            for (int i = 0; i < contentsList.size(); i++) {
                String contents = contentsList.get(i);
                if (isStorageUrl(contents)) {
                    successCount++;
                    StorageReference desertRef = storageRef.child("posts/" + id + "/" + storageUrlToName(contents));
                    desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            successCount--;
                            storeUploader(id);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            showToast(TransferActivity.this, "ERROR");
                        }
                    });
                }
            }
            storeUploader(id);
        }

       @Override
       public void onDelete() {

       }

      /* @Override*/
        public void onModify(int position) {
            String id = postList.get(position).getId();
            startActivity(TransferWriteActivity.class, postList.get(position));
        }

       @Override
       public void onModify() {

       }
   };

    private void postUpdate() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        if (firebaseUser != null) {
            CollectionReference collectionReference = firebaseFirestore.collection("posts3");
            collectionReference.orderBy("createAt", Query.Direction.DESCENDING).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            postList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                postList.add(new Writeinfo(
                                        document.getData().get("title").toString(),
                                        (ArrayList<String>) document.getData().get("contents"),
                                        (ArrayList<String>) document.getData().get("formats"),
                                        document.getData().get("publisher").toString(),
                                        new Date(document.getDate("createAt").getTime()),
                                        document.getId()));
                            }
                            mainAdapter.notifyDataSetChanged();

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    });
        }
    }

    private void storeUploader(String id) {
        if (successCount == 0) {
            firebaseFirestore.collection("posts3").document(id)
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            showToast(TransferActivity.this, "게시글을 삭제하였습니다.");
                            postUpdate();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showToast(TransferActivity.this, "게시글을 삭제하지 못했습니다.");
                        }
                    });
        }
    }

    private void startActivity(Class c, Writeinfo writeinfo) {
        Intent intent = new Intent(this, c);
        intent.putExtra("writeinfo", writeinfo);
        startActivity(intent);
    }


}