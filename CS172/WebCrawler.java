import java.net.*;
import java.io.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.*;


public class WebCrawler{

	private Set <String> url = new HashSet<String>();
	private String linkHref;

    public void printCollection(){
    	Iterator<String> it = url.iterator();
    	while ( it.hasNext() ){
			System.out.println(it.next());
    	}
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
									url.add(linkHref);
							}
						}
      		}
    	}
    	catch(IOException e){
      		e.printStackTrace();
    	}
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

}
