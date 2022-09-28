package edu.ucr.cs172;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.mongodb.client.*;

import org.apache.lucene.document.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;

//import static org.apache.lucene.document.DateTools.dateToString;
import org.apache.lucene.codecs.simpletext.SimpleTextCodec;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class IndexDocs /*implements AutoCloseable*/ {

    // for logging
    protected static Logger logger = LogManager.getLogger(IndexDocs.class);

    // enum for field storage
    private enum Value {
        store, no_store
    }

    public static void main(String[] args) throws Exception {
        // counter for number of docs indexed
        int numDocs = 0;

        // map to define custom analyzers for different fields
        Map<String, Analyzer> analyzerPerField = new HashMap<>();
        analyzerPerField.put("mainText", Utilities.customAnalyzer());

        Analyzer analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerPerField);
        Directory directory = FSDirectory.open(Path.of("index/"));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        // Uncomment for readable index file
        // config.setCodec(new SimpleTextCodec());
        IndexWriter indexWriter = new IndexWriter(directory, config);

        final String uri = "mongodb://localhost:27017";
        MongoClient mongoClient = Utilities.getMongoClient(uri);
        assert mongoClient != null;
        MongoDatabase database = mongoClient.getDatabase("Wikipedia");
        MongoCollection<org.bson.Document> collection = database.getCollection("Pages");

        try (MongoCursor<org.bson.Document> cursor = collection.find().iterator()) {
            while (cursor.hasNext()) {
                org.bson.Document page = cursor.next();
                Document doc = indexDoc(page);
                indexWriter.addDocument(doc);
                logger.info("Indexed document with url: {}", page.getString("url"));
                numDocs++;
            }
        }
        indexWriter.close();
        directory.close();
        logger.info("Finish indexing {} docs", numDocs);
    }
    
    private static Document indexDoc(org.bson.Document document){
        Document doc = new Document();
        Field titleField = new Field("title", document.getString("title"), fieldText(Value.store));
        Field urlField = new Field("url", document.getString("url"), fieldString(Value.store));
//        Field dateField = new Field("lastMod", dateToString(document.getDate("lastMod"),
//                DateTools.Resolution.DAY), fieldString(Value.store));
        Field mainTextField = new Field("mainText", document.getString("mainText"), fieldText(Value.no_store));
        Field boldTextField = new Field("boldText", document.getString("boldText"), fieldText(Value.no_store));
        Field italicTextField = new Field("italicText", document.getString("italicText"), fieldText(Value.no_store));
        doc.add(titleField);
        doc.add(urlField);
//        doc.add(dateField);
        doc.add(mainTextField);
        doc.add(boldTextField);
        doc.add(italicTextField);
        return doc;
    }
    private static FieldType fieldText(Value option){
        FieldType field;
        if (option == Value.store)
            field = new FieldType(TextField.TYPE_STORED);
        else
            field = new FieldType(TextField.TYPE_NOT_STORED);
        field.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
        field.setStoreTermVectors(true);
        field.setStoreTermVectorPositions(true);
        return field;
    }
    private static FieldType fieldString(Value option){
        FieldType field;
        if (option == Value.store)
            field = new FieldType(StringField.TYPE_STORED);
        else
            field = new FieldType(StringField.TYPE_NOT_STORED);
        field.setIndexOptions(IndexOptions.NONE);
        return field;
    }
}

