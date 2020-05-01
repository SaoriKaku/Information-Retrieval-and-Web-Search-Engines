package edgeList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class EdgeList {

	public static void main(String[] args) throws Exception {
		String csvPath = "/Users/suguo/Desktop/CSCI572/hw4/data/URLtoHTML_latimes_news.csv";
		// map<key: url; value: id>
		Map<String, String> urlIdMap = new HashMap<>();
		FileReader fileReader = new FileReader(csvPath);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line = null;
		int count1 = 1;
		while((line = bufferedReader.readLine()) != null) {
			String[] idUrl = line.split(",");
			count1++;
			urlIdMap.put(idUrl[1], idUrl[0]);
		}
		bufferedReader.close();
		System.out.println("idUrlMap.size() = " + count1);
		
		String outputName = "EdgeList.txt";
		String outputPath = "/Users/suguo/Desktop/CSCI572/hw4/data/" + outputName;
		FileWriter fileWriter = new FileWriter(outputPath);
		PrintWriter printWriter = new PrintWriter(fileWriter);
		String directoryPath = "/Users/suguo/Desktop/CSCI572/hw4/crawl_data/";
		File directory = new File(directoryPath);
		File[] htmlFiles = directory.listFiles();
		int count2 = 0;
		for(File htmlFile: htmlFiles) {
			File htmlFilePath = new File(directoryPath + htmlFile.getName());
			Document document = Jsoup.parse(htmlFilePath, "UTF-8", "https://www.latimes.com/");
			Elements links = document.select("a[href]");
			for(Element link: links) {
				String outUrl = link.attr("abs:href").trim();
				if(urlIdMap.containsKey(outUrl)) {
					// edge = html file name + " " + id
					String edge = htmlFile.getName() + " " + urlIdMap.get(outUrl);
					printWriter.println(edge);
					printWriter.flush();
					count2++;
				}
			}
		}
		printWriter.close();
		System.out.println("edgeList.size() = " + count2);
	}
}
