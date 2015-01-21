import java.net.*;
import java.io.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class WebCrawler{

  public static void jsoupParse(String fileName){
    File input = new File(fileName);
    try{
      Document doc = Jsoup.parse(input, "UTF-8", "");
      Element content = doc.getElementById("content");
      Elements links = content.getElementsByTag("a");
      for (Element link : links) {
        String linkHref = link.attr("href");
        String linkText = link.text();
      }
    }
    catch (Exception e){
      System.err.println("Caught IOException: " + e.getMessage());

    }
  }

  public static void downloadFile(String seed, int i) throws IOException, MalformedURLException{

    URL urlObj = new URL(seed);
    BufferedReader x = new BufferedReader(new InputStreamReader(urlObj.openConnection().getInputStream()));
    String fileName = "file" + i + ".html";
    BufferedWriter fos = new BufferedWriter(new FileWriter(fileName));
    while(x.ready()){
      String line = x.readLine();
      fos.write(line);
      fos.write("\n");
    }
    x.close();
    fos.close();
    jsoupParse(fileName);
  }
  //method
  public static void readSeedFile(String fileName, String htmlFile){
    try{
      int i = 0;
      BufferedReader reader = new BufferedReader(new FileReader(fileName));
      String line;
      String outputPossible;
      while ((line = reader.readLine()) != null){
        System.out.println(line);
        downloadFile(line, i++);
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
