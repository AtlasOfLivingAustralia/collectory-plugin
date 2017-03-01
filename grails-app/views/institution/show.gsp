
<%@ page import="au.org.ala.collectory.ProviderGroup; au.org.ala.collectory.Institution" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <g:set var="entityName" value="${message(code: 'institution.label', default: 'Institution')}" />
        <title><g:message code="default.show.label" args="[entityName]" /></title>
        <script type="text/javascript" src="http://maps.google.com/maps/api/js?v=3.3&sensor=false"></script>
        <r:require module="collectory"/>
    </head>
    <body onload="initializeLocationMap('${instance.canBeMapped()}',${instance.latitude},${instance.longitude});">
    <style>
    #mapCanvas {
      width: 200px;
      height: 170px;
      float: right;
    }
    </style>
        <div class="nav">

            <p class="pull-right">
            <span class="button"><cl:viewPublicLink uid="${instance?.uid}"/></span>
            <span class="button"><cl:jsonSummaryLink uid="${instance.uid}"/></span>
            <span class="button"><cl:jsonDataLink uid="${instance.uid}"/></span>
            </p>
            <ul>
            <li><span class="menuButton"><cl:homeLink/></span></li>
            <li><span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span></li>
            <li><span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span></li>
            </ul>
        </div>
        <div class="body">
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog emulate-public">
              <!-- base attributes -->
              <div class="show-section titleBlock well">
                <!-- Name --><!-- Acronym -->
                <g:if test="${instance.name.size() > 50}">
                  <h1 style="display:inline;font-size:1.7em;">${fieldValue(bean: instance, field: "name")}<cl:valueOrOtherwise value="${instance.acronym}"> (${fieldValue(bean: instance, field: "acronym")})</cl:valueOrOtherwise></h1>
                  <cl:partner test="${instance.isALAPartner}"/><br/>
                </g:if>
                <g:else>
                  <h1 style="display:inline">${fieldValue(bean: instance, field: "name")}<cl:valueOrOtherwise value="${instance.acronym}"> (${fieldValue(bean: instance, field: "acronym")})</cl:valueOrOtherwise></h1>
                  <cl:partner test="${instance.isALAPartner}"/><br/>
                </g:else>
                <!-- Institutions -->
                <g:set var='parents' value="${instance.listParents()}"/>
                <g:if test="${parents}">
                  <g:each in="${parents}" var="p">
                    <h2 style="display:inline;font-size:1.2em"><g:link controller="institution" action="show" id="${p.uid}">${p.name}</g:link></h2>
                    <cl:partner test="${p.isALAPartner}"/><br/>
                  </g:each>
                </g:if>
                <!-- GUID    -->
                <p><span class="category"><g:message code="collection.show.span.lsid" />:</span> <cl:guid target="_blank" guid='${(fieldValue(bean: instance, field: "guid"))?:'Not supplied'}'/></p>

                <!-- UID    -->
                <p><span class="category"><g:message code="providerGroup.uid.label" />:</span> ${fieldValue(bean: instance, field: "uid")}</p>

                <!-- Web site -->
                <p><span class="category"><g:message code="collection.show.span.cw" />:</span> <cl:externalLink href="${fieldValue(bean:instance, field:'websiteUrl')}"/></p>

                <!-- Networks -->
                <g:if test="${instance.networkMembership}">
                  <p><cl:membershipWithGraphics coll="${instance}"/></p>
                </g:if>

                <!-- Notes -->
                <g:if test="${instance.notes}">
                  <p><cl:formattedText>${fieldValue(bean: instance, field: "notes")}</cl:formattedText></p>
                </g:if>

                <!-- last edit -->
                <p><span class="category"><g:message code="datahub.show.lastchange" />:</span> ${fieldValue(bean: instance, field: "userLastModified")} on ${fieldValue(bean: instance, field: "lastUpdated")}</p>

                <cl:editButton uid="${instance.uid}" page="/shared/base"/>
              </div>

              <!-- description -->
              <div class="show-section well">
                <!-- Pub Desc -->
                <h2>Description</h2>
                <div class="category"><g:message code="collection.show.span04" /></div><div style="clear:both;"></div>
                <cl:formattedText body="${instance.pubDescription}"/>

                <!-- Tech Desc -->
                <div class="category"><g:message code="collection.show.span05" /></div><div style="clear:both;"></div>
                <cl:formattedText body="${instance.techDescription}"/>

                <!-- Contribution -->
                <div class="category"><g:message code="dataprovider.show.span06" /></div><div style="clear:both;"></div>
                <cl:formattedText>${fieldValue(bean: instance, field: "focus")}</cl:formattedText>

                <!-- Institution type -->
                <p><span class="category"><g:message code="institution.edit.span07" />:</span> ${fieldValue(bean: instance, field: "institutionType")}</p>

                <!-- Collections -->
                <h2>Collections</h2>
                <ul class='fancy'>
                  <g:each in="${instance.listCollections().sort{it.name}}" var="c">
                      <li><g:link controller="collection" action="show" id="${c.uid}">${c?.name}</g:link></li>
                  </g:each>
                </ul>
                <p>
                    <g:link controller="collection" action="create" class="btn" params='[institutionUid: "${instance.uid}"]'>create a new collection for this institution</g:link>
                </p>

                <cl:editButton uid="${instance.uid}" page="description"/>
              </div>

              <!-- images -->
              <g:render template="/shared/images" model="[target: 'logoRef', image: instance.logoRef, title:'Logo', instance: instance]"/>
              <g:render template="/shared/images" model="[target: 'imageRef', image: instance.imageRef, title:'Representative image', instance: instance]"/>

              <!-- location -->
              <g:render template="/shared/location" model="[instance: instance]"/>

              <!-- Record providers and resources -->
              <g:render template="/shared/providers" model="[instance: instance]"/>

              <!-- Contacts -->
              <g:render template="/shared/contacts" model="[contacts: contacts, instance: instance]"/>

              <!-- Attributions -->
              <g:render template="/shared/attributions" model="[instance: instance]"/>

              <!-- external identifiers -->
              <g:render template="/shared/externalIdentifiers" model="[instance: instance]"/>

              <!-- change history -->
              <g:render template="/shared/changes" model="[changes: changes, instance: instance]"/>

            </div>
            <div class="buttons">
              <g:form>
                <g:hiddenField name="id" value="${instance?.id}"/>
                <cl:ifGranted role="${ProviderGroup.ROLE_ADMIN}">
                  <span class="button"><g:actionSubmit class="delete btn" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/></span>
                </cl:ifGranted>
                <span class="pull-right">
                <span class="button btn"><cl:viewPublicLink uid="${instance?.uid}"/></span>
                <span class="button btn"><cl:jsonSummaryLink uid="${instance.uid}"/></span>
                <span class="button btn"><cl:jsonDataLink uid="${instance.uid}"/></span>
               </span>
             </g:form>
            </div>
        </div>
    </body>
</html>
