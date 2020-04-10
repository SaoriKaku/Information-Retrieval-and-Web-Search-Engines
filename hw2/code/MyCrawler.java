import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;

public class MyCrawler extends WebCrawler{
	//private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg" + "|png|mp3|mp3|zip|gz))$");
	//private final static Pattern MATCH = Pattern.compile(".*(\\.(html|doc|docx|pdf|gif|jpg|jpe?g|png|bmp|tiff?))$");
    private final static Pattern FILTERS = Pattern.compile(".*(css|feed|rss|svg|js|mp3|zip|gz|vcf|xml).*");
    String crawlStorageFolder = "./data/";
    String fetchFile = "fetch_usatoday.csv";
    String visitFile = "visit_usatoday.csv";
    String urlsFile = "urls_usatoday.csv";
    private Set<String> urlSet = new HashSet<>();
    
	/**
	* This method receives two parameters. The first parameter is the page
	* in which we have discovered this new url and the second parameter is
	* the new url. You should implement this function to specify whether
	* the given url should be crawled or not (based on your crawling logic).
	* In this example, we are instructing the crawler to ignore urls that
	* have css, js, git, ... extensions and to only accept urls that start
	* with "http://www.viterbi.usc.edu/". In this case, we didn't need the
	* referringPage parameter to make the decision.
	*/
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		// check 1 domain, 2 no duplicate, 3 content type
	    String href = url.getURL();
	    boolean startWith = false;
	    try{
            synchronized(this) {
                BufferedWriter bw = new BufferedWriter(new FileWriter(crawlStorageFolder + urlsFile, true));
                if(href.startsWith("http://www.usatoday.com") || href.startsWith("https://www.usatoday.com")) {
                	startWith = true;
                	bw.write(url.getURL().replace(",", "_") + ",OK\n");
                }
                else {
                	bw.write(url.getURL().replace(",", "_")+ ",N_OK\n");
                }
                bw.close();
            }
        }
	    catch(IOException e) {
            e.printStackTrace();
        }
	    // check domain: !FILTERS.matcher(href).matches() && href.startsWith("http://www.viterbi.usc.edu/");
	    if(!startWith) {
	    	return false;
	    }
	    // check content type
	    if(FILTERS.matcher(href).matches()) {
	    	return false;
	    }
	    // check duplicate
	    if(href.charAt(4) == ':') {
	    	href = href.substring(23);
	    }
	    else {
	    	href = href.substring(24);
	    }
	    return urlSet.add(href);
	}
	   
	/**
     * This function is called once the header of a page is fetched. It can be
     * overridden by sub-classes to perform custom logic for different status
     * codes. For example, 404 pages can be logged, etc.
     *
     * @param webUrl WebUrl containing the statusCode
     * @param statusCode Html Status Code number
     * @param statusDescription Html Status COde description
     */
    @Override
    protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
        try{
            synchronized(this) {
                BufferedWriter bw = new BufferedWriter(new FileWriter(crawlStorageFolder + fetchFile, true));
                bw.write(webUrl.getURL().replace(",", "_") + ", " + statusCode + "\n");
                bw.close();
            }
        } 
        catch(IOException e) {
            e.printStackTrace();
        }
    }

	/**
	 * This function is called when a page is fetched and ready
	 * to be processed by your program.
	 */
	@Override
	public void visit(Page page) {
		String url = page.getWebURL().getURL();
		int size = page.getContentData().length / 1024;
		int numOfOutLink = page.getParseData().getOutgoingUrls().size();
		String contentType = page.getContentType().split(";")[0];
		String[] mimeType = contentType.split("/");
		if(mimeType[1].equals("html") || mimeType[0].equals("image") || mimeType[1].equals("msword") || mimeType[1].equals("vnd.openxmlformats-officedocument.wordprocessingml.document")) {
        	 try{
                 synchronized (this) {
                     BufferedWriter bw = new BufferedWriter(new FileWriter(crawlStorageFolder + visitFile, true));
                     bw.write(url.replace(",", "_") + ", " + size + ", " + numOfOutLink + ", " + contentType +"\n");
                     bw.close();
                 }
             }
        	 catch(IOException e) {
                 e.printStackTrace();
             }
        }
	}
}
