<%@ page import="grails.converters.JSON; au.org.ala.collectory.ProviderGroup; au.org.ala.collectory.DataProvider" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="layout" content="${grailsApplication.config.skin.layout}" />
    <title>
        <g:if test="${repatriate}">
            <g:message code="manage.repatriate.title" default="Repatriate datasets" />
        </g:if>
        <g:else>
        <g:message code="manage.extload.title" />
        </g:else>
    </title>
</head>
<body>
<h1>
<g:message code="manage.repatriate.title01" />
</h1>

<div class="btn-toolbar">
    <ul class="btn-group">
        <li class="btn btn-default"><cl:homeLink/></li>
    </ul>
</div>
<div class="row">
    <div id="baseForm" class="col-md-8">
        <g:form action="searchForRepatResources" controller="manage">
            <g:hiddenField name="configuration.guid" value="${configuration.guid}"/>
            <div class="form-group hide">
                <label for="adaptorString"><g:message code="manage.extload.label04" /><cl:helpText code="manage.extload.label04.help"/></label>
                <g:select name="adaptorString" class="form-control" from="${adaptors}" optionKey="adaptorString" optionValue="name" value="${configuration.adaptorString}"/>
            </div>
            <div class="form-group hide">
                <label for="endpoint"><g:message code="manage.extload.label05" /><cl:helpText code="manage.extload.label05.help"/></label>
                <g:field name="endpoint" class="form-control" type="url" value="${configuration.endpoint}"/>
            </div>
            <div class="form-group hide">
                <label for="uniqueKeyTerm"><g:message code="manage.extload.label05" /><cl:helpText code="manage.extload.label05.help"/></label>
                <g:field name="uniqueKeyTerm" class="form-control" type="url" value="${configuration.uniqueKeyTerm}"/>
            </div>
            <div class="form-group">
                <label for="country"><g:message code="manage.repatriationCountry.label06" /><cl:helpText code="manage.extload.label06.help"/></label>
                <g:select name="country" class="form-control" from="${countryMap.entrySet()}" optionKey="key" optionValue="value" values="${configuration.country}" value="${params.country}"/>
            </div>
            <div class="form-group hide">
                <label for="recordType"><g:message code="manage.extload.label07" /><cl:helpText code="manage.extload.label07.help"/></label>
                <g:select name="recordType" class="form-control" from="${datasetTypeMap.entrySet()}" optionKey="key" optionValue="value" values="${configuration.recordType}"/>
            </div>
            <div class="form-group hide">
                <label for="name"><g:message code="manage.extload.label01" /><cl:helpText code="manage.extload.label01.help"/></label>
                <g:field name="name" class="form-control" type="text" value="${configuration.name}"/>
            </div>
            <div class="form-group hide">
                <label for="description"><g:message code="manage.extload.label02" /><cl:helpText code="manage.extload.label02.help"/></label>
                <g:field name="description" class="form-control" type="text" size="64" value="${configuration.description}"/>
            </div>
            <div class="form-group">
                <label for="dataProviderUid"><g:message code="manage.extload.label03" /><cl:helpText code="manage.extload.label03.help"/></label>
                <g:select name="dataProviderUid"
                          class="form-control"
                          from="${dataProviders}"
                          optionKey="uid"
                          optionValue="name"
                          value="${configuration.dataProviderUid}"
                          noSelection="${['':'Optionally select a provider...']}"
                />
            </div>
            <div class="form-inline">
                <label for="maxNoOfDatasets"><g:message code="manage.extload.label13" /><cl:helpText code="manage.extload.label13.help"/></label>
                <g:field type="number" name="maxNoOfDatasets" class="form-control form-control-sm" value="25" />
            </div>
            <br/>
            <div class="form-inline">
                <label for="minRecordCount"><g:message code="manage.extload.label11" /><cl:helpText code="manage.extload.label11.help"/></label>
                <g:field type="number" name="minRecordCount" class="form-control form-control-sm" value="10000" />
            </div>
            <br/>
            <div class="form-inline">
                <label for="maxRecordCount"><g:message code="manage.extload.label12" /><cl:helpText code="manage.extload.label12.help"/></label>
                <g:field type="number" name="maxRecordCount" class="form-control form-control-sm" value="1000000" />
            </div>
            <div class="form-group hide">
                <label for="username"><g:message code="manage.extload.label08" /><cl:helpText code="manage.extload.label08.help"/></label>
                <g:field type="text" name="username" class="form-control" value="${grailsApplication.config.gbifApiUser}" />
            </div>
            <div class="form-group hide">
                <label for="password"><g:message code="manage.extload.label09" /><cl:helpText code="manage.extload.label09.help"/></label>
                <g:field type="password" name="password" class="form-control" value="${grailsApplication.config.gbifApiPassword}" />
            </div>
            <div>
                <span class="button"><input type="submit" name="performReview" value="Review" class="save btn btn-default"></span>
            </div>
        </g:form>
    </div>
    <div class="well col-md-4">
        <p>
            <g:message code="manage.repatriate.des01" />
            <g:message code="manage.repatriate.des02" />
        </p>
        <p>
            <g:message code="manage.repatriate.des03" />
        </p>
    </div>
</div>
</body>
</html>