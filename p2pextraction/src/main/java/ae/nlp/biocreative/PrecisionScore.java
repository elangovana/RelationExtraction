package ae.nlp.biocreative;

import com.pengyifan.bioc.BioCCollection;
import com.pengyifan.bioc.BioCDocument;
import com.pengyifan.bioc.BioCRelation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;


public class PrecisionScore {

    private HashMap<String, BioCDocument> _docHashMap;

    public  double CalculateScore(BioCCollection trainingSet, BioCCollection predictedSet){
        //Preprocess , build training set hash
        int predTotalRel =0;
        int predCorrectRel = 0;
        HashMap<String, BioCDocument> trainDocHashMap =  buildTrainingSetHash(trainingSet);

        for (BioCDocument predDoc: predictedSet.getDocuments()) {
            BioCDocument trainingDoc = trainDocHashMap.get(predDoc.getID());

            for (BioCRelation predRelation : predDoc.getRelations()){
                Optional<String> predPpiRel = predRelation.getInfon("relation");
                //If not ppim relation ignore relation
                if(! (predPpiRel.isPresent() && predPpiRel.get().equals("PPIm"))) continue;

               String Gene1 = predRelation.getInfon("Gene1").get();
               String Gene2 =  predRelation.getInfon("Gene1").get();

               if (ExistsInTraining(trainingDoc, Gene1, Gene2)){
                   predCorrectRel++;
               }
               predTotalRel++;


            }
        }
        return  (double)predCorrectRel/(double)predTotalRel;
    }

    private boolean ExistsInTraining(BioCDocument trainingDoc, String predGeneRelGene1, String predGeneRelGene2) {
        boolean result = false;
        for (BioCRelation relation: trainingDoc.getRelations()) {

            Optional<String> ppiRel = relation.getInfon("relation");
            //If not ppim relation ignore relation
            if (!(ppiRel.isPresent() && ppiRel.get().equals("PPIm"))) continue;

            String trainGene1 = relation.getInfon("Gene1").get();
            String trainGene2 =  relation.getInfon("Gene1").get();

            //Use hashmap to check for undirected relationship between 2 genes
            HashSet<String> trainGenesRelHash = new HashSet<>();
            trainGenesRelHash.add(trainGene1);
            trainGenesRelHash.add(trainGene2);

            if (trainGenesRelHash.contains(predGeneRelGene1) && trainGenesRelHash.contains(predGeneRelGene2))
                return  true;


        }

        return  result;
    }

    private HashMap<String, BioCDocument>  buildTrainingSetHash(BioCCollection trainingSet) {
        HashMap<String, BioCDocument> docHashMap = new HashMap<>();
        for (BioCDocument doc: trainingSet.getDocuments()   ) {
            docHashMap.put(doc.getID(), doc);

        }
        return  docHashMap;
    }


}