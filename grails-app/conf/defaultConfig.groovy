serverName = 'http://localhost:8080'
grails.serverURL = 'http://localhost:8080/collectory'
security.cas.bypass = true
security.apikey.checkEnabled = false
workDir='/data/dwc-archive/work'
uploadFilePath='/data/collectory-plugin/upload'
google.apikey = "PLEASE_SPECIFY_IN_CONFIG_FILE"
institution.codeLoaderURL='file:/data/collectory/bootstrap/institution_codes.xml'

//cartodb.pattern = 'http://{s}.api.cartocdn.com/light_all/{z}/{x}/{y}.png'
cartodb.pattern = 'https://cartocdn_{s}.global.ssl.fastly.net/light_all/{z}/{x}/{y}.png'

dataResourceChartsJSON = "['country','state','species_group','assertions','type_status','year','month']"
dataProviderChartsJSON = "['country','state','species_group','assertions','type_status','year','month']"
collectionChartsJSON  = "['country','state','species_group','assertions','type_status','year','month']"
institutionChartsJSON  = "['country','state','species_group','assertions','type_status','year','month']"

// GBIF config
gbifApiUser=dmartin
gbifApiPassword=password
gbifApiUrl='https://api.gbif-uat.org/v1'
gbifWebsite='https://www.gbif.org'
gbifRegistrationRole = "ROLE_GBIF_REGISTRATION"
gbifRegistrationDryRun = false
gbifLicenceMappingUrl = this.class.getResource("/default-gbif-licence-mapping.json").toString()
gbifOrphansPublisherID = "" //the ID of data publisher in GBIF to use if there isnt an owner of the resource
gbifEndorsingNodeKey = "" //get this from GBIF
gbifInstallationKey = "" //get this from GBIF

// RIF-CS
rifcs.excludeBounds = false

// Templates for exposed archives
resource.publicArchive.url.template = "https://biocache.ala.org.au/archives/@UID@/@UID@_ror_dwca.zip"
resource.gbifExport.url.template = "https://biocache.ala.org.au/archives/@UID@/@UID@_ror_dwca.zip"

//gbif URLs
gbif.citations.enabled=true
gbif.citation.lookup="https://www.gbif.org/api/resource/search?contentType=literature&gbifDatasetKey="
gbif.citation.search="https://www.gbif.org/resource/search?contentType=literature&gbifDatasetKey="