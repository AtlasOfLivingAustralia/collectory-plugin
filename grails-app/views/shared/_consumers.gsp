<%@ page import="au.org.ala.collectory.ProviderGroup" %>
<div class="show-section  well">
  <h2><g:message code="shared.consumers.title01" /></h2>
  <p><g:message code="shared.consumers.des01" args="[ProviderGroup.textFormOfEntityType(instance.uid)]" />.
  <br/>
    <g:message code="shared.consumers.des02" />.
  </p>
  <ul class="fancy">
    <g:each in="${instance.listConsumers()}" var="con">
      <g:set var="pg" value="${ProviderGroup._get(con)}"/>
      <g:if test="${pg}">
        <li><g:link controller="${cl.controllerFromUid(uid:con)}" action="show" id="${con}">${pg.name}</g:link> (${con[0..1] == 'in' ? 'institution' : 'collection'})</li>
      </g:if>
      <g:else><li><g:message code="shared.consumers.des03" />!</li></g:else>
    </g:each>
  </ul>
  <div style="clear:both;"></div>
  <div>
      <span class="buttons long"><g:link class="edit btn btn-default" action='editConsumers' params="[source:'co']" id="${instance.uid}"><g:message code="shared.consumers.link01" />&nbsp;</g:link></span>
      <span class="buttons long"><g:link class="edit btn btn-default" action='editConsumers' params="[source:'in']" id="${instance.uid}"><g:message code="shared.consumers.link02" /></g:link></span>
  </div>
</div>
