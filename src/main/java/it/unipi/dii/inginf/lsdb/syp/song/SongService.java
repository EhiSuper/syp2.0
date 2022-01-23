package it.unipi.dii.inginf.lsdb.syp.song;

import it.unipi.dii.inginf.lsdb.syp.playlist.Playlist;

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
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import it.unipi.dii.inginf.lsdb.syp.rule.Rule;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.aggregation.ConditionalOperators.IfNull.ifNull;

@Service
public class SongService {
    private final MongoTemplate mongoTemplate;
    private final SongRepository songRepository;

    SongService(MongoTemplate mongoTemplate, SongRepository songRepository) {
        this.mongoTemplate = mongoTemplate;
        this.songRepository = songRepository;
    }

    List<Song> getSongsByRegex(String regex){
        try{
            return mongoTemplate.find(query(where("track").regex("^" + regex, "i")), Song.class);
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    Song getSongById(String id){
        try{
            Query findSongById = new Query(Criteria.where("_id").is(id));
            return mongoTemplate.findOne(findSongById, Song.class);
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    Song saveSong(Song newSong){
        Song savedSong = null;
        try{
            savedSong = mongoTemplate.insert(newSong);
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }

        try{
            songRepository.insert(savedSong.getIdentifier(), savedSong.getTrack());
        } catch (Exception e){
            e.printStackTrace();
            Query findSongById = new Query(Criteria.where("_id").is(savedSong.getIdentifier()));
            mongoTemplate.remove(findSongById, Song.class);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }

        return savedSong;
    }

    //@Transactional
    Song updateSong(Song oldSong, Song newSong){
        Song updatedSong = null;

        try{
            updatedSong = mongoTemplate.save(newSong);
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }

        if(!oldSong.getTrack().equals(newSong.getTrack()) || !oldSong.getArtist().equals(newSong.getArtist())){

            try{
                songRepository.updateTrack(updatedSong.getIdentifier(), updatedSong.getTrack());
            } catch (Exception e){
                e.printStackTrace();
                mongoTemplate.save(oldSong);
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
            }

            Query findPlaylists = new Query(Criteria.where("songs._id").is(new ObjectId(updatedSong.getIdentifier())));
            Update updatePlaylists = new Update().set("songs.$.track", updatedSong.getTrack())
                                                 .set("songs.$.artist", updatedSong.getArtist());
            mongoTemplate.updateMulti(findPlaylists, updatePlaylists, Playlist.class);
        }

        return updatedSong;
    }

    //@Transactional
    void deleteSong(String id){
        Song deletedSong = null;
        try{
            Query findSongById = new Query(Criteria.where("_id").is(id));
            deletedSong = mongoTemplate.findAndRemove(findSongById, Song.class);
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }

        try{
            songRepository.deleteSongByIdentifier(id);
        } catch (Exception e){
            e.printStackTrace();
            mongoTemplate.insert(deletedSong);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }

        Query findPlaylists = new Query(Criteria.where("songs._id").is(new ObjectId(id)));
        Update updatePlaylists = new Update().set("songs.$.track", "this song has been removed by an admin");
        mongoTemplate.updateMulti(findPlaylists, updatePlaylists, Playlist.class);
    }

    public List<Song> getPopularSongs(int numberToReturn) {
        MatchOperation filterSongsWithoutAttributePlaylists = match(new Criteria("playlists").exists(true));
        ProjectionOperation getNumberOfPlaylists = project("_id", "track").and("playlists").size().as("numberOfPlaylists");
        MatchOperation filterSongsContainedInNoPlaylists = match(new Criteria("numberOfPlaylists").gt(0));
        SortOperation sortByNumberOfPlaylists = sort(Sort.by(Sort.Direction.DESC, "numberOfPlaylists"));

        Aggregation aggregation = newAggregation(filterSongsWithoutAttributePlaylists,
                                                 getNumberOfPlaylists,
                                                 filterSongsContainedInNoPlaylists,
                                                 sortByNumberOfPlaylists,
                                                 limit(numberToReturn));

        try{
            AggregationResults<Song> result = mongoTemplate.aggregate(
                    aggregation, "songs", Song.class);
            return result.getMappedResults();
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public List<Song> getMostCommentedSongs(int number) {
        try{
            return songRepository.getMostCommentedSongs(number);
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public Double getAveragePlaylistsPerSong() {
        ProjectionOperation projectEmptyArrayInSongsWithoutPlaylists = project("_id", "track").and("playlists")
                .applyCondition(ifNull("playlists").then(new ArrayList<>()));
        ProjectionOperation getNumberOfPlaylists = project("_id", "track").and("playlists").size().as("numberOfPlaylists");
        GroupOperation groupAllAndGetAveragePlaylists = group().avg("numberOfPlaylists").as("avgNumberOfPlaylists");

        Aggregation aggregation = newAggregation(projectEmptyArrayInSongsWithoutPlaylists,
                                                 getNumberOfPlaylists,
                                                 groupAllAndGetAveragePlaylists);

        try{
            AggregationResults<Document> result = mongoTemplate.aggregate(
                    aggregation, "songs", Document.class);

            Document document = result.getUniqueMappedResult();
            return document.getDouble("avgNumberOfPlaylists");
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public Double getAverageCommentsPerSong(){
        try{
            return songRepository.getAverageCommentsPerSong();
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    
    public List<Song> getSuggestedSongs(List<Song> selectedSongs){
        List<String> antecedentArtists = getArtistsFromSongs(selectedSongs);
        List<String> sequenceArtists = getSequenceArtistsFromAntecedentArtists(antecedentArtists);
        return getPopularSongsFromArtists(sequenceArtists, selectedSongs);
    }

    List<String> getArtistsFromSongs(List<Song> selectedSongs){
        List<String> artists = new ArrayList<>();
        for(Song song : selectedSongs){
            artists.add(song.getArtist());
        }
        return artists;
    }

    List<String> getSequenceArtistsFromAntecedentArtists(List<String> antecedentArtists){
        List<String> sequenceArtists = new ArrayList<>();
        List<Rule> rules = new ArrayList<>();
        try{
            rules = mongoTemplate.find(query(where("premise").size(antecedentArtists.size()).all(antecedentArtists)), Rule.class);
            if(rules.size() == 0){
                rules = mongoTemplate.find(query(where("premise").all(antecedentArtists)), Rule.class);
                if(rules.size() == 0){
                    rules = mongoTemplate.find(query(where("premise").in(antecedentArtists)), Rule.class);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
        for(Rule pattern: rules){
            for(String artist: pattern.getConsequent()){
                 if(!sequenceArtists.contains(artist)) sequenceArtists.add(artist);
            }
        }
        return sequenceArtists;
    }

    List<Song> getPopularSongsFromArtists(List<String> sequenceArtists, List<Song> selectedSongs){
        final int numberOfSongsReturned = 10;
        List<String> trackSelectedSongs = new ArrayList<>();
        for(Song track: selectedSongs){
            trackSelectedSongs.add(track.getIdentifier());
        }
        Aggregation getPopularsongsFromArtists = newAggregation(
            match(where("_id").nin(trackSelectedSongs)),
            match(where("artist").in(sequenceArtists)),
            project("_id", "track", "artist").and("$playlists").size().as("numberOfPlaylists"),
            sort(Sort.Direction.DESC, "numberOfPlaylists"),
            limit(numberOfSongsReturned)
        );

        try{
            AggregationResults<Song> results = mongoTemplate.aggregate(getPopularsongsFromArtists, "songs", Song.class);
            List<Song> songs = results.getMappedResults();
            return songs;
        }catch(Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
    
}
