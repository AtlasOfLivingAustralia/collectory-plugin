<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <g:set var="entityName" value="${message(code: 'institution.label', default: 'Institution')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
    <div class="btn-toolbar">
        <ul class="btn-group">
            <li class="btn btn-default"><cl:homeLink/></li>
            <li class="btn btn-default"><span class="glyphicon glyphicon-plus"></span><g:link class="create" action="create"> <g:message code="default.new.label" args="[entityName]"/></g:link></li>
        </ul>
    </div>
        <div class="body content">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:render template="/shared/institutionList" model="[institutionInstanceList: institutionInstanceList, showALAPartner: 'true' ]" />

            <div class="nav">
                <tb:paginate controller="institution" action="list" total="${institutionInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
