<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <title><g:message code="reports.changes.title" /></title>
    </head>
    <body>
        <div class="btn-toolbar">
            <ul class="btn-group">
                <li class="btn btn-default"><cl:homeLink/></li>
                <li class="btn btn-default"><span class="glyphicon glyphicon-th-list"></span><g:link class="list" action="list"> <g:message code="reports.li.reports"/></g:link></li>
            </ul>
        </div>
        <div class="body">
            <h1><g:message code="reports.changes.title01" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
              <p><g:message code="reports.changes.des01" />.</p>
              <div class="filters">
                  <g:form action="changes">
                    <fieldset>
                        <legend class="box-label"><g:message code="reports.changes.legend01" /></legend>
                        <g:hiddenField name="offset" value="${offset}"/>
                        <label for="who"><g:message code="reports.changes.who" />: <g:textField name="who" value="${who}"/></label>
                        <label for="what"><g:message code="reports.changes.what" />: <g:textField name="what" value="${what}"/></label>
                        <g:submitButton class="submit btn btn-default " name="filter" value="Search"/>
                        <g:submitButton class="submit btn btn-default" name="reset" value="Reset"/>
                        <g:submitButton name="next "  class="btn btn-default" value="Next 100»"/>
                    </fieldset>
                  </g:form>
              </div>
              <br/>
              <table class="table table-striped table-bordered">
                <colgroup><col width="23%"/><col width="25%"/><col width="12%"/><col width="40%"/></colgroup>
                <tr class="reportHeaderRow"><td><g:message code="reports.changes.when" /></td><td><g:message code="reports.changes.who" /></td><td><g:message code="reports.changes.did" /></td><td><g:message code="reports.changes.what" /></td></tr>
                <g:each var='ch' in="${changes}">
                  <tr>
                    <td><g:link controller="auditLogEvent" action="show" id="${ch.id}">${ch.lastUpdated}</g:link></td>
                    <td><g:link controller="auditLogEvent" action="show" id="${ch.id}"><cl:boldNameInEmail name="${ch.actor}"/></g:link></td>
                    <td><g:link controller="auditLogEvent" action="show" id="${ch.id}"><cl:changeEventName event="${ch.eventName}"/></g:link></td>
                    <td>
                      <g:link controller="auditLogEvent" action="show" id="${ch.id}">
                        <g:if test="${ch.eventName == 'UPDATE'}"><b>${ch.propertyName}</b> in</g:if>
                        <cl:shortClassName className="${ch.className}"/><b>
                      </g:link>
                      <g:if test="${ch.uri}">
                        <!-- handle uids -->
                        <g:if test="${ch.eventName=='DELETE' && !ch.className.endsWith('ContactFor')}">
                          ${ch.uri}
                        </g:if>
                        <g:else>
                          <g:link controller="${cl.controllerFromUid(uid:ch.uri)}" action="show" id="${ch.uri}">${ch.uri}</g:link>
                        </g:else>
                      </g:if>
                      <g:else>
                        <!-- handle db ids -->
                        <g:if test="${ch.eventName=='DELETE'}">
                          ${ch.persistedObjectId}
                        </g:if>
                        <g:else>
                          <g:link controller="${cl.controllerFromClassName(className:ch.className)}" action="show" id="${ch.persistedObjectId}">${ch.persistedObjectId}</g:link>
                        </g:else>
                      </g:else>
                      </b>
                    </td>
                  </tr>
                </g:each>
              </table>
            </div>
        </div>
    </body>
</html>
