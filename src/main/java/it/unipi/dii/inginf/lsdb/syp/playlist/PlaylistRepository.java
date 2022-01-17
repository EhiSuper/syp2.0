package it.unipi.dii.inginf.lsdb.syp.playlist;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;

public interface PlaylistRepository extends Neo4jRepository<Playlist, String> {
    @Query("CREATE (n:Playlist {id: $id, name: $name})")
    void insert(String id, String name);

    @Query("MATCH (n:Playlist { id: $id }) SET n.name = $name")
    void updateName(String id, String name);

    //done automatically by spring
    void deletePlaylistByIdentifier(String id);

    @Query("MATCH (n:User) WHERE n.id = $userId MATCH (p:Playlist) WHERE p.id = $playlistId CREATE (n)-[:LIKES]->(p)")
    void addLike(String userId, String playlistId);

    @Query("MATCH (n:User {id: $userId})-[r:LIKES]->(p:Playlist {id: $playlistId}) DELETE r")
    void removeLike(String userId, String playlistId);

    @Query("MATCH (n:User)-[:LIKES]->(followedplaylists) WHERE n.id = $id RETURN followedplaylists")
    List<Playlist> getLikedPlaylists(String id);

    @Query("MATCH (u1)-[:LIKES]->(p) RETURN p, COUNT(u1) as numberOfFollowers ORDER BY numberOfFollowers DESC LIMIT $number")
    List<Playlist> getMostLikedPlaylists(int number);

    @Query("MATCH (u1:User {name : $username})-[:FOLLOWS]->(u2:User)-[:LIKES]->(p2:Playlist) WHERE NOT (u1)-[:LIKES]-> (p2) RETURN DISTINCT p2 LIMIT $number")
    List<Playlist> getSuggestedPlaylists(String username, int number);

    @Query("MATCH (n:Playlist) WITH COUNT(n) as NumberOfPlaylists MATCH ()-[f:LIKES]->() RETURN 1.0*COUNT(f)/NumberOfPlaylists")
    Double getAverageFollowsPerPlaylist();
}
