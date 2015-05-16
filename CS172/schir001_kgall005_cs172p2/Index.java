import java.io.*;
import java.util.*;

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

public class Index{
	private Parser p;

	//Constructor
	public Index(){
 		p = new Parser(); 
	}

	void createIndex(File dir, File outdir){
		try{
			File index = outdir;
			IndexWriter writer = null;

			IndexWriterConfig indexConfig = new IndexWriterConfig(Version.LUCENE_34, new StandardAnalyzer(Version.LUCENE_35) );
			writer = new IndexWriter(FSDirectory.open(index), indexConfig);
		
			File[] dirListing = dir.listFiles();
			if(dirListing != null){
				for(File child: dirListing){
					Document d = p.loadDoc(child);	
					if(d != null){
						writer.addDocument(d);
					}
				}
			}
			if(writer != null){
				try{
					writer.close();
				} catch(CorruptIndexException e){
					e.printStackTrace();
				} catch(IOException e){
					e.printStackTrace();
				}
			}
		}
		catch(IOException e){
			System.err.println("error in creating index");
		}
	}
}
