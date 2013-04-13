package edu.odu.cs.ccoykend.wikicorrelate;

import java.util.Vector;

import android.test.AndroidTestCase;

/**
 * WikiCorrelator unit test implementation
 * NOTE: Some tests rely on article-specifics which may require changing due to 
 * the living nature of the Wikipedia website.
 * @author Chris Coykendall
 *
 */
public class TestWikiCorrelator extends AndroidTestCase {

	public void testGetNumResultsReturnsZeroForNoRun() {
		WikiCorrelator wc = new WikiCorrelator(null, null, 0, 0, 0);
		assertEquals("Should have returned no results for no run", 0, 
				wc.getNumResults());
	}

	public void testFormatSearchStringAddsUnderscores() {
		WikiCorrelator wc = new WikiCorrelator(null, null, 0, 0, 0);
		String s = wc.formatSearchString("test hello ");
		assertEquals("Didn't add underscores for spaces in input",
				"/wiki/test_hello_", s);
	}

	public void testFormatSearchStringAddsWiki() {
		WikiCorrelator wc = new WikiCorrelator(null, null, 0, 0, 0);
		String s = wc.formatSearchString("a");
		assertEquals("Didn't add /wiki/ prefix to input", "/wiki/a", s);
	}
	
	public void testFindReturnsForDifferentCaseDestination() {
		WikiCorrelator wc = new WikiCorrelator("Seattle", "StArbUcks", 1, 0, 20);
		Vector<String> v = wc.find();
		assertEquals("Results should return for different case destination", 
				2, v.size());
	}
	
	public void testFindReturnsEmptyListForNoCorrelation() {
		WikiCorrelator wc = new WikiCorrelator("We_Were_Gentlemen", 
				"huivwbawr", 1, 0, 20);
		Vector<String> v = wc.find();
		assertEquals("Results should be empty for no correlations found", 
				0, v.size());
	}
	
	public void testFindReturnsEmptyListForNoHops() {
		WikiCorrelator wc = new WikiCorrelator("", "", 0, 0, 20);
		Vector<String> v = wc.find();
		assertEquals("Results should be empty for no hops", 0, v.size());
	}
	
	public void testFindReturnsTwoForDirectResult() {
		WikiCorrelator wc = new WikiCorrelator("Seattle", "Starbucks", 0, 0, 20);
		Vector<String> v = wc.find();
		assertEquals("Results should be 2 for direct result", 2, v.size());
	}
	
	public void testFindReturnsThreeForOneHopResult() {
		WikiCorrelator wc = new WikiCorrelator("We_Were_Gentlemen", 
				"Foothills", 1, 0, 20);
		Vector<String> v = wc.find();
		assertEquals("Results should be 3 for 1 hop result", 3, v.size());
	}
	
	public void testFindReturnsCorrectResults() {
		WikiCorrelator wc = new WikiCorrelator("We_Were_Gentlemen", 
				"Foothills", 1, 0, 20);
		Vector<String> v = wc.find();
		Vector<String> known = new Vector<String>();
		known.add("/wiki/We_Were_Gentlemen");
		known.add("/wiki/Virginia");
		known.add("/wiki/Foothills");
		assertEquals("Results do not match known result set", known, v);
	}

}
