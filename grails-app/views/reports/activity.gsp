<%@ page import="au.org.ala.collectory.ReportsController.ReportCommand" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <title><g:message code="reports.activity.title" /></title>
    </head>
    <body>
        <div class="btn-toolbar">
            <ul class="btn-group">
                <li class="btn btn-default"><cl:homeLink/></li>
                <li class="btn btn-default"><span class="glyphicon glyphicon-th-list"></span><g:link class="list" action="list"> <g:message code="reports.li.reports"/></g:link></li>
            </ul>
        </div>
        <div class="body">
            <h1><g:message code="reports.activity.body.title" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
              <h3><g:message code="reports.activity.body.subtitle" /></h3>
              <table class="table table-striped table-bordered">
                <colgroup><col width="30%"/><col width="70%"/></colgroup>
                <tr><td><g:message code="reports.activity.td01" /></td><td>${reports.curatorViews}</td></tr>
                <tr><td><g:message code="reports.activity.td02" /></td><td>${reports.curatorPreviews}</td></tr>
                <tr><td><g:message code="reports.activity.td03" /></td><td>${reports.curatorEdits}</td></tr>
                <tr><td><g:message code="reports.activity.td04" /></td><td>${reports.adminViews}</td></tr>
                <tr><td><g:message code="reports.activity.td05" /></td><td>${reports.adminPreviews}</td></tr>
                <tr><td><g:message code="reports.activity.td06" /></td><td>${reports.adminEdits}</td></tr>
                <tr><td><g:message code="reports.activity.td07" /></td><td></td></tr>
                <g:each var='l' in='${reports.latestActivity}'>
                  <tr><td colspan="2">${l.toString()}</td></tr>
                </g:each>

              </table>
            </div>
        </div>
    </body>
</html>
