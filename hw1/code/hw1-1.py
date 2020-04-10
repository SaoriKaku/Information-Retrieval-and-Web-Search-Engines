
import time
from bs4 import BeautifulSoup
from time import sleep
import requests
from random import randint
from html.parser import HTMLParser
from collections import OrderedDict
import json

USER_AGENT = {'User-Agent':'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36'}
class SearchEngine:
	@staticmethod
	def search1(query, sleep):
		if sleep: # Prevents loading too many pages too soon
			time.sleep(randint(10, 100))
		temp_url = '+'.join(query.split()) #for adding + between words for the query
		url = 'http://www.ask.com/web?q=' + temp_url
		soup = BeautifulSoup(requests.get(url, headers=USER_AGENT).text, "html.parser")
		new_results = SearchEngine.scrape_search_result1(soup)
		return new_results

	@staticmethod
	def search2(query, results, sleep):
		if sleep: # Prevents loading too many pages too soon
			time.sleep(randint(10, 100))
		temp_url = '+'.join(query.split()) #for adding + between words for the query
		url = 'http://www.ask.com/web?q=' + temp_url + '&page=2'
		soup = BeautifulSoup(requests.get(url, headers=USER_AGENT).text, "html.parser")
		new_results = SearchEngine.scrape_search_result2(soup, results)
		return new_results

	@staticmethod
	def scrape_search_result1(soup):
		# Ask: ["div", attrs = {"class" : "PartialSearchResults-item-title"}]
		raw_results1 = soup.find_all("div", attrs = {"class" : "PartialSearchResults-item-title"})
		results = []
		#implement a check to get only 10 results and also check that URLs must not be duplicated
		for result in raw_results1:
			link = result.find('a').get('href')
			if(link not in results):
				results.append(link)
				if(len(results) == 10):
					break
		return results

	@staticmethod
	def scrape_search_result2(soup, results):
		# Ask: ["div", attrs = {"class" : "PartialSearchResults-item-title"}]
		raw_results2 = soup.find_all("div", attrs = {"class" : "PartialSearchResults-item-title"})
		#implement a check to get only 10 results and also check that URLs must not be duplicated
		for result in raw_results2:
			link = result.find('a').get('href')
			if(link not in results):
				results.append(link)
				if(len(results) == 10):
					break
		return results


print("reading input...")
inputFile = open("./input.txt", "r")
queries = [];
for line in inputFile:
	query = line[0:len(line)-3]
	queries.append(query)
inputFile.close()
# just for test queries
print(queries)

data = OrderedDict()
print("start processing...")
counter = 0
for query in queries:
	answer = SearchEngine.search1(query, False)
	if(len(answer) < 10):
		#print("test")
		answer = SearchEngine.search2(query, answer, True)
	data[query] = answer
	counter = counter + 1
	#print "%s is %s whose age is %d" %(name,job,age)
	#print "num=%d" % num
	print("Question %d completed" % counter)
print(data)

print("writing output...")
outputFile = open("./hw1.json", "w")
json_data = json.dumps(data, indent=4)
outputFile.write(json_data)
outputFile.close()
