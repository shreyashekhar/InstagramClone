package com.example.instagramclone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;

public class PostDetailsActivity extends AppCompatActivity {

    Post post;

    TextView tvUsernameDetails;
    ImageView ivImageDetails;
    TextView tvDescriptionDetails;
    TextView tvDateDetails;
    Button btnLikeDetails;
    TextView tvNumLikesDetails;
    int currLikes = 0;
    ParseUser currentUser;
    ArrayList<String> UserLikes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        tvUsernameDetails = findViewById(R.id.tvUsernameDetails);
        ivImageDetails = findViewById(R.id.ivImageDetails);
        tvDescriptionDetails = findViewById(R.id.tvDescriptionDetails);
        tvDateDetails = findViewById(R.id.tvDateDetails);
        btnLikeDetails = findViewById(R.id.btnLikeDetails);
        tvNumLikesDetails = findViewById(R.id.tvNumLikesDetails);


        post = (Post) Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));
        Log.d("PostDetailsActivity", String.format("Showing details for '%s'", post.getUser().getUsername()));

        ParseFile image = post.getImage();
        if (image != null) {
            Glide.with(this).load(post.getImage().getUrl()).into(ivImageDetails);
        }

        tvUsernameDetails.setText(post.getUser().getUsername());
        tvDescriptionDetails.setText(post.getDescription());
        tvDateDetails.setText(Post.getRelativeTimeAgo(post.getDate()));

        currentUser = ParseUser.getCurrentUser();
        UserLikes = post.getUserLikes();

        if (UserLikes == null) {
            UserLikes = new ArrayList<>();
        }

        if (UserLikes != null) {
            currLikes = post.getUserLikes().size();
        }
        tvNumLikesDetails.setText(String.valueOf(currLikes) + " likes");
        if (UserLikes.contains(currentUser.getObjectId())) {
            btnLikeDetails.setBackgroundResource(R.drawable.ufi_heart_active);
        }


        btnLikeDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (UserLikes == null) {
                    UserLikes = new ArrayList<>();
                    UserLikes.add(currentUser.getObjectId());
                    post.setLikes(UserLikes);
                    post.saveInBackground();
                    currLikes += 1;
                    tvNumLikesDetails.setText(String.valueOf(currLikes) + " likes");
                    btnLikeDetails.setBackgroundResource(R.drawable.ufi_heart_active);
                }

                else if (!UserLikes.contains(currentUser.getObjectId())) {
                    UserLikes.add(currentUser.getObjectId());
                    post.setLikes(UserLikes);
                    post.saveInBackground();
                    currLikes += 1;
                    tvNumLikesDetails.setText(String.valueOf(currLikes) + " likes");
                    btnLikeDetails.setBackgroundResource(R.drawable.ufi_heart_active);
                }
                else {
                    UserLikes.remove(currentUser.getObjectId());
                    post.setLikes(UserLikes);
                    post.saveInBackground();
                    currLikes -= 1;
                    tvNumLikesDetails.setText(String.valueOf(currLikes) + " likes");
                    btnLikeDetails.setBackgroundResource(R.drawable.ufi_heart);
                }
            }
        });




        Log.d("PostDetailsActivity", String.format("Date: ", post.getDate()));


    }

}