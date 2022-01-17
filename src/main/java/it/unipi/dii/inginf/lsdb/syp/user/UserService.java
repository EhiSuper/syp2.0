package it.unipi.dii.inginf.lsdb.syp.user;

import it.unipi.dii.inginf.lsdb.syp.playlist.Playlist;
import it.unipi.dii.inginf.lsdb.syp.song.Song;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.aggregation.ArrayOperators.Filter.filter;
import static org.springframework.data.mongodb.core.aggregation.ConditionalOperators.IfNull.ifNull;

@Service
public class UserService {
    private final MongoTemplate mongoTemplate;
    private final UserRepository userRepository;

    UserService(MongoTemplate mongoTemplate, UserRepository neo4jUserRepository) {
        this.mongoTemplate = mongoTemplate;
        this.userRepository = neo4jUserRepository;
    }

    List<User> getUsersByRegex(String regex){
        try{
            Query findUsersByRegex = new Query(Criteria.where("username").regex("^" + regex, "i"));
            return mongoTemplate.find(findUsersByRegex, User.class);
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    User getUserById(String id){
        try{
            Query findUserById = new Query(Criteria.where("_id").is(id));
            return mongoTemplate.findOne(findUserById, User.class);
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    User saveUser(User newUser){
        User savedUser = null;
        try{
            savedUser = mongoTemplate.insert(newUser);
        } catch (Exception e){
            //failed operation
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
        //consistency between graph and document DBs
        try{
            userRepository.insert(savedUser.getIdentifier(), savedUser.getUsername());
        } catch (Exception e){
            //failed operation
            Query findUserById = new Query(Criteria.where("_id").is(savedUser.getIdentifier()));
            mongoTemplate.remove(findUserById, User.class);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }

        return savedUser;
    }

    //@Transactional
    User updateUser(User oldUser, User newUser){
        User updatedUser = null;
        try {
            updatedUser = mongoTemplate.save(newUser); //overwrites the entire document
        } catch (Exception e){
            //failed operation
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }

        if(!oldUser.getUsername().equals(newUser.getUsername())){
            try {
                userRepository.updateUsername(newUser.getIdentifier(), newUser.getUsername());
            } catch (Exception e){
                //failed operation
                mongoTemplate.save(oldUser);
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
            }
            Query findPlaylists = new Query(Criteria.where("creator._id").is(new ObjectId(updatedUser.getIdentifier())));
            Update updatePlaylists = new Update().set("creator.username", updatedUser.getUsername());
            mongoTemplate.updateMulti(findPlaylists, updatePlaylists, Playlist.class);
        }

        return updatedUser;
    }

    //@Transactional
    void deleteUser(String id){
        User deletedUser = null;
        try{
            Query findUserById = new Query(Criteria.where("_id").is(id));
            deletedUser= mongoTemplate.findAndRemove(findUserById, User.class);
        } catch (Exception e){
            //failed operation
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
        try{
            userRepository.deleteUserByIdentifier(id);
        } catch (Exception e){
            //failed operation
            mongoTemplate.insert(deletedUser);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }

        //delete playlists owned by deleted user
        Query findPlaylistsByCreatorId = new Query(Criteria.where("creator._id").is(new ObjectId(id)));
        List<Playlist> deletedPlaylists = mongoTemplate.findAllAndRemove(findPlaylistsByCreatorId, Playlist.class);

        //for each deleted playlist, remove redundant info in songs
        for(Playlist playlist: deletedPlaylists ){
            deleteRedundantInfo(playlist);
        }

    }

    void addFollow(String followerId, String followedId){
        try{
            userRepository.addFollow(followerId, followedId);
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    void removeFollow(String followerId, String followedId){
        try{
            userRepository.removeFollow(followerId, followedId);
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    private void deleteRedundantInfo(Playlist playlist) {
        Playlist embeddedPlaylistInfo = new Playlist(playlist.getIdentifier(), playlist.getName(),
                null, null, null, null);

        List<String> songsIdentifiers = getIdentifiersFromPlaylist(playlist);

        if(songsIdentifiers != null){
            Query findSongs = new Query(Criteria.where("_id").in(songsIdentifiers));
            Update updateSongs = new Update().pull("playlists", embeddedPlaylistInfo);
            mongoTemplate.updateMulti(findSongs, updateSongs, Song.class);
        }
    }

    public List<String> getIdentifiersFromPlaylist(Playlist playlist){
        if(playlist.getSongs() != null) {
            List<String> songsIdentifiers = new ArrayList<>();
            for (Song song : playlist.getSongs()) {
                songsIdentifiers.add(song.getIdentifier());
            }
            return songsIdentifiers;
        }
        return null;
    }


    public List<User> getFollowersById(String id) {
        try{
            return userRepository.getFollowers(id);
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public List<User> getFollowedUsersById(String id) {
        try{
            return userRepository.getFollowedUsers(id);
        } catch(Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public List<User> getLikesById(String id) {
        try{
            return userRepository.getLikesById(id);
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public List<User> getSimilarUsers(String username, int numberOfPlaylist) {
        try{
            return userRepository.getSimilarUsers(username, numberOfPlaylist);
        } catch(Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public List<User> getTopCreators(int numberToReturn) {
        MatchOperation filterUsersWithoutAttributeCreatedPlaylists = match(new Criteria("createdPlaylists").exists(true));
        ProjectionOperation getNumberOfCreatedPlaylists = project("_id", "username").and("createdPlaylists").size().as("numberOfPlaylists");
        MatchOperation matchUsersWithAtLeastOneCreatedPlaylist = match(new Criteria("numberOfPlaylists").gt(0));
        SortOperation sortByNumberOfCreatedPlaylists = sort(Sort.by(Sort.Direction.DESC, "numberOfPlaylists"));

        Aggregation aggregation = newAggregation(filterUsersWithoutAttributeCreatedPlaylists,
                                                 getNumberOfCreatedPlaylists,
                                                 matchUsersWithAtLeastOneCreatedPlaylist,
                                                 sortByNumberOfCreatedPlaylists,
                                                 limit(numberToReturn));

        try{
            AggregationResults<User> result = mongoTemplate.aggregate(
                    aggregation, "users", User.class);
            return result.getMappedResults();
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public List<User> getUsersWithMostSongsOfASpecificArtist(int numberToReturn, String artist) {
        MatchOperation filterPlaylistsWithoutAttributeSongs = match(new Criteria("songs").exists(true));
        ProjectionOperation filterArraySongsBasedOnArtist = project("_id", "creator")
                .and(filter("songs").as("songs").by(
                ComparisonOperators.valueOf("songs.artist").equalToValue(artist))).as("songs");
        ProjectionOperation getNumberOfSongsPerPlaylist = project("_id", "creator").and("songs").size().as("numberOfSongs");
        GroupOperation groupByCreator = group("creator._id").first("creator.username").as("username").sum("numberOfSongs").as("numberOfSongs");
        SortOperation sortByNumberOfSongs = sort(Sort.by(Sort.Direction.DESC, "numberOfSongs"));

        Aggregation aggregation = newAggregation(filterPlaylistsWithoutAttributeSongs,
                                                 filterArraySongsBasedOnArtist,
                                                 getNumberOfSongsPerPlaylist,
                                                 groupByCreator,
                                                 sortByNumberOfSongs,
                                                 limit(numberToReturn));

        try{
            AggregationResults<User> result = mongoTemplate.aggregate(
                    aggregation, "playlists", User.class);

            return result.getMappedResults();
        } catch(Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public List<User> getMostFollowedUsers(int number){
        try{
            return userRepository.getMostFollowedUsers(number);
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public Double getAverageCreatedPlaylistsPerUser() {
        ProjectionOperation projectEmptyArrayInUsersWithoutCreatedPlaylists = project("_id", "username")
                .and("createdPlaylists").applyCondition(ifNull("createdPlaylists").then(new ArrayList<>()));
        ProjectionOperation getNumberOfCreatedPlaylists = project("_id", "username").and("createdPlaylists").size().as("numberOfPlaylists");
        GroupOperation groupAllAndGetAverageCreatedPlaylists = group().avg("numberOfPlaylists").as("avgNumberOfPlaylistsCreated");

        Aggregation aggregation = newAggregation(projectEmptyArrayInUsersWithoutCreatedPlaylists,
                                                 getNumberOfCreatedPlaylists,
                                                 groupAllAndGetAverageCreatedPlaylists);

        try{
            AggregationResults<Document> result = mongoTemplate.aggregate(
                    aggregation, "users", Document.class);

            Document document = result.getUniqueMappedResult();

            return document.getDouble("avgNumberOfPlaylistsCreated");
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public Double getAverageFollows(){
        try{
            return userRepository.getAverageFollows();
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public Double getAverageCommentsPerUser(){
        try{
            return userRepository.getAverageCommentsPerUser();
        } catch(Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}
