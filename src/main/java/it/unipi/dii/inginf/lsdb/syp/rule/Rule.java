package it.unipi.dii.inginf.lsdb.syp.rule;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import weka.associations.Item;

import java.util.ArrayList;
import java.util.Collection;

@Document(collection="rules")
public class Rule {

    @Id
    private String identifier;
    private ArrayList<String> premise;
    private ArrayList<String> consequent;

    public Rule(String identifier, ArrayList<String> premise, ArrayList<String> consequent) {
        this.identifier = identifier;
        this.premise = premise;
        this.consequent = consequent;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public ArrayList<String> getPremise() {
        return premise;
    }

    public void setPremise(ArrayList<String> premise) {
        this.premise = premise;
    }

    public void setPremise(Collection<Item> premise) {
        this.premise = new ArrayList<>();
        for(Item item: premise){
            String artist = item.toString();
            artist = artist.substring(1);
            artist = artist.split("=")[0];
            this.premise.add(artist);
        }
    }

    public ArrayList<String> getConsequent() {
        return consequent;
    }

    public void setConsequent(Collection<Item> Consequent) {
        this.consequent = new ArrayList<>();
        for(Item item: Consequent){
            String artist = item.toString();
            artist = artist.substring(1);
            artist = artist.split("=")[0];
            this.consequent.add(artist);
        }
    }

    public void setConsequent(ArrayList<String> consequent) {
        this.consequent = consequent;
    }


}
