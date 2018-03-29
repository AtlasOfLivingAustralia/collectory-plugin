
<%@ page import="au.org.ala.collectory.Contact; au.org.ala.collectory.ProviderGroup" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <g:set var="entityName" value="${message(code: 'contact.label', default: 'Contact')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>
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
            <h1><g:message code="default.show.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
                <div class="message">${flash.message}</div>
            </g:if>

            <div class=" pull-right">

                <g:if test="${contactInstance?.getContactsFor()}">
                <div class="well">
                    <h3>Contact for</h3>
                    <ul>
                        <g:each in="${contactInstance?.getContactsFor()}" var="contactFor">
                        <li>
                            <g:link controller="${cl.controller(type: contactFor.entity.entityType())}" action="show" id="${contactFor.entity.uid}">
                                ${contactFor.entity.name}
                                <g:if test="${contactFor.isAdmin}">
                                    (Administrator)
                                </g:if>
                            </g:link>
                        </li>
                        </g:each>
                    </ul>
                </div>
                </g:if>

                <g:if test="${contactInstance.approvedAccess}">
                <div>
                    <h3>Sensitive Access Rights</h3>
                    <p>
                        This contact has sensitive access rights for the following data partners:
                    </p>
                    <ul>
                        <g:each var="approvedAccess" in="${contactInstance.approvedAccess}" status="idx">
                            <li>
                                <g:link controller="dataProvider" action="show" id="${approvedAccess.dataProvider.id}">
                                    ${approvedAccess.dataProvider.name}
                                </g:link>
                            </li>
                        </g:each>
                    </ul>
                </div>
                </g:if>

                <g:if test="${changes}">
                <div class="well">

                    <h3>Change history</h3>
                    <table>
                        <g:each var="ch" in="${changes}" status="row">
                            <tr class="prop">
                                <td><g:link controller='auditLogEvent' action='show' id='${ch.id}'>${ch.lastUpdated}: ${ch.actor}
                                    <cl:changeEventName event="${ch.eventName}" highlightInsertDelete="true"/> <strong>${ch.propertyName}</strong>
                                </g:link></td>
                            </tr>
                        </g:each>
                    </table>
                </div>
                </g:if>
            </div>


            <div class=" col-lg-4">
                <table class="table">
                    <tbody>
                        <tr class="prop">
                            <td scope="row" valign="top" class="name"><g:message code="contact.id.label" default="Id" /></td>
                            <td valign="top" class="value">${fieldValue(bean: contactInstance, field: "id")}</td>
                        </tr>
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="contact.systemid.label" default="System ID (CAS)" /></td>
                            <td valign="top" class="value">${fieldValue(bean: contactInstance, field: "userId")}</td>
                        </tr>
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="contact.title.label" default="Title" /></td>
                            <td valign="top" class="value">${fieldValue(bean: contactInstance, field: "title")}</td>
                        </tr>
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="contact.firstName.label" default="First Name" /></td>
                            <td valign="top" class="value">${fieldValue(bean: contactInstance, field: "firstName")}</td>
                        </tr>
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="contact.lastName.label" default="Last Name" /></td>
                            <td valign="top" class="value">${fieldValue(bean: contactInstance, field: "lastName")}</td>
                        </tr>
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="contact.phone.label" default="Phone" /></td>
                            <td valign="top" class="value">${fieldValue(bean: contactInstance, field: "phone")}</td>
                        </tr>
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="contact.mobile.label" default="Mobile" /></td>
                            <td valign="top" class="value">${fieldValue(bean: contactInstance, field: "mobile")}</td>
                        </tr>
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="contact.email.label" default="Email" /></td>
                            <td valign="top" class="value">${fieldValue(bean: contactInstance, field: "email")}</td>
                        </tr>
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="contact.fax.label" default="Fax" /></td>
                            <td valign="top" class="value">${fieldValue(bean: contactInstance, field: "fax")}</td>
                        </tr>
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="contact.notes.label" default="Notes" /></td>
                            <td valign="top" class="value">${fieldValue(bean: contactInstance, field: "notes")}</td>
                        </tr>
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="contact.publish.label" default="Publish" /></td>
                            <td valign="top" class="value"><g:formatBoolean boolean="${contactInstance?.publish}" /></td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <div class="buttons">
                <g:form>
                    <g:hiddenField name="id" value="${contactInstance?.id}" />
                    <span class="button"><g:actionSubmit class="edit btn btn-default" action="edit" value="${message(code: 'default.button.edit.label', default: 'Edit')}" /></span>
                    <span class="button"><g:actionSubmit class="delete btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');" /></span>
                </g:form>
            </div>
        </div>
    </body>
</html>
