<%@ page import="grails.converters.JSON; au.org.ala.collectory.ProviderGroup; au.org.ala.collectory.DataHub" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="layout" content="${grailsApplication.config.ala.skin}" />
    <title><g:message code="dataHub.base.label" default="Edit data hub members metadata" /></title>
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

                <!-- institutions -->
                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="memberInstitutions"><g:message code="dataHub.memberInstitutions.label" default="Institutions" /></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: command, field: 'memberInstitutions', 'errors')}">
                        <g:textArea name="memberInstitutions" class="input-xxlarge"  rows="${cl.textAreaHeight(text:command.memberInstitutions)}" value="${command.memberInstitutions}" />
                        <cl:helpText code="providerGroup.memberInstitutions"/>
                    </td>
                    <cl:helpTD/>
                </tr>

                <!-- collections -->
                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="memberCollections"><g:message code="dataHub.memberCollections.label" default="Collections" /></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: command, field: 'memberCollections', 'errors')}">
                        <g:textArea name="memberCollections" class="input-xxlarge" rows="${cl.textAreaHeight(text:command.memberCollections)}" value="${command.memberCollections}" />
                        <cl:helpText code="providerGroup.memberCollections"/>
                    </td>
                    <cl:helpTD/>
                </tr>

                <!-- data resources -->
                <tr class="prop">
                    <td valign="top" class="name">
                        <label for="memberDataResources"><g:message code="dataHub.memberDataResources.label" default="Data resources" /></label>
                    </td>
                    <td valign="top" class="value ${hasErrors(bean: command, field: 'institutionMembers', 'errors')}">
                        <g:textArea name="memberDataResources" class="input-xxlarge" rows="${cl.textAreaHeight(text:command.memberDataResources)}" value="${command.memberDataResources}" />
                        <cl:helpText code="providerGroup.memberDataResources"/>
                    </td>
                    <cl:helpTD/>
                </tr>

                </tbody>
            </table>
        </div>

        <div class="buttons">
            <span class="button"><input type="submit" name="_action_updateDescription" value="${message(code:"collection.button.update")}" class="save btn"></span>
            <span class="button"><input type="submit" name="_action_cancel" value="${message(code:"collection.button.cancel")}" class="cancel btn"></span>
        </div>
    </g:form>
</div>
</body>
</html>
