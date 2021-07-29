<%@ page import="au.org.ala.collectory.ReportsController.ReportCommand" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <title><g:message code="reports.title" /></title>
    </head>
    <body>
        <div class="btn-toolbar">
            <ul class="btn-group">
                <li class="btn btn-default"><cl:homeLink/></li>
                <li class="btn btn-default"><span class="glyphicon glyphicon-th-list"></span><g:link class="list" action="list"> <g:message code="reports.li.reports"/></g:link></li>
            </ul>
        </div>
        <div class="body">
            <h1><g:message code="reports.data.title01" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
              <table class="table table-striped table-bordered">
                <colgroup><col width="40%"/><col width="10%"/><col width="50%"/></colgroup>

                <tr class="reportGroupTitle"><td colspan="3"><g:message code="reports.data.tr01" /></td></tr>
                <tr><td><g:message code="reports.data.tr02" /></td><td>${reports.totalCollections}</td><td></td></tr>
                <tr><td><g:message code="reports.data.tr03" /></td><td>${reports.totalInstitutions}</td><td></td></tr>
                <tr><td><g:message code="reports.data.tr04" /></td><td>${reports.totalDataProviders}</td><td></td></tr>
                <tr><td><g:message code="reports.data.tr05" /></td><td>${reports.totalDataResources}</td><td></td></tr>
                <tr><td><g:message code="reports.data.tr06" /></td><td>${reports.totalDataHubs}</td><td></td></tr>
                <tr><td><g:message code="reports.data.tr07" /></td><td>${reports.totalContacts}</td><td></td></tr>
              </table>

              <h3><g:message code="reports.data.title02" /></h3>
              <table class="table table-striped table-bordered">
                <tr><cl:totalAndPercent label="Collections with no collection type" without="${reports.collectionsWithType}" total="${reports.totalCollections}"/></tr>
                <tr><cl:totalAndPercent label="Collections with no focus" without="${reports.collectionsWithFocus}" total="${reports.totalCollections}"/></tr>
                <tr><cl:totalAndPercent label="Collections with no description" without="${reports.collectionsWithDescriptions}" total="${reports.totalCollections}"/></tr>
                <tr><cl:totalAndPercent label="Collections with no keywords" without="${reports.collectionsWithKeywords}" total="${reports.totalCollections}"/></tr>
                <tr><cl:totalAndPercent label="Collections with no provider codes" without="${reports.collectionsWithProviderCodes}" total="${reports.totalCollections}"/></tr>
                <tr><cl:totalAndPercent label="Collections with no geo. description" without="${reports.collectionsWithGeoDescription}" total="${reports.totalCollections}"/></tr>
                <tr><cl:totalAndPercent label="Collections with no size" without="${reports.collectionsWithNumRecords}" total="${reports.totalCollections}"/></tr>
                <tr><cl:totalAndPercent label="Collections with no digitised size" without="${reports.collectionsWithNumRecordsDigitised}" total="${reports.totalCollections}"/></tr>

              </table>

              <h3><g:message code="reports.data.title03" /></h3>
              <table class="table table-striped table-bordered">
                <tr><cl:totalAndPercent label="Collections with no contacts" with="${reports.collectionsWithoutContacts}" total="${reports.totalCollections}"/></tr>
                <tr><cl:totalAndPercent label="Collections with no email contacts" with="${reports.collectionsWithoutEmailContacts}" total="${reports.totalCollections}"/></tr>
                <tr><cl:totalAndPercent label="Institutions with no contacts" with="${reports.institutionsWithoutContacts}" total="${reports.totalInstitutions}"/></tr>
                <tr><cl:totalAndPercent label="Institutions with no email contacts" with="${reports.institutionsWithoutEmailContacts}" total="${reports.totalInstitutions}"/></tr>

              </table>
            </div>
        </div>
    </body>
</html>
