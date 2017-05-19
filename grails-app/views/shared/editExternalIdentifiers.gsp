<%@ page import="grails.converters.JSON; au.org.ala.collectory.ProviderGroup; au.org.ala.collectory.Institution" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <title><g:message code="shared.ext.title01" default="Edit external identifiers" /></title>
    </head>
    <body>
        <div class="nav">
            <h1><g:message code="shared.ext.title01" />: ${command.name}</h1>
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
                <div class="dialog">
                    <table>
                        <tbody>

                        <!-- taxonomy hints -->
                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="externalIdentifiers"><g:message code="externalIdentifiers.label" default="External Identifiers" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'externalIdentifiers', 'errors')}">
                              <p><g:message code="shared.ext.des01" /> ${command.urlForm()}.</p>
                              <table class="shy"><colgroup><col width="50%"/><col width="50%"/></colgroup>
                                <tr><td><g:message code="shared.ext.th01" /></td><td><g:message code="shared.ext.th02" /></td><td><g:message code="shared.ext.th03" /></td></tr>
                                <g:set var="ids" value="${command.externalIdentifiers}"/>
                                <g:each var="id" in="${ids}" status="i">
                                  <tr>
                                    <td valign="top"><g:field type="text" size="8" name="source_${i}" value="${id.source.encodeAsHTML()}" /></td>
                                    <td valign="top"><g:field type="text" size="10" name="identifier_${i}" value="${id.identifier.encodeAsHTML()}" /></td>
                                    <td valign="top"><g:field type="url" size="48" name="uri_${i}" value="${id.uri.encodeAsHTML()}" /></td>
                                  </tr>
                                </g:each>
                                <g:set var="j" value="${ids?.size() ?: 0}"/>
                                <g:each var="i" in="${[j, j+1, j+2]}">
                                  <tr>
                                    <td valign="top"><g:field type="text" size="8" name="source_${i}" value="" /></td>
                                    <td valign="top"><g:field type="text" size="10" name="identifier_${i}" value="" /></td>
                                    <td valign="top"><g:field type="url" size="48" name="uri_${i}" value="" /></td>
                                  </tr>
                                </g:each>
                              </table>
                              <cl:helpText code="externalIdentifiers"/>
                              </td>
                              <cl:helpTD/>
                        </tr>

                        </tbody>
                    </table>
                </div>

                <div class="buttons">
                    <span class="button"><input type="submit" name="_action_updateExternalIdentifiers" value="${message(code:"shared.ext.button.update")}" class="save"></span>
                    <span class="button"><input type="submit" name="_action_cancel" value="${message(code:"shared.ext.button.cancel")}" class="cancel"></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
