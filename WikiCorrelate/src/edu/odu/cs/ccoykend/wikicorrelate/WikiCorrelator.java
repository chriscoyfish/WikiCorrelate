package edu.odu.cs.ccoykend.wikicorrelate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * WikiCorrelator class implementation for DFS Wikipedia graph search
 * @author Chris Coykendall
 *
 */
public class WikiCorrelator  {
	
	private HashMap<String, Integer> _visited;
	private Vector<String> _path;
	private String _urlDest;
	private String _urlSource;
	private int _results;
	private int _maxHops;
	private int _msgLevel;
	private int _timeOut;
	
	/**
	 * Recursive depth-first graph search to determine a Wikipedia article node
	 * has a possible path to the class target destination URL. Called by
	 * {@link WikiCorrelator#find()}
	 * @param path Current path state
	 * @param url The URL to be parsed
	 * @param hops The current # of hops taken to arrive at this point
	 * @return True if a path exists to the given URL
	 */
	private boolean hasPathTo(Vector<String> path, String url, int hops) {
		if (hops==0) path.add(url);
		if (hops>_maxHops) return false;
		// If it has been visited and in same or less hops, no good.
		Integer oldhops = _visited.get(url);
		if (oldhops!=null && oldhops<=hops) return false;
		else {
			_visited.remove(url);
			_visited.put(url, hops);
		}
		Document c = null;
		try {
			c = Jsoup.connect("http://en.wikipedia.org" + 
					url).timeout(_timeOut).get();
		} catch (IOException e) {
			System.out.println(url + " - Unable to fetch!");
		}
		if (c==null) return false;
		Elements foundLinks = c.select("a");
		
		if (hops<=_msgLevel && _msgLevel!=0) 
			System.out.println(url + " - " + hops + " hop(s)");
		
		for (Element link : foundLinks) {
			String uri = link.attr("href");
			if (uri.toUpperCase().equals(_urlDest.toUpperCase())) {
				_visited.put(uri,hops);
				path.add(uri);
				_path = path;
				return true;
			}
		}
		
		boolean valid=false;
		for (Element link : foundLinks) {
			if (valid==true) return true;
			String uri = link.attr("href");
			if (!isIrrelevantLink(uri)) {
				_results++;
				Vector<String> newPath = new Vector<String>(path);
				newPath.add(uri);
				valid = hasPathTo(newPath,uri,hops+1);
			}
		}
		return false;
	}
	
	/** 
	 * Utility function to determine if the link is relevant to the article.
	 * @param uri The link to be examined
	 * @return True if the link is irrelevant (Wikipedia main articles, talk,
	 * community links, etc.
	 */
	private boolean isIrrelevantLink(String uri) {
		boolean invalid=false;
		invalid |= !uri.startsWith("/wiki/");
		invalid |= uri.startsWith("/wiki/Main_Page");
		invalid |= uri.contains("_(disambiguation)");
		invalid |= uri.contains(":");
		return invalid;
	}
	
	/**
	 * Constructor for WikiCorrelator class. Note that source and destination 
	 * articles will take additional hops if not in correct format and have to
	 * be special-searched due to case, spacing, etc.
	 * @param source The title of article to begin 
	 * @param destination The destination target article title
	 * @param maxHops Maximum # of hops (levels) to traverse
	 * @param msgLevel Display console output up to this many hops
	 * @param timeout How long to wait for the HTTP request response
	 */
	public WikiCorrelator(String source, String destination, int maxHops, 
			int msgLevel, int timeout) {
		_path = new Vector<String>();
		_visited = new HashMap<String, Integer>();
		_urlSource = formatSearchString(source);
		_results=0;
		_urlDest= formatSearchString(destination);
		_maxHops = maxHops;
		_msgLevel = msgLevel;
		_timeOut = timeout * 1000;
	}

	/** 
	 * Get the number of results
	 * @return Number of results found. Zero if WikiCorrelator has not been 
	 * started using {@link WikiCorrelator#find()}
	 */
	public int getNumResults() {
		return _results;
	}
	
	/**
	 * Execute the WikiCorrelator search. Uses 
	 * {@link WikiCorrelator#hasPathTo(Vector, String, int)} greedy DFS to find
	 * a correlation between two Wikipedia articles.
	 * @return A Vector list of the path taken. Will return an empty list if no
	 * valid path is found.
	 */
	public Vector<String> find() {
		hasPathTo(new Vector<String>(),_urlSource,0);
		if (_msgLevel>0) System.out.println("Determined traversing " + 
				getNumResults() + " child article nodes");
		return _path;
	}
	
	/** 
	 * Utility function to format the URL title to a more Wikipedia-like style.
	 * @param query Search query of article title
	 * @return String with the /wiki/ prefix and spaces replaced by underscores.
	 */
	public String formatSearchString(String query) {
		if  (query==null) return new String();
		query = "/wiki/" + query.replace(" ", "_");
		return query;
	}
	
	/**
	 * Main console entry point
	 * @param args src, dest, hops, msglvl, timeout
	 */
	public static void main(String[] args) {
		if (args.length!=5) {
			System.out.println("WikiCorrelator - by Chris Coykendall\n" + 
					"The Wikipedia Graph DFS Discovery Tool\n" + 
					"Usage: WikiCorrelator src dest hops msglvl timeout");
			return;
		}
		Vector<String> results = new WikiCorrelator(args[0], args[1], 
				Integer.parseInt(args[2]), Integer.parseInt(args[3]), 
				Integer.parseInt(args[4])).find();
		for (int i=0;i<results.size(); i++) {
			if (i!=results.size()-1) System.out.print(results.get(i) + " >> ");
			else System.out.print(results.get(i));
		}
		if (results.size()==0) 
			System.out.println("Unable to find correlation!");
	}
	
}
