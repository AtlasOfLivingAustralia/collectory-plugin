<%@ page import="au.org.ala.collectory.Contact" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <g:set var="entityName" value="${message(code: 'contact.label', default: 'Contact')}" />
        <title><g:message code="default.create.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <ul>
            <li><span class="menuButton"><cl:homeLink/></span></li>
            <li><span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span></li>
            </ul>
        </div>
        <div class="body">
            <h1><g:message code="default.create.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${contactInstance}">
            <div class="errors">
                <g:renderErrors bean="${contactInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:form action="save" method="post" >
                <g:hiddenField name="returnTo" value="${returnTo}"/>
                <div class="form-group">
                    <label for="title"><g:message code="contact.title.label" default="Title" /></label>
                    <g:select name="title" class="form-control" from="${contactInstance.constraints.title.inList}" value="${contactInstance?.title}" valueMessagePrefix="contact.title" noSelection="['': '']" />
                </div>

                <div class="form-group">
                    <label for="firstName"><g:message code="contact.firstName.label" default="First Name" /></label>
                    <g:textArea name="firstName" class="form-control" cols="40" rows="5" value="${contactInstance?.firstName}" />

                </div>

                <div class="form-group">
                    <label for="lastName"><g:message code="contact.lastName.label" default="Last Name" /></label>
                    <g:textArea name="lastName" class="form-control" cols="40" rows="5" value="${contactInstance?.lastName}" />

                </div>

                <div class="form-group">
                    <label for="phone"><g:message code="contact.phone.label" default="Phone" /></label>
                    <g:field type="tel" name="phone" class="form-control" maxlength="45" value="${contactInstance?.phone}" />

                </div>

                <div class="form-group">
                    <label for="mobile"><g:message code="contact.mobile.label" default="Mobile" /></label>
                    <g:field type="tel" name="mobile" class="form-control" maxlength="45" value="${contactInstance?.mobile}" />

                </div>

                <div class="form-group">
                    <label for="email"><g:message code="contact.email.label" default="Email" /></label>
                    <g:field type="email" name="email" class="form-control" value="${contactInstance?.email}" />

                </div>

                <div class="form-group">
                    <label for="fax"><g:message code="contact.fax.label" default="Fax" /></label>
                    <g:field type="tel" name="fax" class="form-control" maxlength="45" value="${contactInstance?.fax}" />

                </div>

                <div class="form-group">
                    <label for="notes"><g:message code="contact.notes.label" default="Notes" /></label>
                    <g:textArea name="notes" class="form-control" cols="40" rows="5" value="${contactInstance?.notes}" />
                </div>

                <div class="form-group">
                    <label for="publish">
                        <g:checkBox name="publish" value="${contactInstance?.publish}" />
                        <g:message code="contact.publish.label" default="Publish" />
                    </label>
                </div>
                <div class="buttons">
                    <span class="button"><g:submitButton name="create" class="save btn btn-default" value="${message(code: 'default.button.create.label', default: 'Create')}" /></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
