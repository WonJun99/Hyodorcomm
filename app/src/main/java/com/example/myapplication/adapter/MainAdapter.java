package com.example.myapplication.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.collection.CircularArray;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.Activity.PostActivity;
import com.example.myapplication.BulletinBoard.NewWriteActivity;
import com.example.myapplication.BulletinBoard.ReturnWriteActivity;
import com.example.myapplication.FirebaseHelper;
import com.example.myapplication.R;
import com.example.myapplication.Writeinfo;
import com.example.myapplication.listener.OnPostListener;
import com.example.myapplication.view.ReadContentsView;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {
private ArrayList<Writeinfo> mDataset;
private Activity activity;
private FirebaseHelper firebaseHelper;
private final int MORE_INDEX = 2;

public static class MainViewHolder extends RecyclerView.ViewHolder {
    public CardView cardView;
    public MainViewHolder(CardView v) {
        super(v);
        cardView = v;

    }
}

    public MainAdapter(Activity activity, ArrayList<Writeinfo> myDataset) {
        this.mDataset = myDataset;
        this.activity = activity;


        firebaseHelper = new FirebaseHelper(activity);
    }

    public void setOnPostListener(OnPostListener onPostListener){
        firebaseHelper.setOnPostListener(onPostListener);
    }

    @Override
    public int getItemViewType(int position){
    return position;
    }

    @NonNull
    @Override
    public MainAdapter.MainViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        final MainViewHolder mainViewHolder = new MainViewHolder(cardView);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, PostActivity.class);
                intent.putExtra("writeinfo", mDataset.get(mainViewHolder.getAdapterPosition()));
                activity.startActivity(intent);
            }
        });

        cardView.findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v, mainViewHolder.getAdapterPosition());
            }
        });

        return mainViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MainViewHolder holder, int position) {
        CardView cardView = holder.cardView;
        TextView titleTextView = cardView.findViewById(R.id.titleTextView);

        Writeinfo writeinfo = mDataset.get(position);
        titleTextView.setText(writeinfo.getTitle());

        ReadContentsView readContentsView = cardView.findViewById(R.id.readContentsView);
        LinearLayout contentsLayout = cardView.findViewById(R.id.contentsLayout);

        if(contentsLayout.getTag() == null || !contentsLayout.getTag().equals(writeinfo)){
            contentsLayout.setTag(writeinfo);
            contentsLayout.removeAllViews();

            readContentsView.setMoreIndex(MORE_INDEX);
            readContentsView.setPostInfo(writeinfo);
        }
    }

    @Override
    public int getItemCount() {
    return mDataset.size();
    }

    private void showPopup(View v, int position) {
        PopupMenu popup = new PopupMenu(activity, v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.modify:
                            startActivity(ReturnWriteActivity.class,mDataset.get(position));
                        return true;
                    case R.id.delete:
                        firebaseHelper.storageDelete(mDataset.get(position));
                        return true;
                    default:
                        return false;
                }
            }
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.post, popup.getMenu());
        popup.show();
    }

    private void startActivity(Class c, Writeinfo writeinfo) {
        Intent intent = new Intent(activity, c);
        intent.putExtra("writeinfo", writeinfo);
        activity.startActivity(intent);
    }
}
