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
                <g:sortableColumn property="uid" title="${message(code: 'institution.uid.label', default: 'UID')}" />
                <g:if test="${showALAPartner == 'true'}">
                  <g:sortableColumn property="isALAPartner" title="${message(code: 'institution.isALAPartner.label', default: 'ALA Partner')}" />
                </g:if>
                <th style="text-align:center;">${message(code: 'institution.linked.resources.label', default: 'Number of linked resources')}</th>
                <g:sortableColumn property="institutionType" title="${message(code: 'institution.institutionType.label', default: 'Type')}" />
                <g:if test="${grailsApplication.config.gbifRegistrationEnabled == 'true'}">
                    <th style="text-align:center;">${message(code: 'institution.gbif.label', default: 'GBIF')}</th>
                </g:if>
            </tr>
        </thead>
        <tbody>
        <g:each in="${institutionInstanceList}" status="i" var="institutionInstance">
          <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

            <td><g:link controller="institution" action="show" id="${institutionInstance.uid}">${fieldValue(bean: institutionInstance, field: "name")}</g:link></td>

            <td>${fieldValue(bean: institutionInstance, field: "acronym")}</td>

            <td>${fieldValue(bean: institutionInstance, field: "uid")}</td>

            <g:if test="${showALAPartner == 'true'}">
              <td><g:if test="${institutionInstance?.isALAPartner}">Yes</g:if></td>
            </g:if>

            <td>${institutionInstance?.getLinkedDataResources()?.size()}</td>

            <td>${fieldValue(bean: institutionInstance, field: "institutionType")}</td>

              <g:if test="${grailsApplication.config.gbifRegistrationEnabled == 'true'}">

                  <td class="text-nowrap">
                      <g:if test="${fieldValue(bean: institutionInstance, field: "gbifRegistryKey")}">
                          <g:link class="btn btn-default" controller="institution" action="updateGBIF" id="${institutionInstance.uid}"
                                  onclick="return confirm('${message(code: 'default.button.update.institution.confirm.message', default: 'Are you sure you want to update this institution?')}');">
                              ${message(code: 'institution.gbif.update', default: 'Update')}
                          </g:link> |
                          <a href="https://gbif.org/publisher/${institutionInstance.gbifRegistryKey}">
                              ${message(code: 'institution.gbif.show', default: 'Show')}
                          </a>
                      </g:if>
                      <g:else>
                          <g:link class="btn btn-default" controller="institution" action="registerGBIF"
                                  id="${institutionInstance.uid}"
                                  onclick="return confirm('${message(code: 'default.button.register.institution.confirm.message', default: 'Are you sure you want to register this institution?')}');">
                              ${message(code: 'institution.gbif.register', default: 'Register')}
                          </g:link>
                      </g:else>
                  </td>

              </g:if>

          </tr>
        </g:each>
        </tbody>
    </table>
</div>
