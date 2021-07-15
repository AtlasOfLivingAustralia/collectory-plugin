<%@ page import="grails.converters.JSON; au.org.ala.collectory.ProviderGroup" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <title><g:message code="reports.title" /></title>
        <link rel="stylesheet" href="${resource(dir:'css/smoothness',file:'jquery-ui-1.12.1.min.css')}" type="text/css" media="screen"/>
        <r:require modules="jquery_ui_custom"></r:require>
    </head>
    <body>
        <div class="btn-toolbar">
            <ul class="btn-group">
                <li class="btn btn-default"><cl:homeLink/></li>
                <li class="btn btn-default"><span class="glyphicon glyphicon-th-list"></span><g:link class="list" action="list"> <g:message code="reports.li.reports"/></g:link></li>
            </ul>
        </div>
        <div class="body">
            <h1><g:message code="reports.harvesters.title01" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
              <p><g:message code="reports.harvesters.des01" />.</p>
              <p><g:message code="reports.harvesters.des02" />.</p>
              <p>
                <span id="toggle"><a class="button" href='javascript:showEmpty();'><g:message code="reports.harvesters.des03" />.</a></span>
                <span id="toggleAll"><a class="button" href='javascript:hideAll();'><g:message code="reports.harvesters.des04" />.</a></span>
              </p>
              <table class="reports table table-striped table-bordered">
                <colgroup width="3"></colgroup>
                <colgroup width="250"></colgroup>
                <colgroup span="5" class="center"></colgroup>
                <thead>
                    <tr class="reportHeaderRow"><th></th></th><th><g:message code="reports.harvesters.th.resource" /></th><th><g:message code="reports.harvesters.th.status" /></th><th class="center"><g:message code="reports.harvesters.th.freq" /></th><th class="center"><g:message code="reports.harvesters.th.lastchecked" /></th><th class="center"><g:message code="reports.harvesters.th.datacurrency" /></th><th class="center"><g:message code="reports.harvesters.th.collection" /></th></tr>
                </thead>
                <tbody>
                    <g:each var='r' in="${resources}">
                      <g:set var="pList" value="${r.connectionParameters ? JSON.parse(r.connectionParameters) : [:]}"/>
                      <g:if test="${r.harvestFrequency || r.lastChecked || r.dataCurrency || r.connectionParameters}">
                          <g:set var="hide" value=""/>
                      </g:if>
                      <g:else>
                          <g:set var="hide" value="class='noValues'"/>
                      </g:else>

                      <tr ${hide} class="ui-state-active">
                        <td><g:if test="${r.connectionParameters}">
                            <span onclick="toggleParams(this)" class="ui-icon ui-glyphicon-triangle-1-e"> </span>
                        </g:if></td>
                        <td align="left">
                            <g:link controller="dataResource" action="show" id="${r.uid}">
                                ${r.acronym ?: r.name}
                            </g:link>
                        </td>
                        <td>${r.status}</td>
                        <td class="center">${r.harvestFrequency}</td>
                        <td class="center">${r.lastChecked}</td>
                        <td class="center">${r.dataCurrency}</td>
                        <td class="center">${pList.protocol}</td>
                      </tr>

                      <g:if test="${r.connectionParameters || r.defaultDarwinCoreValues}">
                          <tr style="display:none;">
                              <td></td>
                              <td colspan="6">
                                  <table>
                                      <g:if test="${r.connectionParameters}">
                                          <tr><td colspan="2"><b>Connection parameters</b></td></tr>
                                      </g:if>
                                      <g:each in="${pList}" var="p">
                                          <g:if test="${p.key != 'protocol'}">
                                              <tr>
                                                  <td>${p.key} =</td>
                                                  <td>
                                                    <g:if test="${p.key == 'keywords'}">
                                                        ${p.value.tokenize(',').join(', ')}
                                                    </g:if>
                                                    <g:elseif test="${p.key == 'termsForUniqueKey'}">
                                                        ${p.value.join(', ')}
                                                    </g:elseif>
                                                    <g:else>
                                                        ${p.value}
                                                    </g:else>
                                                  </td>
                                              </tr>
                                          </g:if>
                                      </g:each>
                                      <g:if test="${r.defaultDarwinCoreValues}">
                                          <tr><td colspan="2"><b>Defaults for DwC terms</b></td></tr>
                                      </g:if>
                                      <g:set var="dwcList" value="${r.defaultDarwinCoreValues ? JSON.parse(r.defaultDarwinCoreValues) : [:]}"/>
                                      <g:each in="${dwcList}" var="p">
                                          <tr>
                                              <td>${p.key} =</td>
                                              <td>${p.value}</td>
                                          </tr>
                                      </g:each>
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
                $('#toggle a span').html('Show resources with no mobilisation values.');
            }
            function showEmpty() {
                $('tr.noValues').removeAttr('style');
                $('#toggle a').attr('href',"javascript:hideEmpty();");
                $('#toggle a span').html('Hide resources with no mobilisation values. ');
            }
            function hideAll() {
                var all = $('span.ui-icon-triangle-1-s');
                for (var i = 0; i < all.length; i++) {
                    toggleParams(all[i]);
                }
                $('#toggleAll a').attr('href',"javascript:showAll();");
                $('#toggleAll a span').html('Show all connection parameters.');
            }
            function showAll() {
                var all = $('span.ui-icon-triangle-1-e');
                for (var i = 0; i < all.length; i++) {
                    toggleParams(all[i]);
                }
                $('#toggleAll a').attr('href',"javascript:hideAll();");
                $('#toggleAll a span').html('Hide all connection parameters.' );
            }
            hideEmpty();
            hideAll();
            $('a.button').button();
        </script>
    </body>
</html>
