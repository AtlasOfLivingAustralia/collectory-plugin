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

casProperties="casServerLoginUrl,serverName,centralServer,casServerName,uriFilterPattern,uriExclusionFilter,authenticateOnlyIfLoggedInFilterPattern,casServerLoginUrlPrefix,gateway,casServerUrlPrefix,contextPath"
serverName="http://localhost:8080"
contextPath="collectory-app"
grails.serverURL="http://localhost:8080/collectory-app"
casServerName="https://auth.ala.org.au"
uriExclusionFilterPattern="/images.*,/css.*,/js.*,/less.*,/theme.*,/img.*"
casServerLoginUrl="https://auth.ala.org.au/cas/login"
gateway=false
casServerUrlPrefix="https://auth.ala.org.au/cas"
security.cas.logoutUrl="https://auth.ala.org.au/cas/logout"
uriFilterPattern="/admin.*,/collection.*,/institution.*,/contact.*,/reports.*,/providerCode.*,/providerMap.*,/dataProvider.*,/dataResource.*,/dataHub.*,/manage/.*"
//uriFilterPattern=/dummy
//Security bypass
security.cas.bypass=false
security.cas.casServerUrlPrefix = "https://auth.ala.org.au/cas"

//Data directories
repository.location.images="/data/collectory-app/data"

grails.resources.adhoc.patterns = ['/images/*', '/data/*', '/img/*', '/theme/default/*', '/css/*', '/js/*', '/plugins/*']

//External API access
api_key="to_be_added"

//Biocache integration
biocacheUiURL="http://biocache.ala.org.au"
biocacheServicesUrl="http://biocache.ala.org.au/ws"

//Skinning
ala.skin="generic"

//DB config
dataSource.username="root"
dataSource.password="password"
dataSource.url="jdbc:mysql://localhost:3306/collectory?autoReconnect=true&connectTimeout=0"

//Map config
//collectionsMap.centreMapLon=-3.7036
//collectionsMap.centreMapLat=40.4169
collectionsMap.defaultZoom=4

//Project name
projectNameShort="Australia"
projectName="Atlas of Living Australia"
regionName="Australia"

//EML config
eml.organizationName="Atlas of Living Australia"
eml.deliveryPoint="CSIRO Ecosystems Services"
eml.city="Canberra"
eml.administrativeArea="ACT"
eml.postalCode="2605"
eml.country="Australia"

//The name displayed top left of header
skin.orgNameLong="Atlas of Living Australia"
skin.orgNameShort="ALA"

//Disable UI components
disableOverviewMap=false
disableAlertLinks=false
disableLoggerLinks=false

//logger URL
loggerURL="http://logger.ala.org.au/service"

chartsBgColour="#fffef7"
