
<%@ page import="au.org.ala.collectory.Contact" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.ala.skin}" />
        <g:set var="entityName" value="${message(code: 'contact.label', default: 'Contact')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <ul>
            <li><span class="menuButton"><cl:homeLink/></span></li>
            <li><span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span></li>
            </ul>
        </div>
        <div class="body content">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table class="table table-striped table-bordered">
                    <thead>
                        <tr>
                        
                            <g:sortableColumn property="id" title="${message(code: 'contact.id.label', default: 'Id')}" />

                            <g:sortableColumn property="mobile" title="${message(code: 'contact.email.label', default: 'Email')}" />

                            <g:sortableColumn property="title" title="${message(code: 'contact.title.label', default: 'Title')}" />
                        
                            <g:sortableColumn property="firstName" title="${message(code: 'contact.firstName.label', default: 'First Name')}" />
                        
                            <g:sortableColumn property="lastName" title="${message(code: 'contact.lastName.label', default: 'Last Name')}" />
                        
                            <g:sortableColumn property="phone" title="${message(code: 'contact.phone.label', default: 'Phone')}" />
                        

                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${contactInstanceList}" status="i" var="contactInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${contactInstance.id}">${fieldValue(bean: contactInstance, field: "id")}</g:link></td>

                            <td>${fieldValue(bean: contactInstance, field: "email")}</td>

                            <td>${fieldValue(bean: contactInstance, field: "title")}</td>
                        
                            <td>${fieldValue(bean: contactInstance, field: "firstName")}</td>
                        
                            <td>${fieldValue(bean: contactInstance, field: "lastName")}</td>
                        
                            <td>${fieldValue(bean: contactInstance, field: "phone")}</td>

                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="pagination">
                <g:paginate total="${contactInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
