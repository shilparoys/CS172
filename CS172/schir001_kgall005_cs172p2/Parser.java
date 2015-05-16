import java.io.*;
import java.util.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class Parser{
    private static String title;
    private static String body;
    private static String url;
    private static String snippet;
    private static Set<String> uniqueDoc;

    //Constructor 
    Parser(){
        title = "";
        body = "";
        url = "";
        snippet = "";
        uniqueDoc = new HashSet<String>();
    }

    public static Document loadDoc(File child)throws IOException{
        org.jsoup.nodes.Document doc = Jsoup.parse(child, "UTF-8", "");

        //get relevant info
        title = doc.title();
        Elements paragraph = doc.select("p");
        for(Element p: paragraph){
            body += p.text();
        }
        url = child.getName();
        url = url.substring(0,4) + ':' + url.substring(5);
        url = url.replace("_", "/");
        snippet = doc.select("p").text();
        

        if(!uniqueDoc.contains(url)){
            uniqueDoc.add(url);
			Document luceneDoc = new Document();

			//double check all store & analyzed
			luceneDoc.add(new Field("title", title, Field.Store.YES, Field.Index.ANALYZED) );
			luceneDoc.add(new Field("url", url, Field.Store.YES, Field.Index.NO) );
			luceneDoc.add(new Field("body", body, Field.Store.YES, Field.Index.ANALYZED) );
			luceneDoc.add(new Field("snippet", snippet, Field.Store.YES, Field.Index.NO) );
			return luceneDoc;

        }
        else{
            System.err.println("Detected Duplicate Page");
			return null;
        }
    }
  
}

