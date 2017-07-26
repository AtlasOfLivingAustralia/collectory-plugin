<%@ page import="grails.converters.JSON; au.org.ala.collectory.ProviderGroup; au.org.ala.collectory.DataProvider" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <title><g:message code="dataProvider.base.label" default="Edit data provider metadata" /></title>
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
                        <!-- public short description -->
                        <div class="form-group">
                                <label for="pubShortDescription"><g:message code="providerGroup.pubShortDescription.label" default="Public Short Description" /><cl:helpText code="providerGroup.pubShortDescription"/></label>
                                 <g:textArea name="pubShortDescription" class="form-control"  rows="${cl.textAreaHeight(text:command.pubShortDescription)}" value="${command.pubShortDescription}" />
                        </div>

                        <!-- public description -->
                        <div class="form-group">
                              <label for="pubDescription"><g:message code="providerGroup.pubDescription.label" default="Public Description" /><cl:helpText code="providerGroup.pubDescription"/></label>
                                <g:textArea name="pubDescription" class="form-control" cols="40" rows="${cl.textAreaHeight(text:command.pubDescription)}" value="${command.pubDescription}" />
                         </div>

                        <!-- tech description -->
                        <div class="form-group">
                              <label for="techDescription"><g:message code="providerGroup.techDescription.label" default="Technical Description" /><cl:helpText code="providerGroup.techDescription"/></label>
                            <g:textArea name="techDescription" class="form-control" cols="40" rows="${cl.textAreaHeight(text:command.techDescription)}" value="${command?.techDescription}" />
                              <cl:helpTD/>
                        </div>

                        <!-- focus -->
                        <div class="form-group">
                              <label for="focus"><g:message code="providerGroup.focus.label" default="Contribution" /><cl:helpText code="providerGroup.focus"/></label>
                                <g:textArea name="focus" cols="40" class="form-control" rows="${cl.textAreaHeight(text:command.focus)}" value="${command?.focus}" />
                        </div>

                        <!-- keywords -->
                        <div class="form-group">
                                <label for="focus"><g:message code="providerGroup.keywords.label" default="Keywords" /><cl:helpText code="providerGroup.focus"/></label>
                                <g:textField name="keywords" cols="40" class="form-control" rows="${cl.textAreaHeight(text:command.keywords)}" value="${command?.keywords}" />
                                <p class="help-block">Recognised keywords include: ${au.org.ala.collectory.Classification.keywordSynonyms.values().flatten().toSet().sort().join(", ")}
                                The keywords should be comma separated. These keywords are used to drive the
                                selections of providers on the <g:link controller="public" action="map">map</g:link>.
                                </p>
                        </div>
                </div>

                <div class="buttons">
                    <span class="button"><input type="submit" name="_action_updateDescription" value="${message(code:"collection.button.update")}" class="save btn btn-success"></span>
                    <span class="button"><input type="submit" name="_action_cancel" value="${message(code:"collection.button.cancel")}" class="cancel btn btn-default"></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
