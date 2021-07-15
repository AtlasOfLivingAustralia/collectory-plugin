<%@ page import="grails.converters.JSON; au.org.ala.collectory.ProviderGroup" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <title><g:message code="reports.title" /></title>
        <link rel="stylesheet" href="${resource(dir:'css/smoothness',file:'jquery-ui-1.12.1.min.css')}" type="text/css" media="screen"/>
        <r:require modules="jquery_ui_custom"/>
    </head>
    <body>
        <div class="btn-toolbar">
            <ul class="btn-group">
                <li class="btn btn-default"><cl:homeLink/></li>
                <li class="btn btn-default"><span class="glyphicon glyphicon-th-list"></span><g:link class="list" action="list"> <g:message code="reports.li.reports"/></g:link></li>
            </ul>
        </div>
        <div class="body">
            <h1><g:message code="reports.rights.title01" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
              <p><g:message code="reports.rights.des01" />.</p>
              <p><g:message code="reports.rights.des02" />.</p>
              <p>
                <span id="toggle"><a class="button" href='javascript:showEmpty();'><g:message code="reports.rights.link01" />.</a></span>
                <span id="toggleAll"><a class="button" href='javascript:hideAll();'><g:message code="reports.rights.link02" />.</a></span>
              </p>
              <table class="reports table table-striped table-bordered">
                <colgroup width="3"></colgroup>
                <colgroup width="240"></colgroup>
                <colgroup span="5" class="center"></colgroup>
                <thead>
                    <tr class="reportHeaderRow"><th></th></th><th><g:message code="reports.rights.th01" /></th><th><g:message code="reports.rights.th02" /></th><th class="center"><g:message code="reports.rights.th03" /></th>
                        <th class="center"><g:message code="reports.rights.th04" /></th><th class="center"><g:message code="reports.rights.th05" /></th><th class="center"><g:message code="reports.rights.th06" /></th></tr>
                </thead>
                <tbody>
                    <g:each var='r' in="${resources}">
                      <g:if test="${r.permissionsDocument || r.permissionsDocumentType || r.licenseType}">
                          <g:set var="hide" value=""/>
                      </g:if>
                      <g:else>
                          <g:set var="hide" value="class='noValues'"/>
                      </g:else>

                      <tr ${hide} class="ui-state-active">
                        <td><g:if test="${r.rights || r.permissionsDocument}">
                            <span onclick="toggleParams(this)" class="ui-icon ui-glyphicon-triangle-1-e"> </span>
                        </g:if></td>
                        <td align="left">
                            <g:link controller="dataResource" action="show" id="${r.uid}">
                                ${r.acronym ?: r.name}
                            </g:link>
                        </td>
                        <td>${r.licenseType}</td>
                        <td class="center">${r.licenseVersion}</td>
                        <td class="center"><cl:shortPermissionsDocument url="${r.permissionsDocument}"/></td>
                        <td class="center"><cl:shortPermissionsDocumentType type="${r.permissionsDocumentType}"/></td>
                        <td class="center"><cl:dpaStatus brief="true" filed="${r.filed}" risk="${r.riskAssessment}"/></td>
                      </tr>

                      <g:if test="${r.rights || r.permissionsDocument}">
                          <tr style="display:none;">
                              <td></td>
                              <td colspan="6">
                                <table class="shy">
                                  <g:if test="${r.rights}">
                                    <tr>
                                      <td><g:message code="reports.rights.td.rights" />:</td>
                                      <td>${r.rights}</td>
                                    </tr>
                                  </g:if>
                                  <g:if test="${r.permissionsDocument}">
                                    <tr>
                                      <td><g:message code="reports.rights.td.permissions" />:</td>
                                      <td>${r.permissionsDocument}</td>
                                    </tr>
                                  </g:if>
                                </table>
                              </td>
                          </tr>
                       </g:if>
                    </g:each>
                  </tbody>
              </table>
            </div>
        </div>
        <script type="text/javascript">
            function toggleParams(me) {
                if ($(me).hasClass('ui-icon-triangle-1-e')) {
                    $(me).switchClass('ui-icon-triangle-1-e','ui-icon-triangle-1-s',10);
                    $(me).parent().parent().next().css('display','table-row');
                } else {
                    $(me).switchClass('ui-icon-triangle-1-s','ui-icon-triangle-1-e',10);
                    $(me).parent().parent().next().css('display','none');
                }
            }
            function hideEmpty() {
                $('tr.noValues').css('display', 'none');
                $('#toggle a').attr('href',"javascript:showEmpty();");
                $('#toggle a span').html('Show resources with no license or permissions documents.');
            }
            function showEmpty() {
                $('tr.noValues').removeAttr('style');
                $('#toggle a').attr('href',"javascript:hideEmpty();");
                $('#toggle a span').html('Hide resources with no license or permissions documents. ');
            }
            function hideAll() {
                var all = $('span.ui-icon-triangle-1-s');
                for (var i = 0; i < all.length; i++) {
                    toggleParams(all[i]);
                }
                $('#toggleAll a').attr('href',"javascript:showAll();");
                $('#toggleAll a span').html('Show all details.');
            }
            function showAll() {
                var all = $('span.ui-icon-triangle-1-e');
                for (var i = 0; i < all.length; i++) {
                  var t = all[i];
                    // only if container is visible
                    if ($(t).parent().parent().css('display') != 'none') {
                      toggleParams(t);
                    }
                }
                $('#toggleAll a').attr('href',"javascript:hideAll();");
                $('#toggleAll a span').html('Hide all details.' );
            }
            hideEmpty();
            hideAll();
            $('a.button').button();
        </script>
    </body>
</html>
