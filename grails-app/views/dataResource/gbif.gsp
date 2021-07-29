<%@ page import="au.org.ala.collectory.DataResource" %>
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
                    <label for="gbifRegistryKey"><g:message code="dataResource.gbif.registrationkey.label" default="GBIF registration key" /><cl:helpText code="dataResource.gbifregistrationkey"/></label>
                    <g:textField name="gbifRegistryKey" class="form-control" value="${command?.gbifRegistryKey}" />
                </div>

                <div class="form-group">
                    <label for="repatriationCountry"><g:message code="dataResource.gbif.repatriationCountry.label" default="GBIF repatriation country" /><cl:helpText code="dataResource.repatriationCountry"/></label>
                    <g:textField name="repatriationCountry" class="form-control" value="${command?.repatriationCountry}" />
                </div>

                <!-- is shareable -->
                <div class="form-group">
                    <label for="isShareableWithGBIF">
                        <g:checkBox name="isShareableWithGBIF" value="${command?.isShareableWithGBIF}" />
                        <g:message code="dataResource.shareablewithgbif.label" default="Is shareable with GBIF" /><cl:helpText code="dataResource.isShareableWithGBIF"/>
                    </label>
                </div>

                <!-- is gbif supplied -->
                <div class="form-group">
                    <label for="gbifDataset">
                        <g:checkBox name="gbifDataset" value="${command?.gbifDataset}" />
                        <g:message code="dataResource.gbifDataset.label" default="Was supplied by GBIF (downloaded via GBIF webservices)?" /><cl:helpText code="dataResource.gbifDataset"/>
                    </label>
                </div>

                <div class="buttons">
                    <span class="button"><input type="submit" name="_action_updateGBIFDetails" value="${message(code:"collection.button.update")}" class="save btn btn-success"></span>
                    <span class="button"><input type="submit" name="_action_cancel" value="${message(code:"dataresource.gbifupload.btn.cancel")}" class="cancel btn btn-default"></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
