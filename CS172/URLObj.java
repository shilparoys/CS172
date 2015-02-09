
public class URLObj{
	
	private String URLstring;
	private int numhops;

	URLObj(String u, int n){
		URLstring = u;
		numhops = n;
	}
	public String getURL(){
		return URLstring;
	}
	public int getHops(){
		return numhops;
	}
	public void setURL(String url){
		URLstring = url;
	}
	public void sethop(int hops){
		numhops = hops;
	}

}
