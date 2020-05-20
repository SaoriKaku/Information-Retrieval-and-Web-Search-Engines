# Information-Retrieval-and-Web-Search-Engines
Course Homework/Project for CSCI572 Search Engine

## Part 1: a simple presentation for Search Engine based on Apache Solr
1. Open the server, cd solr-7.7.2, start solr: bin/solr start, start apache: sudo apachectl start.
![image](https://github.com/SaoriKaku/Information-Retrieval-and-Web-Search-Engines/blob/master/images/Enhanced%20PhP%20Solr%20Client%20-%201.png)

2. Search the word "computer" one character by one character, you will see the autocomplete suggestion in the dropdown list.
![image](https://github.com/SaoriKaku/Information-Retrieval-and-Web-Search-Engines/blob/master/images/Enhanced%20PhP%20Solr%20Client%20-%202.png)

3. Autocomplete suggestions also work when you can search two words "computer science".
![image](https://github.com/SaoriKaku/Information-Retrieval-and-Web-Search-Engines/blob/master/images/Enhanced%20PhP%20Solr%20Client%20-%203.png)

4. Search word "computer science" with Lucene algorithm.
![image](https://github.com/SaoriKaku/Information-Retrieval-and-Web-Search-Engines/blob/master/images/Enhanced%20PHP%20Solr%20Client%20-%204.png)

5. Search word "computer science" with PageRank algorithm.
![image](https://github.com/SaoriKaku/Information-Retrieval-and-Web-Search-Engines/blob/master/images/Enhanced%20PHP%20Solr%20Client%20-%205.png)

6. Spellking corrector will show "did you mean" and a correct word when you mispell a word. 
![image](https://github.com/SaoriKaku/Information-Retrieval-and-Web-Search-Engines/blob/master/images/Enhanced%20PhP%20Solr%20Client%20-%206.png)

## Part 2: technical points

1. Designed an interactive webpage displaying search results for a given query, identifying spelling mistakes and presenting autocomplete coming up in the dropdown under user’s query.
2. Implemented spelling mistakes using Norvig’s spelling corrector to calculate edit distance.
3. Created HTTP requests and parsed responses in PHP fetching suggestion and searching query from solr-php-client API.
4. Retrieved contents from Los Angeles Times with Apache Tika library based on two ranking algorithm, Lucene and PageRank. 
5. Extracting incoming and outgoing links with Java Jsoup library and computed page rank with Python Networks library.

**Thank you for reading!**
