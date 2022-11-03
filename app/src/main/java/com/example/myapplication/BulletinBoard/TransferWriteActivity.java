package com.example.myapplication.BulletinBoard;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.myapplication.Activity.BasicActivity;
import com.example.myapplication.Activity.GalleryActivity;
import com.example.myapplication.R;
import com.example.myapplication.Writeinfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import static com.example.myapplication.Util.INTENT_PATH;
import static com.example.myapplication.Util.isImageFile;
import static com.example.myapplication.Util.isStorageUrl;
import static com.example.myapplication.Util.isVideoFile;
import static com.example.myapplication.Util.showToast;
import static com.example.myapplication.Util.storageUrlToName;

public class TransferWriteActivity extends BasicActivity {
    private static final String TAG = "TransferWriteActivity";
    private FirebaseUser user;
    private StorageReference storageRef;
    private ArrayList<String> pathList = new ArrayList<>();
    private LinearLayout parent;
    private RelativeLayout ButtonBackgroundLayout;
    private ImageView selectedImageView;
    private EditText selectedEditText;
    private EditText contentsEditText;
    private EditText titleEditText;
    private Writeinfo writeinfo;
    private int pathCount, successCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_write_post);

        parent = findViewById(R.id.contentsLayout3);
        ButtonBackgroundLayout = findViewById(R.id.ButtonBackgroundLayout3);
        contentsEditText = findViewById(R.id.contentsEditText3);
        titleEditText = findViewById(R.id.titleEditText3);

        findViewById(R.id.btn_upload3).setOnClickListener(onClickListener);
        findViewById(R.id.btn_image3).setOnClickListener(onClickListener);
        findViewById(R.id.btn_video3).setOnClickListener(onClickListener);
        findViewById(R.id.imageModify3).setOnClickListener(onClickListener);
        findViewById(R.id.videoModify3).setOnClickListener(onClickListener);
        findViewById(R.id.Modifydelete3).setOnClickListener(onClickListener);

        ButtonBackgroundLayout.setOnClickListener(onClickListener);
        contentsEditText.setOnFocusChangeListener(onFocusChangeListener);
        titleEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    selectedEditText = null;
                }
            }
        });

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        writeinfo = (Writeinfo)getIntent().getSerializableExtra("writeinfo");
        postInit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {
                    String path = data.getStringExtra(INTENT_PATH);
                    pathList.add(path);

                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    LinearLayout linearLayout = new LinearLayout(TransferWriteActivity.this);
                    linearLayout.setLayoutParams(layoutParams);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);

                    if(selectedEditText == null){
                        parent.addView(linearLayout);
                    } else {
                        for(int i = 0; i < parent.getChildCount(); i++){
                            if(parent.getChildAt(i) == selectedEditText.getParent()){
                                parent.addView(linearLayout, i + 1);
                                break;
                            }
                        }
                    }

                    ImageView imageView = new ImageView(TransferWriteActivity.this);
                    imageView.setLayoutParams(layoutParams);
                    imageView.setAdjustViewBounds(true);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ButtonBackgroundLayout.setVisibility(View.VISIBLE);
                            selectedImageView = (ImageView) v;
                        }
                    });

                    Glide.with(this).load(path).override(1000).into(imageView);
                    linearLayout.addView(imageView);

                    EditText editText = new EditText(TransferWriteActivity.this);
                    editText.setLayoutParams(layoutParams);
                    editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
                    editText.setHint("내용");
                    editText.setOnFocusChangeListener(onFocusChangeListener);
                    linearLayout.addView(editText);
                }
                break;
            case 1:
                if(resultCode == Activity.RESULT_OK){
                    String path = data.getStringExtra(INTENT_PATH);
                    pathList.set(parent.indexOfChild((View)selectedImageView.getParent()) - 1, path);
                    Glide.with(this).load(path).override(1000).into(selectedImageView);
                }
                break;
        }
    }

    View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()){
            case R.id.btn_upload3:
                storageUpload();
                break;
            case R.id.btn_image3:
                startActivity(GalleryActivity.class, "image",0);
                break;
            case R.id.btn_video3:
                startActivity(GalleryActivity.class, "video",0) ;
                break;
            case R.id.ButtonBackgroundLayout3:
                if(ButtonBackgroundLayout.getVisibility() == View.VISIBLE){
                    ButtonBackgroundLayout.setVisibility(View.GONE);
                }
                break;
            case R.id. imageModify3:
                startActivity(GalleryActivity.class, "image", 1);
                ButtonBackgroundLayout.setVisibility(View.GONE);
                break;
            case R.id.videoModify3:
                startActivity(GalleryActivity.class, "video", 1);
                ButtonBackgroundLayout.setVisibility(View.GONE);
                break;
            case R.id.Modifydelete3:
                View selectedView = (View)selectedImageView.getParent();

                StorageReference desertRef = storageRef.child("posts/"+writeinfo.getId()+"/"+storageUrlToName(pathList.get(parent.indexOfChild(selectedView) -1)));
                desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showToast(TransferWriteActivity.this,"파일 삭제 성공");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        showToast(TransferWriteActivity.this,"파일 삭제 실패");
                    }
                });
                pathList.remove(parent.indexOfChild(selectedView) - 1);
                parent.removeView(selectedView);
                ButtonBackgroundLayout.setVisibility(View.GONE);
                break;
        }
    };

    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if(hasFocus){
                selectedEditText = (EditText) v;
            }
        }
    };

    public void storageUpload() {
        final String title = ((EditText) findViewById(R.id.titleEditText3)).getText().toString();

        if (title.length() > 0) {
            final ArrayList<String> contentsList = new ArrayList<>();
            final ArrayList<String> formatList = new ArrayList<>();

            user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            final DocumentReference documentReference = writeinfo == null ? firebaseFirestore.collection("posts3").document() : firebaseFirestore.collection("posts3").document(writeinfo.getId());
            final Date date = writeinfo == null ? new Date() : writeinfo.getCreateAt();

            for(int i = 0; i < parent.getChildCount(); i++){
                LinearLayout linearLayout = (LinearLayout)parent.getChildAt(i);
                for(int ii = 0; ii < linearLayout.getChildCount(); ii++){
                    View view = linearLayout.getChildAt(ii);
                    if(view instanceof EditText){
                        String text = ((EditText)view).getText().toString();
                        if(text.length() > 0){
                            formatList.add(text);
                        }
                    } else if (!isStorageUrl(pathList.get(pathCount))){
                        String path = pathList.get(pathCount);
                        successCount++;
                        contentsList.add(path);

                        if(isImageFile(path)){
                            formatList.add("image");
                        } else if (isVideoFile(path)){
                            formatList.add("video");
                        } else {
                            formatList.add("text");
                        }

                        String[] pathArray = path.split("\\.");
                        final StorageReference mountainImagesRef = storageRef.child("posts/" +documentReference.getId() + "/"+pathCount+"."+pathArray[pathArray.length -1]);
                        try {
                            InputStream stream = new FileInputStream(new File(pathList.get(pathCount)));
                            StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("index",""+(contentsList.size()-1)).build();
                            UploadTask uploadTask = mountainImagesRef.putStream(stream, metadata);
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    final int index = Integer.parseInt(taskSnapshot.getMetadata().getCustomMetadata("index"));
                                    mountainImagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            contentsList.set(index, uri.toString());
                                            successCount--;
                                            if (successCount == 0){
                                                //완료
                                                Writeinfo writeinfo = new Writeinfo(title, contentsList, formatList, user.getUid(), date);
                                                storeUpload(documentReference, writeinfo);
                                            }
                                        }
                                    });
                                }
                            });
                        } catch (FileNotFoundException e){
                        }

                        pathCount++;
                    }
                }
            }
            if(successCount == 0){
                storeUpload(documentReference, new Writeinfo(title, contentsList, formatList, user.getUid(), date));
            }
        } else {

            showToast(TransferWriteActivity.this,"제목을 입력해주세요.");
        }
    }

    private void storeUpload(DocumentReference documentReference, Writeinfo writeinfo) {
        documentReference.set(writeinfo.getWriteinfo())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

    }

    private void postInit(){
        if(writeinfo != null){
            titleEditText.setText(writeinfo.getTitle());
            ArrayList<String> contentsList = writeinfo.getContents();
            for (int i = 0; i < contentsList.size(); i++) {
                String contents = contentsList.get(i);
                if (isStorageUrl(contents)) {
                    pathList.add(contents);

                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    LinearLayout linearLayout = new LinearLayout(TransferWriteActivity.this);
                    linearLayout.setLayoutParams(layoutParams);
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    parent.addView(linearLayout);

                    ImageView imageView = new ImageView(TransferWriteActivity.this);
                    imageView.setLayoutParams(layoutParams);
                    imageView.setAdjustViewBounds(true);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ButtonBackgroundLayout.setVisibility(View.VISIBLE);
                            selectedImageView = (ImageView) v;
                        }
                    });

                    Glide.with(this).load(contents).override(1000).into(imageView);
                    linearLayout.addView(imageView);

                    EditText editText = new EditText(TransferWriteActivity.this);
                    editText.setLayoutParams(layoutParams);
                    editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT);
                    editText.setHint("내용");
                    if(i < contentsList.size() - 1){
                        String nextContents = contentsList.get(i + 1);
                        if(!isStorageUrl(nextContents)){
                            editText.setText(nextContents);
                        }
                    }
                    editText.setOnFocusChangeListener(onFocusChangeListener);
                    linearLayout.addView(editText);
                }else if(i == 0){
                    contentsEditText.setText(contents);
                }
            }
        }
    }

    private void startActivity(Class c, String media, int requestCode) {
        Intent intent = new Intent(this, c);
        intent.putExtra("media", media);
        startActivityForResult(intent, requestCode);
    }

}
