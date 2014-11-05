<g:if test="${changes}">
  <div class="show-section   well">
    <h2><g:message code="shared.changes.title01" /></h2>
    <p><g:message code="shared.changes.des01" />.</p>
    <ul class=simple>
      <g:each in="${changes}" var="ch">
        <li><g:link controller='auditLogEvent' action='show' id='${ch.id}'>${ch.lastUpdated}: ${ch.actor}
          <cl:changeEventName event="${ch.eventName}" highlightInsertDelete="true"/> <strong>${ch.propertyName}</strong>
          <g:if test="${ch.className.endsWith('ContactFor')}">
            <g:if test="${ch.eventName == 'UPDATE'}"><g:message code="shared.changes.update" /></g:if>
            <g:elseif test="${ch.eventName == 'INSERT'}"><g:message code="shared.changes.insert" /></g:elseif>
            <g:else>Contact</g:else>
          </g:if>
        </g:link></li>
      </g:each>
    </ul>
    <div style="clear:both;"></div>
    <cl:editButton uid="${instance.uid}" action="showChanges"><g:message code="shared.changes.showchanges" /></cl:editButton>
  </div>
</g:if>
