
import json

class HandleData:
	@staticmethod
	def trimLink(link):
		'''
		1. https://www.
		2. https://
		3. http://www.
		4. http://
		5. /
		'''
		# https://
		if(link.startswith("https")):
			link = link[8:len(link)]
		else: # http://
			link = link[7:len(link)]
		# www.
		if(link.startswith("www.")):
			link = link[4:len(link)]
		# /
		if(link.endswith("/")):
			link = link[0:len(link)-1]
		return link

'''
print(HandleData.trimLink("https://www.en.wikipedia.org/wiki/Spinning_mule/"))
print(HandleData.trimLink("https://en.wikipedia.org/wiki/Spinning_mule"))
print(HandleData.trimLink("http://www.en.wikipedia.org/wiki/Spinning_mule/"))
print(HandleData.trimLink("http://en.wikipedia.org/wiki/Spinning_mule"))
'''
print("reading input...")

# Reading data from file google.json
with open('google.json', 'r') as googleFile:
    googleObj = json.load(googleFile)


# Reading data from file hw1.json
with open('hw1.json', 'r') as askFile:
    askObj = json.load(askFile)


# Reading data from file input.txt
inputFile = open("./input.txt", "r")
queries = [];
for line in inputFile:
	query = line[0:len(line)-3]
	queries.append(query)
inputFile.close()
# just for test queries
# print("queries: \n", queries)


# write output
output = "Queries, Number of Overlapping Results, Percent Overlap, Spearman Coefficient\n"
nSum = 0
overPerSum = 0
spearCoeSum = 0

# traverse queries
for i in range(0,100):
	query = queries[i]
	n = 0
	d2Sum = 0
	
	# get google value for key = query
	googleRes = googleObj[query]
	#print("google result: \n", googleRes)

	# put google result in a dict
	googleDict = {}
	for j in range(0, len(googleRes)): 
		googleDict[HandleData.trimLink(googleRes[j])] = j
	#print("google dict: \n", googleDict)

	# get ask value for key = query
	askRes = askObj[query]
	#print("ask result: \n", askRes)
	rankInGoogle = -1
	rankInAsk = -2
	for k in range(0, len(askRes)):
		# dict.get(key, default=None)返回指定键的值，如果值不在字典中返回default值
		link = HandleData.trimLink(askRes[k])
		index = googleDict.get(link, -1)
		#print("index = ", index)
		if(index != -1):
			n += 1
			d2Sum += (k - index)**2
			rankInGoogle = index
			rankInAsk = k

	nSum += n
	overPer = n * 100 / 10
	overPerSum += overPer
	# print("overPer = ", overPer)
	if(n == 0):
		spearCoe = 0
	elif(n == 1):
		if(rankInGoogle == rankInAsk):
			spearCoe = 1
		else:
			spearCoe = 0
	else:
		spearCoe = 1 - ((6 * d2Sum) / (n * (n**2 - 1)))
	spearCoeSum += spearCoe
	# print("spearCoe = ", spearCoe)
	output += "Query " + str(i + 1) + ", " + str(n) + ", " + str(overPer) + ", " + str(spearCoe) + "\n"

nSum /= 100
overPerSum /= 100
spearCoeSum /= 100
output += "Averages, " + str(nSum) + ", " + str(overPerSum) + ", " + str(spearCoeSum)
print(output)

print("writing output...")
outputFile = open("./hw1.csv", "w")
outputFile.write(output)
outputFile.close()
