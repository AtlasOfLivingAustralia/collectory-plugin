<%@ page import="au.org.ala.collectory.CollectoryTagLib; java.text.DecimalFormat; java.text.SimpleDateFormat" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <meta name="layout" content="${grailsApplication.config.skin.layout}"/>
    <meta name="breadcrumbParent"
          content="${createLink(action: 'datasets', controller: 'public')},${message(code: 'breadcrumb.datasets')}"
    />
    <title><cl:pageTitle>${fieldValue(bean: instance, field: "name")}</cl:pageTitle></title>
    <r:require modules="jquery, fancybox, jquery_jsonp, jquery_ui_custom, jstree, taxonTree, datadumper, jquery_i18n, collectory, charts_plugin"/>
    <r:script>
        // define biocache server
        bieUrl = "${grailsApplication.config.bie.baseURL}";
        loadLoggerStats = ${!grailsApplication.config.disableLoggerLinks.toBoolean()};
        $(document).ready(function () {
            $("a#lsid").fancybox({
                'hideOnContentClick': false,
                'titleShow': false,
                'autoDimensions': false,
                'width': 600,
                'height': 180
            });
            $("a.current").fancybox({
                'hideOnContentClick': false,
                'titleShow': false,
                'titlePosition': 'inside',
                'autoDimensions': true,
                'width': 300
            });
        });
    </r:script>
    <script type="text/javascript" src="https://www.google.com/jsapi"></script>
    <script type="text/javascript">google.load('visualization', '1.0', {'packages':['corechart']});</script>
</head>
<body class="nav-datasets">
<div id="content">
<section id="header">
    <cl:pageOptionsPopup instance="${instance}"/>
    <section class="row">
        <div class="col-md-8">
            <cl:h1 value="${instance.name}"/>
            <g:set var="dp" value="${instance.dataProvider}"/>
            <g:if test="${dp}">
                <h2><g:link action="show" id="${dp.uid}">${dp.name}</g:link></h2>
            </g:if>
            <g:if test="${instance.institution}">
                <h2><g:link action="show" id="${instance.institution.uid}">${instance.institution.name}</g:link></h2>
            </g:if>
            <cl:valueOrOtherwise value="${instance.acronym}"><span
                    class="acronym"><g:message code="public.show.header.acronym"/>: ${fieldValue(bean: instance, field: "acronym")}</span></cl:valueOrOtherwise>
        <g:if test="${instance.pubDescription || instance.techDescription || instance.focus}">
            <h2><g:message code="public.des" /></h2>
        </g:if>
        <cl:formattedText>${fieldValue(bean: instance, field: "pubDescription")}</cl:formattedText>
        <cl:formattedText>${fieldValue(bean: instance, field: "techDescription")}</cl:formattedText>
        <cl:formattedText>${fieldValue(bean: instance, field: "focus")}</cl:formattedText>

        <cl:dataResourceContribution resourceType="${instance.resourceType}" status="${instance.status}" tag="p"/>

            <g:if test="${instance.geographicDescription}">
                <h2><g:message code="public.geographicDescription" default="Purpose"/></h2>
                <cl:formattedText>${fieldValue(bean: instance, field: "geographicDescription")}</cl:formattedText>
            </g:if>

        <g:if test="${instance.purpose}">
            <h2><g:message code="public.purpose" default="Purpose"/></h2>
            <cl:formattedText>${fieldValue(bean: instance, field: "purpose")}</cl:formattedText>
        </g:if>

        <g:if test="${instance.qualityControlDescription}">
            <h2><g:message code="public.qualityControlDescription" /></h2>
            <cl:formattedText>${fieldValue(bean: instance, field: "qualityControlDescription")}</cl:formattedText>
        </g:if>

        <g:if test="${instance.methodStepDescription}">
            <h2><g:message code="public.methodStepDescription" /></h2>
            <cl:formattedText>${fieldValue(bean: instance, field: "methodStepDescription")}</cl:formattedText>
        </g:if>

        <g:if test="${instance.contentTypes}">
            <h2><g:message code="public.sdr.content.label02" /></h2>
            <cl:contentTypes types="${instance.contentTypes}"/>
        </g:if>
        <h2><g:message code="public.sdr.content.label03" /></h2>
        <g:if test="${instance.citation}">
            <cl:formattedText>${fieldValue(bean: instance, field: "citation")}</cl:formattedText>
        </g:if>
        <g:else>
            <p><g:message code="public.sdr.content.des01" />.</p>
        </g:else>

        <g:if test="${instance.rights}">
            <h2><g:message code="public.sdr.content.label04" /></h2>
            <cl:formattedText>${fieldValue(bean: instance, field: "rights")}</cl:formattedText>
        </g:if>

        <g:if test="${instance.dataGeneralizations}">
            <h2><g:message code="public.sdr.content.label05" /></h2>
            <cl:formattedText>${fieldValue(bean: instance, field: "dataGeneralizations")}</cl:formattedText>
        </g:if>

        <g:if test="${instance.informationWithheld}">
            <h2><g:message code="public.sdr.content.label06" /></h2>
            <cl:formattedText>${fieldValue(bean: instance, field: "informationWithheld")}</cl:formattedText>
        </g:if>

        <g:if test="${instance.downloadLimit}">
            <h2><g:message code="public.sdr.content.label07" /></h2>

            <p><g:message code="public.sdr.content.des02" /> ${fieldValue(bean: instance, field: "downloadLimit")} <g:message code="public.sdr.content.des03" />.</p>
        </g:if>

        <div id="pagesContributed"></div>

        <g:if test="${instance.resourceType == 'website' && (instance.lastChecked || instance.dataCurrency)}">
            <h2><g:message code="public.sdr.content.label08" /></h2>

            <p><cl:lastChecked date="${instance.lastChecked}"/>
                <cl:dataCurrency date="${instance.dataCurrency}"/></p>
        </g:if>

        <g:if test="${!grailsApplication.config.disableLoggerLinks.toBoolean() && (instance.resourceType == 'website' || instance.resourceType == 'records')}">
            <div id='usage-stats'>
                <h2><g:message code="public.sdr.usagestats.labe" /></h2>

                <div id='usage'>
                    <p><g:message code="public.usage.des" />...</p>
                </div>
                <g:if test="${instance.resourceType == 'website'}">
                    <div id="usage-visualization" style="width: 600px; height: 200px;"></div>
                </g:if>
            </div>
        </g:if>

        <g:if test="${instance.resourceType == 'records'}">
            <h2><g:message code="public.sdr.content.label09" /></h2>

            <div>
                <p><span
                        id="numBiocacheRecords"><g:message code="public.sdr.content.des04" /></span> <g:message code="public.sdr.content.des05" />.
                <cl:lastChecked date="${instance.lastChecked}"/>
                <cl:dataCurrency date="${instance.dataCurrency}"/>
                </p>
                <cl:recordsLink
                        collection="${instance}"><g:message code="public.sdr.content.link01" /> ${instance.name} <g:message code="public.sdr.content.link02" />.</cl:recordsLink>
                <cl:downloadPublicArchive uid="${instance.uid}" available="${instance.publicArchiveAvailable}"/>
            </div>
        </g:if>
        <g:if test="${instance.resourceType == 'records'}">
            <div id="recordsBreakdown" class="section vertical-charts">
                <g:if test="${!grailsApplication.config.disableOverviewMap}">
                    <h3><g:message code="public.sdr.content.label10" /></h3>
                    <cl:recordsMapDirect uid="${instance.uid}"/>
                </g:if>
                <div id="tree" class="well"></div>
                <div id="charts"></div>
            </div>
        </g:if>
        <cl:lastUpdated date="${instance.lastUpdated}"/>
    </div><!--close column-one-->
        <section class="col-md-4">

            <g:if test="${dp?.logoRef?.file}">
                <g:link action="show" id="${dp.uid}">
                    <img class="institutionImage"
                         src='${resource(absolute: "true", dir: "data/dataProvider/", file: fieldValue(bean: dp, field: 'logoRef.file'))}'/>
                </g:link>
            </g:if>
            <g:elseif test="${instance?.logoRef?.file}">
                <img class="institutionImage"
                     src='${resource(absolute: "true", dir: "data/dataResource/", file: fieldValue(bean: instance, field: 'logoRef.file'))}'/>
            </g:elseif>

        <g:if test="${fieldValue(bean: instance, field: 'imageRef') && fieldValue(bean: instance, field: 'imageRef.file')}">
            <section>
                <img alt="${fieldValue(bean: instance, field: "imageRef.file")}"
                     src="${resource(absolute: "true", dir: "data/dataResource/", file: instance.imageRef.file)}"/>
                <cl:formattedText
                        pClass="caption">${fieldValue(bean: instance, field: "imageRef.caption")}</cl:formattedText>
                <cl:valueOrOtherwise value="${instance.imageRef?.attribution}"><p
                        class="caption">${fieldValue(bean: instance, field: "imageRef.attribution")}</p></cl:valueOrOtherwise>
                <cl:valueOrOtherwise value="${instance.imageRef?.copyright}"><p
                        class="caption">${fieldValue(bean: instance, field: "imageRef.copyright")}</p></cl:valueOrOtherwise>
            </section>
        </g:if>

        <div id="dataAccessWrapper" style="display:none;">
            <g:render template="dataAccess" model="[instance:instance]"/>
        </div>

        <g:if test="${instance.isVerified()}">
            <section class="public-metadata">
            <h5>
                <g:message code="public.verified" default="Verified dataset"/>
                <i class="fa fa-check-circle tooltips" style="color:green;"></i>
            </h5>
            </section>
        </g:if>

        <g:if test="${instance.gbifDoi}">
            <section class="public-metadata">
                <h4><g:message code="public.citations" default="Citations" /></h4>
                <div class="btn-group-vertical">
                <a class="btn btn-default" href="https://${instance.gbifDoi.replaceAll('doi:', 'doi.org/')}">
                    <i class="glyphicon glyphicon-info-sign"> </i>
                    ${instance.gbifDoi}
                </a>
                <g:if test="${instance.gbifRegistryKey}">
                    <citations:gbifLink gbifRegistryKey="${instance.gbifRegistryKey}"/>
                </g:if>
                </div>
            </section>
        </g:if>

        <g:if test="${instance.licenseType}">
            <section class="public-metadata">
            <h4><g:message code="public.license" default="Licence" /></h4>
            <p><cl:displayLicenseType type="${instance.licenseType}" version="${instance.licenseVersion}"/></p>
            </section>
        </g:if>

        <g:if test="${instance.beginDate}">
            <section class="public-metadata">
            <h4><g:message code="public.temporal" default="Temporal scope" /></h4>
            <p>${instance.beginDate}
                <g:if test="${instance.endDate}">
                    - ${instance.endDate}
                </g:if>
            </p>
            </section>
        </g:if>

        <!-- use parent location if the collection is blank -->
        <g:set var="address" value="${instance.address}"/>
        <g:if test="${address == null || address.isEmpty()}">
            <g:if test="${instance.dataProvider}">
                <g:set var="address" value="${instance.dataProvider?.address}"/>
            </g:if>
        </g:if>

        <g:if test="${address != null && !address?.isEmpty()}">
            <section class="public-metadata">
                <h4><g:message code="public.location" /></h4>

                <g:if test="${!address?.isEmpty()}">
                    <p>
                        <cl:valueOrOtherwise value="${address?.street}">${address?.street}<br/></cl:valueOrOtherwise>
                        <cl:valueOrOtherwise value="${address?.city}">${address?.city}<br/></cl:valueOrOtherwise>
                        <cl:valueOrOtherwise value="${address?.state}">${address?.state}</cl:valueOrOtherwise>
                        <cl:valueOrOtherwise value="${address?.postcode}">${address?.postcode}<br/></cl:valueOrOtherwise>
                        <cl:valueOrOtherwise value="${address?.country}">${address?.country}<br/></cl:valueOrOtherwise>
                    </p>
                </g:if>

                <g:if test="${instance.email}"><cl:emailLink>${fieldValue(bean: instance, field: "email")}</cl:emailLink><br/></g:if>
                <cl:ifNotBlank value='${fieldValue(bean: instance, field: "phone")}'/>
            </section>
        </g:if>

    <!-- contacts -->
        <g:if test="${instance.makeContactPublic}">
            %{-- added so that contact visibility on website is on data resource level --}%
            <g:set var="contacts" value="${instance.getContacts()}"/>
        </g:if>
        <g:else>
            <g:set var="contacts" value="${instance.getPublicContactsPrimaryFirst()}"/>
            <g:if test="${!contacts}">
                <g:set var="contacts" value="${instance.dataProvider?.getContactsPrimaryFirst()}"/>
            </g:if>
        </g:else>
        <g:render template="contacts" bean="${contacts}"/>

    <!-- web site -->
        <g:if test="${instance.resourceType == 'species-list'}">
            <section class="'public-metadata">
                <h4><g:message code="public.sdr.content.label12" /></h4>
                <div class="webSite">
                    <a class='external_icon' target="_blank"
                       href="${grailsApplication.config.speciesListToolUrl}${instance.uid}"><g:message code="public.sdr.content.link03" /></a>
                </div>
            </section>
        </g:if>
        <g:elseif test="${instance.websiteUrl}">
            <section class="public-metadata">
                <h4><g:message code="public.website" /></h4>
                <div class="webSite">
                    <a class='external_icon' target="_blank"
                       href="${instance.websiteUrl}"><g:message code="public.sdr.content.link04" /></a>
                </div>
            </section>
        </g:elseif>

    <!-- network membership -->
        <g:if test="${instance.networkMembership}">
            <section class="public-metadata">
                <h4><g:message code="public.network.membership.label" /></h4>
                <g:if test="${instance.isMemberOf('CHAEC')}">
                    <p><g:message code="public.network.membership.des01" /></p>
                    <img src="${resource(absolute: "true", dir: "data/network/", file: "butflyyl.gif")}"/>
                </g:if>
                <g:if test="${instance.isMemberOf('CHAH')}">
                    <p><g:message code="public.network.membership.des02" /></p>
                    <a target="_blank" href="http://www.chah.gov.au"><img
                            src="${resource(absolute: "true", dir: "data/network/", file: "CHAH_logo_col_70px_white.gif")}"/>
                    </a>
                </g:if>
                <g:if test="${instance.isMemberOf('CHAFC')}">
                    <p><g:message code="public.network.membership.des03" /></p>
                    <img src="${resource(absolute: "true", dir: "data/network/", file: "CHAFC_sm.jpg")}"/>
                </g:if>
                <g:if test="${instance.isMemberOf('CHACM')}">
                    <p><g:message code="public.network.membership.des04" /></p>
                    <img src="${resource(absolute: "true", dir: "data/network/", file: "chacm.png")}"/>
                </g:if>
            </div>
        </g:if>

    <!-- attribution -->
        <g:set var='attribs' value='${instance.getAttributionList()}'/>
        <g:if test="${attribs.size() > 0}">
            <section class="public-metadata" id="infoSourceList">
                <h4><g:message code="public.sdr.infosourcelist.title" /></h4>
                <ul>
                    <g:each var="a" in="${attribs}">
                        <li><a href="${a.url}" class="external" target="_blank">${a.name}</a></li>
                    </g:each>
                </ul>
            </section>
        </g:if>

     <!-- external identifiers -->
        <g:render template="externalIdentifiers" model="[instance:instance]"/>
    </div>
</div>
</div>

<r:script type="text/javascript">
  // stats
  if(loadLoggerStats){
      if (${instance.resourceType == 'website'}) {
          loadDownloadStats("${grailsApplication.config.loggerURL}", "${instance.uid}","${instance.name}", "2000");
      } else if (${instance.resourceType == 'records'}) {
          loadDownloadStats("${grailsApplication.config.loggerURL}", "${instance.uid}","${instance.name}", "1002");
      }
  }
</r:script>
<g:render template="taxonTree" model="[facet:'data_resource_uid', instance: instance]" />
<g:render template="charts" model="[facet:'data_resource_uid', instance: instance]" />
</body>
</html>
