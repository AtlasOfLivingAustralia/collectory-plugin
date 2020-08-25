jQuery.i18n.properties({
    name: 'messages',
    path: COLLECTORY_CONF.contextPath + '/messages/i18n/',
    mode: 'map',
    language: COLLECTORY_CONF.locale // default is to use browser specified locale
});

function initTaxonTree(treeOptions) {
    var query = treeOptions.query ? treeOptions.query : buildQueryString(treeOptions.instanceUid);

    var targetDivId = treeOptions.targetDivId ? treeOptions.targetDivId : 'tree';
    var $container = $('#' + targetDivId);
    var title = treeOptions.title || jQuery.i18n.prop('charts2.js.explorerecords');
    if (treeOptions.title !== "") {
        $container.append($('<h4>' + title + '</h4>'));
    }
    var $treeContainer = $('<div id="treeContainer"></div>').appendTo($container);
    $treeContainer.resizable({
        maxHeight: 900,
        minHeight: 100,
        maxWidth: 900,
        minWidth: 500
    });
    var $tree = $('<div id="taxaTree"></div>').appendTo($treeContainer);
    $tree
        .bind("after_open.jstree", function(event, data) {
            var children = $.jstree._reference(data.rslt.obj)._get_children(data.rslt.obj);
            // automatically open if only one child node
            if (children.length == 1) {
                $tree.jstree("open_node",children[0]);
            }
            // adjust container size
            var fullHeight = $tree[0].scrollHeight;
            if (fullHeight > $tree.height()) {
                fullHeight = Math.min(fullHeight, 700);
                $treeContainer.animate({height:fullHeight});
            }
        })
        .bind("select_node.jstree", function (event, data) {
            // click will show the context menu
            $tree.jstree("show_contextmenu", data.rslt.obj);
        })
        .bind("loaded.jstree", function (event, data) {
            // get rid of the anchor click handler because it hides the context menu (which we are 'binding' to click)
            //$tree.undelegate("a", "click.jstree");
            $tree.jstree("open_node","#top");
        })
        .jstree({
            json_data: {
                data: {"data":"Kingdoms", "state":"closed", "attr":{"rank":"kingdoms", "id":"top"}},
                ajax: {
                    url: function(node) {
                        var rank = $(node).attr("rank");
                        var u = urlConcat(CHARTS_CONFIG.biocacheServicesUrl, "/breakdown.json?q=") + query + "&rank=";
                        if (rank == 'kingdoms') {
                            u += 'kingdom';  // starting node
                        }
                        else {
                            u += rank + "&name=" + $(node).attr('id');
                        }
                        return u;
                    },
                    dataType: 'jsonp',
                    success: function(data) {
                        var nodes = [];
                        var rank = data.rank;
                        $.each(data.taxa, function(i, obj) {
                            var label = obj.label + " - " + obj.count;
                            if (rank == 'species') {
                                nodes.push({"data":label, "attr":{"rank":rank, "id":obj.label}});
                            }
                            else {
                                nodes.push({"data":label, "state":"closed", "attr":{"rank":rank, "id":obj.label}});
                            }
                        });
                        return nodes;
                    },
                    error: function(xhr, text_status) {
                        //alert(text_status);
                    }
                }
            },
            core: { animation: 200, open_parents: true },
            themes:{
                theme: treeOptions.theme || 'classic',
                icons: treeOptions.icons || false,
                url: COLLECTORY_CONF.contextPath + "/js/themes/" + (treeOptions.theme || 'classic') + "/style.css"
            },
            checkbox: {override_ui:true},
            contextmenu: {select_node: false, show_at_node: false, items: {
                records: {label: jQuery.i18n.prop('charts2.js.showrecords'), action: function(obj) {showRecords(obj, query);}},
                bie: {label: jQuery.i18n.prop('charts2.js.showinformation'), action: function(obj) {showBie(obj);}},
                create: false,
                rename: false,
                remove: false,
                ccp: false }
            },
            plugins: ['json_data','themes','ui','contextmenu']
        });
}

/*------------------------- UTILITIES ------------------------------*/
/************************************************************\
 * build records query handling multiple uids
 * uidSet can be a comma-separated string or an array
 \************************************************************/
function buildQueryString(uidSet) {
    var uids = (typeof uidSet == "string") ? uidSet.split(',') : uidSet;
    var str = "";
    $.each(uids, function(index, value) {
        str += solrFieldNameForUid(value) + ":" + value + " OR ";
    });
    return str.substring(0, str.length - 4);
}
/************************************************************\
 * returns the appropriate facet name for the uid - to build
 * biocache occurrence searches
 \************************************************************/
function solrFieldNameForUid(uid) {
    switch(uid.substring(0,2)) {
        case 'co': return "collection_uid";
        case 'in': return "institution_uid";
        case 'dp': return "data_provider_uid";
        case 'dr': return "data_resource_uid";
        case 'dh': return "data_hub_uid";
        default: return "";
    }
}

/************************************************************\
 * Concatenate url fragments handling stray slashes
 \************************************************************/
function urlConcat(base, context) {
    // remove any trailing slash from base
    base = base.replace(/\/$/, '');
    // remove any leading slash from context
    context = context.replace(/^\//, '');
    // join
    return base + "/" + context;
}
/************************************************************\
 * Go to occurrence records for selected node
 \************************************************************/
function showRecords(node, query) {
    var rank = node.attr('rank');
    if (rank == 'kingdoms') return;
    var name = node.attr('id');
    // url for records list
    var recordsUrl = urlConcat(CHARTS_CONFIG.biocacheWebappUrl, "/occurrences/search?q=") + query +
        "&fq=" + rank + ":\"" + name + "\"";
    document.location.href = recordsUrl;
}
/************************************************************\
 * Go to 'species' page for selected node
 \************************************************************/
function showBie(node) {
    var rank = node.attr('rank');
    if (rank == 'kingdoms') return;
    var name = node.attr('id');
    var sppUrl = CHARTS_CONFIG.bieWebappUrl + "/species/" + name;
    document.location.href = sppUrl;
}
