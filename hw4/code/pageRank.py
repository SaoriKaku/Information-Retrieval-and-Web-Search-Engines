#!/usr/bin/python
import networkx as nx

graph = nx.read_edgelist("/Users/suguo/Desktop/CSCI572/hw4/data/edgeList.txt", create_using=nx.DiGraph())

pr = nx.pagerank(graph, alpha=0.85, personalization=None, max_iter=30, tol=1e-6, nstart=None, weight='weight', dangling=None)

outputFilePath = "/Users/suguo/Desktop/CSCI572/hw4/data/external_pageRankFile.txt"
file = open(outputFilePath, "w")

idPath = "/Users/suguo/Desktop/CSCI572/hw4/crawl_data/"
for id in pr:
	file.write(idPath + id + "=" + str(pr[id]) + "\n")

file.close()
