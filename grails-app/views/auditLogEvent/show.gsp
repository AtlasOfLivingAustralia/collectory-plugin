

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <title><g:message code="ale.show.title" /></title>
    </head>
    <body>
        <div class="btn-toolbar">
            <ul class="btn-group">
                <li class="btn btn-default"><cl:homeLink/></li>
                <li class="btn btn-default"><span class="glyphicon glyphicon-list"></span><g:link class="list" action="list"> <g:message code="ale.list.li01"/></g:link></li>
            </ul>
        </div>
        <div class="body">
            <h1><g:message code="ale.show.title.showaudit" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
                <table class="table table-bordered table-striped">
                    <tbody>

                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="ale.show.cell0101" />:</td>
                            
                            <td valign="top" class="value">${fieldValue(bean:auditLogEventInstance, field:'id')}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="ale.show.cell0201" />:</td>
                            
                            <td valign="top" class="value">${fieldValue(bean:auditLogEventInstance, field:'actor')}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="ale.show.cell0301" />:</td>
                            
                            <td valign="top" class="value">${fieldValue(bean:auditLogEventInstance, field:'uri')}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="ale.show.cell0401" />:</td>
                            
                            <td valign="top" class="value">${fieldValue(bean:auditLogEventInstance, field:'className')}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="ale.show.cell0501" />:</td>
                            
                            <td valign="top" class="value">${fieldValue(bean:auditLogEventInstance, field:'persistedObjectId')}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="ale.show.cell0601" />:</td>
                            
                            <td valign="top" class="value">${fieldValue(bean:auditLogEventInstance, field:'persistedObjectVersion')}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="ale.show.cell0701" />:</td>
                            
                            <td valign="top" class="value">${fieldValue(bean:auditLogEventInstance, field:'eventName')}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="ale.show.cell0801" />:</td>
                            
                            <td valign="top" class="value">${fieldValue(bean:auditLogEventInstance, field:'propertyName')}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="ale.show.cell0901" />:</td>
                            
                            <td valign="top" class="value">${fieldValue(bean:auditLogEventInstance, field:'oldValue')}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="ale.show.cell1001" />:</td>
                            
                            <td valign="top" class="value">${fieldValue(bean:auditLogEventInstance, field:'newValue')}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="ale.show.cell1101" />:</td>
                            
                            <td valign="top" class="value">${fieldValue(bean:auditLogEventInstance, field:'dateCreated')}</td>
                            
                        </tr>
                    
                        <tr class="prop">
                            <td valign="top" class="name"><g:message code="ale.show.cell1201" />:</td>
                            
                            <td valign="top" class="value">${fieldValue(bean:auditLogEventInstance, field:'lastUpdated')}</td>
                            
                        </tr>
                    
                    </tbody>
                </table>
            </div>
        </div>
    </body>
</html>
