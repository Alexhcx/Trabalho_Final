package com.example.trabalhofinal;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.trabalhofinal.adapter.SongsListAdapter;
import com.example.trabalhofinal.databinding.ActivitySongsListBinding;
import com.example.trabalhofinal.models.CategoryModel;

public class SongsListActivity extends AppCompatActivity {

    private ActivitySongsListBinding binding;
    private SongsListAdapter songsListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySongsListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        CategoryModel category = getIntent().getParcelableExtra("category");
        if (category != null) {
            binding.nameTextView.setText(category.getName());
            Glide.with(binding.coverImageView).load(category.getCoverUrl())
                    .apply(new RequestOptions().transform(new RoundedCorners(32)))
                    .into(binding.coverImageView);

            setupSongsListRecyclerView(category);
        }
    }

    private void setupSongsListRecyclerView(CategoryModel category) {
        songsListAdapter = new SongsListAdapter(category.getSongs());
        binding.songsListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.songsListRecyclerView.setAdapter(songsListAdapter);
    }
}
