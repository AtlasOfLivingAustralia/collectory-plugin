<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="${grailsApplication.config.skin.layout}"/>
    <title><g:message code="public.datasets.title" /> | ${grailsApplication.config.projectName} </title>
    <r:require modules="jquery, fancybox, jquery_jsonp, jquery_ui_custom, jquery_i18n, bbq, collectory, charts_plugin,datasets"/>
  </head>

  <body id="page-datasets" class="nav-datasets">
    <div id="content">
        <div id="header">
        <div class="full-width">
          <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
          </g:if>
          <div>
            <h1>${grailsApplication.config.projectName}  <g:message code="public.datasets.header.title" /></h1>
            <p><g:message code="public.datasets.header.message01" /> ${grailsApplication.config.projectName}, <g:message code="public.datasets.header.message02" />.</p>
            <p><g:message code="public.datasets.header.message03" /> <img style="vertical-align:middle;" src="${resource(dir:'/images/skin',file:'ExpandArrow.png')}"/><g:message code="public.datasets.header.message04" />.</p>
          </div><!--close hrgroup-->
        </div><!--close section-->
      </div><!--close header-->

      <noscript>
          <div class="noscriptmsg">
            <g:message code="public.datasets.noscript.message01" />.
          </div>
      </noscript>

      <div class="collectory-content row">
          <div id="sidebarBoxXXX" class="col-md-3 facets well well-small">
            <div class="sidebar-header">
              <h3><g:message code="public.datasets.sidebar.header" /></h3>
            </div>

            <div id="currentFilterHolder">
            </div>

            <div id="dsFacets">
            </div>
          </div>

          <div id="data-set-list" class="col-md-9">
            <div class="well">
                <div class="row">
                      <div>
                          <span id="resultsReturned"><g:message code="public.datasets.resultsreturned.message01" /> <strong></strong>&nbsp;<g:message code="public.datasets.resultsreturned.message02" />.</span>
                      </div>
                    <form class="form-inline">
                        <div class="form-group ">
                            <input type="text" name="dr-search" id="dr-search" class="form-control" style="width:500px;"/>
                        </div>
                        <button href="javascript:void(0);" title="Only show data sets which contain the search term"
                                id="dr-search-link" class="form-control btn btn-default"><g:message code="public.datasets.drsearch.search" /></button>
                        <button href="javascript:reset()" title="Remove all filters and sorting options" class=" form-control btn btn-default">
                            <g:message code="public.datasets.drsearch.resetlist" />
                        </button>
                        <button href="#" id="downloadLink" class="btn btn-default"
                                title="Download metadata for datasets as a CSV file">
                            <span class="glyphicon glyphicon-download"></span>
                            <g:message code="public.datasets.downloadlink.label" />
                        </button>
                    </form>

                    <div class="pull-right">

                    </div>
                </div>
                <hr/>
                <div id="searchControls">
                  <div id="sortWidgets" class="row">
                      <div class="col-md-4">
                          <label for="per-page"><g:message code="public.datasets.sortwidgets.rpp" /></label>
                          <g:select id="per-page" name="per-page" from="${[10,20,50,100,500]}" value="${pageSize ?: 20}"/>
                      </div>
                      <div class="col-md-4">
                          <label for="sort"><g:message code="public.datasets.sortwidgets.sb" /></label>
                          <g:select id="sort" name="sort" from="${['name','type','license']}"/>
                      </div>
                      <div class="col-md-4">
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
              <div id="navLinks" class="nav"></div>
            </div>
          </div>

    </div><!-- close collectory-content-->

    </div><!--close content-->

  </body>
<r:script type="text/javascript">
      var altMap = true;
      $(document).ready(function() {
        $('#nav-tabs > ul').tabs();
        loadResources("${grailsApplication.config.grails.serverURL}","${grailsApplication.config.biocacheUiURL}");
        $('select#per-page').change(onPageSizeChange);
        $('select#sort').change(onSortChange);
        $('select#dir').change(onDirChange);
      });
</r:script>
</html>