<%@ page import="au.org.ala.collectory.Collection" %>
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
            <h1><g:message code="reports.codes.title01" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
              <table class="table table-striped table-bordered">
                <colgroup><col width="40%"/><col width="10%"/><col width="50%"/></colgroup>

                <tr class="reportGroupTitle"><td><g:message code="reports.codes.td01" /></td><td colspan="2">${Collection.count()} <g:message code="reports.codes.td02" />.</td></tr>
                <g:each var='c' in="${codeSummaries}">
                  <tr><td><g:link controller="public" action="show" id="${c.uid}" fragment="statistics">${c.name}</g:link></td><td>${c.derivedInstCodes.join(',')}</td><td>${c.derivedCollCodes.join(',')}</td></tr>
                </g:each>

              </table>
            </div>
        </div>
    </body>
</html>
