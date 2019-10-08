package strategy.strategySave;

import data.RecordDepartament;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.Set;

public class XMLSaveStrategy implements StrategySave {

    private DocumentBuilderFactory builderFactory;
    private DocumentBuilder builderDoc;
    private static final Logger xmlSaveStrLogger = LogManager.getLogger(XMLSaveStrategy.class);


    public XMLSaveStrategy() {
        try {
            builderFactory = DocumentBuilderFactory.newInstance();
            builderDoc = builderFactory.newDocumentBuilder();
            xmlSaveStrLogger.debug("Билдер для сохранения готов");

        } catch (ParserConfigurationException e) {
            xmlSaveStrLogger.error(e);
        }
    }

    /**
     * Структура xml файла следующая:
     * <company>
     *     <Departamnet>
     *         <Code></Code>
     *         <Job></Job>
     *         <Comment></Comment>
     *     </Departamnet>
     * </company>
     *
     * **/

    @Override
    public void save(Set<RecordDepartament> data, File file) {

        Document doc = builderDoc.newDocument();

        Element rootElement = doc.createElement("company");
        doc.appendChild(rootElement);

        for (RecordDepartament x : data) {
            Element departam = doc.createElement("Departament");
            rootElement.appendChild(departam);

            Element code = doc.createElement("Code");
            code.appendChild(doc.createTextNode(x.getCode()));
            departam.appendChild(code);

            Element job = doc.createElement("Job");
            job.appendChild(doc.createTextNode(x.getJob()));
            departam.appendChild(job);

            Element comment = doc.createElement("Comment");
            comment.appendChild(doc.createTextNode(x.getComment()));
            departam.appendChild(comment);
        }


        xmlSaveStrLogger.debug("Дерево построили, осталось записать контент в файл");
        /** запись контента в файл**/

        try {

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(file);

            transformer.transform(source, result);
        } catch (TransformerConfigurationException e) {
            xmlSaveStrLogger.error(e);
        } catch (TransformerException e) {
            xmlSaveStrLogger.error(e);
        }
    }
}