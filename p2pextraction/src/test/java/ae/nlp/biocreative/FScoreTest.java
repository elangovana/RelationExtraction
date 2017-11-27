package ae.nlp.biocreative;

import ae.nlp.biocreative.helpers.ConfigHelper;
import com.pengyifan.bioc.BioCCollection;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DecimalFormat;


public class FScoreTest {
    private FScore _sut;
    private String _testdatadir;

    @BeforeTest
    void setUp() {
        _sut = new FScore();
        //Testdata
        _testdatadir = ConfigHelper.getTestDataDirectory();

    }

    @AfterTest
    void tearDown() {

    }

    @DataProvider(name = "calculateScoreTestCases")
    public static Object[][] calculateScoreTestCases() {
        return new Object[][] {
                {"PMtask_Relations_TrainingSet.xml", "PMtask_Relations_TrainingSet.xml", 1.0}
                ,{"ScoreTestcasePrecision1_Pred.xml", "ScoreTestcasePrecision1_Train.xml", 1.0}
                ,{"fscoreTestdataPredicted.xml","relationtrainingdata.xml",.3159 }
        };
    }

    @Test(dataProvider = "calculateScoreTestCases")
    void calculateScore(String iPredictedRelBiocXML, String iTrainingDataBiocXml , double expectedScore ) throws SAXException, XMLStreamException, ParserConfigurationException, IOException {
        String testdatadir = ConfigHelper.getTestDataDirectory();

        File sampletraindatafile =Paths.get(testdatadir, iTrainingDataBiocXml).toFile();
        File samplePreddatafile = Paths.get(testdatadir,iPredictedRelBiocXML).toFile();

        BioCCollection predSet =new Parser().getBioCCollection(samplePreddatafile).readCollection();
        BioCCollection trainingSet=new Parser().getBioCCollection(sampletraindatafile).readCollection();
        
        //Act
        double actual = _sut.CalculateScore(trainingSet, predSet);

        //Assert
        DecimalFormat numFormat = new DecimalFormat("0.000");
        Assert.assertEquals ( numFormat.format(actual),numFormat.format(expectedScore));
    }

}