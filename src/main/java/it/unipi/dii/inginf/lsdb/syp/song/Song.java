package it.unipi.dii.inginf.lsdb.syp.song;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.unipi.dii.inginf.lsdb.syp.playlist.Playlist;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.util.List;

@Node("Song")
@Document(collection = "songs")
public class Song {

    @Id
    @org.springframework.data.neo4j.core.schema.Id
    @Property("id")
    private String identifier;
    private String track;
    private String artist;
    private Integer year;
    private String lyric;
    private String album;
    @Field("playlists")
    private List<Playlist> playlists;

    private Integer numberOfComments;
    private Integer numberOfPlaylists;

    public Song(String track, String artist, Integer year, String lyric, String album,
                List<Playlist> playlists, Integer numberOfComments, Integer numberOfPlaylists) {
        this.track = track;
        this.artist = artist;
        this.year = year;
        this.lyric = lyric;
        this.album = album;
        this.playlists = playlists;
        this.numberOfComments = numberOfComments;
        this.numberOfPlaylists = numberOfPlaylists;
    }

    @JsonProperty("id")
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public void setPlaylists(List<Playlist> playlists) {
        this.playlists = playlists;
    }

    public Integer getNumberOfComments() {
        return numberOfComments;
    }

    public void setNumberOfComments(Integer numberOfComments) {
        this.numberOfComments = numberOfComments;
    }

    public Integer getNumberOfPlaylists() {
        return numberOfPlaylists;
    }

    public void setNumberOfPlaylists(Integer numberOfPlaylists) {
        this.numberOfPlaylists = numberOfPlaylists;
    }
}
