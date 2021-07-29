<%@ page import="au.org.ala.collectory.ProviderMap" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <g:set var="entityName" value="${message(code: 'providerMap.label', default: 'ProviderMap')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="btn-toolbar">
            <ul class="btn-group">
                <li class="btn btn-default"><cl:homeLink/></li>
                <li class="btn btn-default"><span class="glyphicon glyphicon-plus"></span><g:link class="create" action="create" params="[returnTo: returnTo]"> <g:message code="default.new.label" args="[entityName]"/></g:link></li>
                <g:if test="${returnTo}"><span class="menuButton"><cl:returnLink uid="${returnTo}"/></span></g:if>
            </ul>
        </div>
        <div class="body content">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message alert alert-warning">${flash.message}</div>
            </g:if>
            <div class="list">
                <table class="table table-bordered table-striped">
                    <thead>
                        <tr>
                        
                            <g:sortableColumn property="id" title="${message(code: 'providerMap.id.label', default: 'Id')}" />

                            <g:sortableColumn property="institution" title="${message(code: 'institution.label', default: 'Institution')}" />

                            <g:sortableColumn property="collectionName" title="${message(code: 'collection.label', default: 'Collection')}" />

                            <g:sortableColumn property="exact" title="${message(code: 'providerMap.exact.label', default: 'Exact')}" />
                        
                            <g:sortableColumn property="matchAnyCollectionCode" title="${message(code: 'providerMap.matchAnyCollectionCode.label', default: 'Match Any Collection Code')}" />
                        
                            <th>Institution Codes</th>
                        
                            <th>Collection Codes</th>
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${providerMapInstanceList}" status="i" var="providerMapInstance">
                        <tr>
                        
                            <td><g:link action="show" id="${providerMapInstance.id}" params="[returnTo: returnTo]">${fieldValue(bean: providerMapInstance, field: "id")}</g:link></td>

                            <td>${fieldValue(bean: providerMapInstance, field: "institution")}</td>

                            <td>${fieldValue(bean: providerMapInstance, field: "collection")}</td>
                        
                            <td><g:formatBoolean boolean="${providerMapInstance.exact}" /></td>
                        
                            <td><g:formatBoolean boolean="${providerMapInstance.matchAnyCollectionCode}" /></td>
                        
                            <td>${providerMapInstance.getInstitutionCodes().join(' ')}</td>
                        
                            <td>${providerMapInstance.getCollectionCodes().join(' ')}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="nav">
                <tb:paginate controller="providerMap" action="list" total="${providerMapInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
