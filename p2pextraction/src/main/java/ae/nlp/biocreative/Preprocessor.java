package ae.nlp.biocreative;

import com.pengyifan.bioc.BioCCollection;
import com.pengyifan.bioc.io.BioCCollectionReader;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

/**
 * Created by aparnaelangovan on 5/08/2017.
 */
public interface Preprocessor {

    BioCCollection Process(BioCCollection biocCollection) throws XMLStreamException, IOException, InterruptedException;
}
