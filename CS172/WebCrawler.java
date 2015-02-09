import java.net.*;
import java.io.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.*;
import java.util.concurrent.*;


public class WebCrawler{

  //private member variables
  private String fileName;
  private String linkHref;
  private int maxNumPages;
  private int maxHopsAway;
  private int maxThreads = 5;
  private int i=0;
  private File output;
  private Data d; 

  //constructor
  WebCrawler(String file, int pages, int hops, File ot){
	    d = new Data();
	    fileName = file;
		maxNumPages = pages;
		maxHopsAway = hops;
		output = ot;
  }

	public String parseHttpOnly(){
		if(linkHref.contains("http:"))
			return linkHref;
		else
			return "";
	}

	public String removeBookmark(){
    	int index = 0;
		if(linkHref.contains("#")){
			index = linkHref.indexOf("#");
      		String temp = new String(linkHref.substring(0, index));
			return temp;
    	}
		return linkHref;
	}

  String deleteCharAt(String strValue, int index) {
		return strValue.substring(0, index) + strValue.substring(index + 1);
  }

  //strip last forwardslash
  public String stripForwardSlash(){
	int length = linkHref.length();
	if(linkHref.charAt(length-1) == '/'){
		String newLink = deleteCharAt(linkHref, length-1);
    	return newLink;
    }
    return linkHref;
  }

  //method to clean urls
  public String cleanURL(){
	linkHref = parseHttpOnly();
	if(!linkHref.isEmpty())
		linkHref = removeBookmark();
	return linkHref;
	}

	//method to read from seedFile which contains .edu domains, 0th hop
  public void readSeedFile(){
	try{
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
      	String line;
			//reading from input file here
      	while ((line = reader.readLine()) != null){
	  		if(!line.trim().equals("")){
				linkHref = line;
				linkHref = cleanURL();
				linkHref = stripForwardSlash();
				URLObj u = new URLObj(linkHref, 0);
				d.addToQueue(u);
			}
	  	}
      reader.close();
	}
    catch(IOException e){
		System.err.format("IO exception at readLine");
    }
    catch (Exception e){
    	System.err.format("Exception occurred trying to read '%s'.\n", fileName);
    }
  }

  	public void threadTask(){
		//check number of lines in the seed file and compare
//		if(maxNumPages > numLines)
//			maxThreads = numLines;
		while(true){
			if(d.currPage() >= maxNumPages || d.currHop() > maxHopsAway || d.queueIsEmpty() )
			{
				break;
			}	
	
			if(d.getNumThreads() < maxThreads){
				URLThread t = new URLThread(fileName, d, output);
				t.start();
			}
		}
		d.finishThread();
  	}
}
