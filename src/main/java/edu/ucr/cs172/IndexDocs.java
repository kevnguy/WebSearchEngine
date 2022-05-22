package edu.ucr.cs172;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.mongodb.client.*;

import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.NumericDocValues;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;

import static org.apache.lucene.document.DateTools.dateToString;
//import org.apache.lucene.codecs.simpletext.SimpleTextCodec;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class IndexDocs /*implements AutoCloseable*/ {

    // for logging
    protected static Logger logger = LogManager.getLogger(IndexDocs.class);

    // enum for field storage
    private enum Value {
        store, no_store, pos, pos_offset, no_tvector
    }

    public static void main(String[] args) throws Exception {
        // counter for number of docs indexed
        int numDocs = 0;

        // map to define custom analyzers for different fields
        Map<String, Analyzer> analyzerPerField = new HashMap<>();
        analyzerPerField.put("mainText", Utilities.customAnalyzer());
        analyzerPerField.put("title", new SimpleAnalyzer());

        Analyzer analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerPerField);
        Directory directory = FSDirectory.open(Path.of("index/"));
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        // Uncomment for readable index file
        // config.setCodec(new SimpleTextCodec());
        IndexWriter indexWriter = new IndexWriter(directory, config);

        MongoClient mongoClient = Utilities.getMongoClient(Utilities.URI);
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

    private static Document indexDoc(org.bson.Document document) {
        Document doc = new Document();
        /*  Can take advantage on index on mongo to keep index small
            Field titleField = new Field("title", document.getString("title"), fieldText(Value.store));
            Field dateField = new Field("lastMod", dateToString(document.getDate("lastMod"),
                DateTools.Resolution.DAY), fieldString(Value.store));
            doc.add(titleField);
            doc.add(dateField);
        */
        String lastModified = dateToString(document.getDate("lastMod"), DateTools.Resolution.DAY);
        Field urlField = new Field("url", document.getString("url"), fieldString(Value.store));
        Field titleField = new Field("title", document.getString("title"),
                fieldText(Value.no_store, Value.no_tvector));
        IntPoint dateField = new IntPoint("lastMod", Integer.parseInt(lastModified));
        Field mainTextField = new Field("mainText", document.getString("mainText"),
                fieldText(Value.no_store, Value.pos_offset));
        Field boldTextField = new Field("boldText", document.getString("boldText"),
                fieldText(Value.no_store, Value.no_tvector));
        Field italicTextField = new Field("italicText", document.getString("italicText"),
                fieldText(Value.no_store, Value.no_tvector));
        doc.add(urlField);
        doc.add(titleField);
        doc.add(dateField);
        doc.add(mainTextField);
        doc.add(boldTextField);
        doc.add(italicTextField);
        return doc;
    }

    private static FieldType fieldText(Value store, Value tvector) {
        FieldType field;
        // option to store item in index
        field = (store == Value.store) ? new FieldType(TextField.TYPE_STORED) : new FieldType(TextField.TYPE_NOT_STORED);

        // option to store additional index options
        field.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);

        // option to store term vectors
        if (tvector == Value.pos) {
            field.setStoreTermVectors(true);
            field.setStoreTermVectorPositions(true);
        } else if (tvector == Value.pos_offset) {
            field.setStoreTermVectors(true);
            field.setStoreTermVectorPositions(true);
            field.setStoreTermVectorOffsets(true);
        }
        return field;
    }

    private static FieldType fieldString(Value option) {
        FieldType field;
        if (option == Value.store)
            field = new FieldType(StringField.TYPE_STORED);
        else
            field = new FieldType(StringField.TYPE_NOT_STORED);
        field.setIndexOptions(IndexOptions.NONE);
        return field;
    }

    private static FieldType fieldDate() {
        FieldType date = new FieldType(TextField.TYPE_STORED);
        date.setIndexOptions(IndexOptions.NONE);
        return date;
    }
}