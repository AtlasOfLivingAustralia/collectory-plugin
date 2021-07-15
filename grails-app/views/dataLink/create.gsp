<%@ page import="au.org.ala.collectory.Collection; au.org.ala.collectory.Institution; au.org.ala.collectory.DataProvider; au.org.ala.collectory.DataResource; au.org.ala.collectory.DataLink" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <g:set var="entityName" value="${message(code: 'dataLink.label', default: 'DataLink')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="btn-toolbar">
            <ul class="btn-group">
                <li class="btn btn-default"><cl:homeLink/></li>
                <li class="btn btn-default"><span class="glyphicon glyphicon-list"></span><g:link class="list" action="list"> <g:message code="default.list.label" args="[entityName]"/></g:link></li>
                <g:if test="${returnTo}"><li><cl:returnLink uid="${returnTo}"/></li></g:if>
            </ul>
        </div>
        <div class="body">
            <h1><g:message code="default.create.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${dataLinkInstance}">
            <div class="errors">
                <g:renderErrors bean="${dataLinkInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save" >
                <g:hiddenField name="returnTo" value="${returnTo}"/>
                <div class="form-group ${hasErrors(bean: dataLinkInstance, field: 'consumer', 'errors')}">
                    <label for="consumer"><g:message code="dataLink.consumer.label" default="Consumer" /></label>
                    <g:select class="form-control" from="${Collection.list([sort:'name']) + Institution.list([sort:'name'])}" optionKey="uid" name="consumer" value="${dataLinkInstance?.consumer}" noSelection="['':'Choose a collection or Institution']"/>

                </div>
                <div class="form-group ${hasErrors(bean: dataLinkInstance, field: 'provider', 'errors')}">
                    <label for="provider"><g:message code="dataLink.provider.label" default="Provider" /></label>
                    <g:select class="form-control" from="${DataResource.list([sort:'name']) + DataProvider.list([sort:'name'])}" optionKey="uid" name="provider" value="${dataLinkInstance?.provider}" noSelection="['':'Choose a data provider or resource']"/>
                </div>
                <div class="buttons">
                    <span class="button"><g:submitButton name="create" class="save btn btn-success" value="${message(code: 'default.button.create.label', default: 'Create')}" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
