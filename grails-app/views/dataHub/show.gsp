<%@ page import="au.org.ala.collectory.ProviderGroup; au.org.ala.collectory.DataHub" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <g:set var="entityName" value="${instance.ENTITY_TYPE}" />
        <g:set var="entityNameLower" value="${cl.controller(type: instance.ENTITY_TYPE)}"/>
        <title><g:message code="default.show.label" args="[entityName]" /></title>
        <script async defer
                src="https://maps.googleapis.com/maps/api/js?key=${grailsApplication.config.google?.apikey}"
                type="text/javascript"></script>
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
              <div class="show-section well">
                <!-- Name --><!-- Acronym -->
                <h1>${fieldValue(bean: instance, field: "name")}<cl:valueOrOtherwise value="${instance.acronym}"> (${fieldValue(bean: instance, field: "acronym")})</cl:valueOrOtherwise></h1>

                <!-- GUID    -->
                <p><span class="category"><g:message code="collection.show.span.lsid" />:</span> <cl:guid target="_blank" guid='${fieldValue(bean: instance, field: "guid")}'/></p>

                <!-- UID    -->
                <p><span class="category"><g:message code="collection.show.span.uid" />:</span> ${fieldValue(bean: instance, field: "uid")}</p>

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

                <div><span class="buttons"><g:link class="edit btn" action='edit' params="[page:'/shared/base']" id="${instance.id}">${message(code: 'default.button.edit.label', default: 'Edit')}</g:link></span></div>
              </div>

              <!-- description -->
              <div class="show-section well">
                <h2><g:message code="collection.show.title.description" /></h2>

                <!-- Pub Desc -->
                <div class="source">[Public description]</div><div style="clear:both;"></div>
                <cl:formattedText body="${instance.pubDescription}"/>

                <!-- Tech Desc -->
                <div class="source">[Technical description]</div><div style="clear:both;"></div>
                <cl:formattedText body="${instance.techDescription}"/>

                <!-- Contribution -->
                <div class="source">[Contribution]</div><div style="clear:both;"></div>
                <cl:formattedText>${fieldValue(bean: instance, field: "focus")}</cl:formattedText>

                <div><span class="buttons"><g:link class="edit btn" action='edit' params="[page:'description']" id="${instance.id}">${message(code: 'default.button.edit.label', default: 'Edit')}</g:link></span></div>
              </div>

              <!-- members -->
              <div class="show-section well">
                <h2><g:message code="datahub.show.title02" /></h2>
                <g:if test="${instance.listMemberInstitutions()}">
                    <h3><g:message code="dataHub.memberInstitutions.label" /></h3>
                    <ul class='simple'>
                    <g:each in="${instance.listMemberInstitutions()}" var="i">
                        <li><g:link controller="institution" action="show" id="${i.uid}">${i.name}</g:link></li>
                    </g:each>
                    </ul>
                </g:if>
                <g:if test="${instance.listMemberCollections()}">
                    <h3><g:message code="dataHub.memberCollections.label" /></h3>
                    <ul class='simple'>
                    <g:each in="${instance.listMemberCollections()}" var="i">
                        <li><g:link controller="collection" action="show" id="${i.uid}">${i.name}</g:link></li>
                    </g:each>
                    </ul>
                </g:if>
                <g:if test="${instance.listMemberDataResources()}">
                    <h3><g:message code="datahub.show.tile.resources" /></h3>
                    <ul class='simple'>
                    <g:each in="${instance.listMemberDataResources()}" var="i">
                        <li><g:link controller="dataResource" action="show" id="${i.uid}">${i.name}</g:link></li>
                    </g:each>
                    </ul>
                </g:if>

                <div><span class="buttons"><g:link class="edit btn" action='edit' params="[page:'members']" id="${instance.id}">${message(code: 'default.button.edit.label', default: 'Edit')}</g:link></span></div>
            </div>

              <!-- images -->
              <g:render template="/shared/images" model="[target: 'logoRef', image: instance.logoRef, title:'Logo', instance: instance]"/>
              <g:render template="/shared/images" model="[target: 'imageRef', image: instance.imageRef, title:'Representative image', instance: instance]"/>

              <!-- location -->
              <g:render template="/shared/location" model="[instance: instance]"/>

              <!-- Contacts -->
              <g:render template="/shared/contacts" model="[contacts: contacts, instance: instance]"/>

              <!-- Attributions -->
              <g:render template="/shared/attributions" model="[instance: instance]"/>

              <!-- change history -->
              <g:render template="/shared/changes" model="[changes: changes, instance: instance]"/>

            </div>
            <div class="buttons">
              <g:form>
                <g:hiddenField name="id" value="${instance?.id}"/>
                <cl:ifGranted role="${ProviderGroup.ROLE_ADMIN}">
                  <span class="button"><g:actionSubmit class="delete btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/></span>
                </cl:ifGranted>
                <span class="button"><cl:viewPublicLink uid="${instance?.uid}"/></span>
                <span class="button"><cl:jsonSummaryLink uid="${instance.uid}"/></span>
                <span class="button"><cl:jsonDataLink uid="${instance.uid}"/></span>
              </g:form>
            </div>
        </div>
    </body>
</html>
