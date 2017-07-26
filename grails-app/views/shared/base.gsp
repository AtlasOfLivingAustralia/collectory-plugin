<%@ page import="au.org.ala.collectory.DataResource; au.org.ala.collectory.DataProvider; au.org.ala.collectory.ProviderGroup; au.org.ala.collectory.Institution" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <g:set var="entityName" value="${command.ENTITY_TYPE}"/>
        <g:set var="entityNameLower" value="${command.urlForm()}"/>
        <title><g:message code="collection.base.label" args="[entityNameLower]" default="Edit ${entityNameLower}  metadata" /></title>
    </head>
    <body>
        <div class="nav">
          <g:if test="${mode == 'create'}">
            <h1>Creating a new collection</h1>
          </g:if>
          <g:else>
            <h1>Editing: ${command.name}</h1>
          </g:else>
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
                    <label for="guid"><g:message code="collection.guid.label" default="Guid"/><cl:helpText code="${entityNameLower}.guid"/></label>
                    <g:textField name="guid" class="form-control" maxlength="100" value="${command?.guid}" />
                </div>
                <div class="form-group">
                    <label for="name"><cl:required><g:message code="collection.name.label" default="Name"/></cl:required><cl:helpText code="${entityNameLower}.name"/></label>
                    <g:textField name="name" class="form-control" value="${command?.name}"/>
                </div>
                <div class="form-group">
                    <label for="acronym"><g:message code="collection.acronym.label" default="Acronym"/><cl:helpText code="providerGroup.acronym"/></label>
                    <g:textField name="acronym"  class="form-control" maxlength="45" value="${command?.acronym}" />

                </div>
                <g:if test="${command.ENTITY_TYPE == 'DataResource'}">
                    <div class="form-group">
                        <label for="gbifDoi"><g:message code="collection.gbifDoi.label" default="DOI"/></label>
                        <g:textField name="gbifDoi" class="form-control" maxlength="45" value="${command?.gbifDoi}" />
                    </div>
                    <div class="form-group">
                        <label for="resourceType"><g:message code="collection.resourceType.label" default="Resource type"/> <cl:helpText code="providerGroup.resourceType"/></label>
                        <g:select name="resourceType" class="form-control"
                                  from="${DataResource.resourceTypeList}"
                                  value="${command.resourceType}" />
                    </div>
                </g:if>
                <g:if test="${command.ENTITY_TYPE == 'DataProvider'}">
                    <div class="form-group">
                        <label for="resourceType"><g:message code="dataprovider.gbif.country" default="GBIF Attribution" /> <cl:helpText code="dataprovider.gbifCountryToAttribute" default="Select the country to attribute within GBIF.org as the publishing country"/></label>
                        <g:countrySelect id="country"  class="form-control" name="gbifCountryToAttribute" value="${command?.gbifCountryToAttribute}"
                                         noSelection="['':'-Leave empty for international organisations-']"/>
                    </div>
                </g:if>
                <g:if test="${command.ENTITY_TYPE == 'Collection'}">
                    <!-- institution -->
                    <div class="form-group">
                        <label for="institution.id"><g:message code="collection.institution.label" default="Institution"/><cl:helpText code="collection.institution"/></label>
                        <g:select name="institution.id" class="form-control"
                                  from="${Institution.list([sort:'name'])}"
                                  optionKey="id"
                                  noSelection="${['null':'Select an institution']}"
                                  value="${command.institution?.id}" />
                    </div>
                </g:if>
                <g:if test="${command.ENTITY_TYPE == 'DataResource'}">
                    <!-- data provider -->
                    <div class="form-group">
                        <label for="dataProvider.id"><g:message code="dataResource.dataProvider.label" default="Data provider"/><cl:helpText code="dataResource.dataProvider"/></label>
                        <g:select name="dataProvider.id" class="form-control"
                                  from="${DataProvider.list([sort:'name'])}"
                                  optionKey="id"
                                  noSelection="${['null':'Select a data provider']}"
                                  value="${command.dataProvider?.id}" />
                    </div>
                    <!-- institution -->
                    <div class="form-group">
                        <label for="institution.id"><g:message code="institution.dataProvider.label" default="Institution"/><cl:helpText code="dataResource.institution"/></label>
                        <g:select name="institution.id" class="form-control"
                                  from="${Institution.list([sort:'name'])}"
                                  optionKey="id"
                                  noSelection="${['null':'Select an institution']}"
                                  value="${command.institution?.id}"/>
                    </div>
                </g:if>
                <!-- ALA partner -->
                <cl:ifGranted role="${ProviderGroup.ROLE_ADMIN}">
                    <div class="checkbox">
                        <label for="isALAPartner">
                            <g:checkBox name="isALAPartner" value="${command?.isALAPartner}" />
                            <g:message code="providerGroup.isALAPartner.label" default="=Is Atlas Partner" />
                        </label>
                    </div>
                </cl:ifGranted>
                <!-- network membership -->
                <div class="form-group">
                    <label for="networkMembership"><g:message code="providerGroup.networkMembership.label" default="Belongs to" /><cl:helpText code="providerGroup.networkMembership"/></label>
                    <cl:checkboxSelect name="networkMembership" from="${ProviderGroup.networkTypes}" value="${command?.networkMembership}" multiple="yes" valueMessagePrefix="providerGroup.networkMembership" noSelection="['': '']" />
                </div>
                <!-- web site url -->
                <div class="form-group">
                    <label for="websiteUrl"><g:message code="providerGroup.websiteUrl.label" default="Website Url" /><cl:helpText code="providerGroup.websiteUrl"/></label>
                    <g:textField name="websiteUrl"  class="form-control" value="${command?.websiteUrl}" />
                </div>
                <!-- notes -->
                <div class="form-group">
                    <label for="notes"><g:message code="providerGroup.notes.label" default="Notes" /><cl:helpText code="collection.notes"/></label>
                    <g:textArea name="notes" cols="40"  class="form-control" rows="${cl.textAreaHeight(text:command.notes)}" value="${command?.notes}" />
                </div>
                <div class="buttons">
                    <span class="button"><input type="submit" name="_action_updateBase" value="Update" class="save btn btn-success"></span>
                    <span class="button"><input type="submit" name="_action_cancel" value="Cancel" class="cancel btn btn-default"></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
