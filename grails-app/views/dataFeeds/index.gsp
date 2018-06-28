<%@ expressionCodec="xml" %><%@ page contentType="text/html;charset=UTF-8" %><?xml version="1.0" encoding="UTF-8" standalone="no"?>
<g:set var="orgNameLong" value="${grailsApplication.config.skin.orgNameLong}"/>
<registryObjects xmlns="http://ands.org.au/standards/rif-cs/registryObjects"
                 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 xsi:schemaLocation="http://ands.org.au/standards/rif-cs/registryObjects http://services.ands.org.au/documentation/rifcs/schema/registryObjects.xsd">
    <g:each var='provider' in="${providers}">
        <registryObject group="${orgNameLong}">
            <key>ala.org.au/${provider.uid}</key>
            <originatingSource>${provider.name}</originatingSource>
            <party type="group">
                <name type="primary">
                    <namePart type="full">${provider.name}</namePart>
                </name>
                <location>
                    <g:if test="${provider.websiteUrl}">
                    <address>
                        <electronic type="uri">
                            <value>${provider.websiteUrl}</value>
                        </electronic>
                    </address>
                    </g:if>
                    <g:if test="${provider.resolveAddress()}">
                    <address>
                        <physical type="streetAddress">
                            <addressPart type="locationDescriptor">${provider.resolveAddress()}</addressPart>
                        </physical>
                    </address>
                    </g:if>
                </location>
            </party>
        </registryObject>
    </g:each>
    <g:each var='resource' in="${resources}">
        <registryObject group="Atlas of Living Australia">
            <key>ala.org.au/${resource.uid}</key>
            <g:if test="${resource.dataProvider}">
            <originatingSource>${resource.dataProvider.name}</originatingSource>
            </g:if>
            <g:else>
            <originatingSource><g:message code="rifcs.index.title" args="[orgNameLong]" /></originatingSource>
            </g:else>
            <collection type="dataset">
                <identifier type="local">ala.org.au/${resource.uid}</identifier>
                <name type="primary">
                    <namePart type="full">${resource.name}</namePart>
                </name>
                <location>
                    <address>
                        <electronic type="uri">
                            <value>${resource.buildPublicUrl()}</value>
                        </electronic>
                    </address>
                </location>

                <g:if test="${resource.websiteUrl}">
                <relatedInfo type="website">
                    <identifier type="uri">${resource.websiteUrl}</identifier>
                    <g:if test="${resource.dataProvider}">
                    <title><g:message code="rifcs.index.title01" /> ${resource.dataProvider.name}</title>
                    </g:if>
                </relatedInfo>
                </g:if>
                <relatedInfo type="website">
                    <identifier type="uri">${resource.buildPublicUrl()}</identifier>
                </relatedInfo>
                <g:if test="${resource.dataProvider}">
                <relatedObject>
                    <key>ala.org.au/${resource.dataProvider.uid}</key>
                    <relation type="isManagedBy" />
                </relatedObject>
                </g:if>
                <relatedObject>
                    <key>Contributor:Atlas of Living Australia</key>
                    <relation type="hasCollector" />
                </relatedObject>
                <rights>
                    <licence type="${resource.licenseType}" />
                    <g:if test="${resource.rights}">
                    <rightsStatement><![CDATA[${resource.rights}]]></rightsStatement>
                    </g:if>
                </rights>
                <description type="brief"><![CDATA[${resource.pubDescription}]]></description>
                <g:if test="${resContentTypes[resource.uid]}">
                <description type="notes">Includes: ${resContentTypes[resource.uid]}</description>
                </g:if>
                <g:if test="${resource.citation}">
                <citationInfo>
                    <fullCitation><![CDATA[${resource.citation}]]></fullCitation>
                </citationInfo>
                </g:if>
                <g:if test="${resBoundingBoxCoords.containsKey(resource.uid)}" >
                    <coverage><spatial type="iso19139dcmiBox">northlimit=${resBoundingBoxCoords[resource.uid][3]}; southlimit=${resBoundingBoxCoords[resource.uid][1]}; westlimit=${resBoundingBoxCoords[resource.uid][0]}; eastLimit=${resBoundingBoxCoords[resource.uid][2]}; projection=WGS84</spatial></coverage>
                </g:if>
            </collection>
        </registryObject>
    </g:each>
</registryObjects>