<%@ page import="grails.converters.JSON; au.org.ala.collectory.ProviderGroup; au.org.ala.collectory.Institution" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.ala.skin}" />
        <title><g:message code="institution.base.label" default="Edit institution metadata" /></title>
    </head>
    <body>
        <div class="nav">
        <h1><g:message code="collection.title.editing" />: ${command.name}</h1>
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

                        <!-- public description -->
                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="pubDescription"><g:message code="providerGroup.pubDescription.label" default="Public Description" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'pubDescription', 'errors')}">
                                <g:textArea name="pubDescription" cols="40" rows="${cl.textAreaHeight(text:command.pubDescription)}" value="${command.pubDescription}" />
                                <cl:helpText code="institution.pubDescription"/>
                              </td>
                              <cl:helpTD/>
                        </tr>

                        <!-- tech description -->
                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="techDescription"><g:message code="providerGroup.techDescription.label" default="Technical Description" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'techDescription', 'errors')}">
                                <g:textArea name="techDescription" cols="40" rows="${cl.textAreaHeight(text:command.techDescription)}" value="${command?.techDescription}" />
                                <cl:helpText code="institution.techDescription"/>
                              </td>
                              <cl:helpTD/>
                        </tr>

                        <!-- focus -->
                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="focus"><g:message code="providerGroup.focus.label" default="Contribution" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'focus', 'errors')}">
                                <g:textArea name="focus" cols="40" rows="${cl.textAreaHeight(text:command.focus)}" value="${command?.focus}" />
                                <cl:helpText code="institution.focus"/>
                            </td>
                          <cl:helpTD/>
                        </tr>

                        <!-- type -->
                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="institutionType"><g:message code="providerGroup.institutionType.label" default="Institution Type" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'institutionType', 'errors')}">
                              <g:select name="institutionType" from="${Institution.constraints.institutionType.inList}" value="${command.institutionType}"/>
                              <cl:helpText code="collection.collectionType"/>
                            </td>
                          <td><img class="helpButton" alt="help" src="${resource(dir:'images/skin', file:'help.gif')}" onclick="toggleHelp(this);"/></td>
                        </tr>

                        </tbody>
                    </table>
                </div>

                <div class="buttons">
                    <span class="button"><input type="submit" name="_action_updateDescription" value="${message(code:"collection.button.update")}" class="save"></span>
                    <span class="button"><input type="submit" name="_action_cancel" value="${message(code:"collection.button.cancel")}" class="cancel"></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
