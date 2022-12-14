package com.zkrallah.postswithroom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MAINACTIVITY";
    private RecyclerView postsRecyclerView;
    private EditText titleEt, bodyEt;
    PostsAdapter adapter;
    PostsDatabase postsDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button insertBtn = findViewById(R.id.insertButton);
        Button getBtn = findViewById(R.id.getButton);

        titleEt = findViewById(R.id.editTexttitle);
        bodyEt = findViewById(R.id.editTextBody);

        postsRecyclerView = findViewById(R.id.posts_recyclerView);
        postsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        postsRecyclerView.setLayoutManager(linearLayoutManager);

        postsDatabase = PostsDatabase.getInstance(this);

        insertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insert(titleEt.getEditableText().toString(), bodyEt.getEditableText().toString());
                getData();

            }
        });


        getBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData();
            }
        });

    }

    private void insert(String title, String body){
        postsDatabase.postsDao().insertPost(new Post(2, title, body))
                .subscribeOn(Schedulers.computation())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e);
                    }
                });
        Toast.makeText(MainActivity.this, "Inserted Successfully", Toast.LENGTH_SHORT).show();
        titleEt.setText("");
        bodyEt.setText("");
    }

    public void getData(){
        postsDatabase.postsDao().getPosts()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Post>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<Post> posts) {
                        adapter = new PostsAdapter(MainActivity.this, posts);
                        postsRecyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: " + e);
                    }
                });
    }
}