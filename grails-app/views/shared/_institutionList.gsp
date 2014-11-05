<%@ page import="au.org.ala.collectory.ProviderGroup" %>
<div class="list">
    <table class="table table-striped table-bordered">
      <colgroup>
        <g:if test="${showALAPartner == 'true'}">
          <col width="50%"/><col width="10%"/><col width="12%"/><col width="30%"/>
        </g:if>
        <g:else>
          <col width="40%"/><col width="10%"/><col width="50%"/>
        </g:else>
      </colgroup>
        <thead>
            <tr>
                <g:sortableColumn property="name" title="${message(code: 'institution.name.label', default: 'Name')}" />
                <g:sortableColumn property="acronym" title="${message(code: 'institution.acronym.label', default: 'Acronym')}" />
                <g:if test="${showALAPartner == 'true'}">
                  <g:sortableColumn property="isALAPartner" title="${message(code: 'institution.isALAPartner.label', default: 'ALA Partner')}" />
                </g:if>
                <g:sortableColumn property="institutionType" title="${message(code: 'institution.institutionType.label', default: 'Type')}" />
            </tr>
        </thead>
        <tbody>
        <g:each in="${institutionInstanceList}" status="i" var="institutionInstance">
          <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

            <td><g:link controller="institution" action="show" id="${institutionInstance.uid}">${fieldValue(bean: institutionInstance, field: "name")}</g:link></td>

            <td>${fieldValue(bean: institutionInstance, field: "acronym")}</td>

            <g:if test="${showALAPartner == 'true'}">
              <td><g:if test="${institutionInstance?.isALAPartner}">Yes</g:if></td>
            </g:if>

            <td>${fieldValue(bean: institutionInstance, field: "institutionType")}</td>

          </tr>
        </g:each>
        </tbody>
    </table>
</div>
