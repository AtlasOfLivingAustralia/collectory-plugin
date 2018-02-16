<r:script type="text/javascript">
  var CHARTS_CONFIG = {
      biocacheServicesUrl: "${grailsApplication.config.biocacheServicesUrl}",
      biocacheWebappUrl: "${grailsApplication.config.biocacheUiURL}",
      bieWebappUrl: "${grailsApplication.config.bieUiURL}",
      collectionsUrl: "${grailsApplication.config.grails.serverURL}"
  };
  var taxonomyTreeOptions = {
      /* base url of the collectory */
      collectionsUrl: CHARTS_CONFIG.collectionsUrl,
      /* base url of the biocache ws*/
      biocacheServicesUrl: CHARTS_CONFIG.biocacheServicesUrl,
      /* base url of the biocache webapp*/
      biocacheWebappUrl: CHARTS_CONFIG.biocacheWebappUrl,
      /* the id of the div to create the charts in - defaults is 'charts' */
      targetDivId: "tree",
      /* a uid or list of uids to chart - either this or query must be present */
      instanceUid: "${instance.uid}"
  }

  // records
  if (${!instance.hasProperty('resourceType') || instance.resourceType == 'records'}) {
      // summary biocache data
      var queryUrl = CHARTS_CONFIG.biocacheServicesUrl + "/occurrences/search.json?pageSize=0&q=${facet}:${instance.uid}";

      $.ajax({
        url: queryUrl,
        dataType: 'jsonp',
        timeout: 30000,
        complete: function(jqXHR, textStatus) {
            if (textStatus == 'timeout') {
                noData();
                alert('Sorry - the request was taking too long so it has been cancelled.');
            }
            if (textStatus == 'error') {
                noData();
                alert('Sorry - the records breakdowns are not available due to an error.');
            }
        },
        success: function(data) {
            // check for errors
            if (data.length == 0 || data.totalRecords == undefined || data.totalRecords == 0) {
                noData();
            } else {
                setNumbers(data.totalRecords);
                if(data.totalRecords > 0){
                    $('#dataAccessWrapper').css({display:'block'});
                    $('#totalRecordCountLink').html(data.totalRecords.toLocaleString() + " ${g.message(code: 'public.show.rt.des03')}");
                }
            }
        }
      });
  }
</r:script>
<r:script type="text/javascript">
    <charts:biocache
            biocacheServiceUrl="${grailsApplication.config.biocacheServicesUrl}"
            biocacheWebappUrl="${grailsApplication.config.biocacheUiURL}"
            q="${facet}:${instance.uid}"
            qc=""
            fq=""
            autoLoad="false"
    />
    var charts = ALA.BiocacheCharts('charts', chartConfig);
</r:script>
