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
	//one queue = 1 depth
	private Queue<String> readylist1 = new LinkedList<String>();
	private Queue<String> readylist2 = new LinkedList<String>();
	private Set<String> url = new HashSet<String>();

  private String linkHref;
  private int currenthop;
  private int maxNumPages;
  private int maxHopsAway;
  private int addTo;
  private int i=0;
  private boolean keepgoing = true;
  private String output;

  //constructor
  WebCrawler(int pages, int hops, String ot){
		maxNumPages = pages;
		maxHopsAway = hops;
		output = ot;
		currenthop = 0;
		addTo = 1;
  }

  //method to print out Collection
  public void printCollection(){
		Iterator it = url.iterator();
		while(it.hasNext())
			System.out.println(it.next());
	}

	//add to all lists
	public void addToList(String currURL){
		//they're both empty at the beginning
		if(addTo == 1)
			readylist1.add(currURL);
		else if(addTo == 2)
			readylist2.add(currURL);
		else
			System.out.println("addTo error");

		url.add(currURL);
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

	//method to parse html file
  public void jsoupParse(String fileName, String baseUrl){
	try{
		String ot = "./" + output + "/";
		File input = new File(ot + fileName);
      Document doc = Jsoup.parse(input, "UTF-8", "baseUrl");
      Elements links = doc.select("a[href]");
      for (Element link : links) {
				linkHref = link.attr("abs:href");
				linkHref.trim();
				if(!linkHref.isEmpty()){
					linkHref = cleanURL();
					if(!linkHref.isEmpty() && !url.contains(linkHref)){
						linkHref = stripForwardSlash();
							addToList(linkHref);
						
					}
				}
      }
    }
  	catch(IOException e){
			e.printStackTrace();
    }
  }

	public void outputToFile(String fileName2){
		try{
			BufferedWriter wr = new BufferedWriter(new FileWriter(new File(fileName2, "answers.txt")));
			Iterator<String> iterator = url.iterator();
			while(iterator.hasNext()){
				String temp = iterator.next().toString();
				wr.write(temp + "\n");
			}
			wr.close();
		}
		catch(IOException e){
				System.err.format("IO exception at downloadfile");
			}

	}

	//downlaoding file contents
  public void downloadFile(String seed, File dir) throws IOException, MalformedURLException{
	try{
		i++;
		URL urlObj = new URL(seed);
  	BufferedReader x = new BufferedReader(new InputStreamReader(urlObj.openStream() ) );

	String fileName = "file" + i + ".html";

//System.out.println("download file i: " + i);

    BufferedWriter fos = new BufferedWriter(new FileWriter(new File(dir, fileName)));

    String line;
    while((line = x.readLine()) != null && keepgoing){
      fos.write(line);
      fos.write("\n");
    }
  	x.close();
  	fos.close();
	jsoupParse(fileName, seed);
    if(i >= maxNumPages){
		keepgoing = false;

// System.out.println("downloadpages false");
	}

	}
 catch(IOException e){
			System.err.format("IO exception at downloadfile");
    }
//System.out.println("download file : end");

  }

	//method to read from seedFile which contains .edu domains, 0th hop
  public void readSeedFile(String fileName, String htmlFile, String output){
	try{
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
      	String line;
      	String outputPossible;
      	File dir = new File(output);
        if(!dir.exists()){
      	    dir.mkdir();
        }
			//reading from input file here
      	while ((line = reader.readLine()) != null){
	  		if(!line.trim().equals("")){
				//FIX THIS
					linkHref = line;
					linkHref = cleanURL();
					linkHref = stripForwardSlash();
					addToList(linkHref);
			}
	  	}
      reader.close();
	  //now we want to read from the queues
	  currenthop++;
	  while((currenthop <= maxHopsAway)){
		readMoreURL(dir);
		currenthop++;
	  }
    }
    catch(IOException e){
			System.err.format("IO exception at readLine");
    }
    catch (Exception e){
    	System.err.format("Exception occurred trying to read '%s'.\n", fileName);
    }
  }

    //similar to readSeedFile, reads from readylist
	public void readMoreURL(File dir){
		String myURL = "";
		try{
			if(addTo == 1){
				myURL = readylist1.poll();
				while(myURL != null && keepgoing){
					RobotExclusionUtil r = new RobotExclusionUtil();
					boolean follow = r.robotsShouldFollow(myURL);
					if(follow && i < maxNumPages){
	System.out.println("readmoreurl myURL: " + myURL);
						downloadFile(myURL, dir);
						
					}
					myURL = readylist1.poll();
				}
				addTo = 2;
			}

			else if(addTo == 2){
				myURL = readylist2.poll();
				while(myURL != null && keepgoing){
					RobotExclusionUtil r = new RobotExclusionUtil();
					boolean follow = r.robotsShouldFollow(myURL);
					if(follow && i<maxNumPages)
						downloadFile(myURL, dir);
					myURL = readylist2.poll();
				}
				addTo = 1;
			}
			else
				System.out.println("addTo error");


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
