package it.unipi.dii.inginf.lsdb.syp.song;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;

public interface SongRepository extends Neo4jRepository<Song, String> {

    @Query("CREATE (n:Song {id: $id, track: $track})")
    void insert(String id, String track);

    @Query("MATCH (n:Song { id: $id }) SET n.track = $track")
    void updateTrack(String id, String track);

    //done automatically by spring
    void deleteSongByIdentifier(String id);

    @Query("MATCH (n:Song) WITH COUNT(n) as NumberOfSongs MATCH ()-[r:RELATED]->() RETURN 1.0*COUNT(r)/NumberOfSongs")
    Double getAverageCommentsPerSong();

    @Query("MATCH (song)<-[:RELATED]-(comment) RETURN song, COUNT(comment) as numberOfComments " +
           "ORDER BY numberOfComments DESC LIMIT $number")
    List<Song> getMostCommentedSongs(int number);
}
