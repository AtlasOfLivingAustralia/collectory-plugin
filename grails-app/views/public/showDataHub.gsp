<%@ page contentType="text/html;charset=UTF-8" import="org.codehaus.groovy.grails.commons.ConfigurationHolder; au.org.ala.collectory.DataHub"%>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="layout" content="${ConfigurationHolder.config.skin.layout}" />
    <title><cl:pageTitle>${fieldValue(bean: instance, field: "name")}</cl:pageTitle></title>
    <g:javascript src="jquery.fancybox/fancybox/jquery.fancybox-1.3.1.pack.js" />
    <link rel="stylesheet" type="text/css" href="${resource(dir:'js/jquery.fancybox/fancybox',file:'jquery.fancybox-1.3.1.css')}" media="screen" />
    <script type="text/javascript" language="javascript" src="http://www.google.com/jsapi"></script>
    <r:require modules="jquery, fancybox, jquery_jsonp, jstree, jquery_ui_custom, charts, datadumper"/>
    <script type="text/javascript">
      biocacheServicesUrl = "${grailsApplication.config.biocacheServicesUrl}";
      biocacheWebappUrl = "${grailsApplication.config.biocacheUiURL}";

      $(document).ready(function() {
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
      });
    </script>
  </head>
  <body class="two-column-right">
    <div id="content">
      <div id="header" class="collectory">
          <!--Breadcrumbs-->
          <div id="breadcrumb">
              <ol class="breadcrumb">
                  <li><cl:breadcrumbTrail/> <span class=" icon icon-arrow-right"></span></li>
                  <li><cl:pageOptionsLink>${fieldValue(bean:instance,field:'name')}</cl:pageOptionsLink></li>
              </ol>
          </div>
        <cl:pageOptionsPopup instance="${instance}"/>
        <div class="row-fluid">
          <div class="span8">
            <cl:h1 value="${instance.name}"/>
            <cl:valueOrOtherwise value="${instance.acronym}"><span class="acronym"><g:message code="public.sdh.header.span01" />: ${fieldValue(bean: instance, field: "acronym")}</span></cl:valueOrOtherwise>
            <g:if test="${instance.guid?.startsWith('urn:lsid:')}">
              <span class="lsid"><a href="#lsidText" id="lsid" class="local" title="Life Science Identifier (pop-up)"><g:message code="public.lsid" /></a></span>
              <div style="display:none; text-align: left;">
                  <div id="lsidText" style="text-align: left;">
                      <b><a class="external_icon" href="http://lsids.sourceforge.net/" target="_blank"><g:message code="public.lsidtext.link" />:</a></b>
                      <p style="margin: 10px 0;"><cl:guid target="_blank" guid='${fieldValue(bean: instance, field: "guid")}'/></p>
                      <p style="font-size: 12px;"><g:message code="public.lsidtext.des" />. </p>
                  </div>
              </div>
            </g:if>
          </div>
          <div class="span4">
            <!-- logo -->
            <g:if test="${fieldValue(bean: instance, field: 'logoRef') && fieldValue(bean: instance, field: 'logoRef.file')}">
              <img class="institutionImage" src='${resource(absolute:"true", dir:"data/"+instance.urlForm()+"/",file:fieldValue(bean: instance, field: 'logoRef.file'))}' />
            </g:if>
          </div>
        </div>
      </div><!--close header-->

      <div class="row-fluid">

      <div id="column-one" class="span8">
      <div class="section">
        <g:if test="${instance.pubDescription}">
          <h2><g:message code="public.des" /></h2>
          <cl:formattedText>${fieldValue(bean: instance, field: "pubDescription")}</cl:formattedText>
          <cl:formattedText>${fieldValue(bean: instance, field: "techDescription")}</cl:formattedText>
        </g:if>
        <g:if test="${instance.focus}">
          <h2><g:message code="public.sdh.co.label02" /></h2>
          <cl:formattedText>${fieldValue(bean: instance, field: "focus")}</cl:formattedText>
        </g:if>

        <h2><g:message code="public.sdh.co.label03" /></h2>
        <p><g:message code="public.sdh.co.des01" /> <span id="totalRecords"><g:message code="public.usage.des" />...</span> <g:message code="public.sdh.co.des03" />.
            <a href="${grailsApplication.config.biocacheUiURL}/occurrences/search?q=data_hub_uid:${instance.uid}" class="btn"><g:message code="public.sdh.co.allrecords" /></a>
            %{--&nbsp;&nbsp;&nbsp;<button type=button id="showTimings">Show timings</button>--}%
        </p>
        <div id="charts" class="section vertical-charts">
        </div>

        <cl:lastUpdated date="${instance.lastUpdated}"/>

      </div><!--close section-->
    </div><!--close column-one-->
      <div id="column-two" class="span4">
      <div class="section sidebar">
        <g:if test="${fieldValue(bean: instance, field: 'imageRef') && fieldValue(bean: instance, field: 'imageRef.file')}">
          <div class="section">
            <img alt="${fieldValue(bean: instance, field: "imageRef.file")}"
                    src="${resource(absolute:"true", dir:"data/"+instance.urlForm()+"/", file:instance.imageRef.file)}" />
            <cl:formattedText pClass="caption">${fieldValue(bean: instance, field: "imageRef.caption")}</cl:formattedText>
            <cl:valueOrOtherwise value="${instance.imageRef?.attribution}"><p class="caption">${fieldValue(bean: instance, field: "imageRef.attribution")}</p></cl:valueOrOtherwise>
            <cl:valueOrOtherwise value="${instance.imageRef?.copyright}"><p class="caption">${fieldValue(bean: instance, field: "imageRef.copyright")}</p></cl:valueOrOtherwise>
          </div>
        </g:if>

        <div class="section">
          <h3><g:message code="public.location" /></h3>
          <g:if test="${instance.address != null && !instance.address.isEmpty()}">
            <p>
              <cl:valueOrOtherwise value="${instance.address?.street}">${instance.address?.street}<br/></cl:valueOrOtherwise>
              <cl:valueOrOtherwise value="${instance.address?.city}">${instance.address?.city}<br/></cl:valueOrOtherwise>
              <cl:valueOrOtherwise value="${instance.address?.state}">${instance.address?.state}</cl:valueOrOtherwise>
              <cl:valueOrOtherwise value="${instance.address?.postcode}">${instance.address?.postcode}<br/></cl:valueOrOtherwise>
              <cl:valueOrOtherwise value="${instance.address?.country}">${instance.address?.country}<br/></cl:valueOrOtherwise>
            </p>
          </g:if>
          <g:if test="${instance.email}"><cl:emailLink>${fieldValue(bean: instance, field: "email")}</cl:emailLink><br/></g:if>
          <cl:ifNotBlank value='${fieldValue(bean: instance, field: "phone")}'/>
        </div>

        <!-- contacts -->
        <g:render template="contacts" bean="${instance.getPublicContactsPrimaryFirst()}"/>

        <!-- web site -->
        <g:if test="${instance.websiteUrl}">
          <div class="section">
            <h3><g:message code="public.website" /></h3>
            <div class="webSite">
              <a class='external_icon' target="_blank" href="${instance.websiteUrl}"><g:message code="public.sdh.ct.link01" /></a>
            </div>
          </div>
        </g:if>

        <!-- network membership -->
        <g:if test="${instance.networkMembership}">
          <div class="section">
            <h3><g:message code="public.network.membership.label" /></h3>
            <g:if test="${instance.isMemberOf('CHAEC')}">
              <p><g:message code="public.network.membership.des01" /></p>
              <img src="${resource(absolute:"true", dir:"data/network/",file:"butflyyl.gif")}"/>
            </g:if>
            <g:if test="${instance.isMemberOf('CHAH')}">
              <p><g:message code="public.network.membership.des02" /></p>
              <a target="_blank" href="http://www.chah.gov.au"><img src="${resource(absolute:"true", dir:"data/network/",file:"CHAH_logo_col_70px_white.gif")}"/></a>
            </g:if>
            <g:if test="${instance.isMemberOf('CHAFC')}">
              <p><g:message code="public.network.membership.des03" /></p>
            </g:if>
            <g:if test="${instance.isMemberOf('CHACM')}">
              <p><g:message code="public.network.membership.des04" /></p>
              <img src="${resource(absolute:"true", dir:"data/network/",file:"chacm.png")}"/>
            </g:if>
          </div>
        </g:if>
      </div>


    </div><!--close column-two-->

      </div>
  </div><!--close content-->

<script type="text/javascript">
/************************************************************\
* Charts
\************************************************************/
$.ajaxSetup({cache: true});
// configure the charts
var facetChartOptions = {
    backgroundColor: "${grailsApplication.config.chartsBgColour}",
    /* base url of the collectory */
    collectionsUrl: "${grailsApplication.config.grails.serverURL}",
    /* base url of the biocache ws*/
    biocacheServicesUrl: "${grailsApplication.config.biocacheServicesUrl}",
    /* base url of the biocache webapp*/
    biocacheWebappUrl: "${grailsApplication.config.biocacheUiURL}",
    /* support click-thru to records subset - default is true */
    clickThru: true,
    /* a uid or list of uids to chart - either this or query must be present */
    instanceUid: "${instance.uid}",
    /* a query to set the scope of the records */
    //query: 'state:"Tasmania"',
    /* the id of the div to create the charts in - defaults is 'charts' */
    targetDivId: "charts",
    /* the jQuery selector for the element to write the total number of records */
    totalRecordsSelector: "span#totalRecords",
    /* the list of charts to be drawn (these are specified in the one call because a single request can get the data for all of them) */
    charts: ['institution_uid','country','state','species_group','assertions','type_status',
        'biogeographic_region','state_conservation','occurrence_year'],
    /* override default options for individual charts */
    assertions: {width:500, height: 400}
}
var taxonomyChartOptions = {
    backgroundColor: "${grailsApplication.config.chartsBgColour}",
    /* base url of the collectory */
    collectionsUrl: "${grailsApplication.config.grails.serverURL}",
    /* base url of the biocache ws*/
    biocacheServicesUrl: "${grailsApplication.config.biocacheServicesUrl}",
    biocacheWebappUrl: "${grailsApplication.config.biocacheUiURL}",
    /* support click-thru to records subset - default is true */
    clickThru: true,
    /* support drill down into chart - default is false */
    drillDown: true,
    /* a uid or list of uids to chart - either this or query must be present */
    instanceUid: "${instance.uid}",
    /* a query to set the scope of the records */
    //query: 'state:"Tasmania"',
    /* the id of the div to create the charts in - defaults is 'charts' */
    targetDivId: "charts",
    /* threshold value to use for automagic rank selection - defaults to 55 */
    threshold: 55,
    /* taxonomic rank to use for initial breakdown - overrides threshold */
    rank: 'phylum'
    /* taxonomic name to use for initial breakdown - requires rank to be specified */
    //name: 'Aves'
    /* override default options */
}
// load the packages
google.load("visualization", "1", {packages:["corechart"]});
// make it so
google.setOnLoadCallback(function() {
    loadTaxonomyChart(taxonomyChartOptions);
    loadFacetCharts(facetChartOptions);
});

</script>

  </body>
</html>