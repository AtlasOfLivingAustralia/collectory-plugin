<!-- GBIF integration -->
<div class="well">
  <h2><g:message code="data provider.show.gbif.sync" default="GBIF synchronisation" /></h2>
  <p class="pull-right">
    <g:if test="${!instance.gbifRegistryKey}">
      <g:link controller="${controller}" action="registerGBIF" class="btn btn-default" id="${instance.id}">Register with GBIF</g:link>
      <g:link controller="${controller}" action="registerGBIF" class="btn btn-default" id="${instance.id}" params="[syncDataResources:true, syncContacts:true]">Register with GBIF & sync data resources</g:link>
    </g:if>
    <g:else>
      <g:link controller="${controller}"  action="updateGBIF" class="btn btn-default" id="${instance.id}">Update GBIF</g:link>
      <g:link controller="${controller}"  action="updateGBIF" class="btn btn-default" id="${instance.id}" params="[syncDataResources:true, syncContacts:true]">Update GBIF & sync resources</g:link>
    </g:else>
  </p>
  <p><span class="category">GBIF registry key:</span> ${instance.gbifRegistryKey ?: 'Not registered with GBIF'}</p>
  <cl:editButton uid="${instance.uid}" page="/shared/gbif"/>
</div>
