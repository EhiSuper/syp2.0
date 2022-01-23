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
    @Field("playlists")
    private List<Playlist> playlists;

    private Integer numberOfComments;
    private Integer numberOfPlaylists;

    public Song(String track, String artist, List<Playlist> playlists, Integer numberOfComments, Integer numberOfPlaylists) {
        this.track = track;
        this.artist = artist;
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
