// requires some values (GSP variables, etc.) to be passed in from GSP calling page
// in global variable: SHOW_REC (JS Object)

$(document).ready(function() {
    // exit if SHOW_REC global var is not set
    if (typeof SHOW_REC == "undefined") {
        alert("Warning: The required global variable SHOW_REC is not defined");
        return false;
    }

    // FancyBox popups
    $("a#lsid").fancybox({
        'hideOnContentClick' : false,
        'titleShow' : false,
        'autoDimensions' : false,
        'width' : 600,
        'height' : 180
    });
    $("a.current").fancybox({
        'hideOnContentClick' : false,
        'titleShow' : false,
        'titlePosition' : 'inside',
        'autoDimensions' : true,
        'width' : 300
    });

    // Initialise BS tabs
    $('#overviewTabs a:first').tab('show');

    // Load images tab images
    loadImagesTab();

    // Load downloads stats
    if (SHOW_REC.loadLoggerStats) {
        loadDownloadStats(SHOW_REC.loggerServiceUrl, SHOW_REC.instanceUuid, SHOW_REC.instanceName, "1002");
    }

    // JS to populate $('#progress') was lost with commit https://github.com/AtlasOfLivingAustralia/collectory-plugin/commit/943609ea46254bedb963ded9ec316c3d2ec2904d
    // So hide element until fixed TODO fix this OR remove completely
    $('#progress').hide();
});

/**
 * Populate Images tab body with images via AJAX call to Biocache
 */
function loadImagesTab() {
    var wsBase = "/occurrences/search.json";
    var uiBase = "/occurrences/search";
    var imagesQueryUrl = "?facets=type_status&fq=multimedia%3AImage&pageSize=100&q=" + buildQueryString(SHOW_REC.instanceUuid);
    $.ajax({
        url: urlConcat(SHOW_REC.biocacheServicesUrl, wsBase + imagesQueryUrl),
        dataType: 'jsonp',
        timeout: 20000,
        complete: function(jqXHR, textStatus) {
            if (textStatus == 'timeout') {
                noBiocacheData();
            }
            if (textStatus == 'error') {
                noBiocacheData();
            }
        },
        success: function(data) {
            // check for errors
            if (data.length == 0 || data.totalRecords == undefined || data.totalRecords == 0) {
                //noBiocacheData();
            } else {
                if(data.totalRecords > 0){
                    $('#imagesTabEl').css({display:'block'});
                    var description = ""
                    if(data.facetResults.length>0 && data.facetResults[0].fieldResult !== undefined){
                        description = "Of these images there: ";
                        $.each(data.facetResults[0].fieldResult, function(idx, facet){
                            if(idx>0){
                                description += ', '
                            }
                            var queryUrl = biocacheWebappUrl + uiBase + imagesQueryUrl + '&fq=' + data.facetResults[0].fieldName + ':' + facet.label;
                            description += '<a href="' + queryUrl + '">' + (facet.count + ' ' + facet.label) + '</a>';
                        })
                    }
                    $('#imagesSpiel').html('<p><a href="' + SHOW_REC.biocacheWebappUrl + uiBase + imagesQueryUrl +'">' + data.totalRecords + ' images</a> have been made available from the ' + SHOW_REC.instanceName + '. <br/> ' + description + '</p>');
                    $.each(data.occurrences, function(idx, item){
                        var imageText = item.scientificName;
                        if(item.typeStatus !== undefined){
                            imageText = item.typeStatus + " - " + imageText;
                        }
                        $('#imagesList').append('<div class="imgCon"><a href="' + SHOW_REC.biocacheWebappUrl + '/occurrences/' + item.uuid + '"><img src="' + item.smallImageUrl + '"/><br/>'+ imageText + '</a></div>');
                    })
                }
            }
        }
    });
}
