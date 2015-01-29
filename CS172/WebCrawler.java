import java.net.*;
import java.io.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.*;

public class WebCrawler{

  static Set <String> url = new HashSet<String>();
  static URL urlObj;

  public static void printCollection(Set<String> c){
    Iterator<String> it = c.iterator();
    while ( it.hasNext() ){
      System.out.println(it.next());
    }

  }

  public static void parseHttpOnly(Set<String>url){
    Iterator<String> it = url.iterator();
    while(it.hasNext()){
      if(it.next().contains("http:")){
      }
      else{
        it.remove();
      }
    }
  //  printCollection(url);
  }

  public static void removeBookmark(Set<String>url){
    Iterator<String> it = url.iterator();
    while(it.hasNext()){
      if(it.next().contains("#")){
        int index = it.next().indexOf("#");
      //  String temp =it.next().substring(0, index-1);
        //it.remove();
      //  System.out.println(temp);
        //url.add(temp);
      }
    }
    printCollection(url);
  }

  //method to clean urls
  public static void cleanURL(Set<String> url){
    //parse only http links
    parseHttpOnly(url);
    removeBookmark(url);

  }

  //method to parse html file
  public static void jsoupParse(String fileName, String baseUrl){
    try{

      File input = new File("./htmlfolder/"+ fileName);
      Document doc = Jsoup.parse(input, "UTF-8", baseUrl);
      Elements links = doc.select("a[href]");
      for (Element link : links) {
        String linkHref = link.attr("abs:href");
        url.add(linkHref);
      }
    //  printCollection(url);
    }
    catch(IOException e){
      e.printStackTrace();
    }

    cleanURL(url);
  }

  //downlaoding file contents
  public static void downloadFile(String seed, int i, File dir) throws IOException, MalformedURLException{

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
  public static void readSeedFile(String fileName, String htmlFile){
    try{
      int i = 0;
      BufferedReader reader = new BufferedReader(new FileReader(fileName));
      String line;
      String outputPossible;
      File dir = new File("htmlfolder");
      dir.mkdir();
      while ((line = reader.readLine()) != null){
        downloadFile(line, i++, dir);
      }
      reader.close();
    }
    catch (Exception e){
      System.err.format("Exception occurred trying to read '%s'.\n", fileName);
    }
  }

  //main method
  public static void main(String [] args){

    String htmlFile = "";
    //read input from command line
    String fileName = args[0];
    int numPages = Integer.parseInt(args[1]);
    int hopsAway = Integer.parseInt(args[2]);
    String output = args[3];
    //read seed file
    readSeedFile(fileName, htmlFile);

  }

}
