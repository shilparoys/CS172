import java.net.*;
import java.io.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.*;


public class WebCrawler{

	private Set <String> url = new TreeSet<String>();

    public void printCollection(){
    	Iterator<String> it = url.iterator();
    	while ( it.hasNext() ){
			System.out.println(it.next());
    	}
	}

	public void parseHttpOnly(){
    	Iterator<String> it = url.iterator();
    	while(it.hasNext()){
    		String temp = it.next();
			if(temp.contains("http:")){}
        	else{
        		it.remove();
        	}
    	}
		printCollection();
	}

	public void removeBookmark(){
    	Iterator<String> it = url.iterator();
    	int index = 0;
    	while(it.hasNext()){
        	String curr = it.next();
      		if(curr.contains("#")){
        		index = curr.indexOf("#");
        		String temp = new String(curr.substring(0, index));
        		it.remove();
        		if(!url.contains(temp)){
        			url.add(temp);
        		}
      		}
    	}	
    	printCollection();
	}

    //method to clean urls
  	public void cleanURL(){
		parseHttpOnly();
      	//removeBookmark();
	}

	//method to parse html file
  	public void jsoupParse(String fileName, String baseUrl){
    	try{
        	File input = new File("./htmlfolder/"+ fileName);
        	Document doc = Jsoup.parse(input, "UTF-8", baseUrl);
        	Elements links = doc.select("a[href]");
        	for (Element link : links) {
				String linkHref = link.attr("abs:href");

		    	//if(url.contains(linkHref)){} //doesn't work for removing repeats
		    	//else
					url.add(linkHref);
      		}
    		printCollection();
    	}
    	catch(IOException e){
      		e.printStackTrace();
    	}
    	//cleanURL();
  	}

	//downlaoding file contents
  	public void downloadFile(String seed, int i, File dir) throws IOException, MalformedURLException{
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


	//method to read from seedFile which contains .edu domains
  	public void readSeedFile(String fileName, String htmlFile){
    	try{
      		int i = 0;
      		BufferedReader reader = new BufferedReader(new FileReader(fileName));
      		String line;
      		String outputPossible;
      		File dir = new File("htmlfolder");
      		dir.mkdir();
      		while ((line = reader.readLine()) != null){
        		if(!line.trim().equals(""))
        		downloadFile(line, i++, dir);
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

	//main method
  	// public static void main(String [] args){
		//String htmlFile = "";
  	    //read input from command line
  	    //String fileName = args[0];
     	//int numPages = Integer.parseInt(args[1]);
     	//int hopsAway = Integer.parseInt(args[2]);
     	//String output = args[3];
        //read seed file
     	//readSeedFile(fileName, htmlFile);
     	//}
}
