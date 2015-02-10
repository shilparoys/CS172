import java.io.File;

public class Test{

  public static void main(String[] args){

      if(args.length < 4){
        System.err.println("invaid param num");
        System.exit(1);
      }

      String fileName = args[0];
      int numPages = Integer.parseInt(args[1]);
      int hopsAway = Integer.parseInt(args[2]);
      String output = args[3];
//	  int fileLines = Integer.parseInt(args[4]); 

      //check if numPages and hopsAway is a valid no.
      if(numPages <= 0 ||  hopsAway < 0){
	    System.err.println("Invalid Number");
        System.exit(1);
      }

	  File dir = new File(output);
	  if(!dir.exists())
		  dir.mkdir();

      WebCrawler wb = new WebCrawler(fileName, numPages, hopsAway, dir);// fileLines);

      //check if inputFile exists
      File f = new File(fileName);
      if(!f.exists() && !f.isFile()){
        System.err.println("Seed file not found");
        System.exit(1);
      }
      wb.readSeedFile();

	  wb.threadTask();

  }
}
