import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CalculateHW2 {
	public static void main(String[] args) throws IOException {
		String crawlFolder = "./data/";
	    String fetchFile = "fetch_usatoday.csv";
	    String visitFile = "visit_usatoday.csv";
	    String urlsFile = "urls_usatoday.csv";
	    String reportFile = "CrawlReport_usatoday.txt";
	    boolean firstLine = true;
	    
	    // urlsFile
        BufferedReader in = new BufferedReader(new FileReader(crawlFolder + urlsFile));
        Set<String> urls = new HashSet<>();
        String line;
        int totalURLs = 0;
        int totalUniqueURLs = 0;
        int uniqueURLsWithin = 0;
        int uniqueURLsOutside = 0;
        while ((line = in.readLine()) != null) {
        	if(firstLine) {
        		firstLine = false;
        		continue;
        	}
        	totalURLs += 1;
            String[] temp = line.split(",");
            String url = temp[0].trim();
            if(urls.add(url)) {
            	totalUniqueURLs += 1;
            	String indicator = temp[1].trim();
            	if(indicator.equals("OK")) {
            		uniqueURLsWithin += 1;
            	}
            	else {
            		 uniqueURLsOutside += 1;
            	}
            }
        }
        in.close();
        
        // fetchFile
        in = new BufferedReader(new FileReader(crawlFolder + fetchFile));
        Map<String, Integer> stateCodeMap = new HashMap<>();
        int fetchAttempted = 0;
        int fetchSucceeded = 0;
        int fetchFailed = 0;
        firstLine = true;
        while ((line = in.readLine()) != null) {
        	if(firstLine) {
        		firstLine = false;
        		continue;
        	}
        	fetchAttempted += 1;
            String[] temp = line.split(",");
            String stateCode = temp[1].trim();
            if(stateCode.charAt(0) == '2') {
            	fetchSucceeded += 1;
            }
            else {
            	fetchFailed += 1;
            }
            stateCodeMap.put(stateCode, stateCodeMap.getOrDefault(stateCode, 0) + 1);
        }
        in.close();
        
        // visitFile
        in = new BufferedReader(new FileReader(crawlFolder + visitFile));
        Map<String, Integer> contentTypeMap = new HashMap<>();
        int[] sizes = new int[5];
        firstLine = true;
        while ((line = in.readLine()) != null) {
        	if(firstLine) {
        		firstLine = false;
        		continue;
        	}
            String[] temp = line.split(",");
            int size = Integer.valueOf(temp[1].trim());
            /*
            < 1KB:
			1KB ~ <10KB:
			10KB ~ <100KB:
			100KB ~ <1MB:
			>= 1MB:
             */
            if(size < 1) {
            	sizes[0] += 1;
            }
            else if(size >= 1 && size < 10) {
            	sizes[1] += 1;
            }
            else if(size >= 10 && size < 100) {
            	sizes[2] += 1;
            }
            else if(size >= 100 && size < 1024) {
            	sizes[3] += 1;
            }
            else {
            	sizes[4] += 1;
            }
            String contentType = temp[3].trim();
            contentTypeMap.put(contentType, contentTypeMap.getOrDefault(contentType, 0) + 1);
        }
        in.close();
        
		BufferedWriter bw = new BufferedWriter(new FileWriter(crawlFolder + reportFile, true));
		bw.write("Name: Su Guo\n");
		bw.write("USC ID: 4906010061\n");
		bw.write("News site crawled: usatoday.com\n");
		bw.write("\n");
		
		/* fetch_usatoday.csv
		Fetch Statistics 
		================
		# fetches attempted: near or <= 20000
		# fetches succeeded: status code == 2XX
		# fetches failed or aborted: 3XX 0r 4XX or 5XX
		 */
		bw.write("Fetch Statistics\n");
		bw.write("==============\n");
		bw.write("# fetches attempted: " + fetchAttempted + "\n");
		bw.write("# fetches succeeded: " + fetchSucceeded + "\n");
		bw.write("# fetches failed or aborted: " + fetchFailed + "\n");
		bw.write("\n");
		
		/* urls_usatoday.csv
		Outgoing URLs:
		==============
		Total URLs extracted: all url
		# unique URLs extracted: 去重以后的url
		# unique URLs within News Site:
		# unique URLs outside News Site:
		 */
		bw.write("Outgoing URLs:\n");
		bw.write("==============\n");
		bw.write("Total URLs extracted: " + totalURLs + "\n");
		bw.write("# unique URLs extracted: " + totalUniqueURLs + "\n");
		bw.write("# unique URLs within usatoday.com: " + uniqueURLsWithin + "\n");
		bw.write("# unique URLs outside usatoday.com: " + uniqueURLsOutside + "\n");
		bw.write("\n");
		
		/* fetch_usatoday.csv
		Status Codes: 
		=============
		200 OK:
		301 Moved Permanently: 
		401 Unauthorized:
		403 Forbidden:
		404 Not Found:
		 */
		bw.write("Status Codes:\n");
		bw.write("=============\n");
		for(String stateCode: stateCodeMap.keySet()) {
			bw.write(stateCode + ": " + stateCodeMap.get(stateCode) + "\n");
		}
		bw.write("\n");
		
		/* visit_usatoday.csv
		File Sizes:
		===========
		< 1KB:
		1KB ~ <10KB:
		10KB ~ <100KB:
		100KB ~ <1MB:
		>= 1MB:
		 */
		bw.write("File Sizes:\n");
		bw.write("===========\n");
		bw.write("< 1KB: " + sizes[0] + "\n");
		bw.write("1KB ~ <10KB: " + sizes[1] + "\n");
		bw.write("10KB ~ <100KB: " + sizes[2] + "\n");
		bw.write("100KB ~ <1MB: " + sizes[3] + "\n");
		bw.write(">= 1MB: " + sizes[4] + "\n");
		bw.write("\n");
		
		/* visit_usatoday.csv
		Content Types:
		==============
		text/html:
		image/gif:
		image/jpeg:
		image/png:
		application/pdf:
		 */
		bw.write("Content Types:\n");
		bw.write("==============\n");
		for(String contentType: contentTypeMap.keySet()) {
			bw.write(contentType + ": " + contentTypeMap.get(contentType) + "\n");
		}
		bw.write("\n");
		
        bw.close();		
	}
}
