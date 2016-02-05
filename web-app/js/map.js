/*
 * Mapping - plot collection locations
 */

/************************************************************\
 * i18n
 \************************************************************/
//jQuery.i18n.properties({
//    name: 'messages',
//    path: COLLECTORY_CONF.contextPath + '/messages/i18n/',
//    mode: 'map',
//    language: COLLECTORY_CONF.locale // default is to use browser specified locale
//});
/************************************************************/

/* some globals */
// the map
var map;

// the WGS projection
var proj = new OpenLayers.Projection("EPSG:4326");

// projection options for interpreting GeoJSON data
var proj_options;

// the data layer
var vectors;

// the server base url
var baseUrl;

// the ajax url for getting filtered features
var featuresUrl;

// flag to make sure we only apply the url initial filter once
var firstLoad = true;

if (altMap == undefined) {
    var altMap = false;
}

//var extent = new OpenLayers.Bounds(-20037508.34, -20037508.34, 20037508.34, 20037508.34);

// centre point for map of Australia - this value is transformed
// to the map projection once the map is created.
var centrePoint;

var defaultZoom;

// represents the number in 'all' collections - used in case the total number changes on an ajax request
var maxCollections = 0;

/************************************************************\
* initialise the map
* note this must be called from body.onload() not jQuery document.ready() as the latter is too early
\************************************************************/
function initMap(mapOptions) {

    centrePoint = new OpenLayers.LonLat(mapOptions.centreLon, mapOptions.centreLat);
    defaultZoom = mapOptions.defaultZoom;

    // serverUrl is the base url for the site eg http://collections.ala.org.au in production
    // cannot use relative url as the context path varies with environment
    baseUrl = mapOptions.serverUrl;
    featuresUrl = mapOptions.serverUrl + "/public/mapFeatures";

    var featureGraphicUrl = mapOptions.serverUrl + "/images/map/orange-dot.png";
    var clusterGraphicUrl = mapOptions.serverUrl + "/images/map/orange-dot-multiple.png";

    // create the map
    map = new OpenLayers.Map('map_canvas', {
        controls: [],
        sphericalMercator: true,
        layers: [
            new OpenLayers.Layer.XYZ("Base layer",
            ["http://${s}.basemaps.cartocdn.com/light_all/${z}/${x}/${y}.png"], {
                sphericalMercator: true,
                wrapDateLine: true
            })
        ]
    });

    // restrict mouse wheel chaos
    map.addControl(new OpenLayers.Control.Navigation({zoomWheelEnabled:false}));
    map.addControl(new OpenLayers.Control.ZoomPanel());
    map.addControl(new OpenLayers.Control.PanPanel());

    // zoom map
    map.zoomToMaxExtent();

    // add layer switcher for now - review later
    map.addControl(new OpenLayers.Control.LayerSwitcher());

    // centre the map on Australia
    map.setCenter(centrePoint.transform(proj, map.getProjectionObject()), defaultZoom);

    // set projection options
    proj_options = {
        'internalProjection': map.baseLayer.projection,
        'externalProjection': proj
    };

    // create a style that handles clusters
    var style = new OpenLayers.Style({
        externalGraphic: "${pin}",
        graphicHeight: "${size}",
        graphicWidth: "${size}",
        graphicYOffset: -23
    }, {
        context: {
            pin: function(feature) {
                return (feature.cluster) ? clusterGraphicUrl : featureGraphicUrl;
            },
            size: function(feature) {
                return (feature.cluster) ? 25 : 23;
            }
        }
    });

    // create a layer for markers and set style
    var clusterStrategy = new OpenLayers.Strategy.Cluster({distance: 11, threshold: 2});
    vectors = new OpenLayers.Layer.Vector("Collections", {
        strategies: [clusterStrategy],
        styleMap: new OpenLayers.StyleMap({"default": style})});

    // listen for feature selection
    vectors.events.register("featureselected", vectors, selected);
    map.addLayer(vectors);

    // listen for changes to visible region
    map.events.register("moveend", map, moved);
    
    // control for hover labels
    var hoverControl = new OpenLayers.Control.SelectFeature(vectors, {
        hover: true,
        highlightOnly: true,
        renderIntent: "default",
        eventListeners: {
            //beforefeaturehighlighted: hoverOn,
            featurehighlighted: hoverOn,
            featureunhighlighted: hoverOff
        }
    });

    // control for selecting features (on click)
    var control = new OpenLayers.Control.SelectFeature(vectors, {
        clickout: true
    });
    map.addControl(control);
    control.activate();

    // create custom button to zoom extents to Australia
    var button = new OpenLayers.Control.Button({
        displayClass: "resetZoom",
        title: jQuery.i18n.prop('zoom.to.australia'),
        trigger: resetZoom
    });
    var panel = new OpenLayers.Control.Panel({defaultControl: button});
    panel.addControls([button]);
    map.addControl(panel);

    // initial data load
    reloadData();
}

/************************************************************\
*   load features via ajax call
\************************************************************/
function reloadData() {
    if (altMap) {
        $.get(featuresUrl, {filters: 'all'}, dataRequestHandler);
    } else {
        $.get(featuresUrl, {filters: getAll()}, dataRequestHandler);
    }
}

/************************************************************\
*   handler for loading features
\************************************************************/
function dataRequestHandler(data) {
    // clear existing
    vectors.destroyFeatures();

    // parse returned json
    var features = new OpenLayers.Format.GeoJSON(proj_options).read(data);

    // update list
    updateList(features);

    // add features to map
    vectors.addFeatures(features);

    // remove non-mappable collections
    var unMappable = new Array();
    for (var i = 0; i < features.length; i++) {
        if (!features[i].attributes.isMappable) {
            unMappable.push(features[i]);
        }
    }
    vectors.destroyFeatures(unMappable);

    // update number of unmappable collections
    var unMappedText = "";
    switch (unMappable.length) {
        case 0: unMappedText = ""; break;
        //case 1: unMappedText = "1 collection cannot be mapped."; break;
        //default: unMappedText = unMappable.length + " collections cannot be mapped."; break;
        case 1: unMappedText = "1 " + jQuery.i18n.prop('map.js.collectioncannotbemapped'); break;
        default: unMappedText = unMappable.length + " " + jQuery.i18n.prop('map.js.collectionscannotbemapped'); break;
    }
    $('span#numUnMappable').html(unMappedText);

    // update display of number of features
    var selectedFilters = getSelectedFiltersAsString();
    var selectedFrom = jQuery.i18n.prop('map.js.collectionstotal');
    if (selectedFilters != 'all') {
        selectedFrom = jQuery.i18n.prop(selectedFilters)/*selectedFilters*/ + " " + jQuery.i18n.prop('collections');
    }
    var innerFeatures = "";

    //console.log('Console: ' + jQuery.i18n.prop('map.js.nocollectionsareselected'));

    switch (features.length) {
        //case 0: innerFeatures = "No collections are selected."; break;
        //case 1: innerFeatures = "One collection is selected."; break;
        case 0: innerFeatures = jQuery.i18n.prop('map.js.nocollectionsareselected'); break;
        case 1: innerFeatures = jQuery.i18n.prop('map.js.onecollectionisselected'); break;
        default: innerFeatures = features.length + " "+ selectedFrom + "."; break;
    }
    $('span#numFeatures').html(innerFeatures);

    // fire moved to initialise number visible
    moved(null);

    // first time only: select the filter if one is specified in the url
    if (firstLoad) {
        selectInitialFilter();
        firstLoad = false;
    }
}

/************************************************************\
*   build human-readable string from selected filter list
\************************************************************/
function getSelectedFiltersAsString() {
    var list;
    //alert(altMap);
    if (altMap) {
        // new style
        list = getSelectedFilters();
    } else {
        // old style
        list = getAll();
    }
    // transform some
    list = list.replace(/plants/,"plant");
    list = list.replace(/microbes/,"microbial");

    // remove trailing comma
    if (list.substr(list.length - 1) == ',') {
        list = list.substring(0,list.length - 1);
    }
    // replace last with 'and'
    var last = list.lastIndexOf(',');
    if (last > 0) {
        list = list.substr(0,last) + " and " + list.substr(last + 1);
    }
    // insert space after remaining commas
    list = list.replace(/,/g,", ");
    return list;
}

/************************************************************\
*   regenerate list of collections - update total number
\************************************************************/
function updateList(features) {
    // update the potential total
    maxCollections = Math.max(features.length, maxCollections);
    if (!$('div#all').hasClass('inst')) {  // don't change text if showing institutions
        $('span#allButtonTotal').html(jQuery.i18n.prop('show.all') + " " + maxCollections + " " + jQuery.i18n.prop('collections') + ".")
    }
    // update display of number of features
    var innerFeatures = "";
    switch (features.length) {
        //case 0: innerFeatures = "No collections are selected"; break;
        //case 1: innerFeatures = features.length + " collection is listed"; break;
        //default: innerFeatures = features.length + " collections are listed alphabetically"; break;
        case 0: innerFeatures = jQuery.i18n.prop('map.js.nocollectionsareselected'); break;
        case 1: innerFeatures = features.length + " " + jQuery.i18n.prop('map.js.collectionislisted'); break;
        default: innerFeatures = features.length + " " + jQuery.i18n.prop('map.js.collectionsarelistedalphabetically'); break;
    }
    $('span#numFilteredCollections').html(innerFeatures);

    // group by institution
    var sortedParents = groupByParent(features, true);

    var innerHtml = "";
    var orphansHtml = "";
    for (var i = 0; i < sortedParents.length; i++) {
        var collList = sortedParents[i];
        // show institution - use name of institution from first collection
        var firstColl = collList[0];
        var content = "";
        if (firstColl.attributes.instName == null && firstColl.attributes.entityType == "Collection") {
            content += "<li><span class='highlight'>" + jQuery.i18n.prop('collections.with.no.institution') + "</span><ul>";
        } else if (firstColl.attributes.instName == null && firstColl.attributes.entityType == "DataProvider") {
            content += "<li><span class='highlight'>" + jQuery.i18n.prop('dataproviders.list') + "</span><ul>";

        } else {
            content += "<li><a class='highlight' href='" + baseUrl + "/public/show/" + firstColl.attributes.instUid + "'>" +
                    firstColl.attributes.instName + "</a><ul>";
        }
        // show each collection
        for (var c = 0; c < collList.length; c++) {
            var coll = collList[c];
            var acronym = "";
            if (coll.attributes.acronym != undefined) {
                acronym = " (" + coll.attributes.acronym + ")"
            }
            content += "<li>";
            content += "<a href=" + baseUrl + "/public/show/" + coll.attributes.uid + ">" +
                    coll.attributes.name + acronym + "</a>";
            if (!coll.attributes.isMappable) {
              content += "<img style='vertical-align:middle' src='" + baseUrl + "/images/map/nomap.gif'/>";
            }
            content += "</li>";
        }
        content += "</ul></li>"
        if (firstColl.attributes.instName == null) {
            orphansHtml = content;
        } else {
            innerHtml += content;
        }
    }
    innerHtml += orphansHtml;
    $('ul#filtered-list').html(innerHtml);
}

/************************************************************\
*   hover handlers
\************************************************************/
function hoverOff(evt) {
    feature = evt.feature;
    if (feature != null && feature.popup != null) {
        map.removePopup(feature.popup);
        feature.destroyPopup(feature.popup);
    }
}

/************************************************************\
*   hovers NOT USED
\************************************************************/
function hoverOn(evt) {
  feature = evt.feature;
    var content = "";
    if (feature.cluster) {
        content = "<ul class='hoverPop'>";
        for(var c = 0; c < feature.cluster.length; c++) {
            content += "<li>"
                    + feature.cluster[c].attributes.name
                    + "</li>";
        }
        content += "</ul>";
    } else {
        content = feature.attributes.name;
    }
    var popup = new OpenLayers.Popup.FramedCloud(feature.attributes.id,
                        feature.geometry.getBounds().getCenterLonLat(),
                        new OpenLayers.Size(10, 10),
                        content,
                        null,
                        false);
    // attach to feature
    feature.popup = popup;
    // add to map
    map.addPopup(popup);
    // fit to content
    popup.updateSize();
}

/************************************************************\
*   handle map movement (zoom pan)
\************************************************************/
function moved(evt) {
    // determine how many individual features are visible
    var visibleCount = 0;
    var totalCount = 0;
    for (var c = 0; c < vectors.features.length; c++) {
        var f = vectors.features[c];
        if (f.cluster) {
            totalCount += f.cluster.length;
            // for clusters count each feature
            if (f.onScreen(true)) {
                visibleCount += f.cluster.length;
            }
        } else {
            totalCount++;
            // single feature
            if (f.onScreen(true)) {
                visibleCount++;
            }
        }
    }
    // update display of number of features visible
    var innerFeatures = "";
    switch (visibleCount) {
        case 0:
            //innerFeatures = "No collections are currently visible on the map.";
            innerFeatures = jQuery.i18n.prop('map.js.nocollectionsarecurrentlyvisible');
            break;
        case 1:
            if (totalCount == 1) {
                //innerFeatures = "It is currently visible on the map.";
                innerFeatures = jQuery.i18n.prop('map.js.itiscurrentlyvisible');
            } else {
                //innerFeatures = visibleCount + " collection is currently visible on the map.";
                innerFeatures = visibleCount + " " + jQuery.i18n.prop('map.js.collectioniscurrentlyvisible');
            }
            break;
        default:
            if (visibleCount == totalCount) {
                //innerFeatures = "All are currently visible on the map.";
                innerFeatures = jQuery.i18n.prop('map.js.allarecurrentlyvisible');
            } else {
                //innerFeatures = visibleCount + " collections are currently visible on the map.";
                innerFeatures = visibleCount + " " + jQuery.i18n.prop('map.js.collectionarecurrentlyvisible');
            }
            break;
    }
    $('span#numVisible').html(innerFeatures);
}


/************************************************************\
*   handle feature selection
\************************************************************/
function selected(evt) {
    feature = evt.feature;

    // get rid of any dags - hopefully
    clearPopups();

    // build content
    var content = "";
    if (feature.cluster) {
        content = outputClusteredFeature(feature);
    } else {
        content = outputSingleFeature(feature);
    }

    // create popoup
    var popup = new OpenLayers.Popup.FramedCloud("featurePopup",
            feature.geometry.getBounds().getCenterLonLat(),
            new OpenLayers.Size(50, 100),
            content,
            null, true, onPopupClose);

    // control shape
    if (!feature.cluster) {
        popup.maxSize = new OpenLayers.Size(350, 500);
    }

    popup.onmouseup(function() {
        alert('up');
    });

    // add to map
    map.addPopup(popup);

}

/************************************************************\
*   generate html for a single collection
\************************************************************/
function outputSingleFeature(feature) {
    if ($('div#all').hasClass('inst') && $('div#all').hasClass('selected')) { // simple list if showing institutions
        return outputSingleInstitution(feature);
    } else {
        var address = "";
        if (feature.attributes.address != null && feature.attributes.address != "") {
            address = feature.attributes.address;
        }
        var desc = feature.attributes.desc;
        var acronym = "";
        if (feature.attributes.acronym != undefined) {
            acronym = " (" + feature.attributes.acronym + ")"
        }
        var instLink = "";
        if (feature.attributes.instUid != null) {
            instLink = outputInstitutionOnOwnLine(feature) + "<br/>";
            return instLink + "<a style='margin-left:5px;' href='" + feature.attributes.url + "'>"
                        + getShortCollectionName(feature) + "</a>" + acronym
                        + "<div class='address'>" + address + "</div><hr>"
                        + "<div class='desc'>" + desc + "</div>";
        } else {
            return "<a href='" + feature.attributes.url + "'>"
                        + feature.attributes.name + "</a>" + acronym
                        + "<div class='address'>" + address + "</div><hr>"
                        + "<div class='desc'>" + desc + "</div>";
        }
    }
}

/************************************************************\
*   generate html for a single institution
\************************************************************/
function outputSingleInstitution(feature) {
    var address = "";
    if (feature.attributes.address != null && feature.attributes.address != "") {
        address = feature.attributes.address;
    }
    var acronym = "";
    if (feature.attributes.instAcronym != undefined) {
        acronym = " (" + feature.attributes.instAcronym + ")"
    }
    var content = "<a class='highlight' href='" + baseUrl + "/public/show/" + feature.attributes.instUid + "'>" + feature.attributes.instName + "</a>";
    content += acronym + "<div class='address'>" + address + "</div>";
    return content;
}

/************************************************************\
*   group features by their parent institutions
*   groupOrphans = true -> orphans are grouped in zz-other rather than interspersed
\************************************************************/
function groupByParent(features, groupOrphans) {
    // build 'map' of institutions and orphan collections
    var parents = {};
    for(var c = 0; c < features.length; c++) {
        var collectionFeature = features[c];
        var instUid = collectionFeature.attributes.instUid;
        if (instUid == undefined && groupOrphans) {
            instUid = 'zz-other';
        }
        if (instUid == undefined) {
            // add as orphan collection
            parents[collectionFeature.attributes.uid] = collectionFeature;
        } else {
            var collList = parents[instUid];
            if (collList == undefined) {
                // create new inst entry
                collList = new Array();
                collList.push(collectionFeature);
                parents[instUid] = collList;
            } else {
                // add to existing inst entry
                collList.push(collectionFeature);
            }
        }
    }
    // move to an array so we can sort
    var sortedParents = [];
    for (var key in parents) {
        sortedParents.push(parents[key]);
    }
    // sort
    sortedParents.sort(function(a,b) {
        var aname = getName(a);
        var bname = getName(b);
        if (aname < bname)
            return -1;
        if (aname > bname)
            return 1;
        return 0;
    });
    return sortedParents;
}

/************************************************************\
*   generate html for a clustered feature
\************************************************************/
function outputClusteredFeature(feature) {
    var sortedParents = groupByParent(feature.cluster, false);
    // output the parents list
    var content = "<ul>";
    if ($('div#all').hasClass('inst') && $('div#all').hasClass('selected')) { // simple list if showing institutions
        content += outputMultipleInstitutions(sortedParents);
    } else {
        // adopt different collapsing strategies based on number to display
        var strategy = 'verbose';
        if (sortedParents.length == 1) {strategy = 'veryVerbose';}
        if (sortedParents.length > 4) {strategy = 'brief';}
        if (sortedParents.length > 6) {strategy = 'terse';}
        // show them
        for (var k = 0; k < sortedParents.length; k++) {
            var item = sortedParents[k];
            if (item instanceof Array) {
                content += outputMultipleCollections(item, strategy);
            } else {
                content += outputCollectionOnOwnLine(item);
            }
        }
    }

    content += "</ul>";
    return content;
}

/************************************************************\
*   generate html for a list of institutions
\************************************************************/
function outputMultipleInstitutions(parents) {
    var content = "";
    for (var i = 0; i < parents.length; i++) {
        var obj = parents[i];
        // use name of institution from first collection
        if (obj instanceof Array) {obj = obj[0]}
        // skip collections with no institution
        var name = obj.attributes.instName;
        if (name != null && name != undefined) {
            content += "<li><a class='highlight' href='" + baseUrl + "/public/show/" + obj.attributes.instUid + "'>" + getTightInstitutionName(obj, 55) + "</a></li>";
        }
    }
    return content;
}

/************************************************************\
*   grab name from institution
\************************************************************/
function getName(obj) {

    if ($.isArray(obj) && obj[0].attributes && obj[0].attributes.name && obj[0].attributes.entityType != "Collection") {
        return obj[0].attributes.name;
    } else if (!$.isArray(obj) && obj.attributes && obj.attributes.name && obj.attributes.entityType != "Collection") {
        return obj.attributes.name;
    }

    var name;
    if ($.isArray(obj)) {
        name = obj[0].attributes.instName;
    } else {
        name = obj.attributes.instName;
    }
    // remove leading 'The ' so the institutions sort by first significant letter
    if (name !== null && name.length > 4 && name.substr(0,4) === "The ") {
        name = name.substr(4);
    }
    return name;
}

/************************************************************\
*   build html for multiple collection for an institution
\************************************************************/
function outputMultipleCollections(obj, strategy) {
    // use name of institution from first collection
    var content;
    var limit = 4;
    if (strategy == 'brief') {limit = 2;}
    if (strategy == 'terse') {limit = 0;}
    if (strategy == 'veryVerbose') {limit = 10;}
    if (obj.length < limit) {
        content = "<li>" + outputInstitutionOnOwnLine(obj[0]) + "<ul>";
        for(var c = 0;c < obj.length;c++) {
            content += outputCollectionOnOwnLine(obj[c]);
        }
        content += "</ul>";
    } else {
        if (obj.length == 1) {
            content = outputCollectionWithInstitution(obj[0], strategy);
        } else {
            content = "<li>" + outputInstitutionOnOwnLine(obj[0]) + " - " + obj.length + " " + jQuery.i18n.prop('collections') + "</li>"
        }
    }
    return content;
}

/************************************************************\
* abbreviates institution name if long (assumes inst is present)
\************************************************************/
function getTightInstitutionName(obj, max) {
    if (obj.attributes.instName.length > max && obj.attributes.instAcronym != null) {
        return obj.attributes.instAcronym;
    } else {
        return obj.attributes.instName;
    }
}

/************************************************************\
* abbreviates collections name by removing leading institution name
\************************************************************/
function getShortCollectionName(obj) {
    var inst = obj.attributes.instName;
    var shortName = obj.attributes.name;
    if (inst != null && inst.match("^The ") == "The ") {
        inst = inst.substr(4);
    }
    if (inst != null && obj.attributes.name.match("^" + inst) == inst && // coll starts with the inst name
            inst != shortName) { // but not if inst name is the whole of the coll name (ie they are the same)
        shortName = obj.attributes.name.substr(inst.length);
        // check for stupid collection names
        if (shortName.substr(0,2) == ", ") {
            shortName = shortName.substr(2);
        }
    }
    return shortName;
}

/************************************************************\
*   build html for an institution on its own line
\************************************************************/
function outputInstitutionOnOwnLine(obj) {
    return "<a class='highlight' href='" + baseUrl + "/public/show/" + obj.attributes.instUid + "'>" + getTightInstitutionName(obj, 55) + "</a>";
}

/************************************************************\
*   build html for a single collection with an institution
\************************************************************/
function outputCollectionWithInstitution(obj, strategy) {
    var max = 60;
    var acronym = "";
    if (obj.attributes.acronym != undefined) {
        acronym = " (" + obj.attributes.acronym + ")"
    }
    var instLink = "<a class='highlight' href='" + baseUrl + "/public/show/" + obj.attributes.instUid + "'>";

    if (strategy == 'verbose') {
        if (obj.attributes.name.length + acronym.length > max) {
            // drop acronym
            return "<li>" + instLink + obj.attributes.instName + "</a><ul>"
                    + "<li>" + "<a href='" + obj.attributes.url + "'>"
                    + obj.attributes.name + "</a>" + "</li></ul></li>";
        } else {
            return "<li>" + instLink + obj.attributes.instName + "</a><ul>"
                    + "<li>" + "<a href='" + obj.attributes.url + "'>"
                    + obj.attributes.name + acronym + "</a>" + "</li></ul></li>";
        }
    } else {
        // present both in one line
        var inst = obj.attributes.instName;
        var briefInst = getTightInstitutionName(obj, 25);
        var coll = obj.attributes.name;

        // try full inst + full coll + acronym
        if (inst.length + coll.length  + acronym.length <max) {
            return "<li>" + instLink + inst + "</a> - <a href='" + obj.attributes.url + "'>"
                + coll + acronym  + "</a>" + "</li>";

        // try full inst + short coll + acronym
        } else if (inst.length + getShortCollectionName(obj).length + acronym.length < max) {
            return "<li>" + instLink + inst + "</a> - <a href='" + obj.attributes.url + "'>"
                + getShortCollectionName(obj) + acronym + "</a>" + "</li>";

        // try full inst + short coll
        } else if (inst.length + getShortCollectionName(obj).length < max) {
            return "<li>" + instLink + inst + "</a> - <a href='" + obj.attributes.url + "'>"
                + getShortCollectionName(obj) + "</a>" + "</li>";

        // try acronym of inst + full coll + acronym
        } else if (briefInst.length + coll.length  + acronym.length < max) {
            return "<li>" + instLink + briefInst + "</a> - <a href='" + obj.attributes.url + "'>"
                + coll + "</a>" + "</li>";

        // try acronym of inst + full coll
        } else if (briefInst.length + coll.length < max) {
            return "<li>" + instLink + briefInst + "</a> - <a href='" + obj.attributes.url + "'>"
                + coll + "</a>" + "</li>";

        // try acronym of inst + short coll
        } else if (briefInst.length + getShortCollectionName(obj).length < max) {
            return "<li>" + instLink + briefInst + "</a> - <a href='" + obj.attributes.url + "'>"
                + getShortCollectionName(obj) + "</a>" + "</li>";

        // try acronym of inst + coll acronym
        } else if (acronym != "") {
            return "<li>" + instLink + briefInst + "</a> - <a href='" + obj.attributes.url + "'>"
                + acronym + "</a>" + "</li>";

        // try full inst + 1 collection
        } else if (inst.length < max - 12) {
            //console.log("Collection: " + jQuery.i18n.prop('collection'));
            return "<li>" + instLink + inst + "</a> - 1 " + jQuery.i18n.prop('collection') + "</li>";

        // try acronym of inst + 1 collection (worst case!)
        } else {
            return "<li>" + instLink + briefInst + "</a> - 1 " + jQuery.i18n.prop('collection') + "</li>";
        }
    }
}

/************************************************************\
*   build html for a collection on its own line
\************************************************************/
function outputCollectionOnOwnLine(obj) {
    var max = 60;
    // build acronym
    var acronym = "";
    if (obj.attributes.acronym != undefined) {
        acronym = " <span>(" + obj.attributes.acronym + ")</span>";
    }

    /* try combos in order of preference */
    var name;
    // try full name + acronym
    if (obj.attributes.name.length + acronym.length < max/2) { // favour next option unless very short
        name = obj.attributes.name + "</a>" + acronym;

    // try short name + acronym
    } else if (getShortCollectionName(obj).length + acronym.length < max){
        name = getShortCollectionName(obj) + acronym + "</a>";

    // try name
    } else if (obj.attributes.name.length < max) {
        name = obj.attributes.name + "</a>";

    // try short name
    } else if (getShortCollectionName(obj).length < max){
        name = getShortCollectionName(obj) + "</a>";

    // try acronym
    //} else if (acronym != "") {
    //    name = acronym + "</a>";

    // stuck with name
    } else {
        name = obj.attributes.name + "</a>";
    }

    return "<li>" + "<a href='" + obj.attributes.url + "'>"
                + name + "</li>";
}

/************************************************************\
*   remove pop-ups on close
\************************************************************/
function onPopupClose(evt) {
    //evt.feature.removePopup(this);
    map.removePopup(this);
}

/************************************************************\
*   clear all pop-ups
\************************************************************/
function clearPopups() {
    for (pop in map.popups) {
        map.removePopup(map.popups[pop])
    }
    // maybe iterate features and clear popups?
}

/************************************************************\
*   reset map to initial view of Australia
\************************************************************/
function resetZoom() {
    // centre the map on Australia
    // note that the point has already been transformed
    map.setCenter(centrePoint);
    map.zoomTo(4);
}
/* END plot collection locations */


/*
 * Helpers for managing Filter checkboxes
 */
/************************************************************\
*   set all boxes checked and trigger change handler
\************************************************************/
function setAll() {
    $('input[name=filter]').attr('checked', $('input#all').is(':checked'));
    filterChange();
}


/************************************************************\
*   build comma-separated string representing all selected boxes
\************************************************************/
function getAll() {
    if ($('input#all').is(':checked')) {
        return "all";
    }
    var checked = "";
    $('input[name=filter]').each(function(index, element){
        if (element.checked) {
            checked += element.value + ",";
        }
    });

    return checked;
}


/************************************************************\
*   need separate handler for ento change because we need to know which checkbox changed
*   to manage the ento-fauna paradigm
\************************************************************/
function entoChange() {
    // set state of faunal box
    if ($('input#fauna').is(':checked') && !$('input#ento').is(':checked')) {
        $('input#fauna').attr('checked', false);
    }
    filterChange();
}


/************************************************************\
*   handler for filter selection change
\************************************************************/
function filterChange() {
    // set ento based on faunal
    // set state of faunal box
    if ($('input#fauna').is(':checked') && !$('input#ento').is(':checked')) {
        $('input#ento').attr('checked', true);
    }
    // find out if they are all checked
    var all = true;
    $('input[name=filter]').each(function(index, element){
        if (!element.checked) {
            all = false;
        }
    });
    // set state of 'select all' box
    if ($('input#all').is(':checked') && !all) {
        $('input#all').attr('checked', false);
    } else if (!$('input#all').is(':checked') && all) {
        $('input#all').attr('checked', true);
    }

    // reload features based on new filter selections
    reloadData();
}
/* END filter checkboxes */

/*
 * Helpers for managing Filter buttons
 */
/************************************************************\
*   handle filter button click
\************************************************************/
function toggleButton(button) {
    // if already selected do nothing
    if ($(button).hasClass('selected')) {
        return;
    }

    // de-select all
    $('div.filter-buttons div').toggleClass('selected',false);

    // select the one that was clicked
    $(button).toggleClass("selected", true);

    // reloadData
    var filters = button.id;
    if (filters == 'fauna') {filters = 'fauna,entomology'}
    $.get(featuresUrl, {filters: filters}, dataRequestHandler);
    
}

/************************************************************\
 *   select filter if one is specified in the url
 \************************************************************/
function selectInitialFilter() {
    var params = $.deparam.querystring(),
        start = params.start,
        filter;
    if (start) {
        if (start === 'insects') { start = 'entomology'; }
        filter = $("#" + start);
        if (filter.length > 0) {
            toggleButton(filter[0]);
        }
    }
}

/************************************************************\
*   build comma separated string of selected buttons - NOT USED
\************************************************************/
function getSelectedFilters() {
    var checked = "";
    $('div.filter-buttons div').each(function(index, element){
        if ($(element).hasClass('selected')) {
            checked += element.id + ",";
        }
    });
    if (checked == 'fauna,entomology,microbes,plants,') {
        checked = 'all';
    }

    return checked;
}

/* END filter buttons */
