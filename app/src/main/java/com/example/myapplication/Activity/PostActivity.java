package com.example.myapplication.Activity;

import android.app.Activity;
import android.content.Intent;
import android.icu.text.Transliterator;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.myapplication.BulletinBoard.NewWriteActivity;
import com.example.myapplication.BulletinBoard.ReturnWriteActivity;
import com.example.myapplication.FirebaseHelper;
import com.example.myapplication.R;
import com.example.myapplication.Writeinfo;
import com.example.myapplication.listener.OnPostListener;
import com.example.myapplication.view.ContentsItemView;
import com.example.myapplication.view.ReadContentsView;

import static com.example.myapplication.Util.INTENT_PATH;

public class PostActivity extends BasicActivity {
    private Writeinfo writeinfo;
    private FirebaseHelper firebaseHelper;
    private ReadContentsView readContentsView;
    private LinearLayout contentsLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        writeinfo = (Writeinfo)getIntent().getSerializableExtra("writeinfo");
        contentsLayout = findViewById(R.id.contentsLayout);
        readContentsView = findViewById(R.id.readContentsView);

        firebaseHelper = new FirebaseHelper(this);
        firebaseHelper.setOnPostListener(onPostListener);
        uiUpdate();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == Activity.RESULT_OK) {
                    writeinfo = (Writeinfo) data.getSerializableExtra("writeinfo");
                    contentsLayout.removeAllViews();
                    uiUpdate();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                firebaseHelper.storageDelete(writeinfo);
                finish();
                return true;

            case R.id.modify:
                startActivity(NewWriteActivity.class, writeinfo);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    OnPostListener onPostListener = new OnPostListener() {
        @Override
        public void onDelete() {
            Log.e("로그","삭제 성공");
        }

        @Override
        public void onModify() {
            Log.e("로그","수정 성공");

        }
    };

    private void uiUpdate(){
        readContentsView.setPostInfo(writeinfo);
        setToolbarTitle(writeinfo.getTitle());
    }

    private void startActivity(Class c, Writeinfo writeinfo) {
        Intent intent = new Intent(this, c);
        intent.putExtra("writeinfo", writeinfo);
        startActivityForResult(intent,0);
    }
}
