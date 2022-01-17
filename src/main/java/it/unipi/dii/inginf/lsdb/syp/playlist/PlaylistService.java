package it.unipi.dii.inginf.lsdb.syp.playlist;

import it.unipi.dii.inginf.lsdb.syp.song.Song;
import it.unipi.dii.inginf.lsdb.syp.user.User;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.aggregation.ConditionalOperators.IfNull.ifNull;

@Service
public class PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final MongoTemplate mongoTemplate;

    PlaylistService(PlaylistRepository neo4jPlaylistRepository, MongoTemplate mongoTemplate) {
        this.playlistRepository = neo4jPlaylistRepository;
        this.mongoTemplate = mongoTemplate;
    }

    List<Playlist> getPlaylistsByRegex(String regex){
        try{
            Query findPlaylistsByRegex = new Query(Criteria.where("name").regex("^" + regex, "i"));
            return mongoTemplate.find(findPlaylistsByRegex, Playlist.class);
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    Playlist getPlaylistById(String id){
        try{
            Query findPlaylistById = new Query(Criteria.where("_id").is(id));
            return mongoTemplate.findOne(findPlaylistById, Playlist.class);
        } catch(Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    //@Transactional
    Playlist savePlaylist(Playlist newPlaylist){
        Playlist savedPlaylist = null;
        try {
            savedPlaylist = mongoTemplate.insert(newPlaylist);
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }

        try{
            playlistRepository.insert(savedPlaylist.getIdentifier(), savedPlaylist.getName());
        } catch (Exception e){
            e.printStackTrace();
            Query findPlaylistById = new Query(Criteria.where("_id").is(savedPlaylist.getIdentifier()));
            mongoTemplate.remove(findPlaylistById, Playlist.class);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }

        //manage redundancy
        Playlist embeddedPlaylistInfo = new Playlist(savedPlaylist.getIdentifier(), savedPlaylist.getName(),
                                            null, null, null, null);

        //update creator's createdPlaylists array
        Query findCreator = new Query(Criteria.where("_id").is(savedPlaylist.getCreator().getIdentifier()));
        Update updateCreator = new Update().push("createdPlaylists", embeddedPlaylistInfo);
        mongoTemplate.updateFirst(findCreator, updateCreator, User.class);

        //update contained songs' playlists array
        List<String> songsIdentifiers = getIdentifiersFromPlaylist(savedPlaylist);

        if(songsIdentifiers != null){
            Query findSongs = new Query(Criteria.where("_id").in(songsIdentifiers));
            Update updateSongs = new Update().push("playlists", embeddedPlaylistInfo);
            mongoTemplate.updateMulti(findSongs, updateSongs, Song.class);
        }


        return savedPlaylist;
    }

    //@Transactional
    public Playlist updatePlaylist(Playlist oldPlaylist, Playlist newPlaylist) {
        Playlist savedPlaylist = null;
        try{
            savedPlaylist = mongoTemplate.save(newPlaylist);
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }

        //if name changed, redundancy must be managed
        if (!oldPlaylist.getName().equals(newPlaylist.getName())){
            try{
                playlistRepository.updateName(newPlaylist.getIdentifier(), newPlaylist.getName());
            } catch (Exception e){
                e.printStackTrace();
                mongoTemplate.save(oldPlaylist);
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
            }
            updateName(newPlaylist);
        }

        //manage redundancy on songs
        List<String> removedSongsIdentifiers = getSongsOnlyInFirstPlaylist(oldPlaylist, newPlaylist);

        List<String> insertedSongsIdentifiers = getSongsOnlyInFirstPlaylist(newPlaylist, oldPlaylist);

        if(removedSongsIdentifiers != null){
            Playlist embeddedOldPlaylistInfo = new Playlist(oldPlaylist.getIdentifier(), oldPlaylist.getName(),
                                                 null, null, null, null);

            Query findSongs = new Query(Criteria.where("_id").in(removedSongsIdentifiers));
            Update updateSongs = new Update().pull("playlists", embeddedOldPlaylistInfo);
            mongoTemplate.updateMulti(findSongs, updateSongs, Song.class);
        }

        if(insertedSongsIdentifiers != null){
            Playlist embeddedNewPlaylistInfo = new Playlist(newPlaylist.getIdentifier(), newPlaylist.getName(),
                                                 null, null, null, null);

            Query findSongs = new Query(Criteria.where("_id").in(insertedSongsIdentifiers));
            Update updateSongs = new Update().push("playlists", embeddedNewPlaylistInfo);
            mongoTemplate.updateMulti(findSongs, updateSongs, Song.class);
        }

        return savedPlaylist;
    }

    void updateName(Playlist playlist){
        Query findCreator = new Query(Criteria.where("createdPlaylists._id").is(new ObjectId(playlist.getIdentifier())));
        Update updateCreator = new Update().set("createdPlaylists.$.name", playlist.getName());
        mongoTemplate.updateFirst(findCreator, updateCreator, User.class);

        Query findSongs = new Query(Criteria.where("playlists._id").is(new ObjectId(playlist.getIdentifier())));
        Update updateSongs = new Update().set("playlists.$.name", playlist.getName());
        mongoTemplate.updateMulti(findSongs, updateSongs, Song.class);
    }

    List<String> getSongsOnlyInFirstPlaylist(Playlist firstPlaylist, Playlist secondPlaylist){

        List<String> firstSongsIdentifiers = new ArrayList<>();
        for (Song song : firstPlaylist.getSongs()) {
            firstSongsIdentifiers.add(song.getIdentifier());
        }
        if(firstSongsIdentifiers.isEmpty()){
            return null;
        }

        List<String> secondSongsIdentifiers = new ArrayList<>();
        for (Song song : secondPlaylist.getSongs()) {
            secondSongsIdentifiers.add(song.getIdentifier());
        }
        if(secondSongsIdentifiers.isEmpty()){
            return firstSongsIdentifiers;
        }

        firstSongsIdentifiers.removeAll(secondSongsIdentifiers);
        return firstSongsIdentifiers;
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

    //@Transactional
    public void deletePlaylist(String id) {
        Playlist deletedPlaylist = null;
        try{
            Query findPlaylistById = new Query(Criteria.where("_id").is(id));
            deletedPlaylist = mongoTemplate.findAndRemove(findPlaylistById, Playlist.class);
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }

        //remove node from graph database
        try{
            playlistRepository.deletePlaylistByIdentifier(deletedPlaylist.getIdentifier());
        } catch (Exception e){
            e.printStackTrace();
            mongoTemplate.insert(deletedPlaylist);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }

        //remove redundancy
        Playlist embeddedPlaylistInfo = new Playlist(deletedPlaylist.getIdentifier(), deletedPlaylist.getName(),
                                          null, null, null, null);

        //eliminate createdPlaylists array entry regarding deleted playlist
        Query findCreator = new Query(Criteria.where("_id").is(deletedPlaylist.getCreator().getIdentifier()));
        Update updateCreator = new Update().pull("createdPlaylists", embeddedPlaylistInfo);
        mongoTemplate.updateFirst(findCreator, updateCreator, User.class);

        List<String> songsIdentifiers = getIdentifiersFromPlaylist(deletedPlaylist);

        if(songsIdentifiers != null){
            //eliminate playlists array entry regarding deleted playlist
            Query findSongs = new Query(Criteria.where("_id").in(songsIdentifiers));
            Update updateSongs = new Update().pull("playlists", embeddedPlaylistInfo);
            mongoTemplate.updateMulti(findSongs, updateSongs, Song.class);
        }
    }

    public void addLike(String userId, String playlistId) {
        try{
            playlistRepository.addLike(userId, playlistId);
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public void removeLike(String userId, String playlistId) {
        try{
            playlistRepository.removeLike(userId, playlistId);
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public List<Playlist> getLikedPlaylists(String userId) {
        try{
            return playlistRepository.getLikedPlaylists(userId);
        } catch(Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public List<Playlist> getMostLikedPlaylists(int number){
        try{
            return playlistRepository.getMostLikedPlaylists(number);
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public List<Playlist> getSuggestedPlaylists(String username, int number){
        try{
            List<Playlist> playlistToReturn = playlistRepository.getSuggestedPlaylists(username, number);
            return playlistToReturn;
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public Double getAverageSongsContained() {
        ProjectionOperation projectEmptyArrayInPlaylistsWithoutSongs = project("_id", "name").and("songs")
                .applyCondition(ifNull("songs").then(new ArrayList<>()));
        ProjectionOperation getNumberOfSongs = project("_id", "name").and("songs").size().as("numberOfSongs");
        GroupOperation groupAllAndGetAverageSongs = group().avg("numberOfSongs").as("avgNumberOfSongs");

        Aggregation aggregation = newAggregation(projectEmptyArrayInPlaylistsWithoutSongs,
                                                 getNumberOfSongs,
                                                 groupAllAndGetAverageSongs);

        try{
            AggregationResults<Document> result = mongoTemplate.aggregate(
                    aggregation, "playlists", Document.class);

            Document document = result.getUniqueMappedResult();

            return document.getDouble("avgNumberOfSongs");
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public Double getAverageFollowsPerPlaylist(){
        try{
            return playlistRepository.getAverageFollowsPerPlaylist();
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}
