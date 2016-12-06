/*
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 *
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 */
/**
 * User: markew
 * Date: 15/08/11
 * Time: 8:47 AM
 */

/* holds full list of resources */
var allResources;

/* holds current filtered list */
var resources;

/* list of filters currently in effect - items are {name, value} */
var currentFilters = [];

/* pagination offset into the record set */
var offset = 0;

/* size of current filtered list */
var total = 0;

/* the base url of the home server */
var baseUrl;

/* the base url of the biocache server */
var biocacheUrl;

/* options for all tooltips */
var tooltipOptions = {position:'center right',offset:[-10,5],predelay:130, effect:'fade', fadeOutSpeed: 200}

/** load resources and show first page **/
function loadResources(serverUrl, biocacheRecordsUrl) {
    baseUrl = serverUrl;
    biocacheUrl = biocacheRecordsUrl;
    $.getJSON(baseUrl + "/public/resources.json", function(data) {
        allResources = data;
        // no filtering at this stage
        resources = allResources;

        setStateFromHash();
        updateTotal();
        calculateFacets();
        showFilters();
        resources.sort(comparator);
        displayPage();
        wireDownloadLink();
        wireSearchLink();

        // set up tooltips
        // - don't do download link because the title changes and the tooltip app does not update
        // - also limit to content to exclude links in header
        $('div.collectory-content [title][id!="downloadLink"]').tooltip(tooltipOptions);
    });
}
/*************************************************\
 *  List display
 \*************************************************/
/** display a page **/
function displayPage() {
    // clear list
    $('#results div').remove();

    // paginate and show list
    for (var i = 0; i < pageSize(); i++) {
        var item = resources[offset + i];
        // item will be undefined if there are less items than the page size
        if (item != undefined) {
            appendResource(item);
        }
    }
    activateClicks();
    showPaginator();
}
/** append one resource to the list **/
function appendResource(value) {
    // clear the loading sign
    $('#loading').remove();

    // create a container inside results
    var $div = $('<div class="result"></div>');
    $('#results').append($div);

    // add three 'rows'
    var $rowA = $('<h4 class="rowA"></h4>').appendTo($div);
    var $rowB = $('<p class="rowB"></p>').appendTo($div);
    var $rowC = $('<div class="rowC" style="display:none;">').appendTo($div);  // starts hidden

    // row A
    $rowA.append('<img title="'+ jQuery.i18n.prop('datasets.js.appendresource01') + '" src="' + baseUrl + '/images/skin/ExpandArrow.png"/>');  // twisty
    $rowA.append('<span class="result-name"><a title="' + jQuery.i18n.prop('datasets.js.appendresource02') + '" href="' + baseUrl + '/public/showDataResource/' + value.uid + '">' + value.name + '</a></span>'); // name
    $rowA.find('a').tooltip(tooltipOptions);
    $rowA.find('img').tooltip($.extend({},tooltipOptions,{position:'center left'}));

    // row B
    $rowB.append('<span><strong class="resultsLabelFirst">'+ jQuery.i18n.prop('datasets.js.appendresource06') +': </strong>' + value.resourceType + '</span>');  // resource type
    $rowB.append('<span><strong class="resultsLabel">'+ jQuery.i18n.prop('datasets.js.appendresource07') +': </strong>' + (value.licenseType == null ? '' : value.licenseType) + '</span>'); // license type
    $rowB.append('<span><strong class="resultsLabel">'+ jQuery.i18n.prop('datasets.js.appendresource08') +': </strong>' + (value.licenseVersion == null ? '' : value.licenseVersion) + '</span>'); // license version
    if (value.resourceType == 'records') {
        $rowB.append('<span class="viewRecords"><a title="' + jQuery.i18n.prop('datasets.js.appendresource03') + '" href="' + biocacheUrl + '/occurrences/search?q=data_resource_uid:' + value.uid + '">'+ jQuery.i18n.prop('datasets.js.appendresource10') +'</a></span>'); // records link
    }
    if (value.resourceType == 'website' && value.websiteUrl) {
        $rowB.append('<span class="viewWebsite"><a title="' + jQuery.i18n.prop('datasets.js.appendresource04') + '" class="external" target="_blank" href="' + value.websiteUrl + '">'+ jQuery.i18n.prop('datasets.js.appendresource11') +'</a></span>'); // website link
    }
    $rowB.find('a').tooltip(tooltipOptions);

    // row C
    var desc = "";
    if (value.pubDescription != null && value.pubDescription != "") {
        desc += value.pubDescription;
    }
    if (value.techDescription != null && value.techDescription != "") {
        desc += value.techDescription;
    }
    if (desc != "") {
        $rowC.append('<p>' + desc + '</p>'); // description
    }

    if (value.contentTypes != null) {
        $rowC.append('<span><strong class="resultsLabel">'+ jQuery.i18n.prop('datasets.js.appendresource09') +':</strong></span>'); // label for content types
        var $ul = $('<ul class="contentList"></ul>').appendTo($rowC);
        var ctList = $.parseJSON(value.contentTypes);
        $.each(ctList, function(i,v) {
            $ul.append("<li>" + v + "</li>");
        });
    }  // content types

    if ($rowC.children().length == 0) {
        $rowC.append(jQuery.i18n.prop('datasets.js.appendresource05'));
    }
}

/** bind click handler to twisty **/
function activateClicks() {
    $('.rowA img').rotate({
        bind:
            {
                click: function() {
                    // hide tooltip
                    hideTooltip(this);

                    var $target = $(this).parent().parent().find('.rowC');
                    if ($target.css('display') == 'none') {
                        $(this).rotate({animateTo:90,duration:350});
                    }
                    else {
                        $(this).rotate({animateTo:0,duration:350});
                    }
                    $target.slideToggle(350, function() {
                    });
                    return false;
                }
            }
    });
}
/** clear the list and reset values **/
function clearList() {
    resources = [];
    total = 0;
    offset = 0;
    $.bbq.removeState('offset');
}
/** display the current size of the filtered list **/
function updateTotal() {
    total = resources.length;
    $('#resultsReturned').html(jQuery.i18n.prop('datasets.js.updatetotal03') +  ' <strong>' + total + '</strong> ' + jQuery.i18n.prop('datasets.js.updatetotal04') + (total == 1 ? jQuery.i18n.prop('datasets.js.updatetotal05') : jQuery.i18n.prop('datasets.js.updatetotal06')));
    $('#downloadLink').attr('title', jQuery.i18n.prop('datasets.js.updatetotal01') + ' ' + total + ' ' + jQuery.i18n.prop('datasets.js.updatetotal02'));
}
function hideTooltip(element) {
    if (element == undefined) return;
    if ($(element).data("tooltip") != null) {
        $(element).data("tooltip").hide();
    }
}
/*************************************************\
 *  Filters
 \************************************************/
/** applies current filters to the list **/
function filterList() {
    // clear list of data sets
    clearList();
    // clear search term
    $('#dr-search').val('');
    // revert to full list
    resources = allResources;

    // aggregate all search criteria
    var searchTerms = [];
    $.each(currentFilters, function(i, obj) {
        if (obj.name == 'contains') {
            searchTerms.push(obj.value.toLowerCase());
        }
    });

    // perform any solr search and wait for result
    if (searchTerms.length > 0) {
        $('.collectory-content').css('cursor','wait');
        // build query string from terms
        var query = '';
        $.each(searchTerms, function(i, term) {
            query += (i == 0) ? term : "&fq=text:" + term
        });
        // do search
        console.log('Doing a search with query: ' + query);
        $.ajax({url: baseUrl + "/public/dataSetSearch?q=" + query,
            success: function(uids) {
                applyFilters(uids);
                $('.collectory-content').css('cursor','default');
            }
        });
    }
    // no search - so just do it now
    else {
        // do it now
        applyFilters();
    }
}
function applyFilters(uidList) {
    // apply each filter in effect
    $.each(currentFilters, function(i, obj) {
        filterBy(obj, uidList);
    });
    updateTotal();
    calculateFacets();
    showFilters();
    displayPage();
}

/** applies a single filter to the list **/
function filterBy(filter, uidList) {
    var newResourcesList = [];
    var facet = facets[filter.name];
    $.each(resources, function(index, resource) {

        // filter by has
        if (facet.action == "has") {
            if (resource[filter.name] && resource[filter.name].indexOf(filter.value) > 0) {
                newResourcesList.push(resource);
            }
        }
        // filter by search results
        else if (facet.action == "containedIn") {
            if (uidList.length == 1 && resource.uid == uidList[0]) { // don't know why this is needed - seems to be a bug
                newResourcesList.push(resource);
            }
            else if (uidList && $.inArray(resource.uid,uidList) >= 0) {
                newResourcesList.push(resource);
            }
        }
        // filter by equality
        else if (resource[filter.name] == filter.value || (filter.value == 'noValue' && resource[filter.name] == null)) {
            newResourcesList.push(resource);
        }
    });
    resources = newResourcesList;
}
/** displays the current filters **/
function showFilters() {
    $('#currentFilter').remove();
    if (currentFilters.length > 0) {
        // create the container
        $('#currentFilterHolder').append('<div id="currentFilter"><h4><span class="FieldName">Current Filters</span></h4>' +
                '<div id="subnavlist"><ul></ul></div></div>');
    }
    $.each(currentFilters, function(index, obj) {
        var displayValue = obj.name == 'contains' ? obj.value : labelFor(obj.value);
        $('#currentFilter #subnavlist ul').append('<li>' + labelFor(obj.name) + ': <b>' + displayValue + '</b>&nbsp;' +
                '[<b><a href="#" onclick="removeFilter(\'' + obj.name + "','" + obj.value + '\',this);return false;"' +
                'class="removeLink" title="remove filter">X</a></b>]</li>');
    });
}
/** adds a filter and re-filters list**/
function addFilter(facet, value, element) {
    // hide tooltip
    hideTooltip(element);

    if (findInCurrentFilters(facet, value) >= 0) {
        // duplicate of existing filter so do nothing
        return;
    }
    var filter = {name:facet, value:value, action: facets[facet].action};
    currentFilters.push(filter);
    serialiseFiltersToHash();
    filterList();
}
/** removes a filter and re-filters list**/
function removeFilter(facet, value, element) {
    var idx = findInCurrentFilters(facet, value);
    if (idx > -1) {
        currentFilters.splice(idx, 1);
    }
    // make sure no tooltips are left visible
    hideTooltip(element);
    serialiseFiltersToHash();
    filterList();
}
function findInCurrentFilters(facet, value) {
    var idx = -1;
    $.each(currentFilters, function(index, obj) {
        if (obj.name == facet && obj.value == value) {
            idx = index;
        }
    });
    return idx;
}
/*************************************************\
 *  Pagination
 \*************************************************/
/** build and append the pagination widget **/
function showPaginator() {
    if (total <= pageSize()) {
        // no pagination required
        $('div#navLinks').html("");
        return;
    }
    var currentPage = Math.floor(offset / pageSize()) + 1;
    var maxPage = Math.ceil(total / pageSize());
    var $pago = $("<div class='pagination'></div>");
    // add prev
    if (offset > 0) {
        $pago.append('<a href="javascript:prevPage();">« Previous</a></li>');
    }
    else {
        $pago.append('« Previous</span>');
    }
    for (var i = 1; i <= maxPage && i<20; i++) {
        if (i == currentPage) {
            $pago.append('<span class="currentPage disabled">' + i + '</span>');
        }
        else {
            $pago.append('<a href="javascript:gotoPage(' + i + ');">' + i + '</a>');
        }
    }
    // add next
    if ((offset + pageSize()) < total) {
        $pago.append('<a href="javascript:nextPage();">Next »</a>');
    }
    else {
        $pago.append('Next »');
    }

    $('div#navLinks').html($pago);
}
/** get current page size **/
function pageSize() {
    return parseInt($('select#per-page').val());
}
/** show the specified page **/
function gotoPage(pageNum) {
    // calculate new offset
    offset = (pageNum - 1) * pageSize();
    displayPage();
}
/** show the previous page **/
function prevPage() {
    // calculate new offset
    offset = offset - pageSize();
    displayPage();
}
/** show the next page **/
function nextPage() {
    // calculate new offset
    offset = offset + pageSize();
    $.bbq.pushState({offset:offset});
    displayPage();
}
/** action for changes to the pageSize widget */
function onPageSizeChange() {
    offset = 0;
    $.bbq.pushState({pageSize:pageSize()});
    displayPage();
}
/** action for changes to the sort widget */
function onSortChange() {
    offset = 0;
    $.bbq.pushState({sort:$('select#sort').val()});
    resources.sort(comparator);
    displayPage();
}
/** action for changes to the sort order widget */
function onDirChange() {
    offset = 0;
    $.bbq.pushState({dir:$('select#dir').val()});
    resources.sort(comparator);
    displayPage();
}
/* comparator for data resources */
function comparator(a, b) {
    var va, vb;
    var sortBy = $('select#sort').val();
    switch ($('select#sort').val()) {
        case 'name':
            va = a.name;
            vb = b.name;
            break;
        case 'type':
            va = a.resourceType;
            vb = b.resourceType;
            break;
        case 'license':
            va = a.licenseType;
            vb = b.licenseType;
            break;
        default:
            va = a.name;
            vb = b.name;
    }
    if (va == vb) {
        // sort on name
        va = a.name;
        vb = b.name;
    }
    // use lowercase
    va = va == null ? "" : va.toLowerCase();
    vb = vb == null ? "" : vb.toLowerCase();

    if ($('select#dir').val() == 'ascending') {
        return (va < vb ? -1 : (va > vb ? 1 : 0));
    }
    else {
        return (vb < va ? -1 : (vb > va ? 1 : 0));
    }
}

/*************************************************\
 *  Facet management
 \*************************************************/

/* this holds the display text for all values of facets - keyed by the facet value */
// the default if not in this list is to capitalise the record value
// also holds display text for the facet categories
var displayText = {
    ccby:jQuery.i18n.prop('datasets.js.displaytext01'), ccbync:jQuery.i18n.prop('datasets.js.displaytext02'), ccbysa:jQuery.i18n.prop('datasets.js.displaytext03'), ccbyncsa:jQuery.i18n.prop('datasets.js.displaytext04'), other:jQuery.i18n.prop('datasets.js.displaytext05'),
    noLicense:jQuery.i18n.prop('datasets.js.displaytext06'), noValue:jQuery.i18n.prop('datasets.js.displaytext07'), '3.0':jQuery.i18n.prop('datasets.js.displaytext08'), '2.5':jQuery.i18n.prop('datasets.js.displaytext09'),
    dataAvailable:jQuery.i18n.prop('datasets.js.displaytext10'),linksAvailable:jQuery.i18n.prop('datasets.js.displaytext11'),inProgress:jQuery.i18n.prop('datasets.js.displaytext12')
};

var helpText = {
    records:jQuery.i18n.prop('datasets.js.helptext01'),website:jQuery.i18n.prop('datasets.js.helptext02'),
    document:jQuery.i18n.prop('datasets.js.helptext03'),uploads:jQuery.i18n.prop('datasets.js.helptext04'),
    'CC BY':jQuery.i18n.prop('datasets.js.helptext05'), 'CC BY-NC':jQuery.i18n.prop('datasets.js.helptext06'),
    'CC BY-SA':jQuery.i18n.prop('datasets.js.helptext07'), 'CC BY-NC-SA':jQuery.i18n.prop('datasets.js.helptext08'),
    other:jQuery.i18n.prop('datasets.js.helptext09'),noLicense:jQuery.i18n.prop('datasets.js.helptext10'), noValue:jQuery.i18n.prop('datasets.js.helptext11'),
    '3.0':jQuery.i18n.prop('datasets.js.helptext12'), '2.5':jQuery.i18n.prop('datasets.js.helptext13'),
    dataAvailable:jQuery.i18n.prop('datasets.js.helptext14'),linksAvailable:jQuery.i18n.prop('datasets.js.helptext15'),
    inProgress:jQuery.i18n.prop('datasets.js.helptext16'),declined:jQuery.i18n.prop('datasets.js.helptext17'),
    identified:jQuery.i18n.prop('datasets.js.helptext18')
};

/* Map of dataset attributes to treat as facets */
var facets = {
    resourceType:{name:"resourceType",display:jQuery.i18n.prop('datasets.js.facets01')},
    licenseType:{name:"licenseType",display:jQuery.i18n.prop('datasets.js.facets02')},
    licenseVersion:{name:"licenseVersion",display:jQuery.i18n.prop('datasets.js.facets03')},
    status:{name:"status",display:jQuery.i18n.prop('datasets.js.facets04'),help:jQuery.i18n.prop('datasets.js.facets05')},
    contentTypes:{name:"contentTypes", action:"has",display:jQuery.i18n.prop('datasets.js.facets06'),help:jQuery.i18n.prop('datasets.js.facets07')},
    contains:{name:"contains", action:"containedIn", display:jQuery.i18n.prop('datasets.js.facets08')},
    institution:{name:"institution", display:jQuery.i18n.prop('datasets.js.facets09'),help:jQuery.i18n.prop('datasets.js.facets10')}};

/** calculate facet totals and display them **/
function calculateFacets() {
    $('#dsFacets div').remove();
    $.each(facets, function(i, obj) {
        if (obj.name != 'contains') {
            var list = sortByCount(getSetOfFacetValuesAndCounts(obj));
            // don't show if only one value
            if (list.length > 1) {
                $('#dsFacets').append(displayFacet(obj, list));
            }
        }
    });
}

/** Returns a map of distinct values of the facet and the number of each for the current filtered list **/
function getSetOfFacetValuesAndCounts(facet) {
    var map = {};
    $.each(resources, function(index, value) {
        var attr = value[facet.name];
        if (facet.action == 'has') {
            if (attr) {
                // treat each value in the json list as a facet value
                $.each($.parseJSON(attr), function(i, v) {
                    addToMap(map, v);
                });
            }
        }
        else {
            if (!attr) {
                attr = "noValue"
            }
            addToMap(map, attr);
        }
    });
    return map;
}
/* add the count of an value to the map */
function addToMap(map, attr) {
    if (map[attr] == undefined) {
        map[attr] = 1;
    }
    else {
        map[attr]++;
    }
}
/** Creates DOM elements to represent the facet **/
function displayFacet(facet, list) {
    // create dom container
    var $div = $("<div></div>");

    // add facet name
    var help = facet.help == undefined ? '' : 'title="' + facet.help + '"';
    $div.append('<h4><span ' + help + ' class="FieldName">' + facet.display + '</span></h4>');
    $div.find('h4 span[title]').tooltip(tooltipOptions);

    // add each value
    var $list = $('<ul class="facets"></ul>').appendTo($div);
    $.each(list, function(index, value) {
        // only show first 5 + a 'more' link if the list has more than 6 items
        if (list.length > 6 && index == 5) {
            // add link to show more
            $list.append(moreLink());
            // add this item as hidden
            $list.append(displayFacetValue(facet, value, true));
        }
        else {
            // create as hidden after the first 5
            $list.append(displayFacetValue(facet, value, index > 5));
        }
    });
    return $div;
}
function moreLink() {
    var $more = $('<li class="link"><i class="icon-hand-right"></i> '+ jQuery.i18n.prop('datasets.js.morelink')+'</li>');
    $more.click(function() {
        // make following items visible and add a 'less' link
        $(this).parent().find('li').css('display','list-item');
        // add 'less' link
        $(this).parent().append(lessLink());
        // remove this link
        $(this).remove();
    });
    return $more
}
function lessLink() {
    var $less = $('<li class="link"><i class="icon-hand-right"></i> ' + jQuery.i18n.prop('datasets.js.lesslink') + '</li>');
    $less.click(function() {
        // make items > 5 hidden and add a 'more' link
        $(this).parent().find('li:gt(4)').css('display','none');
        // add 'more' link
        $(this).parent().append(moreLink());
        // remove this link
        $(this).remove();
    });
    return $less
}
function displayFacetValue(facet, value, hide) {
    var attr = value.facetValue;
    var count = value.count;
    var help = helpText[attr] == undefined ? '' : ' title="' + helpText[attr] + '"';
    var $item = $('<li></li>');
    if (hide) {
        $item.css('display','none');
    }
    var $link = $('<span class="link"' + help + '>' + labelFor(attr) + '</span>').appendTo($item);
    $link.click(function() {
        addFilter(facet.name, attr, this);
    });
    $item.append(' (<span>' + count + '</span>)');
    if (help) {
        $link.tooltip(tooltipOptions);
    }
    return $item
}
/* sorts a map in desc order based on the map values */
function sortByCount(map) {
    // turn it into an array of maps
    var list = [];
    for (var item in map) {
        list.push({facetValue:item, count:map[item]});
    }
    list.sort(function(a, b) {
        return b.count - a.count
    });
    return list;
}

/* returns a display label for the facet */
function labelFor(item) {
    var text = displayText[item];
    if (text == undefined) {
        // just capitalise - TODO: break out camel case
        text = capitalise(item);
    }
    return text;
}
/* capitalises the first letter of the passed string */
function capitalise(item) {
    if (!item) {
        return item;
    }
    if (item.length == 1) {
        return item.toUpperCase();
    }
    return item.substring(0, 1).toUpperCase() + item.substring(1, item.length);
}

/*************************************************\
 *  Download csv of data sets
\*************************************************/
function wireDownloadLink() {
    $('#downloadLink').click(function() {
        var uids = [];
        $.each(resources, function(i,obj) {
            uids.push(obj.uid);
        });
        document.location.href = baseUrl + "/public/downloadDataSets?uids=" + uids.join(',');
        return false;
    });
}

/*************************************************\
 *  Searching
\*************************************************/
function wireSearchLink() {
    $('#dr-search-link').click(function() {
        if ($('#dr-search').val() != "") {
            addFilter('contains',$('#dr-search').val());
        }
    });
    $('#dr-search').keypress(function(event) {
        if (event.which == 13 && $('#dr-search').val() != "") {
            event.preventDefault();
            addFilter('contains',$('#dr-search').val());
        }
    });
}
function extractListOfUidsFromSearchResults(data) {
    var list = [];
    $.each(data.searchResults.results,  function(i, obj) {
        var uri = obj.guid;
        var slash = uri.lastIndexOf('/');
        if (slash > 0) {
            list.push(uri.substr(slash + 1, uri.length - slash));
        }
    });
    return list;
}
/*************************************************\
 *  State handling
 \*************************************************/
/* called on page load to set the initial state */
function setStateFromHash() {
    var hash = $.deparam.fragment(true);
    if (hash.pageSize) {
         $('select#per-page').val(hash.pageSize);
    }
    if (hash.sort) {
        $('select#sort').val(hash.sort);
    }
    if (hash.dir) {
        $('select#dir').val(hash.dir);
    }
    if (hash.filters) {
        deserialiseFilters(hash.filters);
    }
    if (hash.offset && hash.offset < total) {
        offset = hash.offset;
    }
}
/* puts the current filter state into the hash */
function serialiseFiltersToHash() {
    var str = "";
    $.each(currentFilters, function(i, obj) {
        str += obj.name + ":" + obj.value + ";";
    });
    if (str.length == 0) {
        $.bbq.removeState('filters');
    }
    else {
        str = str .substr(0,str.length - 1);
        $.bbq.pushState({filters:str});
    }
}
/* reinstates current filters from the specified hash */
function deserialiseFilters(filters) {
    var list = filters.split(";");
    $.each(list, function(i, obj) {
        var bits = obj.split(':');
        addFilter(bits[0], bits[1]);
    });
}
/* sets the controls for the heart of the sun */
function reset() {
    currentFilters = [];
    resources = allResources;
    offset = 0;
    $('select#per-page').val(20);
    $('select#sort').val('name');
    $('select#dir').val('asc');
    $.bbq.removeState();
    updateTotal();
    calculateFacets();
    showFilters();
    displayPage();
}