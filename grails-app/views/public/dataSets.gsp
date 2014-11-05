<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="${grailsApplication.config.ala.skin}"/>
    <title><g:message code="public.datasets.title" /> | ${grailsApplication.config.projectName} </title>
    <r:require modules="datasets, jquery_json, bbq, rotate, jquery_tools"/>
    <script type="text/javascript">
      var altMap = true;
      $(document).ready(function() {
        $('#nav-tabs > ul').tabs();
        loadResources("${grailsApplication.config.grails.serverURL}","${grailsApplication.config.biocacheUiURL}");
        $('select#per-page').change(onPageSizeChange);
        $('select#sort').change(onSortChange);
        $('select#dir').change(onDirChange);
      });
    </script>
  </head>

  <body id="page-datasets" class="nav-datasets">
    <div id="content">
        <div id="header">
        <!--Breadcrumbs-->
        <div id="breadcrumb">
            <ol class="breadcrumb">
                <li><cl:breadcrumbTrail home="dataSets" atBase="true"/></li>
            </ol>
        </div>
        <div class="full-width">
          <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
          </g:if>
          <div>
            <h1>${grailsApplication.config.projectName}  <g:message code="public.datasets.header.title" /></h1>
            <p style="padding-bottom:6px !important;"><g:message code="public.datasets.header.message01" /> ${grailsApplication.config.projectName}, <g:message code="public.datasets.header.message02" />.</p>
            <p><g:message code="public.datasets.header.message03" /> <img style="vertical-align:middle;" src="${resource(dir:'/images/skin',file:'ExpandArrow.png')}"/><g:message code="public.datasets.header.message04" />.</p>
          </div><!--close hrgroup-->
        </div><!--close section-->
      </div><!--close header-->

      <noscript>
          <div class="noscriptmsg">
            <g:message code="public.datasets.noscript.message01" />.
          </div>
      </noscript>

      <div class="collectory-content row-fluid">
          <div id="sidebarBoxXXX" class="span3 facets well well-small">
            <div class="sidebar-header">
              <h3><g:message code="public.datasets.sidebar.header" /></h3>
            </div>

            <div id="currentFilterHolder">
            </div>

            <div id="dsFacets">
            </div>
          </div>

          <div id="data-set-list" class="span9">
            <div class="well">
                <div class="row-fluid">
                      <div class="pull-left">
                          <span id="resultsReturned"><g:message code="public.datasets.resultsreturned.message01" /> <strong></strong>&nbsp;<g:message code="public.datasets.resultsreturned.message02" />.</span>
                          <div class="input-append">
                              <input type="text" name="dr-search" id="dr-search"/>
                              <a href="javascript:void(0);" title="Only show data sets which contain the search term" id="dr-search-link" class="btn"><g:message code="public.datasets.drsearch.search" /></a>
                              <a href="javascript:void(0);" id="reset"><a href="javascript:reset()" title="Remove all filters and sorting options" class="btn"><g:message code="public.datasets.drsearch.resetlist" /></a></a>
                          </div>
                     </div>
                    <div class="pull-right">
                      <a href="#" id="downloadLink" class="btn"
                           title="Download metadata for datasets as a CSV file">
                            <i class="icon-download"></i>
                            <g:message code="public.datasets.downloadlink.label" /></a>
                    </div>
                </div>
                <hr/>
                <div id="searchControls">
                  <div id="sortWidgets" class="row-fluid">
                      <div class="span4">
                          <label for="per-page"><g:message code="public.datasets.sortwidgets.rpp" /></label>
                          <g:select id="per-page" name="per-page" from="${[10,20,50,100,500]}" value="${pageSize ?: 20}"/>
                      </div>
                      <div class="span4">
                          <label for="sort"><g:message code="public.datasets.sortwidgets.sb" /></label>
                          <g:select id="sort" name="sort" from="${['name','type','license']}"/>
                      </div>
                      <div class="span4">
                          <label for="dir"><g:message code="public.datasets.sortwidgets.so" /></label>
                          <g:select id="dir" name="dir" from="${['ascending','descending']}"/>
                      </div>
                  </div>
                </div><!--drop downs-->
            </div>

            <div id="results">
              <div id="loading"><g:message code="public.datasets.loading" /> ..</div>
            </div>

            <div id="searchNavBar" class="clearfix">
              <div id="navLinks"></div>
            </div>
          </div>

    </div><!-- close collectory-content-->

    </div><!--close content-->

  </body>
</html>