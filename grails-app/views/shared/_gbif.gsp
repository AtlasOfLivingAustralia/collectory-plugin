<!-- GBIF integration -->
<div class="well">
  <h2><g:message code="data provider.show.gbif.sync" default="GBIF synchronisation"/></h2>
  <cl:ifGranted role="${grailsApplication.config.gbifRegistrationRole}">
    <p class="pull-right">
      <g:if test="${!instance.gbifRegistryKey}">
        <g:link controller="${controller}" action="registerGBIF" class="btn btn-default" id="${instance.id}" params="[syncDataResources:false, syncContacts:true]">Register with GBIF</g:link>
        <g:link controller="${controller}" action="registerGBIF" class="btn btn-default" id="${instance.id}" params="[syncDataResources:true, syncContacts:true]">Register with GBIF & sync data resources</g:link>
      </g:if>
      <g:else>
        <g:link controller="${controller}"  action="updateGBIF" class="btn btn-default" id="${instance.id}" params="[syncDataResources:false, syncContacts:true]">Update GBIF</g:link>
        <g:link controller="${controller}"  action="updateGBIF" class="btn btn-default" id="${instance.id}" params="[syncDataResources:true, syncContacts:true]">Update GBIF & sync resources</g:link>
      </g:else>
    </p>
  </cl:ifGranted>

  <p><span class="category">GBIF registry key:</span> ${instance.gbifRegistryKey ?: 'Not registered with GBIF'}</p>
  <p><span class="category">GBIF country attribute (which country to associate the publisher data with in GBIF):</span> ${instance.gbifCountryToAttribute ?: 'Not supplied'}</p>
  <g:if test="${instance.gbifRegistryKey}">
    <p>
      <span class="category">GBIF Link:</span>
      <a href="${grailsApplication.config.gbifWebsite}/publisher/${instance.gbifRegistryKey}">View details on GBIF.org</a>
    </p>
    <p>
      <span class="category">GBIF webservices Link:</span>
      <a href="${grailsApplication.config.gbifApiUrl}/organization/${instance.gbifRegistryKey}">View details on GBIF.org</a>
    </p>
  </g:if>

  <cl:editButton uid="${instance.uid}" page="/shared/gbif"/>
</div>