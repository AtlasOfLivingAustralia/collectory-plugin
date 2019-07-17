<%@ page import="au.org.ala.collectory.DataResource" %>
<%@ page import="au.org.ala.collectory.IsoCodeService" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <title><g:message code="dataResource.base.label" default="Edit data resource metadata" /></title>
        <link rel="stylesheet" href="${resource(dir:'css/smoothness',file:'jquery-ui-1.12.1.min.css')}" type="text/css" media="screen"/>
        <r:require modules="jquery_ui_custom, debug"/>
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

                <!-- GBIF registration key-->
                <div class="form-group">
                    <label for="gbifRegistryKey"><g:message code="dataResource.gbif.registrationkey.label" default="GBIF registration key" /><cl:helpText code="providerGroup.gbifregistrationkey"/></label>
                    <g:textField name="gbifRegistryKey" class="form-control" value="${command?.gbifRegistryKey}" />
                </div>

                <g:set var="iso" bean="isoCodeService"/>
                <div class="form-group">
                    <label for="gbifCountryToAttribute"><g:message code="dataResource.gbif.countryattribute.label" default="GBIF country to attribute data to" /><cl:helpText code="providerGroup.gbifCountryToAttribute"/></label>
                    <g:select from="${iso.isoCodesMap.entrySet()}" name="gbifCountryToAttribute" value="${command?.gbifCountryToAttribute}"  optionKey="key" optionValue="key"/>
                </div>

                <div class="buttons">
                    <span class="button"><input type="submit" name="_action_updateGBIFDetails" value="${message(code:"collection.button.update")}" class="save btn btn-success"></span>
                    <span class="button"><input type="submit" name="_action_cancel" value="${message(code:"dataresource.gbifupload.btn.cancel")}" class="cancel btn btn-default"></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
