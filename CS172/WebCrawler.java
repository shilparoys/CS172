import java.net.*;
import java.io.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.*;


public class WebCrawler{

  public static void printCollection(Set<String> c){
    Iterator<String> it = c.iterator();
    while ( it.hasNext() ){
      System.out.println(it.next());
    }

  }

  //method to parse html file
  public static void jsoupParse(String fileName){
    try{
      Set <String> url = new HashSet<String>();

      File input = new File("./htmlfolder/"+ fileName);
      Document doc = Jsoup.parse(input, "UTF-8");

      //Element content = doc.getElementById("content");
      Elements links = doc.select("a[href]");
      for (Element link : links) {
        String linkHref = link.attr("abs:href");
        System.out.println(linkHref);
        url.add(linkHref);
      }
      printCollection(url);

    }
    catch(IOException e){
      e.printStackTrace();
    }



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
    jsoupParse(fileName);
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
        System.out.println(line);
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
