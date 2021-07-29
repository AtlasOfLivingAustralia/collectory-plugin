<%@ page import="au.org.ala.collectory.Classification; au.org.ala.collectory.Collection" %>
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
            <h1><g:message code="reports.ct.title01" /></h1>
            <p>${Collection.count()} <g:message code="reports.ct.des01" /> <span style="color:#dd3102;"><g:message code="reports.ct.des02" /></span> <g:message code="reports.ct.des03" />.
            <g:message code="reports.ct.des04" />.</p>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
              <table class="table table-striped table-bordered">
                <col width="49%"/><col width="7%"/><col width="7%"/><col width="7%"/><col width="20%"/><col width="10%"/>

                <tr class="reportGroupTitle"><th><g:message code="reports.ct.th.collection" /></th><th><g:message code="reports.ct.th.preserved" /></th><th><g:message code="reports.ct.th.cellcultures" /></th><th><g:message code="reports.ct.th.living" /></th><th><g:message code="reports.ct.th.other" /></th><th><g:message code="reports.ct.th.noun" /></th></tr>
                <g:each var='c' in="${collections}" status="i">
                  <g:set var="types" value="${c.listCollectionTypes()}"/>
                  <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                    <td>
                      <g:if test="${types.size() > 0}">
                        <cl:showOrEdit entity="${c}"/>
                      </g:if>
                      <g:else>
                        <span class="dataWarning"><cl:showOrEdit entity="${c}"/></span>
                      </g:else>
                    </td>
                    <td style="text-align:center;"><cl:tick isTrue="${types.contains('preserved')}"/></td>
                    <td style="text-align:center;"><cl:tick isTrue="${types.contains('cellcultures')}"/></td>
                    <td style="text-align:center;"><cl:tick isTrue="${types.contains('living')}"/></td>
                    <td style="text-align:center;">${(types - ["preserved","cellcultures","living"]).join(', ')}</td>
                    <td><cl:nounForTypes types="${types}"/></td>
                  </tr>
                </g:each>

              </table>
            </div>
        </div>
    </body>
</html>
