
<%@ page import="au.org.ala.collectory.ProviderGroup; au.org.ala.collectory.DataLink" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <g:set var="entityName" value="${message(code: 'dataLink.label', default: 'DataLink')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="btn-toolbar">
            <ul class="btn-group">
                <li class="btn btn-default"><cl:homeLink/></li>
                <li class="btn btn-default"><span class="glyphicon glyphicon-list"></span><g:link class="list" action="list"> <g:message code="default.list.label" args="[entityName]"/></g:link></li>
                <li class="btn btn-default"><span class="glyphicon glyphicon-plus"></span><g:link class="create" action="create"> <g:message code="default.new.label" args="[entityName]"/></g:link></li>
                <g:if test="${returnTo}"><li><cl:returnLink uid="${returnTo}"/></li></g:if>
            </ul>
        </div>
        <div class="body">
            <h1><g:message code="default.show.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:set var="provider" value="${ProviderGroup._get(dataLinkInstance.provider)}"/>
            <g:set var="consumer" value="${ProviderGroup._get(dataLinkInstance.consumer)}"/>
            <div class="dialog">
                <table>
                    <colgroup><col width="15%"><col width="60%"><col width="25%"></colgroup>
                    <tbody>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="dataLink.id.label" default="Id" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: dataLinkInstance, field: "id")}</td>

                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="dataLink.consumer.label" default="Consumer" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: consumer, field: "name")}
                            <g:link controller="${cl.controllerFromUid(uid:consumer.uid)}" action="show" id="${consumer.uid}">(${consumer.uid})</g:link></td>
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="dataLink.provider.label" default="Provider" /></td>
                            
                            <td valign="top" class="value">${fieldValue(bean: provider, field: "name")}
                            <g:link controller="${cl.controllerFromUid(uid:provider.uid)}" action="show" id="${provider.uid}">(${provider.uid})</g:link></td>
                        </tr>
                    
                    </tbody>
                </table>
            </div>
            <div class="buttons">
                <g:form>
                    <g:hiddenField name="id" value="${dataLinkInstance?.id}" />
                    <g:hiddenField name="returnTo" value="${returnTo}"/>
                    <span class="button"><g:actionSubmit class="edit btn btn-default" action="edit" value="${message(code: 'default.button.edit.label', default: 'Edit')}" /></span>
                    <span class="button"><g:actionSubmit class="delete btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></span>
                </g:form>
            </div>
        </div>
    </body>
</html>
