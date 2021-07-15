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
            <h1><g:message code="reports.classification.title01" /></h1>
            <p>${Collection.count()} <g:message code="reports.classification.des01" /> <span style="color:red;"><g:message code="reports.classification.des02" /></span> <g:message code="reports.classification.des03" />.</p>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
              <table class="table table-striped table-bordered">
                <col width="55%"/><col width="9%"/><col width="9%"/><col width="9%"/><col width="9%"/><col width="9%"/>

                <tr class="reportGroupTitle"><th><g:message code="reports.classification.th.collection" /></th><th><g:message code="reports.classification.th.acronym" /></th><th><g:message code="reports.classification.th.herbaria" /></th><th><g:message code="reports.classification.th.fauna" /></th><th><g:message code="reports.classification.th.ento" /></th><th><g:message code="reports.classification.th.microbes" /></th></tr>
                <g:each var='c' in="${collections}" status="i">
                  <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                    <td>
                      <g:if test="${Classification.matchKeywords(c.keywords, 'plants,fauna,entomology,microbes')}">
                        <cl:showOrEdit entity="${c}"/>
                      </g:if>
                      <g:else>
                        <span class="dataWarning"><cl:showOrEdit entity="${c}"/></span>
                      </g:else>
                    </td>
                    <td style="text-align:center;color:gray;">${c.acronym}</td>
                    <td style="text-align:center;"><cl:reportClassification keywords="${c.keywords}" filter="plants"/></td>
                    <td style="text-align:center;"><cl:reportClassification keywords="${c.keywords}" filter="fauna"/></td>
                    <td style="text-align:center;"><cl:reportClassification keywords="${c.keywords}" filter="entomology"/></td>
                    <td style="text-align:center;"><cl:reportClassification keywords="${c.keywords}" filter="microbes"/></td>
                  </tr>
                </g:each>
                <tr>
                  <td><g:message code="reports.classification.tr.totals" /></td><td></td><td>${plants}</td><td>${fauna}</td><td>${entomology}</td><td>${microbes}</td>
                </tr>

              </table>
            </div>
        </div>
    </body>
</html>
