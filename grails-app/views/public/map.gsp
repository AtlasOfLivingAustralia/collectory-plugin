<%@ page import="org.codehaus.groovy.grails.commons.ConfigurationHolder; au.org.ala.collectory.CollectionLocation" %>
<html>
    <head>
        <!-- this is not the current version - use map3 -->
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <title><g:message code="public.map.title01" /> | <g:message code="public.map.title02" /></title>
        <script src="https://maps.google.com/maps/api/js?v=3&sensor=true"></script>
        <script type="text/javascript" src="${resource(dir:'js', file:'map.js')}"></script>
        <script type="text/javascript">
          $(document).ready(function() {
            $('#nav-tabs > ul').tabs();
            greyInitialValues();
          });
        </script>
    </head>
    <body id="page-collections-map" onload="initMap('${grailsApplication.config.grails.serverURL}')">
    <div id="content">
      <div id="header">
        <!--Breadcrumbs-->
        <div id="breadcrumb"><a href="${grailsApplication.config.ala.baseURL}"><g:message code="public.map.breadcrumb.home" /></a> <a href="${ConfigurationHolder.config.ala.baseURL}/explore/"><g:message code="public.map.breadcrumb.explore" /></a><span class="current"><g:message code="public.map.breadcrumb.des" /></span></div>
        <div class="section full-width">
          <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
          </g:if>
          <div class="hrgroup">
            <h1><g:message code="public.map.header.title" /></h1>
            <p><g:message code="public.map.header.des" />.</p>
          </div><!--close hrgroup-->
        </div><!--close section-->
      </div><!--close header-->

      <div id="nav-tabs">
          <ul class="ui-tabs-nav">
              <li><a href="#map">Map</a></li>
              <li><a href="#list">List</a></li>
          </ul>
      </div>

      <div class="row-fluid"><!-- wrap map and list-->
        <div id="span6" class="fudge">
          <div class="section">
            <p style="padding:15px 10px 0 10px"><g:message code="public.map.label01" />:</p>
            <ul id="map-collections">
              <li><input id="all" name="all" type="checkbox" value="all" checked="checked" onclick="setAll();"/><label for="all"><g:message code="public.map.selectall" /></label>
                <ul class="taxaBreakdown">
                  <li id="herbariaBreakdown"><input style="margin-right:8px;" onclick="filterChange()" name="filter" type="checkbox" checked="checked" value="plants" id="plants"/><label for="plants"><g:message code="public.map.herbaria" /></label></li>
                  <li id="faunaBreakdown"><input style="margin-right:8px;" onclick="filterChange()" name="filter" type="checkbox" checked="checked" value="fauna" id="fauna"/><label for="fauna"><g:message code="public.map.fc" /></label>
                    <ul>
                      <li id="entoBreakdown"><input style="margin-right:8px;" onclick="entoChange()" name="filter" type="checkbox" checked="checked" value="entomology" id="ento"/><label for="ento"><g:message code="public.map.ec" /></label></li>
                    </ul>
                  </li>
                  <li id="microbialBreakdown"><input style="margin-right:8px;" onclick="filterChange()" name="filter" type="checkbox" checked="checked" value="microbes" id="microbes"/><label for="microbes"><g:message code="public.map.mc" /></label></li>
                </ul>
              </li>
            </ul>
            <div style="width:235px;margin-left:0;">
              <p class="collectionsCount"><span id='numFeatures'></span></p>
              <p class="collectionsCount"><span id='numVisible'></span> <span id="numUnMappable"></span></p>
            </div>
          </div><!--close section-->
        </div><!--close column-one-->
        <div id="map" class="span6">
          <div class="map-column">
            <div class="section">
              <p style="width:588px;padding-bottom:8px;padding-left:30px;"><g:message code="public.map.map.des01" />.</p>
              <div id="map-container">
                <div id="map_canvas"></div>
              </div>
              <p style="padding-left:150px;"><img style="vertical-align: middle;" src="${resource(dir:'images/map', file:'orange-dot-multiple.png')}" width="20" height="20"/><g:message code="public.map.map.des02" />.<br/></p>
            </div><!--close section-->
          </div><!--close column-two-->
        </div><!--close map-->
        <div id="list" class="span6">
          <div class="list-column">
            <div class="nameList section" id="names">
              <p><g:message code="public.map.list.des01" />.</p>
              <ul id="filtered-list">
              </ul>
              <p><g:message code="public.map.list.des02" /> <img style="vertical-align:middle" src="${resource(dir:'images/map', file:'nomap.gif')}"/>.</p>
            </div><!--close nameList-->
          </div><!--close column-one-->
        </div><!--close list-->
      </div><!--close map/list div-->
    </div><!--close content-->
  </body>
</html>