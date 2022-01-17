package it.unipi.dii.inginf.lsdb.syp.playlist;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.unipi.dii.inginf.lsdb.syp.song.Song;
import it.unipi.dii.inginf.lsdb.syp.user.User;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

import java.util.Date;
import java.util.List;

@Node("Playlist")
@Document(collection="playlists")
public class Playlist {
    @Id
    @org.springframework.data.neo4j.core.schema.Id
    @Property("id")
    private String identifier;
    private String name;
    private Date creationDate;
    private User creator;
    private List<Song> songs;

    private Integer numberOfFollowers;

    //constructor, getters and setters

    public Playlist(String identifier, String name, Date creationDate, User creator,
                    List<Song> songs, Integer numberOfFollowers) {
        this.identifier = identifier;
        this.name = name;
        this.creationDate = creationDate;
        this.creator = creator;
        this.songs = songs;
        this.numberOfFollowers = numberOfFollowers;
    }

    @JsonProperty("id")
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    public Integer getNumberOfFollowers() {
        return numberOfFollowers;
    }

    public void setNumberOfFollowers(Integer numberOfFollowers) {
        this.numberOfFollowers = numberOfFollowers;
    }
}
