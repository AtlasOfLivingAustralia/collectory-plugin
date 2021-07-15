
<%@ page import="au.org.ala.collectory.ProviderGroup; au.org.ala.collectory.DataLink" %>
<html>
    <head>

        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <g:set var="entityName" value="${message(code: 'dataLink.label', default: 'DataLink')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="btn-toolbar">
            <ul class="btn-group">
                <li class="btn btn-default"><cl:homeLink/></li>
                <li class="btn btn-default"><span class="glyphicon glyphicon-plus"></span><g:link class="create" action="create"> <g:message code="default.new.label" args="[entityName]"/></g:link></li>
                <g:if test="${returnTo}"><li><cl:returnLink uid="${returnTo}"/></li></g:if>
            </ul>
        </div>
        <div class="body">
          <g:set var="filter" value="${consumer ? consumer : provider}"/>
          <g:if test="${filter}">
            <h1>DataLinks for ${ProviderGroup._get(filter).name}</h1>
          </g:if>
          <g:else>
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
          </g:else>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                            <g:sortableColumn property="id" title="${message(code: 'dataLink.id.label', default: 'Id')}" />
                        
                            <g:sortableColumn property="consumer" title="${message(code: 'dataLink.consumer.label', default: 'Consumer')}" />
                        
                            <g:sortableColumn property="provider" title="${message(code: 'dataLink.provider.label', default: 'Provider')}" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${dataLinkInstanceList}" status="i" var="dataLinkInstance">
                        <g:set var="provider" value="${ProviderGroup._get(dataLinkInstance.provider)}"/>
                        <g:set var="consumer" value="${ProviderGroup._get(dataLinkInstance.consumer)}"/>
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${dataLinkInstance.id}" params="[returnTo: returnTo]">${fieldValue(bean: dataLinkInstance, field: "id")}</g:link></td>
                        
                            <td>${fieldValue(bean: consumer, field: "name")}</td>
                        
                            <td>${fieldValue(bean: provider, field: "name")}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="nav">
                <tb:paginate controller="dataLink" action="list" total="${dataLinkInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
