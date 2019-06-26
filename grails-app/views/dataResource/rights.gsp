<%@ page import="au.org.ala.collectory.DataResource" %>
<%@ page import="au.org.ala.collectory.Licence" %>
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
                <!-- citation -->
                <div class="form-group">
                    <label for="citation"><g:message code="dataResource.citation.label" default="Citation" /><cl:helpText code="dataResource.citation"/></label>
                    <g:textArea class="form-control" name="citation" cols="40" rows="${cl.textAreaHeight(text:command.citation)}" value="${command.citation}" />
                </div>

                <!-- rights -->
                <div class="form-group">
                    <label for="rights"><g:message code="dataResource.rights.label" default="Rights" /><cl:helpText code="dataResource.rights"/></label>
                    <g:textArea class="form-control" name="rights" cols="40" rows="${cl.textAreaHeight(text:command.rights)}" value="${command?.rights}" />
                </div>

                <!-- license -->
                <div class="form-group">
                    <label for="licenceID"><g:message code="dataResource.licenseType.label" default="License type" /><cl:helpText code="dataResource.licenseType"/></label>
                    <g:select
                            name="licenceID"
                            from="${au.org.ala.collectory.Licence.findAll()}"
                            optionKey="id"
                            optionValue=""
                            value="${Licence.findByAcronymAndLicenceVersion(command.licenseType, command.licenseVersion)?.id }"
                            class="form-control"
                            noSelection="['':'--- Choose licence ---']"/>
                </div>

                <!-- permissions document -->
                <div class="form-group">
                    <label for="permissionsDocument"><g:message code="dataResource.permissionsDocument.label" default="Permissions document" /><cl:helpText code="dataResource.permissionsDocument"/></label>
                    <g:textField name="permissionsDocument" class="form-control" value="${command?.permissionsDocument}" />
                </div>

                <!-- permissions document type -->
                <div class="form-group">
                    <label for="permissionsDocumentType"><g:message code="dataResource.permissionsDocumentType.label" default="Permissions document type" /><cl:helpText code="dataResource.permissionsDocumentType"/></label>
                    <g:select name="permissionsDocumentType" class="form-control" from="${DataResource.permissionsDocumentTypes}" value="${command.permissionsDocumentType}"/>
                </div>

                <!-- permissions document type flags -->
                <div class="form-group">
                    <label for="riskAssessment">
                        <g:checkBox name="riskAssessment" value="${command?.riskAssessment}" />
                        <g:message code="dataResource.riskAssessment.label" default="Risk assessment completed" /><cl:helpText code="dataResource.riskAssessment"/>
                    </label>
                </div>
                <div class="form-group">

                    <label for="filed">
                        <g:checkBox name="filed" value="${command?.filed}" />
                        <g:message code="dataResource.filed.label" default="Agreement filed" /><cl:helpText code="dataResource.filed"/>
                    </label>
                </div>

                <!-- download limit -->
                <div class="form-group">
                    <label for="downloadLimit"><g:message code="dataResource.downloadLimit.label" default="Download limit" /><cl:helpText code="dataResource.downloadLimit"/></label>
                    <g:field type="number" name="downloadLimit" class="form-control" value="${fieldValue(bean:command,field:'downloadLimit')}" />
                </div>

                <div class="buttons">
                    <span class="button"><input type="submit" name="_action_updateRights" value="${message(code:"collection.button.update")}" class="save btn btn-success"></span>
                    <span class="button"><input type="submit" name="_action_cancel" value="${message(code:"dataresource.gbifupload.btn.cancel")}" class="cancel btn btn-default"></span>
                </div>
            </g:form>
        </div>
    <script type="text/javascript">
        $(document).ready(function() {
            if ($('select#permissionsDocumentType').val() != "Data Provider Agreement") {
                $('input#filed').parent().parent().css('display','none');
                $('input#riskAssessment').parent().parent().css('display','none');
            }
            $('select#permissionsDocumentType').change(function(eventObject) {
                //examine(eventObject);
                if ($(eventObject.currentTarget).val() == "Data Provider Agreement") {
                    $('input#filed').parent().parent().css('display','table-row');
                    $('input#riskAssessment').parent().parent().css('display','table-row');
                }
                else {
                    $('input#filed').parent().parent().css('display','none');
                    $('input#riskAssessment').parent().parent().css('display','none');
                }
            });
        });

    </script>
    </body>
</html>
