<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <title><g:message code="reports.title" /></title>
    </head>
    <body>
        <div class="btn-toolbar">
            <ul class="btn-group">
                <li class="btn btn-default"><cl:homeLink/></li>
                <li class="btn btn-default"><span class="glyphicon glyphicon-th-list"></span><g:link class="list" action="list"> <g:message code="reports.li.reports"/></g:link></li>
            </ul>
        </div>
        <div class="body">
            <h1><g:message code="reports.cfc.title01" /> (${contacts.size()})</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
              <table class="table table-striped table-bordered">
                <colgroup><col width="45%"/><col width="5%"/><col width="23%"/><col width="27%"/></colgroup>
                <g:each var="cfc" in="${contacts}">
                  <tr>
                    <g:if test="${cfc.entityName.endsWith('Collection')}">
                      <g:set var="name" value="${cfc.entityName - 'Collection'}"/>
                    </g:if>
                    <g:else>
                      <g:set var="name" value="${cfc.entityName}"/>
                    </g:else>
                    <td><g:link controller="collection" action="show" id="${cfc.entityUid}">${name}</g:link></td>
                    <td>${cfc.entityAcronym}</td>
                    <td style="color:blue;">${cfc.contactName}</td>
                    <td>${cfc.contactEmail}</td>
                  </tr>
                </g:each>

              </table>
            </div>
        </div>
    </body>
</html>
