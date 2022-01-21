package it.unipi.dii.inginf.lsdb.syp;

import java.util.List;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;

@Node("FrequentArtists")
@Document(collection = "frequent-artists")
public class FrequentArtists{
    @Id
    @org.springframework.data.neo4j.core.schema.Id
    @Property("id")
    private String identifier;
    private List<String> antecedentArtists;
    private List<String> sequenceArtists;


    public FrequentArtists() {
    }

    public FrequentArtists(String identifier, List<String> antecedentArtists, List<String> sequenceArtists) {
        this.identifier = identifier;
        this.antecedentArtists = antecedentArtists;
        this.sequenceArtists = sequenceArtists;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public List<String> getAntecedentArtists() {
        return this.antecedentArtists;
    }

    public void setAntecedentArtists(List<String> antecedentArtists) {
        this.antecedentArtists = antecedentArtists;
    }

    public List<String> getSequenceArtists() {
        return this.sequenceArtists;
    }

    public void setSequenceArtists(List<String> sequenceArtists) {
        this.sequenceArtists = sequenceArtists;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof FrequentArtists)) {
            return false;
        }
        FrequentArtists frequentArtists = (FrequentArtists) o;
        return Objects.equals(identifier, frequentArtists.identifier) && Objects.equals(antecedentArtists, frequentArtists.antecedentArtists) && Objects.equals(sequenceArtists, frequentArtists.sequenceArtists);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, antecedentArtists, sequenceArtists);
    }

    @Override
    public String toString() {
        return "{" +
            " identifier='" + getIdentifier() + "'" +
            ", antecedentArtists='" + getAntecedentArtists() + "'" +
            ", sequenceArtists='" + getSequenceArtists() + "'" +
            "}";
    }

}