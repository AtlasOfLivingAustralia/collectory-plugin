<%@ page import="au.org.ala.collectory.Collection" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <g:set var="entityName" value="${message(code: 'collection.label', default: 'Collection')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <ul>
            <li><span class="menuButton"><cl:homeLink/></span></li>
            <li><span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span></li>
            <li><span class="menuButton"><g:link class="list" action="myList"><g:message code="default.myList.label" args="[entityName]" /></g:link></span></li>
            <li><span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span></li>
            </ul>
        </div>
        <div class="body content">
            <h1><g:message code="collection.searchList.label" args="${criteria}" default="Collections" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:render template="collectionList"
                    model="[collectionInstanceList: providerGroupInstanceList]"
                    params="[term: '${term}']"/>
            <div class="nav">
                <tb:paginate controller="collection" action="searchList" params="${[term:term]}" total="${providerGroupInstanceTotal}" />
            </div>
        </div>
    </body>
</html>