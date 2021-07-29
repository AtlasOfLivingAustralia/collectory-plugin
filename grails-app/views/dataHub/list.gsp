<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="layout" content="${grailsApplication.config.skin.layout}" />
    <g:set var="entityName" value="${entityType}" />
    <g:set var="entityNameLower" value="${cl.controller(type: entityType)}"/>
    <title><g:message code="default.list.label" args="[entityName]" /></title>
</head>
<body>
<div class="btn-toolbar">
    <ul class="btn-group">
        <li class="btn btn-default"><cl:homeLink/></li>
        <li class="btn btn-default"><span class="glyphicon glyphicon-list"></span><g:link class="list" action="list"> <g:message code="default.list.label" args="[entityName]"/></g:link></li>
        <li class="btn btn-default"><span class="glyphicon glyphicon-plus"></span><g:link class="create" action="create"> <g:message code="default.new.label" args="[entityName]"/></g:link></li>
    </ul>
</div>
<div class="body">
    <h1><g:message code="default.list.label" args="[entityName]" /></h1>
    <g:if test="${flash.message}">
        <div class="message">${flash.message}</div>
    </g:if>
    <div class="list">
        <table class="table table-striped table-bordered">
            <thead>
                <g:sortableColumn property="name" title="${message(code: 'dataHub.name.label', default: 'Name')}" />

                <g:sortableColumn property="uid" title="${message(code: 'providerGroup.uid.label', default: 'UID')}" />

                <g:sortableColumn property="acronym" title="${message(code: 'dataHub.acronym.label', default: 'Acronym')}" />
            </thead>
            <tbody>
            <g:each in="${instanceList}" status="i" var="instance">
                <tr >

                    <td><g:link action="show" id="${instance.uid}">${fieldValue(bean: instance, field: "name")}</g:link></td>

                    <td>${fieldValue(bean: instance, field: "uid")}</td>

                    <td>${fieldValue(bean: instance, field: "acronym")}</td>

                </tr>
            </g:each>
            </tbody>
        </table>
    </div>

    <div class="nav">
        <tb:paginate controller="dataHub" action="list" total="${instanceTotal}" />
    </div>
</div>
</body>
</html>
