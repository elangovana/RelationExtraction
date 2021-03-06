package ae.nlp.biocreative;

import com.pengyifan.bioc.BioCAnnotation;
import com.pengyifan.bioc.BioCCollection;
import com.pengyifan.bioc.BioCDocument;
import com.pengyifan.bioc.BioCPassage;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by aparnaelangovan on 4/09/2017.
 */
public class NerNormalisationFMeasureScorer {

    private static Logger theLogger =
            Logger.getLogger(NerNormalisationFMeasureScorer.class.getName());


    public double Score(BioCCollection biocCollectionTraining, BioCCollection biocCollectionTest) throws XMLStreamException, IOException, InterruptedException {


        int testCorrectCount = 0;
        int totalGenesInTrain = 0;
        int totalGenesInTest = 0;
        HashMap<String, BioCDocument> trainDocHash = buildDocIdDocumentHash(biocCollectionTraining);
        for (Iterator<BioCDocument> testDocIterator = biocCollectionTest.documentIterator(); testDocIterator.hasNext(); ) {
            BioCDocument testDoc = testDocIterator.next();

            HashSet<String> testGeneNamesInDoc = new HashSet<>();
            HashSet<String> trainGeneNamesInDoc = new HashSet<>();


            //Populate train geene
            for (BioCPassage passage : trainDocHash.get(testDoc.getID()).getPassages()) {
                List<String> genesInPassage =new BiocGeneHelper().getNormliasedGenes(passage);
                //To avoid duplicates
                trainGeneNamesInDoc.addAll(genesInPassage);

            }
            totalGenesInTrain += trainGeneNamesInDoc.size();

            //Populate test geene
            for (BioCPassage passage : testDoc.getPassages()) {
                List<String> genesInPassage = new BiocGeneHelper().getNormliasedGenes(passage);
                //To avoid duplicates
                testGeneNamesInDoc.addAll(genesInPassage);


            }
            totalGenesInTest = totalGenesInTest + testGeneNamesInDoc.size();

            //Correct in test
            for (String testGene : testGeneNamesInDoc) {
                if (trainGeneNamesInDoc.contains(testGene)) testCorrectCount++;

            }

        }


        double recall = (float) testCorrectCount / (float) totalGenesInTrain;

        double precision = (float) testCorrectCount / (float) totalGenesInTest;
        double f_measure = (2 * recall * precision) / (recall + precision);

        theLogger.info(String.format("Total genes in train %1$d, total in test %2$d, correct in test %3$d", totalGenesInTrain, totalGenesInTest, testCorrectCount));

        theLogger.info(String.format("Precision %1$.3f, recall %2$.3f, fmeasure %3$.3f", precision, recall, f_measure));


        return f_measure;

    }



    private HashMap<String, BioCDocument> buildDocIdDocumentHash(BioCCollection trainingSet) {
        HashMap<String, BioCDocument> docHashMap = new HashMap<>();
        for (BioCDocument doc : trainingSet.getDocuments()) {
            docHashMap.put(doc.getID(), doc);

        }
        return docHashMap;
    }
}
