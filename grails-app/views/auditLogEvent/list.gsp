<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <title><g:message code="ale.list.title" /></title>
    </head>
    <body class="content">
        <div class="btn-toolbar">
            <ul class="btn-group">
                <li class="btn btn-default"><cl:homeLink/></li>
                <li class="btn btn-default"><span class="glyphicon glyphicon-list"></span><g:link class="list" action="list"> <g:message code="ale.list.li01"/></g:link></li>
            </ul>
        </div>
        <div class="body">
            <h1><g:message code="ale.list.title01" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="list">
                <table class="table table-bordered table-striped">
                    <thead>
                        <tr>
                        
                   	        <g:sortableColumn property="id" title="Id" />
                        
                   	        <g:sortableColumn property="actor" title="Actor" />
                        
                   	        <g:sortableColumn property="uri" title="Uri" />
                        
                   	        <g:sortableColumn property="className" title="Class Name" />
                        
                   	        <g:sortableColumn property="persistedObjectId" title="Persisted Object Id" />

                   	        <g:sortableColumn property="persistedObjectVersion" title="Persisted Object Version" />

                            <g:sortableColumn property="lastUpdated" title="Last updated" />
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${auditLogEventInstanceList}" status="i" var="auditLogEventInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${auditLogEventInstance.id}">${fieldValue(bean:auditLogEventInstance, field:'id')}</g:link></td>
                        
                            <td>${fieldValue(bean:auditLogEventInstance, field:'actor')}</td>
                        
                            <td>${fieldValue(bean:auditLogEventInstance, field:'uri')}</td>
                        
                            <td>${fieldValue(bean:auditLogEventInstance, field:'className')}</td>
                        
                            <td>${fieldValue(bean:auditLogEventInstance, field:'persistedObjectId')}</td>

                            <td>${fieldValue(bean:auditLogEventInstance, field:'persistedObjectVersion')}</td>

                            <td>${fieldValue(bean:auditLogEventInstance, field:'lastUpdated')}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="nav">
                <tb:paginate controller="licence" action="list" total="${auditLogEventInstanceTotal}" />
            </div>
        </div>
    </body>
</html>
