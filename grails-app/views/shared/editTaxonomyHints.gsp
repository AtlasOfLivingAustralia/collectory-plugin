<%@ page import="grails.converters.JSON; au.org.ala.collectory.ProviderGroup; au.org.ala.collectory.Institution" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <title><g:message code="collection.base.label" default="Edit taxonomy hints" args="[command.ENTITY_TYPE]" /></title>
    </head>
    <body>
        <div class="nav">
            <h1><g:message code="shared.eth.title01" />: ${command.name}</h1>
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
            <g:form class="form-horizontal" method="post" name="baseForm" action="base">
                <g:hiddenField name="id" value="${command?.id}" />
                <g:hiddenField name="version" value="${command.version}" />
                <div class="form-group">
                    <label class="col-sm-2"><g:message code="taxonomyHints.label" default="Taxonomy hints" /></label>
                    <div class="col-sm-10"><p class="form-control-static"><g:message code="shared.eth.des01" args="${[command.urlForm()]}"/></p></div>
                </div>
                <div class="form-group">
                    <label class="col-sm-offset-2 col-sm-4"><g:message code="shared.eth.th01" /></label>
                    <label class="col-sm-6"><g:message code="shared.eth.th02" /></label>
                </div>
                <g:set var="hints" value="${command.listTaxonomyHints()}"/>
                <g:each var="hint" in="${hints}" status="i">
                    <div class="form-group">
                        <div class="col-sm-offset-2 col-sm-4"><g:textField class="form-control" name="rank_${i}" value="${hint.rank.encodeAsHTML()}" /></div>
                        <div class="col-sm-6"><g:textField class="form-control" name="name_${i}" value="${hint.name.encodeAsHTML()}" /></div>
                    </div>
                </g:each>
                <g:set var="j" value="${hints.size()}"/>
                <g:each var="i" in="${[j, j+1, j+2]}">
                    <div class="form-group">
                        <div class="col-sm-offset-2 col-sm-4"><g:textField class="form-control" name="rank_${i}" value="" /></div>
                        <div class="col-sm-6"><g:textField class="form-control" name="name_${i}" value="" /></div>
                    </div>
                </g:each>
                <div class="buttons">
                    <span class="button"><input type="submit" name="_action_updateTaxonomyHints" value="${message(code:"shared.eth.button.update")}" class="save btn btn-success"></span>
                    <span class="button"><input type="submit" name="_action_cancel" value="${message(code:"shared.eth.button.cancel")}" class="cancel btn btn-default"></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
