package ae.nlp.biocreative;

import ae.nlp.biocreative.helpers.FileDownloadHelper;
import com.pengyifan.bioc.io.BioCCollectionReader;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertTrue;


class ParserIT {
    private Parser sut;
    private static File tempFile;
    private static final Logger logger =
            Logger.getLogger(ParserIT.class.getName());



    @BeforeAll
    static void  classSetup() throws IOException, ConfigurationException {
        //Download biocreative data file
        tempFile= File.createTempFile("biocreative",".xml");
        String downloadUrl= new XMLConfiguration("config.xml").getString("xmlrelationshipTrainDataUrl");
        FileDownloadHelper.downloadFromUrl(new URL(downloadUrl), tempFile.getAbsolutePath());

    }


    @BeforeEach
    void setUp() throws IOException {
        sut = new Parser();
    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void process() throws IOException, XMLStreamException, ParserConfigurationException, SAXException {
        //Act
        BioCCollectionReader actual = sut.getBioCCollection(tempFile);
        //Assert
        assertTrue(actual.readCollection().getDocmentCount() > 0);
    }


}