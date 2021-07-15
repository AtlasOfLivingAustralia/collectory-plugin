<%@ page import="au.org.ala.collectory.ProviderMap" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <g:set var="entityName" value="${message(code: 'providerMap.label', default: 'ProviderMap')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="btn-toolbar">
            <ul class="btn-group">
                <li class="btn btn-default"><cl:homeLink/></li>
                <li class="btn btn-default"><span class="glyphicon glyphicon-list"></span><g:link class="list" action="list"> <g:message code="default.list.label" args="[entityName]"/></g:link></li>
                <li class="btn btn-default"><span class="glyphicon glyphicon-plus"></span><g:link class="create" action="create" params="[returnTo: returnTo]"> <g:message code="default.new.label" args="[entityName]"/></g:link></li>
            </ul>
        </div>
        <div class="body">
            <h1><g:message code="default.show.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
                <table>
                    <tbody>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="institution.label" default="Institution" /></td>

                            <td valign="top" class="value"><g:link controller="institution" action="show" id="${providerMapInstance?.institution?.uid}">${providerMapInstance?.institution?.encodeAsHTML()}</g:link></td>

                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="collection.label" default="Collection" /></td>

                            <td valign="top" class="value"><g:link controller="collection" action="show" id="${providerMapInstance?.collection?.uid}">${providerMapInstance?.collection?.encodeAsHTML()}</g:link></td>

                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="providerMap.institutionCodes.label" default="Institution Codes" /></td>

                            <td valign="top" style="text-align: left;" class="value">
                                <ul>
                                <g:each in="${providerMapInstance.institutionCodes}" var="i">
                                    <li><g:link controller="providerCode" action="show" id="${i.id}">${i?.encodeAsHTML()}</g:link></li>
                                </g:each>
                                </ul>
                            </td>

                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="providerMap.collectionCodes.label" default="Collection Codes" /></td>
                            
                            <td valign="top" style="text-align: left;" class="value">
                                <ul>
                                <g:each in="${providerMapInstance.collectionCodes}" var="c">
                                    <li><g:link controller="providerCode" action="show" id="${c.id}">${c?.encodeAsHTML()}</g:link></li>
                                </g:each>
                                </ul>
                            </td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="providerMap.exact.label" default="Exact" /></td>
                            
                            <td valign="top" class="value"><g:formatBoolean boolean="${providerMapInstance?.exact}" /></td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="providerMap.warning.label" default="Warning" /></td>

                            <td valign="top" class="value">${fieldValue(bean: providerMapInstance, field: "warning")}</td>

                        </tr>

                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="providerMap.matchAnyCollectionCode.label" default="Match Any Collection Code" /></td>
                            
                            <td valign="top" class="value"><g:formatBoolean boolean="${providerMapInstance?.matchAnyCollectionCode}" /></td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="providerMap.lastUpdated.label" default="Last Updated" /></td>
                            
                            <td valign="top" class="value"><g:formatDate date="${providerMapInstance?.lastUpdated}" /></td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="providerMap.dateCreated.label" default="Date Created" /></td>
                            
                            <td valign="top" class="value"><g:formatDate date="${providerMapInstance?.dateCreated}" /></td>
                            
                        </tr>
                    
                    </tbody>
                </table>
            </div>
            <div class="buttons">
                <g:form>
                    <g:hiddenField name="id" value="${providerMapInstance?.id}" />
                    <g:hiddenField name="returnTo" value="${returnTo}" />
                    <span class="button"><g:actionSubmit class="edit btn btn-default" action="edit" value="${message(code: 'default.button.edit.label', default: 'Edit')}" /></span>
                    <span class="button"><g:actionSubmit class="delete btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></span>
                </g:form>
            </div>
        </div>
    </body>
</html>
