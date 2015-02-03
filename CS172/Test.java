import java.io.File;

public class Test{

  public static void main(String[] args){
      String htmlFile = "";
      //read input from command line
      String fileName = args[0];
      int numPages = Integer.parseInt(args[1]);
      int hopsAway = Integer.parseInt(args[2]);
      String output = args[3];
      //check if inputFile exists
      File f = new File(fileName);
      if(f.exists() && f.isFile()){
      	//read seed file
      	WebCrawler wb = new WebCrawler();
      	wb.readSeedFile(fileName, htmlFile);
      	//wb.printCollection("b");
      }
      else{
        System.err.println("Seed file not found");
      }
  }
}
