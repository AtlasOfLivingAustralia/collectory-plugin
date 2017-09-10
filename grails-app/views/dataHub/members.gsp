<%@ page import="grails.converters.JSON; au.org.ala.collectory.ProviderGroup; au.org.ala.collectory.DataHub" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="layout" content="${grailsApplication.config.skin.layout}" />
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
        <!-- institutions -->
        <div class="form-group">
            <label for="memberInstitutions"><g:message code="dataHub.memberInstitutions.label" default="Institutions" /><cl:helpText code="providerGroup.memberInstitutions"/></label>
            <g:textArea name="memberInstitutions" class="form-control"  rows="${cl.textAreaHeight(text:command.memberInstitutions)}" value="${command.memberInstitutions}" />
        </div>

        <!-- collections -->
        <div class="form-group">
            <label for="memberCollections"><g:message code="dataHub.memberCollections.label" default="Collections" /><cl:helpText code="providerGroup.memberCollections"/></label>
            <g:textArea name="memberCollections" class="form-control" rows="${cl.textAreaHeight(text:command.memberCollections)}" value="${command.memberCollections}" />
        </div>

        <!-- data resources -->
        <div class="form-group">
            <label for="memberDataResources"><g:message code="dataHub.memberDataResources.label" default="Data resources" /><cl:helpText code="providerGroup.memberDataResources"/></label>
            <g:textArea name="memberDataResources" class="form-control" rows="${cl.textAreaHeight(text:command.memberDataResources)}" value="${command.memberDataResources}" />
        </div>

        <div class="buttons">
            <span class="button"><input type="submit" name="_action_updateDescription" value="${message(code:"collection.button.update")}" class="save btn btn-success"></span>
            <span class="button"><input type="submit" name="_action_cancel" value="${message(code:"collection.button.cancel")}" class="cancel btn btn-default"></span>
        </div>
    </g:form>
</div>
</body>
</html>
