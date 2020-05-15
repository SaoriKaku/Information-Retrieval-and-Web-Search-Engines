
<?php
ini_set('memory_limit','-1');
// include 'SpellCorrector.php';
// make sure browsers see this page as utf-8 encoded HTML
header('Content-Type: text/html; charset=utf-8');
include 'SpellCorrector.php';

$limit = 10;
$query = isset($_REQUEST['q']) ? $_REQUEST['q'] : false;
$results = false;
$algorithmType = isset($_GET['algorithmType']) ? $_GET['algorithmType'] : false;
$luceneParameters = array('fl' => 'title,og_url,og_description,id');
$pageRankParameters = array('fl' => 'title,og_url,og_description,id','sort' => 'pageRankFile desc');
$isCorrectWords = isset($_REQUEST['correct']) ? $_REQUEST['correct'] : false;

// variable: spell correct 
$showCorrectMessage = false; // $div
$correctWordsPlus = ""; // correct
$correctWordsSpace = ""; // correct1
$correctMessage = ""; // $output

if($query) {
    // The Apache Solr Client library should be on the include path which is usually most easily accomplished by placing in the same directory as this script ( . or current directory is a default php include path entry in the php.ini)
    require_once('Apache/Solr/Service.php');
    // create a new solr service instance - host, port, and webapp path (all defaults in this example)
    $solr = new Apache_Solr_Service('localhost', 8983, '/solr/hw4solrcore');
    // if magic quotes is enabled then stripslashes will be needed
    if(get_magic_quotes_gpc() == 1) {
        $query = stripslashes($query);
    }
    // in production code you'll always want to use a try/catch for any possible exceptions emitted  by searching (i.e. connection problems or a query parsing error)
    try {
        if($algorithmType == "lucene") {
            $results = $solr->search($query, 0, $limit, $luceneParameters);
        } 
        else {
            $results = $solr->search($query, 0, $limit, $pageRankParameters);
        }
        // spell correct
        if($isCorrectWords == false) {
        	$words = explode(" ", $query);
	        for($i = 0; $i < sizeOf($words); $i++) {
	        	$temp = SpellCorrector::correct($words[$i]);
	           	$correctWordsSpace = $correctWordsSpace." ".trim($temp);
	        }
	        $correctWordsSpace = trim($correctWordsSpace);
	        $correctWordsPlus = str_replace(" ", "+", $correctWordsSpace);
	        // show correct message
	        if(strtolower($query) != strtolower($correctWordsSpace)) {
	            $showCorrectMessage = true;
	            $correctMessage = "<div class='message'>Did you mean: <a href='http://localhost/hw5/index.php?q=".$correctWordsPlus."&correct=true'>".$correctWordsSpace."</a></div></br>";
	        }
        }
    } 
    catch (Exception $e) {
        // in production you'd probably log or email this error to an admin and then show a special message to the user but for this example we're going to show the full exception
        die("<html><head><title>SEARCH EXCEPTION</title><body><pre>{$e->__toString()}</pre></body></html>");
    }
}
?>

<html>
    <head>
        <title>Enhanced PHP Solr Client</title>
        <link rel="stylesheet" href="http://code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
        <script src="http://code.jquery.com/jquery-1.10.2.js"></script>
        <script src="http://code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
        <style type="text/css">
        	body {
        		font-family: Arial;
        	}
            ol {
                /*font-size: 14px;*/
                list-style-type: none;
            }
            table {
                border: 0px solid black; 
                text-align: left; 
                font-size: 14px
            }
            h3 {
            	margin-top: 30px;
            	text-align: center;
            }
            #q {
            	width: 200px;
            	height: 30px;
            	font-size: 16px;
            	margin-right: 10px;
            	text-indent: 5px;
            }
            .message {
            	display: inline-block;
            	width: 250px;
            	margin-left: 45px;
            	margin-bottom: 10px;
                font-size: 14px;
            }
            .message a {
            	color: red;
            	font-weight: 700;
            	text-decoration: none;
            }
            .container {
                font-size: 16px;
                margin: 20px auto 0px;
                width: 600px;
            }
            .search {
            	outline: none;
            	background: none;
            	height: 30px;
            	width: 100px;
            	margin-left: 10px;
            	border-radius: 5px;
            	font-size: 16px;
            }
            .result {
            	display: inline-block;
            	width: 250px;
                margin-left: 45px;
                font-size: 14px;
                font-weight: 700;
            }
            .title {
                font-weight: 700;
            }
            .url {
            }
            .description {
            }
            .id {
                color:grey;
            }
        </style>
    </head>

    <body>
    	<h3>Solr Search Engine</h3>
        <div class="container">
            <form accept-charset="utf-8" method="get">
                <label for="q">Search:&nbsp;</label>
                <!-- autocomplete input -->
                <input id="q" name="q" type="text" value="<?php echo htmlspecialchars($query, ENT_QUOTES, 'utf-8'); ?>" list="searchResults"/>
                <!-- autocomplete list -->
                <datalist id="searchResults"></datalist>
                <input id="radio1" name="algorithmType" type="radio" value="lucene" <?php if($algorithmType != "pageRank") {echo "checked='checked'";} ?>/> 
                <label for="radio1">Lucene&nbsp;</label>
                <input id="radio2" name="algorithmType" type="radio" value="pageRank" <?php if($algorithmType == "pageRank") {echo "checked='checked'";} ?>/>
                <label for="radio2">PageRank&nbsp;</label>
                <input class="search" type="submit" value="Search"/>
             </form>
        </div>

    <script>
        $(function() {
            var URL_PREFIX = "http://localhost:8983/solr/hw4solrcore/suggest?q=";
            var URL_SUFFIX = "&wt=json&indent=true";
            $("#q").autocomplete({
                source: function(request, response) {
                    var checkWord = ""; // correct
                    var prevWord = ""; // before
                    var query = $("#q").val().toLowerCase();
                    var lastSpaceIndex = query.lastIndexOf(' ');
                    if(lastSpaceIndex >= 0 && lastSpaceIndex < query.length - 1) {
                        checkWord = query.substr(lastSpaceIndex + 1);
                        prevWord = query.substr(0, lastSpaceIndex);
                    }
                    else {
                        checkWord = query; 
                    }
                    $.ajax({
                        url: URL_PREFIX + checkWord + URL_SUFFIX,
                        success: function(data) {
                            var strData = JSON.stringify(data.suggest.suggest);
                            var jsonData = JSON.parse(strData);
                            var result = jsonData[checkWord].suggestions;
                            var terms = [];
                            for(var i = 0; i < result.length; i++) {
                                var term = result[i].term;
                                if(term.indexOf('.') >= 0 || term.indexOf('_') >= 0 || term.indexOf(':') >= 0 || term == checkWord.trim() || terms.indexOf(term) >= 0) {
              						continue;
            					}
            					var candidate = prevWord + " " + term;
                                terms.push(candidate.trim());
                                if(terms.length == 5) {
                                    break;
                                }
                            }
                            response(terms);
                        },
                        dataType: 'jsonp',
                        jsonp: 'json.wrf'
                    }); // ajax
                }, // source function
                minLength: 1
            }); // autocomplete
        }); // function
    </script>
            
<?php
// display correct message
if($showCorrectMessage) {
    echo $correctMessage;
}
// display results
if($results) {
    $total = (int)$results->response->numFound;
    $start = min(1, $total);
    $end = min($limit, $total);
    $urlArray = array();

    function getUrlArrayFromCsvFile() {
        $resultArray = array();
        set_time_limit(1000);
        if (($handle = fopen("URLtoHTML_latimes_news.csv", "r")) !== false) {
            while (($data = fgetcsv($handle, 1000, ",")) !== FALSE) {
                $fileId = $data[0];
                $fileUrl = $data[1];
                $fileId = "/Users/suguo/Desktop/CSCI572/hw4/crawl_data/".$fileId;
                $resultArray[$fileId] = $fileUrl;
            }
        }
        fclose($handle);
        return $resultArray;
    }
?>
    
        <div class="result"><span>Results <?php echo $start; ?> - <?php echo $end;?> of <?php echo $total; ?>:</span></div>
        <ol>

<?php
// iterate result documents
foreach ($results->response->docs as $doc) {
    $id = $doc->id;
    if(isset($doc->og_url)) {
        $url = $doc->og_url;
    } 
    else {
        if(sizeof($urlArray) == 0) {
            $urlArray = getUrlArrayFromCsvFile();
            $url = $urlArray[$id];
        } 
        else {
            $url = $urlArray[$id];
        }
    }
?>
            <li>
                <table>
                    <!-- title -->
                    <tr>
                        <td><span class="title">Title</span><td>
                        <td>
                            <span class="title">
                            <?php
                            if(isset($doc->title)) {
                                echo htmlspecialchars($doc->title, ENT_NOQUOTES, 'utf-8'); 
                            } 
                            else {
                                echo "NA";
                            }
                            ?>
                          </span>
                        </td>
                    </tr>
                    <!-- url -->
                    <tr>
                        <td><span class="title">Link</span><td>
                        <td>
                            <a target="_blank" href="<?php echo htmlspecialchars($url, ENT_NOQUOTES, 'utf-8');?>">
                                <span class="url"><?php echo htmlspecialchars($url, ENT_NOQUOTES, 'utf-8');?></span>
                            </a>
                        </td>
                    </tr>
                    <!-- description -->
                    <tr>
                        <td><span class="title">Description</span><td>
                        <td>
                            <span class="description">
                            <?php
                            if(isset($doc->og_description)) {
                                echo htmlspecialchars($doc->og_description, ENT_NOQUOTES, 'utf-8');
                            }
                            else {
                                echo "NA";
                            }
                            ?>
                            </span>
                        </td>
                    </tr>
                    <!-- id -->
                    <tr>
                        <td><span class="title">ID</span><td>
                        <td>
                            <span class="id"><?php echo htmlspecialchars($id, ENT_NOQUOTES, 'utf-8');?></span>
                        </td>
                    </tr>
                </table>
            </li>
<?php
}
?>
        </ol>
<?php
}
?>
    </body>
</html>
