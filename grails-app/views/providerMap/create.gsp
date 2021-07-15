<%@ page import="au.org.ala.collectory.ProviderMap" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <g:set var="entityName" value="${message(code: 'providerMap.label', default: 'ProviderMap')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="btn-toolbar">
            <ul class="btn-group">
                <li class="btn btn-default"><cl:homeLink/></li>
            </ul>
        </div>
        <div class="body">
            <h1><g:message code="default.create.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${providerMapInstance}">
            <div class="errors">
                <g:renderErrors bean="${providerMapInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:render template="form"/>
        </div>

    </body>
</html>
