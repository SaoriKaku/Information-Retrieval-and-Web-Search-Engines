
<?php
// make sure browsers see this page as utf-8 encoded HTML
header('Content-Type: text/html; charset=utf-8');

$limit = 10;
$query = isset($_REQUEST['q']) ? $_REQUEST['q'] : false;
$results = false;
$algorithmType = isset($_GET['algorithmType']) ? $_GET['algorithmType'] : false;

$luceneParameters = array('fl' => 'title,og_url,og_description,id');
$pageRankParameters = array('fl' => 'title,og_url,og_description,id','sort' => 'pageRankFile desc');

if($query) {
  // The Apache Solr Client library should be on the include path
  // which is usually most easily accomplished by placing in the
  // same directory as this script ( . or current directory is a default
  // php include path entry in the php.ini)
  require_once('Apache/Solr/Service.php');

  // create a new solr service instance - host, port, and webapp
  // path (all defaults in this example)
  $solr = new Apache_Solr_Service('localhost', 8983, '/solr/hw4solrcore');

  // if magic quotes is enabled then stripslashes will be needed
  if(get_magic_quotes_gpc() == 1) {
    $query = stripslashes($query);
  }

  // in production code you'll always want to use a try /catch for any
  // possible exceptions emitted  by searching (i.e. connection
  // problems or a query parsing error)
  try {
    if($algorithmType == "lucene") {
      $results = $solr->search($query, 0, $limit, $luceneParameters);
    } 
    else {
      $results = $solr->search($query, 0, $limit, $pageRankParameters);
    }
  } catch (Exception $e) {
    // in production you'd probably log or email this error to an admin
    // and then show a special message to the user but for this example
    // we're going to show the full exception
    die("<html><head><title>SEARCH EXCEPTION</title><body><pre>{$e->__toString()}</pre></body></html>");
  }
}
?>

<html>
    <head>
        <title>Su Guo hw4 PHP Solr Client</title>
        <style type="text/css">
            ol {
                font-size: 14px;
            }
            table {
                border: 0px solid black; 
                text-align: left; 
                font-size: 14px
            }
            .container {
                font-size: 14px;
                margin-top: 25px;
                margin-left: 25px;
            }
            .result {
                font-size: 14px;
                font-weight: 700;
                margin-left: 25px;
            }
            .title {
                font-weight: 400;
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
        <div class="container">
            <form accept-charset="utf-8" method="get">
                <label for="q">Search:</label>
                <input id="q" name="q" type="text" value="<?php echo htmlspecialchars($query, ENT_QUOTES, 'utf-8'); ?>"/>
                <input id="radio1" name="algorithmType" type="radio" value="lucene" <?php if($algorithmType != "pageRank") {echo "checked='checked'";} ?>/> 
                <label for="radio1">Lucene&nbsp;&nbsp;</label>
                <input id="radio2" name="algorithmType" type="radio" value="pageRank" <?php if($algorithmType == "pageRank") {echo "checked='checked'";} ?>/>
                <label for="radio2">PageRank&nbsp;&nbsp;</label>
                <input type="submit" value="submit"/>
             </form>
        </div>
            
<?php
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
                        <td><span>title</span><td>
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
                        <td><span>url</span><td>
                        <td>
                            <a target="_blank" href="<?php echo htmlspecialchars($url, ENT_NOQUOTES, 'utf-8');?>">
                                <span class="url">
                                    <?php echo htmlspecialchars($url, ENT_NOQUOTES, 'utf-8');?>
                                </span>
                            </a>
                        </td>
                    </tr>
                    <!-- description -->
                    <tr>
                        <td><span>description</span><td>
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
                        <td><span>id</span><td>
                        <td>
                            <span class="id">
                                <?php echo htmlspecialchars($id, ENT_NOQUOTES, 'utf-8');?>
                            </span>
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
