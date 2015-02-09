import java.util.concurrent.*;
import java.util.*;
import java.io.*;

public class Data{
	
	private List<Thread> threadList;
	private BlockingQueue<URLObj> readylist;
	private Set<String> url;
	private int currentNumPages;
	private int numThreads;
	private int currentHopsAway;
	private int maxThreads;
	
	Data(){
		readylist = new LinkedBlockingQueue<URLObj>();
		threadList = new ArrayList<Thread>();
		url = new HashSet<String>();
		numThreads = 0;
		currentNumPages = 0;
		currentHopsAway = 0;
		maxThreads = 80;
	}
	public synchronized void urlAdd(String a){
		url.add(a);
	}
	public boolean urlContains(String a){
		return url.contains(a);
	}
	public synchronized void addToQueue(URLObj a){
		readylist.add(a);
	}
	public synchronized URLObj removeQueue(){
		return readylist.poll();
	}
	public URLObj peekQueue(){
		return readylist.peek();
	}
	public boolean queueIsEmpty(){
		return readylist.isEmpty();
	}
	public synchronized void numPagesinc(){
		currentNumPages++;
	}
	public synchronized void hopsinc(){
		currentHopsAway++;
	}
	public synchronized int currPage(){
		return currentNumPages;
	}
	public int currHop(){
		return currentHopsAway;
	}
	public int getNumThreads(){
		return numThreads;
	}
	public synchronized void addNumThreads(){
		numThreads++;
	}
	public synchronized void subNumThreads(){
		numThreads--;
	}
	public synchronized void addThread(Thread a){
		threadList.add(a);
	}
	public synchronized void finishThread(){
		for(Thread thread : threadList){
			if(thread.isAlive() )
				thread.interrupt();
		}
	}
}
