package com.example.instagramclone;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbRequest;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;


import org.json.JSONArray;
import org.parceler.Parcels;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private Context context;
    private List<Post> posts;

    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        posts.addAll(list);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvUsername;
        private ImageView ivImage;
        private TextView tvDescription;
        private TextView tvDate;
        private Button btnLike;
        private TextView tvNumLikes;
        private RelativeLayout container;
        int currLikes = 0;
        ArrayList<String> UserLikes;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnLike =itemView.findViewById(R.id.btnLike);
            tvNumLikes = itemView.findViewById(R.id.tvNumLikes);
            container = itemView.findViewById(R.id.container);
        }

        public void bind(final Post post) {
            //bind the post data to the view elements
            tvDescription.setText(post.getDescription());
            tvUsername.setText(post.getUser().getUsername());
            tvDate.setText(Post.getRelativeTimeAgo(post.getDate()));

            if (post.getUserLikes() != null) {
                currLikes = post.getUserLikes().size();
            }
            tvNumLikes.setText(String.valueOf(currLikes) + " likes");

            final ParseUser currentUser = ParseUser.getCurrentUser();
            UserLikes = post.getUserLikes();

            if (UserLikes != null && UserLikes.contains(currentUser.getObjectId())) {
                btnLike.setBackgroundResource(R.drawable.ufi_heart_active);
            }
            if (UserLikes != null && !UserLikes.contains(currentUser.getObjectId())) {
                btnLike.setBackgroundResource(R.drawable.ufi_heart);
            }


            ParseFile image = post.getImage();
            if (image != null) {
                Glide.with(context).load(post.getImage().getUrl()).into(ivImage);
            }

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, PostDetailsActivity.class);
                    intent.putExtra(Post.class.getSimpleName(), Parcels.wrap(post));
                    context.startActivity(intent);

                }
            });

            btnLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (UserLikes == null) {
                        UserLikes = new ArrayList<>();
                        UserLikes.add(currentUser.getObjectId());
                        post.setLikes(UserLikes);
                        post.saveInBackground();
                        currLikes += 1;
                        tvNumLikes.setText(String.valueOf(currLikes) + " likes");
                        btnLike.setBackgroundResource(R.drawable.ufi_heart_active);
                    }

                    else if (!UserLikes.contains(currentUser.getObjectId())) {
                        UserLikes.add(currentUser.getObjectId());
                        post.setLikes(UserLikes);
                        post.saveInBackground();
                        currLikes += 1;
                        tvNumLikes.setText(String.valueOf(currLikes) + " likes");
                        btnLike.setBackgroundResource(R.drawable.ufi_heart_active);
                    }
                    else {
                        UserLikes.remove(currentUser.getObjectId());
                        post.setLikes(UserLikes);
                        post.saveInBackground();
                        currLikes -= 1;
                        tvNumLikes.setText(String.valueOf(currLikes) + " likes");
                        btnLike.setBackgroundResource(R.drawable.ufi_heart);
                    }
                }
            });


        }
    }
}
