<%@ page import="au.org.ala.collectory.DataResource" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.ala.skin}" />
        <title><g:message code="dataResource.base.label" default="Edit data resource metadata" /></title>
        <link rel="stylesheet" href="${resource(dir:'css/smoothness',file:'jquery-ui-1.8.14.custom.css')}" type="text/css" media="screen"/>
        <r:require modules="jquery, jquery_ui_custom, debug"/>
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

                        <!-- citation -->
                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="citation"><g:message code="dataResource.citation.label" default="Citation" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'citation', 'errors')}">
                                <g:textArea name="citation" cols="40" rows="${cl.textAreaHeight(text:command.citation)}" value="${command.citation}" />
                                <cl:helpText code="dataResource.citation"/>
                              </td>
                              <cl:helpTD/>
                        </tr>

                        <!-- rights -->
                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="rights"><g:message code="dataResource.rights.label" default="Rights" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'rights', 'errors')}">
                                <g:textArea name="rights" cols="40" rows="${cl.textAreaHeight(text:command.rights)}" value="${command?.rights}" />
                                <cl:helpText code="dataResource.rights"/>
                            </td>
                          <cl:helpTD/>
                        </tr>

                        <!-- license -->
                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="licenseType"><g:message code="dataResource.licenseType.label" default="License type" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'licenseType', 'errors')}">
                                <g:select name="licenseType"
                                        from="${DataResource.ccDisplayList}"
                                        optionKey="type"
                                        optionValue="display"
                                        value="${command.licenseType}"/>
                                <cl:helpText code="dataResource.licenseType"/>
                            </td>
                          <cl:helpTD/>
                        </tr>

                        <!-- license version -->
                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="licenseVersion"><g:message code="dataResource.licenseVersion.label" default="License version" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'licenseVersion', 'errors')}">
                                <g:select name="licenseVersion"
                                        from="${['','2.5','3.0']}"
                                        value="${command.licenseVersion}"/>
                                <cl:helpText code="dataResource.licenseVersion"/>
                            </td>
                          <cl:helpTD/>
                        </tr>

                        <!-- permissions document -->
                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="permissionsDocument"><g:message code="dataResource.permissionsDocument.label" default="Permissions document" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'permissionsDocument', 'errors')}">
                                <g:textField name="permissionsDocument" value="${command?.permissionsDocument}" />
                                <cl:helpText code="dataResource.permissionsDocument"/>
                            </td>
                          <cl:helpTD/>
                        </tr>

                        <!-- permissions document type -->
                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="permissionsDocumentType"><g:message code="dataResource.permissionsDocumentType.label" default="Permissions document type" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'permissionsDocumentType', 'errors')}">
                                <g:select name="permissionsDocumentType"
                                        from="${DataResource.permissionsDocumentTypes}"
                                        value="${command.permissionsDocumentType}"/>
                                <cl:helpText code="dataResource.permissionsDocumentType"/>
                            </td>
                          <cl:helpTD/>
                        </tr>

                        <!-- permissions document type flags -->
                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="riskAssessment"><g:message code="dataResource.riskAssessment.label" default="Risk assessment completed" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'riskAssessment', 'errors')}">
                                <g:checkBox name="riskAssessment" value="${command?.riskAssessment}" />
                                <cl:helpText code="dataResource.riskAssessment"/>
                            </td>
                          <cl:helpTD/>
                        </tr>
                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="filed"><g:message code="dataResource.filed.label" default="Agreement filed" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'filed', 'errors')}">
                                <g:checkBox name="filed" value="${command?.filed}" />
                                <cl:helpText code="dataResource.filed"/>
                            </td>
                          <cl:helpTD/>
                        </tr>

                        <!-- download limit -->
                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="downloadLimit"><g:message code="dataResource.downloadLimit.label" default="Download limit" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'downloadLimit', 'errors')}">
                                <g:textField name="downloadLimit" value="${fieldValue(bean:command,field:'downloadLimit')}" />
                                <cl:helpText code="dataResource.downloadLimit"/>
                            </td>
                          <cl:helpTD/>
                        </tr>

                      </tbody>
                    </table>
                </div>

                <div class="buttons">
                    <span class="button"><input type="submit" name="_action_updateRights" value="${message(code:"collection.button.update")}" class="save btn"></span>
                    <span class="button"><input type="submit" name="_action_cancel" value="${message(code:"dataresource.gbifupload.btn.cancel")}" class="cancel btn"></span>
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
