package it.unipi.dii.inginf.lsdb.syp.user;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;


public interface UserRepository extends Neo4jRepository<User, String> {

    @Query("CREATE (n:User {id: $id, name: $name})")
    void insert(String id, String name);

    @Query("MATCH (n:User { id: $id }) SET n.name = $name")
    void updateUsername(String id, String name);

    //done automatically by spring
    void deleteUserByIdentifier(String id);

    @Query("MATCH (n:User {id: $followerId}), (n2:User {id: $followedId}) CREATE (n)-[:FOLLOWS]->(n2)")
    void addFollow(String followerId, String followedId);

    @Query("MATCH (n:User {id: $followerId})-[r:FOLLOWS]->(n2:User {id: $followedId}) DELETE r")
    void removeFollow(String followerId, String followedId);

    @Query("MATCH (n:User)<-[:FOLLOWS]-(followers) WHERE n.id = $id RETURN followers")
    List<User> getFollowers(String id);

    @Query("MATCH (n:User)-[:FOLLOWS]->(followed) WHERE n.id = $id RETURN followed")
    List<User> getFollowedUsers(String id);

    @Query("MATCH (p:Playlist)<-[:LIKES]-(followers) WHERE p.id = $id RETURN followers")
    List<User> getLikesById(String id);

    @Query( "MATCH path = ((n:User {name: $username})-[:LIKES]->(p)<-[:LIKES]-(users)) " +
            "WHERE NOT (n.id = users.id) " +
            "WITH DISTINCT users as possibleUsers, count(path) as numberOfSharedPlaylist " +
            "WHERE numberOfSharedPlaylist >= $numberOfPlaylist " +
            "RETURN possibleUsers ORDER BY numberOfSharedPlaylist DESC")
    List<User> getSimilarUsers(String username, int numberOfPlaylist);

    @Query("MATCH (u1)-[:FOLLOWS]->(u2) RETURN u2, COUNT(u1) as numberOfFollowers " +
           "ORDER BY numberOfFollowers DESC LIMIT $number")
    List<User> getMostFollowedUsers(int number);

    @Query("MATCH (n:User) WITH COUNT(n) as NumberOfUsers MATCH ()-[f:FOLLOWS]->() RETURN 1.0*COUNT(f)/NumberOfUsers")
    Double getAverageFollows();

    @Query("MATCH (n:User) WITH COUNT(n) as NumberOfUsers MATCH ()-[w:WRITE]->() RETURN 1.0*COUNT(w)/NumberOfUsers")
    Double getAverageCommentsPerUser();
}
