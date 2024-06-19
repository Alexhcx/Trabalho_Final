package com.example.trabalhofinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.trabalhofinal.adapter.CategoryAdapter;
import com.example.trabalhofinal.adapter.SectionSongListAdapter;
import com.example.trabalhofinal.databinding.ActivityMainBinding;
import com.example.trabalhofinal.models.CategoryModel;
import com.example.trabalhofinal.models.SongModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private CategoryAdapter categoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        AdsServices.loadFullscreenADS(MainActivity.this);

        getCategories();
        setupSection("section_1", binding.section1MainLayout, binding.section1Title, binding.section1RecyclerView);
        setupSection("section_2", binding.section2MainLayout, binding.section2Title, binding.section2RecyclerView);
        setupSection("section_3", binding.section3MainLayout, binding.section3Title, binding.section3RecyclerView);
        setupMostlyPlayed("mostly_played", binding.mostlyPlayedMainLayout, binding.mostlyPlayedTitle, binding.mostlyPlayedRecyclerView);

        binding.optionBtn.setOnClickListener(v -> showPopupMenu());
    }

    private void showPopupMenu() {
        PopupMenu popupMenu = new PopupMenu(this, binding.optionBtn);
        popupMenu.getMenuInflater().inflate(R.menu.option_menu, popupMenu.getMenu());
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.logout) {
                logout();
                return true;
            }
            return false;
        });
    }

    private void logout() {
        MyExoplayer.release();
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
        AdsServices.showFullscreenADS(MainActivity.this);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showPlayerView();
    }

    private void showPlayerView() {
        binding.playerView.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, PlayerActivity.class)));

        SongModel currentSong = MyExoplayer.getCurrentSong();
        if (currentSong != null) {
            binding.playerView.setVisibility(View.VISIBLE);
            binding.songTitleTextView.setText("Música: " + currentSong.getTitle());
            Glide.with(binding.songCoverImageView).load(currentSong.getCoverUrl())
                    .apply(new RequestOptions().transform(new RoundedCorners(32)))
                    .into(binding.songCoverImageView);
        } else {
            binding.playerView.setVisibility(View.GONE);
        }
    }

    // Categories
    private void getCategories() {
        FirebaseFirestore.getInstance().collection("category")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    List<CategoryModel> categoryList = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        categoryList.add(documentSnapshot.toObject(CategoryModel.class));
                    }
                    setupCategoryRecyclerView(categoryList);
                });
    }

    private void setupCategoryRecyclerView(List<CategoryModel> categoryList) {
        categoryAdapter = new CategoryAdapter(categoryList);
        binding.categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.categoriesRecyclerView.setAdapter(categoryAdapter);
    }

    // Sections
    private void setupSection(String id, RelativeLayout mainLayout, TextView titleView, RecyclerView recyclerView) {
        FirebaseFirestore.getInstance().collection("sections")
                .document(id)
                .get().addOnSuccessListener(documentSnapshot -> {
                    CategoryModel section = documentSnapshot.toObject(CategoryModel.class);
                    if (section != null) {
                        mainLayout.setVisibility(View.VISIBLE);
                        titleView.setText(section.getName());
                        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        recyclerView.setAdapter(new SectionSongListAdapter(section.getSongs()));
                        mainLayout.setOnClickListener(v -> {
                            // Pass section directly to SongsListActivity
                            Intent intent = new Intent(MainActivity.this, SongsListActivity.class);
                            intent.putExtra("category", section); // Pass category model as intent extra
                            startActivity(intent);
                        });
                    }
                });
    }

    private void setupMostlyPlayed(String id, RelativeLayout mainLayout, TextView titleView, RecyclerView recyclerView) {
        FirebaseFirestore.getInstance().collection("sections")
                .document(id)
                .get().addOnSuccessListener(documentSnapshot -> {
                    CategoryModel section = documentSnapshot.toObject(CategoryModel.class);
                    if (section != null) {
                        // Query for most played songs
                        FirebaseFirestore.getInstance().collection("songs")
                                .orderBy("count", Query.Direction.DESCENDING)
                                .limit(5)
                                .get().addOnSuccessListener(songListSnapshot -> {
                                    List<SongModel> songsModelList = new ArrayList<>();
                                    for (DocumentSnapshot songDocument : songListSnapshot) {
                                        songsModelList.add(songDocument.toObject(SongModel.class));
                                    }

                                    // Extract song IDs from SongModel objects
                                    List<String> songsIdList = new ArrayList<>();
                                    for (SongModel songModel : songsModelList) {
                                        songsIdList.add(songModel.getId());
                                    }

                                    // Set songs in section
                                    section.setSongs(songsIdList);

                                    // Populate UI elements
                                    mainLayout.setVisibility(View.VISIBLE);
                                    titleView.setText(section.getName());
                                    recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                                    recyclerView.setAdapter(new SectionSongListAdapter(section.getSongs()));

                                    // Handle click to navigate to SongsListActivity
                                    mainLayout.setOnClickListener(v -> {
                                        Intent intent = new Intent(MainActivity.this, SongsListActivity.class);
                                        intent.putExtra("category", section);
                                        startActivity(intent);
                                    });
                                });
                    }
                });
    }

}
