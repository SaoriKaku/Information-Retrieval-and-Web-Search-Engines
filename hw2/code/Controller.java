import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Controller {
    public static void main(String[] args) throws Exception {
    	
        String crawlStorageFolder = "./data/";
        String fetchFile = "fetch_usatoday.csv";
        String visitFile = "visit_usatoday.csv";
        String urlsFile = "urls_usatoday.csv";
        
        // configure crawler
        int numberOfCrawlers = 7;
        int maxPagesToFetch = 20000;
        int maxDepthOfCrawling = 16;
        int politeDelay = 200;

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(crawlStorageFolder);
        config.setMaxPagesToFetch(maxPagesToFetch);
        config.setMaxDepthOfCrawling(maxDepthOfCrawling);
        //config.setPolitenessDelay(politeDelay);
        config.setIncludeHttpsPages(true);
        config.setFollowRedirects(true);
        config.setIncludeBinaryContentInCrawling(true);
        config.setResumableCrawling(true);
        
        /*
         * Instantiate the controller for this crawl.
		*/
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
		
		// create file header
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(crawlStorageFolder + fetchFile));
            bw.write("URL, Status Code\n");
            bw.close();

            bw = new BufferedWriter(new FileWriter(crawlStorageFolder + visitFile));
            bw.write("URL Downloaded, Size(KB), # of Outlinks Found, Content Type\n");
            bw.close();

            bw = new BufferedWriter(new FileWriter(crawlStorageFolder + urlsFile));
            bw.write("URL Encountered, Indicator\n");
            bw.close();
        }
		catch (IOException e) {
            e.printStackTrace();
        }
		
		/*
		* For each crawl, you need to add some seed urls. These are the first
		* URLs that are fetched and then the crawler starts following links
		* which are found in these pages
		*/
		controller.addSeed("http://www.usatoday.com/");
		/*
		 * Start the crawl. This is a blocking operation, meaning that your code
		 * will reach the line after this only when crawling is finished.
		 */
		controller.start(MyCrawler.class, numberOfCrawlers);
    }
}