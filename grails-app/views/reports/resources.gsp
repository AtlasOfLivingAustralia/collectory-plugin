<%@ page import="au.org.ala.collectory.DataHub; au.org.ala.collectory.DataResource; au.org.ala.collectory.DataProvider" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="layout" content="${grailsApplication.config.skin.layout}" />
    <title><g:message code="reports.title" /> - <g:message code="reports.resources.title" /></title>
</head>
<body>
<div class="btn-toolbar">
    <ul class="btn-group">
        <li class="btn btn-default"><cl:homeLink/></li>
        <li class="btn btn-default"><span class="glyphicon glyphicon-th-list"></span><g:link class="list" action="list"> <g:message code="reports.li.reports"/></g:link></li>
    </ul>
</div>
<div class="body">
    <h1><g:message code="reports.resources.title01" /></h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>

    <div class="drs">
        <table class="table table-striped table-bordered">
            <colgroup><col width="53%"/><col width="7%"/><col width="40%"/></colgroup>
            <tr class="reportGroupTitle"><td><g:message code="reports.resources.tr0101" /> (${DataResource.count()})</td><td><g:message code="reports.providers" /></td><td><g:message code="reports.resources.tr0103" /></td></tr>
            <g:each var='c' in="${DataResource.list([sort: 'name'])}">
                <tr>
                    <td><g:link controller="public" action="show" id="${c.uid}">${fieldValue(bean: c, field: "name")}</g:link></td>
                    <td><g:link controller="dataResource" action="show" id="${c.uid}">${c.uid}</g:link></td>
                    <td><cl:publicArchiveLink uid="${c.uid}" available="${c.publicArchiveAvailable}"/></td>
                </tr>
            </g:each>
        </table>
    </div>

</div>
</body>
</html>
