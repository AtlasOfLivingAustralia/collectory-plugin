<%@ page import="au.org.ala.collectory.ProviderGroup; au.org.ala.collectory.Institution" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <g:set var="entityName" value="${command.ENTITY_TYPE}"/>
        <g:set var="entityNameLower" value="${command.ENTITY_TYPE.toLowerCase()}"/>
        <title><g:message code="${entityNameLower}.base.label" default="Edit ${entityNameLower} metadata" /></title>
    </head>
    <body>
        <div class="nav">
          <h1>Editing: ${command.name}</h1>
        </div>
        <div id="baseForm" class="body">
            <g:if test="${message}">
            <div class="message">${message}</div>
            </g:if>
            <g:hasErrors bean="${command}">
            <div class="errors">
                <g:renderErrors bean="${command}" as="list" />
            </div>
            </g:hasErrors>
            <g:form method="post" name="baseForm" action="base">
                <g:hiddenField name="id" value="${command?.id}" />
                <g:hiddenField name="version" value="${command.version}" />
                <g:hiddenField name="contactForId" value="${cf.id}" />
                <g:hiddenField name="returnTo" value="${returnTo}" />
                <div class="dialog">
                    <table>
                      <colgroup><col width="15%"><col width="10%"><col width="75%"></colgroup>
                        <tbody>
                          <tr><td colspan="3"><g:message code="shared.cr.table0101" /> ${cf.contact?.buildName()} for ${command.name}</td></tr>
                          <tr class="prop">
                            <td style="vertical-align:middle;"><g:message code="shared.cr.table0201" />:</td>
                            <td colspan="2" valign="top" class="value"><g:textField name="role" value="${cf?.role}"/></td>
                          </tr>
                          <tr class="checkbox">
                            <td style="vertical-align:middle;"><g:message code="shared.cr.table0301" /></td>
                            <td><g:checkBox style="margin-left:7px;" name="administrator" value="${cf?.administrator}"/></td>
                            <td><g:message code="shared.cr.table0302" /> ${entityNameLower}.</td>
                          </tr>
                          <tr class="checkbox">
                            <td style="vertical-align:middle;"><g:message code="shared.cr.table0401" /></td>
                            <td><g:checkBox style="margin-left:7px;" name="notify" value="${cf?.notify}"/></td>
                            <td><g:message code="shared.cr.table0402" /> ${entityNameLower}.</td>
                          </tr>
                          <tr class="checkbox">
                            <td style="vertical-align:middle;"><g:message code="shared.cr.table0501" />:</td>
                            <td valign="top" class="value"><g:checkBox style="margin-left:7px;" name="primaryContact" value="${cf?.primaryContact}"/></td>
                            <td><g:message code="shared.cr.table0502" /> ${entityNameLower}.</td>
                          </tr>

                        </tbody>
                    </table>
                </div>

                <div class="buttons">
                    <span class="button"><input type="submit" name="_action_updateContactRole" value="Update" class="save"></span>
                    <span class="button"><input type="submit" name="_action_cancel" value="Cancel" class="cancel"></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
