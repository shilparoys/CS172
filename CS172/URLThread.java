import java.net.*;
import java.io.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.*;
import java.util.concurrent.*;

public class URLThread implements Runnable{
	
	private String output;
	private Thread t;	
	private Data curr;
	private File dir;
	private String linkHref;
	
	URLThread(String out, Data a, File d){
		output = out;
		curr = a;
		dir = d;
	}	
	//downloading file contents
	public void downloadFile() throws IOException, MalformedURLException{
	try{
		curr.numPagesinc();
		URLObj currseed = curr.removeQueue();
		String seed = currseed.getURL();

		RobotExclusionUtil robotcheck = new RobotExclusionUtil();
		if(!robotcheck.robotsShouldFollow(seed) ){
			curr.subNumThreads();
			return;
		}
	
		curr.urlAdd(seed);
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("./" + dir +"/answers.txt", true) ) );
		out.println(seed);
		out.close();
		//hops inc
		URLObj nextseed = curr.peekQueue();
		if(currseed.getHops() != nextseed.getHops() ){
			curr.hopsinc();
		}

		
		URL urlObj = new URL(seed);
  		BufferedReader x = new BufferedReader(new InputStreamReader(urlObj.openStream() ) );
		String fileName = seed.substring(7, seed.length() );
    	BufferedWriter fos = new BufferedWriter(new FileWriter(new File(dir, fileName)));

    	String line;
    	while((line = x.readLine()) != null){
			fos.write(line);
      		fos.write("\n");
    	}
  		x.close();
  		fos.close();
		jsoupParse(fileName, seed, currseed.getHops() + 1);
		curr.subNumThreads();
	System.out.println("done with jsoup");
	}
 	catch(IOException e){
		System.err.format("IO exception at downloadfile");
    }	
  }

  public void jsoupParse(String fileName, String baseUrl, int hops){
	try{
		String ot = "./" +  dir + "/";
		File input = new File(ot + fileName);
     	Document doc = Jsoup.parse(input, "UTF-8", "baseUrl");
      	Elements links = doc.select("a[href]");
      	for (Element link : links) {
			linkHref = link.attr("abs:href");
			linkHref.trim();
			if(!linkHref.isEmpty()){
				linkHref = cleanURL();
				if(!linkHref.isEmpty() && !(curr.urlContains(linkHref) ) ){
					linkHref = stripForwardSlash();
					if(validURL(linkHref)){
						URLObj add = new URLObj(linkHref, hops);
						curr.addToQueue(add);
					}
						
				}
			}	
      	}
    }
  	catch(IOException e){
		e.printStackTrace();
   	 }
  }


public boolean validURL(String url){
	try{
    	URL urlObj = new URL(url);
		HttpURLConnection.setFollowRedirects(false);
		HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
		con.setRequestMethod("GET");
		con.connect();
		int exists = con.getResponseCode();
		if(exists == 200)
			return true;
		return false;
	}
    catch (Exception e){
		System.err.format("Exception occurred in validURL");
    }
	return false;

}
public void run(){
	try{
		downloadFile();
	}
	catch(IOException e){
		System.err.format("IO exception at downloadfile");
    }	
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

	public void start(){
		if(t == null)
		{
			t = new Thread(this);
			curr.addNumThreads();
			curr.addThread(t);
			t.start();
		}
	}
}
