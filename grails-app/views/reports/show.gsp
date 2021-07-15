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
            <h1><g:message code="reports.li.reports" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
              <table class="table table-striped table-bordered">
                <colgroup><col width="40%"/><col width="10%"/><col width="50%"/></colgroup>

                <tr class="reportGroupTitle"><td colspan="3"><g:message code="reports.show.tr01" /></td></tr>
                <tr><td><g:message code="reports.show.tr02" /></td><td>${reports.totalCollections}</td><td></td></tr>
                <tr><td><g:message code="reports.show.tr03" /></td><td>${reports.totalInstitutions}</td><td></td></tr>
                <tr><td><g:message code="reports.show.tr04" /></td><td>${reports.totalContacts}</td><td></td></tr>
                <tr><td><g:message code="reports.show.tr05" /></td><td>${reports.totalLogons}</td><td></td></tr>
              </table>

              <h3><g:message code="reports.show.title01" /></h3>
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

              <h3><g:message code="reports.show.title02" /></h3>
              <table class="table table-striped table-bordered">
                <tr><cl:totalAndPercent label="Collections with no contacts" with="${reports.collectionsWithoutContacts}" total="${reports.totalCollections}"/></tr>
                <tr><cl:totalAndPercent label="Collections with no email contacts" with="${reports.collectionsWithoutEmailContacts}" total="${reports.totalCollections}"/></tr>
                <tr><cl:totalAndPercent label="Institutions with no contacts" with="${reports.institutionsWithoutContacts}" total="${reports.totalInstitutions}"/></tr>
                <tr><cl:totalAndPercent label="Institutions with no email contacts" with="${reports.institutionsWithoutEmailContacts}" total="${reports.totalInstitutions}"/></tr>
              </table>

              <h3><g:message code="reports.show.title03" /></h3>
              <table class="table table-striped table-bordered">
                <tr><cl:totalAndPercent label="Collections with infosource data" with="${reports.collectionsWithInfosource}" total="${reports.totalCollections}"/></tr>
              </table>

              <h3><g:message code="reports.show.title04" /></h3>
              <table class="table table-striped table-bordered">
                <tr><td><g:message code="reports.show.table0101" /></td><td>${reports.totalLogins}</td><td></td></tr>
                <tr><td><g:message code="reports.show.table0102" /></td><td>${reports.uniqueLogins}</td><td></td></tr>
                <tr><td><g:message code="reports.show.table0103" /></td><td>${reports.supplierLogins}</td><td></td></tr>
                <tr><td><g:message code="reports.show.table0104" /></td><td>${reports.uniqueSupplierLogins}</td><td></td></tr>
                <tr><td><g:message code="reports.show.table0105" /></td><td>${reports.curatorViews}</td><td></td></tr>
                <tr><td><g:message code="reports.show.table0106" /></td><td>${reports.curatorPreviews}</td><td></td></tr>
                <tr><td><g:message code="reports.show.table0107" /></td><td>${reports.curatorEdits}</td><td></td></tr>

                <tr class="reportGroupTitle"><td><g:message code="reports.show.table0201" /></td>
                  <!-- put first member on same line as title -->
                  <td colspan="2">
                    <g:if test="${reports.partners.size() > 0}">${reports.partners[0].name}</g:if><g:else>None</g:else>
                  </td></tr>
                <g:each var="p" in="${reports.partners}" status="i">
                  <!-- skip first member -->
                  <g:if test="${i > 0}"><tr><td></td><td colspan="2">${p.name}</td></tr></g:if>
                </g:each>

                <tr class="reportGroupTitle"><td><g:message code="reports.chah" /></td>
                  <!-- put first member on same line as title -->
                  <td colspan="2">
                    <g:if test="${reports.chahMembers.size() > 0}">${reports.chahMembers[0].name}</g:if><g:else>None</g:else>
                  </td></tr>
                <g:each var="p" in="${reports.chahMembers}" status="i">
                  <!-- skip first member -->
                  <g:if test="${i > 0}"><tr><td></td><td colspan="2">${p.name}</td></tr></g:if>
                </g:each>

                <tr class="reportGroupTitle"><td><g:message code="reports.chafc" /></td>
                  <!-- put first member on same line as title -->
                  <td colspan="2">
                    <g:if test="${reports.chafcMembers.size() > 0}">${reports.chafcMembers[0].name}</g:if><g:else>None</g:else>
                  </td></tr>
                <g:each var="p" in="${reports.chafcMembers}" status="i">
                  <!-- skip first member -->
                  <g:if test="${i > 0}"><tr><td></td><td colspan="2">${p.name}</td></tr></g:if>
                </g:each>

                <tr class="reportGroupTitle"><td><g:message code="reports.chaec" /></td>
                  <!-- put first member on same line as title -->
                  <td colspan="2">
                    <g:if test="${reports.chaecMembers.size() > 0}">${reports.chaecMembers[0].name}</g:if><g:else>None</g:else>
                  </td></tr>
                <g:each var="p" in="${reports.chaecMembers}" status="i">
                  <!-- skip first member -->
                  <g:if test="${i > 0}"><tr><td></td><td colspan="2">${p.name}</td></tr></g:if>
                </g:each>

                <tr class="reportGroupTitle"><td><g:message code="reports.chacm" /></td>
                  <!-- put first member on same line as title -->
                  <td colspan="2">
                    <g:if test="${reports.amrrnMembers.size() > 0}">${reports.amrrnMembers[0].name}</g:if><g:else>None</g:else>
                  </td></tr>
                <g:each var="p" in="${reports.amrrnMembers}" status="i">
                  <!-- skip first member -->
                  <g:if test="${i > 0}"><tr><td></td><td colspan="2">${p.name}</td></tr></g:if>
                </g:each>

                <tr class="reportGroupTitle"><td><g:message code="reports.camd" /></td>
                  <!-- put first member on same line as title -->
                  <td colspan="2">
                    <g:if test="${reports.camdMembers.size() > 0}">${reports.camdMembers[0].name}</g:if><g:else>None</g:else>
                  </td></tr>
                <g:each var="p" in="${reports.camdMembers}" status="i">
                  <!-- skip first member -->
                  <g:if test="${i > 0}"><tr><td></td><td colspan="2">${p.name}</td></tr></g:if>
                </g:each>

              </table>
            </div>
        </div>
    </body>
</html>
