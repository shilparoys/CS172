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
	//queue for URLs to crawl
	private Queue<String> readylist = new LinkedList<String>();
	//list that contains all of the urls 
	private ConcurrentHashMap<String, Integer> url = new ConcurrentHashMap<String, Integer>();
    private String linkHref;
	private int currenthop;
    private int numPages; 
    private int maxHopsAway;
    int i = 0;

    //constructor
    WebCrawler(int pages, int hops){
		numPages = pages;
		maxHopsAway = hops;
		currenthop = 0;
    }
    
    //method to print out Collection
    public void printCollection(){
		Iterator it = readylist.iterator();
		System.out.println(url.size());

/*		while(it.hasNext()){
			System.out.println(it.next());
		}
		System.out.println("\n\n");
*/

        //for url, concurrenthashmap iteration
		for(Iterator<String> i = url.keySet().iterator(); i.hasNext(); ) {
			String key = i.next();
			System.out.println(key);
    	}
		
	}

	//add to both lists
	public void addToList(String currURL, int hop){
		readylist.add(currURL);
		url.putIfAbsent(currURL, hop);


		readMoreURL();
	}

	public String parseHttpOnly(){
		if(linkHref.contains("http:")){
			return linkHref;
		}
        else{
			return "";
      }
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
		if(!linkHref.isEmpty()){
			linkHref = removeBookmark();
		}
		return linkHref;
	}

	//method to parse html file
  	public void jsoupParse(String fileName, String baseUrl){
    	try{
        	File input = new File("./htmlfolder/"+ fileName);
        	Document doc = Jsoup.parse(input, "UTF-8", "baseUrl");
        	Elements links = doc.select("a[href]");
        	for (Element link : links) {
				   	linkHref = link.attr("abs:href");
						linkHref.trim();
						if(!linkHref.isEmpty()){
							linkHref = cleanURL();
							if(!linkHref.isEmpty() && !url.contains(linkHref)){
								linkHref = stripForwardSlash();
								if(url.size() < numPages ){
									addToList(linkHref,currenthop);
								}
								else
									return;
							}
						}
      		}
    	}
    	catch(IOException e){
      		e.printStackTrace();
    	}
  	}

	//downlaoding file contents
  	public void downloadFile(String seed,/* int i,*/ File dir) throws IOException, MalformedURLException{
		i++;
    	URL urlObj = new URL(seed);
    	BufferedReader x = new BufferedReader(new InputStreamReader(urlObj.openConnection().getInputStream()));
    	String fileName = "file" + i + ".html";
    	BufferedWriter fos = new BufferedWriter(new FileWriter(new File(dir, fileName)));
    	while(x.ready()){
    		String line = x.readLine();
      		fos.write(line);
      		fos.write("\n");
    	}
    	x.close();
    	fos.close();
    	jsoupParse(fileName, seed);
  	}

	//method to read from seedFile which contains .edu domains, 0th hop
  	public void readSeedFile(String fileName, String htmlFile){
    	try{
      		BufferedReader reader = new BufferedReader(new FileReader(fileName));
      		String line;
      		String outputPossible;
      		File dir = new File("htmlfolder");
      		dir.mkdir();
      		while ((line = reader.readLine()) != null){
        		if(!line.trim().equals(""))
        			downloadFile(line,/* i++,*/ dir); 
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

    //similar to readSeedFile, reads from readylist
	public void readMoreURL(){ 
		String myURL = "";
		try{
			currenthop++;
			if(currenthop > maxHopsAway ) 
				return;
		
      		File dir = new File("htmlfolder");
			 myURL = readylist.poll();
			if(myURL != null){
				RobotExclusionUtil r = new RobotExclusionUtil();  
				boolean follow = r.robotsShouldFollow(myURL);
				if(follow)
					downloadFile(myURL, dir);
			}
		}
		catch(IOException e){
      		System.err.format("IO exception at readLine");
    	}
    	catch (Exception e){
      		System.err.format("Exception occurred trying to read '%s'.\n", myURL);
    	}
	}

    //checks if URL is valid
	public boolean validURL(String url){
		try{
    	    URL urlObj = new URL(url);

			/* Check if URL exists. */
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

}
