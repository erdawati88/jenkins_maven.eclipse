package com.sddevops.jenkins_maven.eclipse;

import java.util.*;
import java.io.*;
import java.net.*;
import org.json.JSONObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SongCollection {

    private List<Song> songs = new ArrayList<>();   // S8688 fixed
    private int capacity;
    private LocalDateTime timeCreated;

    public SongCollection() {
        this.capacity = 20;
        this.timeCreated = LocalDateTime.now();
    }

    public SongCollection(int capacity) {
        this.capacity = capacity;
        this.timeCreated = LocalDateTime.now();
    }

    public String getYearCreated() {
        return String.valueOf(this.timeCreated.getYear());
    }

    public String getFullDateCreated() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return this.timeCreated.format(formatter);
    }

    public List<Song> getSongs() {
        return new ArrayList<>(songs);   // S4507 fixed (defensive copy)
    }

    public void addSong(Song song) {
        if (songs.size() != capacity) {
            songs.add(song);
        }
    }

    public List<Song> sortSongsByTitle() {   // S1319 fixed
        Collections.sort(songs, Song.titleComparator);
        return new ArrayList<>(songs);       // S4507 fixed
    }

    public List<Song> sortSongsBySongLength() {   // S1319 fixed
        Collections.sort(songs, Song.songLengthComparator);
        return new ArrayList<>(songs);            // S4507 fixed
    }

    public Song findSongsById(String id) {
        for (Song s : songs) {
            if (s.getId().equals(id)) return s;
        }
        return null;
    }

    public Song findSongByTitle(String title) {
        for (Song s : songs) {
            if (s.getTitle().equals(title)) return s;
        }
        return null;
    }

    protected String getApiUrl() {
        return "https://mocki.io/v1/e1b14dea-d272-4b03-b102-252325168182";
    }

    protected String fetchSongJson() {
        String urlString = getApiUrl();
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();
                return response.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Song fetchSongOfTheDay() {
        try {
            String jsonStr = fetchSongJson();
            if (jsonStr == null) return null;

            JSONObject json = new JSONObject(jsonStr);

            Song song = new Song(
                json.getString("id"),
                json.getString("title"),
                json.getString("artiste"),
                json.getDouble("songLength")
            );

            String artiste = song.getArtiste().trim();

            if (artiste.equals("Taylor Swift")) {
                song.setArtiste("TS");
                this.addSong(song);
            } else if (artiste.equals("Bruno Mars")) {
                song.setArtiste("BM");
                this.addSong(song);
            }

            return song;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String compareCollection(SongCollection other) {
        if (this.timeCreated.isBefore(other.timeCreated)) {
            return "My collection is older!";
        } else if (this.timeCreated.isEqual(other.timeCreated)) {
            return "My collection was created at the same time!";
        } else {
            return "My collection is newer!";
        }
    }
}
