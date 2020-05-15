# Information-Retrieval-and-Web-Search-Engines
Course Homework/Project for CSCI572 Search Engine

## Part 1: a simple sample to present this Search Engine based on Apache Solr
1. cd solr-7.7.2, start solr: bin/solr start, start apache: sudo apachectl start


## Part 2: technical points

1. Designed an interactive webpage displaying search results for a given query, identifying spelling mistakes and presenting autocomplete coming up in the dropdown under user’s query.
2. Implemented spelling mistakes using Norvig’s spelling corrector to calculate edit distance.
3. Created HTTP requests and parsed responses in PHP fetching suggestion and searching query from solr-php-client API.
4. Retrieved contents from Los Angeles Times with Apache Tika library based on two ranking algorithm, Lucene and PageRank. 
5. Extracting incoming and outgoing links with Java Jsoup library and computed page rank with Python Networks library.
