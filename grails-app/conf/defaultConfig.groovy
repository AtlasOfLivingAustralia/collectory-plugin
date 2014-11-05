//# Copyright (C) 2011 Atlas of Living Australia
//#  All Rights Reserved.
//#
//#  The contents of this file are subject to the Mozilla Public
//#  License Version 1.1 (the "License"); you may not use this file
//#  except in compliance with the License. You may obtain a copy of
//#  the License at http://www.mozilla.org/MPL/
//#
//#  Software distributed under the License is distributed on an "AS
//#  IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
//#  implied. See the License for the specific language governing
//#  rights and limitations under the License.
//#casProperties=casServerLoginUrl,serverName,centralServer,casServerName,uriFilterPattern,uriExclusionFilter,authenticateOnlyIfLoggedInFilterPattern,casServerLoginUrlPrefix,gateway,casServerUrlPrefix,contextPath

//serverName = "http://devt.ala.org.au:8080"

serverName = "http://localhost:8080"
contextPath = "collectory-app"
casServerName = "https://auth.ala.org.au"
uriFilterPattern = "/admin.*,/collection.*,/institution.*,/contact.*,/reports.*,/providerCode.*,/providerMap.*,/dataProvider.*,/dataResource.*,/dataHub.*,/manage/.*"

uriExclusionFilterPattern = "/images.*,/css.*,/js.*,/less.*,/img.*,/theme.*"

authenticateOnlyIfLoggedInFilterPattern = ""
casServerLoginUrl = "https://auth.ala.org.au/cas/login"
gateway = true
casServerUrlPrefix = "https://auth.ala.org.au/cas"

//Security bypass
security.cas.bypass = true
ala.skin = "generic"

//skin.header=classpath:/header.gsp
//skin.header=http://localhost/~dos009/headers.gsp


map.cloudmade.key = "BC9A493B41014CAABB98F0471D759707"
map.defaultFacetMapColourBy = "basis_of_record"
map.pointColour = "df4a21"
map.zoomOutsideScopedRegion = true
map.defaultLatitude
map.defaultLongitude
map.defaultZoom
// 3rd part WMS layer to show on maps. TODO: Allow multiple overlays
map.overlay.url
map.overlay.name


// prod version follows
repository.location.images = "/data/collectory-app/data"
grails.serverURL = "http://localhost:8080/collectory-app"
//grails.serverURL = "http://devt.ala.org.au:8080/collectory"
biocacheServicesUrl = "http://biocache.ala.org.au/ws"
biocache.baseURL = "http://biocache.ala.org.au/"
google.maps.v2.key = "ABQIAAAAJdniJYdyzT6MyTJB-El-5RQumuBjAh1ZwCPSMCeiY49-PS8MIhSVhrLc20UWCGPHYqmLuvaS_b_FaQ"
security.cas.serverName = "https://auth.ala.org.au"
security.cas.context = "/cas"
ui.showChartsForInstitutions = true
api_key = "Venezuela"
useNewBiocache = true
biocache.records.url = "http://biocache.ala.org.au/"
biocache.search = "occurrences/search"
biocache.occurrences.json = "ws/occurrences/search.json"
dataSource.username = "alin"
dataSource.password = "alan32"
