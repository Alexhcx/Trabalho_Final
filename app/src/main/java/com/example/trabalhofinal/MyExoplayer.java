package com.example.trabalhofinal;

import android.content.Context;

import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;

import com.example.trabalhofinal.models.SongModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MyExoplayer {

    private static ExoPlayer exoPlayer;
    private static SongModel currentSong;

    public static SongModel getCurrentSong() {
        return currentSong;
    }

    public static ExoPlayer getInstance() {
        return exoPlayer;
    }

    public static void startPlaying(Context context, SongModel song) {
        if (exoPlayer == null) {
            exoPlayer = new ExoPlayer.Builder(context).build();
        }

        if (currentSong != song) {
            // It's a new song so start playing
            currentSong = song;
            updateCount();
            if (currentSong.getUrl() != null) {
                MediaItem mediaItem = MediaItem.fromUri(currentSong.getUrl());
                exoPlayer.setMediaItem(mediaItem);
                exoPlayer.prepare();
                exoPlayer.play();
            }
        }
    }

    public static void updateCount() {
        if (currentSong.getId() != null) {
            String id = currentSong.getId();
            FirebaseFirestore.getInstance().collection("songs")
                    .document(id)
                    .get().addOnSuccessListener(documentSnapshot -> {
                        Long latestCount = documentSnapshot.getLong("count");
                        if (latestCount == null) {
                            latestCount = 1L;
                        } else {
                            latestCount += 1;
                        }

                        Map<String, Object> updateMap = new HashMap<>();
                        updateMap.put("count", latestCount);
                        FirebaseFirestore.getInstance().collection("songs")
                                .document(id)
                                .update(updateMap);
                    });
        }
    }

    public static void release() {
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }
}
