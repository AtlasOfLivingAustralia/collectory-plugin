<!-- external identifiers -->
<div class="show-section  well">
  <h2><g:message code="shared.ext.title01" /></h2>
  <ul class='simple'>
    <g:each in="${instance.externalIdentifiers}" var="id">
      <li><g:fieldValue bean="${id}" field="label"/><g:if test="${id.uri}"><a href="${id.uri}" target="_blank" class="external">&nbsp;<g:fieldValue bean="${id}" field="uri"/></a></g:if></li>
    </g:each>
  </ul>
  <div style="clear:both;"></div>
  <cl:editButton uid="${instance.uid}" page="/shared/editExternalIdentifiers"/>
</div>
