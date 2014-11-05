<%@ page import="au.org.ala.collectory.CollectionLocation" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.ala.skin}" />
        <title><g:message code="public.map3.title" /> | ${grailsApplication.config.projectName}</title>
        <script src="https://maps.google.com/maps/api/js?v=3&sensor=true"></script>
        <r:require modules="bigbuttons,bbq,openlayers,map"/>
        <script type="text/javascript">
          var altMap = true;
          var COLLECTIONS_MAP_OPTIONS = {
              serverUrl:   "${grailsApplication.config.grails.serverURL}",
              centreLat:   ${grailsApplication.config.collectionsMap.centreMapLat?:'-28.2'},
              centreLon:   ${grailsApplication.config.collectionsMap.centreMapLon?:'134'},
              defaultZoom: ${grailsApplication.config.collectionsMap.defaultZoom?:'4'}
          }
        </script>
    </head>
    <body id="page-collections-map" onload="initMap(COLLECTIONS_MAP_OPTIONS)">
    <div id="content">
      <div id="header">
        <!--Breadcrumbs-->
        <div id="breadcrumb">
          <ol class="breadcrumb">
              <li><cl:breadcrumbTrail /></li>
          </ol>
        </div>
        <div class="section full-width">
          <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
          </g:if>
          <div class="hrgroup">
            <h1>${grailsApplication.config.regionName}<g:message code="public.map3.header.title" /></h1>
            <p><g:message code="public.map3.header.des01" /> ${grailsApplication.config.projectNameShort} <g:message code="public.map3.header.des02" args="[grailsApplication.config.regionName]"/>.</p>
          </div><!--close hrgroup-->
        </div><!--close section-->
      </div><!--close header-->

      <div class="row-fluid"><!-- wrap map and list-->
        <div class="span4">
          <div class="section">
            <p><g:message code="public.map3.des01" />.</p>
          </div>
          <div class="section filter-buttons">
            <div class="all selected" id="all" onclick="toggleButton(this);return false;">
              <h2><a href=""><g:message code="public.map3.link.allcollections" /><span id="allButtonTotal"><g:message code="public.map3.link.showall" /> <collections></collections></span></a></h2>
            </div>
            <div class="fauna" id="fauna" onclick="toggleButton(this);return false;">
              <h2><a href=""><g:message code="public.map3.link.fauna" /><span style=""><g:message code="public.map3.link.mammals" />.</span></a></h2>
            </div>
            <div class="insects" id="entomology" onclick="toggleButton(this);return false;">
              <h2><a href=""><g:message code="public.map3.link.insect" /><span style=""><g:message code="public.map3.link.insects" />.</span></a></h2>
            </div>
            <div class="microbes" id="microbes" onclick="toggleButton(this);return false;">
              <h2><a href=""><g:message code="public.map3.link.mos" /><span style=""><g:message code="public.map3.link.protists" />.</span></a></h2>
            </div>
            <div class="plants" id="plants" onclick="toggleButton(this);return false;">
              <h2><a href=""><g:message code="public.map3.link.plants" /><span style=""><g:message code="public.map3.link.vascular" />.</span></a></h2>
            </div>
          </div><!--close section-->
          <!--div class="section" style="margin-top:5px;margin-bottom:5px;"><p style="margin-left:8px;padding-bottom:0;color:#666">Note that fauna includes insects.</p></div-->
          <div id="collectionTypesFooter">
            <h4 class="collectionsCount"><span id='numFeatures'></span></h4>
            <h4 class="collectionsCount"><span id='numVisible'></span>
                <br/><span id="numUnMappable"></span>
            </h4>
          </div>

          <div id="adminLink" class="dropdown" style="margin-top:110px;">
              <g:link controller="manage" action="list" style="color:#DDDDDD; margin-top:80px;"><g:message code="public.map3.adminlink" /></g:link>
          </div>
        </div><!--close column-one-->

        <div class="span8" id="map-list-col">
            <div class="tabbable">
                <ul class="nav nav-tabs" id="home-tabs">
                    <li class="active"><a href="#map" data-toggle="tab"><g:message code="public.map3.maplistcol.map" /></a></li>
                    <li><a href="#list" data-toggle="tab"><g:message code="public.map3.maplistcol.list" /></a></li>
                </ul>
            </div>
            <div class="tab-content">
              <div class="tab-pane active" id="map">
              <div  class="map-column">
                <div class="section">
                  <p style="width:100%;padding-bottom:8px;"><g:message code="public.map3.maplistcol.des01" />.</p>
                  <div id="map-container">
                    <div id="map_canvas"></div>
                  </div>
                  <p style="padding-left:150px;"><img style="vertical-align: middle;" src="${resource(dir:'images/map', file:'orange-dot-multiple.png')}" width="20" height="20"/><g:message code="public.map3.maplistcol.des02" />.<br/></p>
                </div><!--close section-->
              </div><!--close column-two-->
            </div><!--close map-->

            <div id="list" class="tab-pane">
              <div  class="list-column">
                <div class="nameList section" id="names">
                  <p><span id="numFilteredCollections"><g:message code="public.map3.maplistcol.des03" /></span>. <g:message code="public.map3.maplistcol.des04" /> <img style="vertical-align:middle" src="${resource(dir:'images/map', file:'nomap.gif')}"/>.</p>
                  <ul id="filtered-list" style="padding-left:15px;">
                    <g:each var="c" in="${collections}" status="i">
                      <li>
                        <g:link controller="public" action="show" id="${c.uid}">${fieldValue(bean: c, field: "name")}</g:link>
                        <g:if test="${!c.canBeMapped()}">
                          <img style="vertical-align:middle" src="${resource(dir:'images/map', file:'nomap.gif')}"/>
                        </g:if>
                      </li>
                    </g:each>
                  </ul>
                </div><!--close nameList-->
              </div><!--close column-one-->
            </div><!--close list-->
        </div><!-- /.tab-content -->
      </div><!--close map/list div-->
    </div><!--close content-->
    </div>
  </body>
</html>