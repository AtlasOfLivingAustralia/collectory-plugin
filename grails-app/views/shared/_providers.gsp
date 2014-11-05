<%@ page import="au.org.ala.collectory.ProviderGroup" %>
<div class="show-section  well">
  <h2><g:message code="shared.providers.title01" /></h2>
  <p><g:message code="shared.providers.des01" args="[ProviderGroup.textFormOfEntityType(instance.uid)]" />.</p>
  <ul class="fancy">
    <g:each in="${instance.listProviders()}" var="prov">
      <g:set var="pg" value="${ProviderGroup._get(prov)}"/>
      <g:if test="${pg}">
        <g:set var="isProvider" value="${prov[0..1] == 'dp'}"/>
        <li><g:link controller="${cl.controllerFromUid(uid:prov)}" action="show" id="${prov}">${pg.name}</g:link> (${isProvider ? 'provider' : 'resource'})</li>
        <g:if test="${isProvider}">
          <!-- list resources -->
          <ul class='resources'>
            <g:each in="${pg.resources}" var="res">
              <li>${res.name} (resource)</li>
            </g:each>
          </ul>
        </g:if>
      </g:if>
      <g:else><li><g:message code="shared.providers.li01" />!</li></g:else>
    </g:each>
    <!-- for collections try their institution -->
    <g:if test="${instance instanceof au.org.ala.collectory.Collection && instance.institution}">
      <g:each in="${instance.institution.listProviders()}" var="prov">
        <g:set var="pg" value="${ProviderGroup._get(prov)}"/>
        <g:if test="${pg}">
          <li><g:link controller="${cl.controllerFromUid(uid:prov)}" action="show" id="${prov}">${pg.name}</g:link> (${isProvider ? 'provider' : 'resource'}) - (via the institution)</li>
        </g:if>
        <g:else><li><g:message code="shared.providers.li01" />!</li></g:else>
      </g:each>
    </g:if>
  </ul>
  <div style="clear:both;"></div>
  <div>
      <p><g:message code="shared.providers.des02" />
      <g:link controller="dataResource" action="list"><g:message code="shared.providers.link01" /></g:link>
        <g:message code="shared.providers.des03" /> <strong><g:message code="shared.providers.des04" /></strong>.</p>
  </div>
</div>
