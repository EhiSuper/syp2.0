package it.unipi.dii.inginf.lsdb.syp.rule;

import weka.associations.Apriori;
import weka.associations.AssociationRule;
import weka.associations.FPGrowth;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToNominal;
import weka.filters.unsupervised.instance.Denormalize;

import java.util.ArrayList;
import java.util.List;

public class RuleMining {
    public static List<AssociationRule> minePatterns(List<String[]> records) throws Exception{
        List<Instances> datasets = createDataset(records);
        System.out.println(datasets.get(1));
        Instances denormalizedDataset = denormalizeDataset(datasets);
        return runApriori(denormalizedDataset);
    }

    private static List<Instances> createDataset(List<String[]> records) throws Exception{
        Attribute playlistID = new Attribute("playlistID");
        Attribute artistContained = new Attribute("", true);
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(playlistID);
        attributes.add(artistContained);
        Instances dataset = new Instances("normalizedDataset", attributes, 0);
        Instances structure = new Instances("normalizedDataset", attributes, 0);
        for(String[] record: records){
            double[] values = new double[dataset.numAttributes()];
            values[0] = Double.parseDouble(record[0]);
            values[1] = dataset.attribute(1).addStringValue(record[1]);
            Instance instance = new DenseInstance(1.0, values);
            dataset.add(instance);
        }
        StringToNominal stringToNominalFilter = new StringToNominal();
        stringToNominalFilter.setInputFormat(structure);
        Instances populatedDataset = Filter.useFilter(dataset, stringToNominalFilter);
        ArrayList<Instances> datasets = new ArrayList<>();
        datasets.add(structure);
        datasets.add(populatedDataset);
        return datasets;
    }


    private static Instances denormalizeDataset(List<Instances> datasets) throws Exception{
        Instances structure = datasets.get(0);
        Instances dataset = datasets.get(1);
        Denormalize denormalizeFilter = new Denormalize();
        denormalizeFilter.setInputFormat(dataset);
        String[] options = new String[2];
        options[0] = "-G";
        options[1] = "playlistID";
        denormalizeFilter.setOptions(options);
        Instances denormalizedDataset = Filter.useFilter(dataset, denormalizeFilter);
        denormalizedDataset.deleteAttributeAt(0);
        return denormalizedDataset;
    }

    private static List<AssociationRule> runFPGrowth(Instances denormalizedDataset) throws Exception{
        FPGrowth miner = new FPGrowth();
        miner.setUpperBoundMinSupport(0.0025);
        miner.setLowerBoundMinSupport(0.0025);
        miner.setMinMetric(0.5);
        miner.setFindAllRulesForSupportLevel(true);
        miner.buildAssociations(denormalizedDataset);
        System.out.println(miner);
        return miner.getAssociationRules().getRules();
    }

    private static List<AssociationRule> runApriori(Instances denormalizedDataset) throws Exception{
        Apriori miner = new Apriori();
        miner.setUpperBoundMinSupport(0.0025);
        miner.setLowerBoundMinSupport(0.0025);
        miner.setMinMetric(0.5);
        miner.buildAssociations(denormalizedDataset);
        System.out.println(miner);
        return miner.getAssociationRules().getRules();
    }
}
