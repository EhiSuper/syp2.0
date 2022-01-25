package it.unipi.dii.inginf.lsdb.syp.rule;

import it.unipi.dii.inginf.lsdb.syp.playlist.Playlist;
import it.unipi.dii.inginf.lsdb.syp.song.Song;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import weka.associations.AssociationRule;

import java.util.ArrayList;
import java.util.List;

import static it.unipi.dii.inginf.lsdb.syp.rule.RuleMining.minePatterns;

@Service
public class RuleService {

    private final MongoTemplate mongoTemplate;

    RuleService(MongoTemplate mongoTemplate){
        this.mongoTemplate = mongoTemplate;
    }

    void startMining(){
        List<String[]> records = buildDataset();
        try{
            List<AssociationRule> rules = minePatterns(records);
            saveRules(rules);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    List<Playlist> getAllPlaylists(){
        try{
            return mongoTemplate.findAll(Playlist.class);
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    void saveRule(Rule rule){
        try {
            mongoTemplate.insert(rule);
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    void dropRuleCollection(){
        try {
            mongoTemplate.dropCollection(Rule.class);
        } catch (Exception e){
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public List<String[]> buildDataset(){
        List<Playlist> playlists = getAllPlaylists();
        List<String[]> records = new ArrayList<>();

        int playlistID = 1;
        for(Playlist playlist : playlists){
            List<String> artists = getArtists(playlist);
            for(int i=0; i< artists.size(); i++){
                String[] record = new String[2];
                record[0] = "" + playlistID;
                record[1] = artists.get(i);
                records.add(record);
            }
            playlistID++;
        }
        return records;
    }

    public List<String> getArtists(Playlist playlist){
        List<Song> songs = playlist.getSongs();
        List<String> artists = new ArrayList<>();
        for(int i=0; i<songs.size(); i++){
            if(songs.get(i).getArtist() == null){
                continue;
            }
            if(artists.isEmpty() || !artists.contains(songs.get(i).getArtist())){
                artists.add(songs.get(i).getArtist());
            }
        }
        return artists;
    }

    public void saveRules(List<AssociationRule> rules){
            dropRuleCollection();
            for(AssociationRule rule: rules){
                Rule ruleDTO = new Rule(null, null, null,
                                   null, null, null);
                ruleDTO.setPremise(rule.getPremise());
                ruleDTO.setConsequent(rule.getConsequence());
                //ruleDTO.setPremiseSupport(rule.getPremiseSupport());
                //ruleDTO.setConsequentSupport(rule.getConsequenceSupport());
                //ruleDTO.setRuleSupport(rule.getTotalSupport());
                saveRule(ruleDTO);
            }
    }

}
