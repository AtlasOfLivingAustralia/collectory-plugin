<%@ page import="org.codehaus.groovy.grails.commons.ConfigurationHolder" %>
<!DOCTYPE html>
<html>
  <head>
      <title><g:message code="public.chart.title" default="Chart generator"/></title>
      <link rel="stylesheet" href="${resource(dir:'css',file:'charts.css')}" />
      <g:javascript library="jquery-1.5.1.min"/>
      <script type="text/javascript" language="javascript" src="http://www.google.com/jsapi"></script>
      <script type="text/javascript" src="http://jquery-jsonp.googlecode.com/files/jquery.jsonp-2.1.4.min.js"></script>
      %{--<script type="text/javascript" src="http://collections.ala.org.au/js/charts.js"></script>--}%
      <g:javascript library="charts2"/>
      <g:javascript library="datadumper"/>
      <style>
          input[type=radio] {margin-left: 15px;}
          h1 {color: #718804 }
      </style>
  </head>
  <body style="padding: 20px;">
    <h1><g:message code="public.chart.body.title01" default="Chart sampler"/></h1>
    <h2><g:message code="public.chart.body.title02" default="Enter a query and choose a chart type"/></h2>
    <label for="query"><g:message code="public.chart.label.query" default="Query"/>:</label> <input id="query" type="text" size="80" value="Macropus"/>
    <button type="button" id="draw"><g:message code="public.chart.button.drawchart" default="Draw chart"/></button>
    <div style="margin: 20px 0;">
        <div style="padding-right: 10px; float:left; height:120px;"><g:message code="public.chart.label.type" default="Type"/>:</div>
        <div id="types" style="display:inline; max-width:600px;">
            <div style="padding-bottom:10px;">
                <g:radio name="type" value="taxonomy"/> <g:message code="public.chart.label.taxonomy" default="Taxonomy"/>
                (<g:message code="public.chart.label.optional" default="optional"/>: - <label for="rank"><g:message code="public.chart.label.sr" default="starting rank"/>:</label> <input id="rank" type="text" size="20"/> <g:message code="public.chart.label.or" default="OR"/>
                <label for="max"><g:message code="public.chart.label.threshold" default="threshold"/>:</label> <input id="max" type="text" size="20"/>)
            </div>
            <div style="padding-bottom: 8px;">
                <g:radio name="type" value="state"/> <g:message code="public.chart.radio.state" default="State"/>
                <g:radio name="type" value="institution_uid" checked="checked"/> <g:message code="public.chart.radio.institution" default="Institution"/>
                <g:radio name="type" value="data_resource_uid"/> <g:message code="public.chart.radio.dataset" default="Data set"/>
                <g:radio name="type" value="type_status"/> <g:message code="public.chart.radio.types" default="Types"/>
                <g:radio name="type" value="species_group"/> <g:message code="public.chart.radio.cgs" default="Common groups"/>
            </div>
            <div style="padding-bottom: 8px;">
                <g:radio name="type" value="assertions"/> <g:message code="public.chart.radio.das" default="Data assertions"/>
                <g:radio name="type" value="occurrence_year"/> <g:message code="public.chart.radio.decades" default="Decades"/>
                <g:radio name="type" value="biogeographic_region"/> <g:message code="public.chart.radio.bggr" default="Biogeographic region"/>
                <g:radio name="type" value="state_conservation"/> <g:message code="public.chart.radio.sc" default="State conservation"/>
            </div>
            <div style="padding-bottom: 8px;">
                <g:radio name="type" value="other"/> <g:message code="public.chart.radio.onf" default="Other named facet"/>:
                <label for="other"></label> <input id="other" type="text" size="40"/>
            </div>
            <div style="padding-bottom: 8px;">
                <g:radio name="type" value="el"/> <g:message code="public.chart.radio.el" default="Environmental layer"/>:
                <label for="el"></label> <input id="el" type="text" size="40" value="radiation"/>
            </div>
        </div>
    </div>

    <div id="charts"></div>

    <script type="text/javascript">
        var biocacheServicesUrl = "${grailsApplication.config.biocacheServicesUrl}";
        var biocacheWebappUrl = "${grailsApplication.config.biocacheUiURL}";
        var taxonomyChartOptions = { rank: "kingdom", error: "badQuery" }
        var facetChartOptions = { error: "badQuery" }
        $('#draw').click(drawChart);
        $('body').keypress(function(event) {
            if (event.which == 13) {
                event.preventDefault();
                drawChart();
            }
        });

        function drawChart() {
            var facetName;
            $('#charts').html("");
            var query = $('#query').val();
            var type = $('#types input:checked').val();
            if (type == "taxonomy") {
                taxonomyChartOptions.query = query;
                taxonomyChartOptions.rank = $('#rank').val();
                if (taxonomyChartOptions.rank == "") {
                    taxonomyChartOptions.threshold = $('#max').val();
                } else {
                    taxonomyChartOptions.threshold = "";
                }
                taxonomyChart.load(taxonomyChartOptions);
            }
            else if (type == "other") {
                facetChartOptions.query = query;
                facetName = $('#other').val();
                if (facetName == "") {
                    alert("You must enter the name of the facet you wish to chart.");
                    return;
                }
                facetChartOptions.charts = [facetName];
                facetChartGroup.loadAndDrawFacetCharts(facetChartOptions);
            }
            else if (type == "el") {
                facetName = $('#el').val();
                if (facetName == "") {
                    alert("You must enter the name of the environmental layer you wish to chart.");
                    return;
                }
                facetChartOptions.query = query;
                facetChartOptions.charts = [facetName];
                facetChartOptions[facetName] = {chartType: 'scatter'};
                facetChartGroup.loadAndDrawFacetCharts(facetChartOptions);
            }
            else {
                facetChartOptions.query = query;
                facetChartOptions.charts = [type];
                facetChartGroup.loadAndDrawFacetCharts(facetChartOptions);
            }
        }

        function badQuery() {
            $('#charts').append($('<span>Bad query</span>'));
        }
        google.load("visualization", "1", {packages:["corechart"]});
        google.setOnLoadCallback(function() {
            // if there is already text in the query box (eg the browser has inserted it from history), draw the chart
            if ($('#query').val() != "") {
                drawChart();
            }
        });
//lsid:urn:lsid:biodiversity.org.au:afd.taxon:aa745ff0-c776-4d0e-851d-369ba0e6f537&facets=el895
    </script>
  </body>
</html>