<%@ page import="grails.converters.JSON; au.org.ala.collectory.Classification; au.org.ala.collectory.Collection" %>
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
            <h1><g:message code="reports.th.title01" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
              <table class="table table-striped table-bordered">
                <col width="55%"/><col width ="5%"/><col width="40%"/>

                <tr class="reportGroupTitle"><th><g:message code="reports.th.th01" /></th><th><g:message code="reports.th.th02" /></th><th><g:message code="reports.th.th03" /></th></tr>
                <g:each var='c' in="${collections}" status="i">
                  <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                    <td>
                      <cl:showOrEdit entity="${c}"/>
                    </td>
                    <td style="text-align:center;color:gray;">${c.acronym}</td>
                    <td>
                      <g:if test="${c.taxonomyHints}">
                        <g:set var="hints" value="${JSON.parse(c.taxonomyHints)?.coverage}"/>
                          <g:each var="h" in="${hints}">
                            ${h.keySet().iterator().next()} = ${h[h.keySet().iterator().next()]}<br/>
                          </g:each>
                      </g:if>
                    </td>
                  </tr>
                </g:each>

              </table>
            </div>
        </div>
    </body>
</html>
