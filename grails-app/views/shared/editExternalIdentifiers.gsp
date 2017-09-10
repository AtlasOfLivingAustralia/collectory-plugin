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
                <div class="form-group">
                    <label clas="col-sm-2"><g:message code="externalIdentifiers.label" default="Taxonomy hints" /><cl:helpText code="externalIdentifiers"/></label>
                    <div class="col-sm-10"><p class="form-control-static"><g:message code="shared.ext.des01" args="${[command.urlForm()]}"/></p></div>

                </div>
                <div class="form-group">
                    <label class="col-sm-2"><g:message code="shared.ext.th01" /></label>
                    <label class="col-sm-4"><g:message code="shared.ext.th02" /></label>
                    <label class="col-sm-6"><g:message code="shared.ext.th03" /></label>
                </div>
                <g:set var="ids" value="${command.externalIdentifiers}"/>
                <g:each var="id" in="${ids}" status="i">
                    <div class="form-group">
                        <div class="col-sm-2"><g:field type="text" class="form-control" name="source_${i}" value="${id.source.encodeAsHTML()}" /></div>
                        <div class="col-sm-4"><g:field type="text" class="form-control" name="identifier_${i}" value="${id.identifier.encodeAsHTML()}" /></div>
                        <div class="col-sm-6"><g:field type="url" class="form-control" name="uri_${i}" value="${id.uri.encodeAsHTML()}" /></div>
                    </div>
                </g:each>
                <g:set var="j" value="${ids.size()}"/>
                <g:each var="i" in="${[j, j+1, j+2]}">
                    <div class="form-group">
                        <div class="col-sm-2"><g:field type="text" class="form-control" name="source_${i}" value="" /></div>
                        <div class="col-sm-4"><g:field type="text" class="form-control" name="identifier_${i}" value="" /></div>
                        <div class="col-sm-6"><g:field type="url" class="form-control" name="uri_${i}" value="" /></div>
                    </div>
                </g:each>
                <div class="buttons">
                    <span class="button"><input type="submit" name="_action_updateExternalIdentifiers" value="${message(code:"shared.ext.button.update")}" class="save btn btn-success"></span>
                    <span class="button"><input type="submit" name="_action_cancel" value="${message(code:"shared.ext.button.cancel")}" class="cancel btn btn-default"></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
