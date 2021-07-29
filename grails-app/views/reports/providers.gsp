<%@ page import="au.org.ala.collectory.DataHub; au.org.ala.collectory.DataResource; au.org.ala.collectory.DataProvider" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <title><g:message code="reports.title" /></title>
    </head>
    <body>
        <div class="btn-toolbar">
            <ul class="btn-group">
                <li class="btn btn-default"><cl:homeLink/></li>
                <li class="btn btn-default"><span class="glyphicon glyphicon-th-list"></span><g:link class="list" action="list"> <g:message code="reports.li.reports"/></g:link></li>
            </ul>
        </div>
        <div class="body">
            <h1><g:message code="reports.providers.title01" /></h1>
            <g:if test="${flash.message}">
               <div class="message">${flash.message}</div>
            </g:if>

            <div id="dps">
              <table class="table table-striped table-bordered">
                <colgroup><col width="80%"/><col width="10%"/><col width="10%"/></colgroup>
                <tr class="reportGroupTitle"><td><g:message code="reports.providers.tr0101" /> (${DataProvider.count()})</td><td><g:message code="reports.providers" /></td><td><g:message code="reports.providers.tr0103" /></td></tr>
                <g:each var='c' in="${DataProvider.list([sort: 'name'])}">
                  <tr>
                    <td><g:link controller="public" action="show" id="${c.uid}">${fieldValue(bean: c, field: "name")}</g:link></td>
                    <td><g:link controller="dataProvider" action="show" id="${c.uid}">${c.uid}</g:link></td>
                    <td>${c.resources?.size()}</td>
                  </tr>
                </g:each>
              </table>
            </div>

            <div class="drs">
              <table class="table table-striped table-bordered">
                <colgroup><col width="53%"/><col width="7%"/><col width="30%"/><col width="10%"/></colgroup>
                <tr class="reportGroupTitle"><td><g:message code="reports.providers.tr0201" /> (${DataResource.count()})</td><td><g:message code="reports.providers" /></td><td><g:message code="reports.providers.tr0203" /></td><td><g:message code="reports.providers.tr0204" /></td></tr>
                <g:each var='c' in="${DataResource.list([sort: 'name'])}">
                  <tr>
                    <td><g:link controller="public" action="show" id="${c.uid}">${fieldValue(bean: c, field: "name")}</g:link></td>
                    <td><g:link controller="dataResource" action="show" id="${c.uid}">${c.uid}</g:link></td>
                    <td><cl:ifNotBlank tagName="" value="${c.shortProviderName()}"/></td>
                    <td><cl:acronymOrShortName entity="${c.institution}"/></td>
                  </tr>
                </g:each>
              </table>
            </div>

            <div id="hubs">
              <table>
                <colgroup><col width="70%"/><col width="30%"/></colgroup>
                <tr><td><g:message code="reports.providers.tr0301" /></td><td><g:message code="reports.providers" /></td></tr>
                <tr class="reportGroupTitle"><td colspan="3"><g:message code="reports.providers.tr0401" /> (${DataHub.count()})</td></tr>
                <g:each var='c' in="${DataHub.list([sort: 'name'])}">
                  <tr>
                    <td>${fieldValue(bean: c, field: "name")}</td>
                    <td>${c.uid}</td>
                  </tr>
                </g:each>
              </table>
            </div>
        </div>
    </body>
</html>
