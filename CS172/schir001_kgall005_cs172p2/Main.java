import java.io.*;
import java.util.*;

import org.apache.lucene.search.TopDocs;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.document.Document;

public class Main{
    public static void main(String [] args){
        if(args.length != 1){
            System.err.println("missing output_dir");
            System.exit(1);
        }
        //Checks for 5GB data folder under /out
        String fileName = "./out";
        File dir = new File(fileName);
        String outName = args[0];
        File outdir = new File(outName);
        if(!outdir.exists()){
            outdir.mkdir();
        }
        if(dir.exists() && dir.isDirectory()){
			Index i = new Index();
			i.createIndex(dir, outdir);
        }
	}
}
 
