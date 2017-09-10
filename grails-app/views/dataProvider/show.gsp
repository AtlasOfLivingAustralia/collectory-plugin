<%@ page import="au.org.ala.collectory.ProviderGroup; au.org.ala.collectory.DataProvider" %>
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
      width: 500px;
      height: 400px;
      float: right;
    }
    </style>
        <div class="btn-toolbar">
            <ul class="btn-group">
                <li class="btn"><cl:homeLink/></li>
                <li class="btn"><span class="glyphicon glyphicon-list"></span><g:link class="list" action="list"> <g:message code="default.list.label" args="[entityName]"/></g:link></li>
                <li class="btn"><span class="glyphicon glyphicon-list"></span><g:link class="list" action="myList"> <g:message code="default.myList.label" args="[entityName]"/></g:link></li>
                <li class="btn"><span class="glyphicon glyphicon-plus"></span><g:link class="create" action="create"> <g:message code="default.new.label" args="[entityName]"/></g:link></li>
            </ul>
            <ul class="btn-group pull-right">
                <li class="btn"><cl:viewPublicLink uid="${instance?.uid}"/></li>
                <li class="btn"><cl:jsonSummaryLink uid="${instance.uid}"/></li>
                <li class="btn"><cl:jsonDataLink uid="${instance.uid}"/></li>
                <g:if test="${instance.getPrimaryContact()?.contact?.email}"><li class="btn"><a href="mailto:${instance.getPrimaryContact()?.contact?.email}?subject=Request to review web pages presenting information about the ${instance.name}.&body=${contactEmailBody}"><span class="glyphicon glyphicon-envelope"></span><g:message code="default.query.label"/></a></li></g:if>
            </ul>
        </div>
    <div class="body">
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog emulate-public">
              <!-- base attributes -->
              <div class="show-section well  titleBlock">
                <!-- Name --><!-- Acronym -->
                <h1 style="display:inline">${fieldValue(bean: instance, field: "name")}<cl:valueOrOtherwise value="${instance.acronym}"> (${fieldValue(bean: instance, field: "acronym")})</cl:valueOrOtherwise></h1>
                <cl:partner test="${instance.isALAPartner}"/><br/>

                <!-- GUID    -->
                <p><span class="category"><g:message code="collection.show.span.lsid" />:</span> <cl:guid target="_blank" guid='${fieldValue(bean: instance, field: "guid")}'/></p>

                <!-- UID    -->
                <p><span class="category"><g:message code="providerGroup.uid.label" />:</span> ${fieldValue(bean: instance, field: "uid")}</p>

                <!-- Web site -->
                <p><span class="category"><g:message code="dataprovider.show.span.cw" default="Website URL" />:</span> <cl:externalLink href="${fieldValue(bean:instance, field:'websiteUrl')}"/></p>

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


              <div class="show-section well">
                  <h2>IPT integration (Beta)</h2>
                  <p>
                      If your data provider is an IPT instance, set the website URL to be the URL of the IPT endpoint.
                      <br/> e.g. http://data.canadensys.net/ipt
                      <br/>
                  </p>
                  <p class="iptStatus alert alert-info hide">

                  </p>
                  <p>
                    <button class="iptCheck iptBtn btn btn-default"><r:img class="spinner hide" uri="/images/spinner.gif"></r:img> Check endpoint</button>
                    <button class="iptUpdate iptBtn btn btn-warning"><r:img class="spinner hide" uri="/images/spinner.gif"></r:img>  Update data resources</button>
                  </p>
              </div>

              <!-- description -->
              <div class="show-section well">
                <!-- Pub Desc -->
                <h2><g:message code="collection.show.title.description" /></h2>

                <!-- Pub Short Desc -->
                <span class="category"><g:message code="collection.show.pubShort"  default="Public short description"/></span><br/>
                <cl:formattedText body="${instance.pubShortDescription?:'Not provided'}"/>

                <!-- Pub Desc -->
                <span class="category"><g:message code="collection.show.span04" /></span><br/>
                <cl:formattedText body="${instance.pubDescription?:'Not provided'}"/>

                <!-- Tech Desc -->
                <span class="category"><g:message code="collection.show.span05" /></span><br/>
                <cl:formattedText body="${instance.techDescription?:'Not provided'}"/>

                <!-- Contribution -->
                <span class="category"><g:message code="dataprovider.show.span06" /></span><br/>
                <cl:formattedText body="${instance.focus?:'Not provided'}"/>

                <!-- Keywords -->
                <span class="category"><g:message code="dataprovider.show.span07" /></span><br/>
                <cl:formattedText body="${instance.keywords?:'Not provided'}"/>

                <cl:editButton uid="${instance.uid}" page="description"/>
              </div>

              <div class="well">
                <!-- Resources -->
                <h2>Data resources</h2>
                <ul class='fancy'>
                  <g:each in="${instance.getResources().sort{it.name}}" var="c">
                      <li><g:link controller="dataResource" action="show" id="${c.uid}">${c?.name}</g:link></li>
                  </g:each>
                </ul>
                <p>
                    <g:link controller="dataResource"  class="btn btn-default" action="create" params='[dataProviderUid: "${instance.uid}"]'><g:message code="dataprovider.show.link01" /></g:link>
                </p>
              </div>

              <!-- images -->
              <g:render template="/shared/images" model="[target: 'logoRef', image: instance.logoRef, title:'Logo', instance: instance]"/>
              <g:render template="/shared/images" model="[target: 'imageRef', image: instance.imageRef, title:'Representative image', instance: instance]"/>

              <!-- location -->
              <g:render template="/shared/location" model="[instance: instance]"/>

              <!-- Record consumers -->
              <g:render template="/shared/consumers" model="[instance: instance]"/>

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
                  <span class="button"><g:actionSubmit class="delete btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/></span>
                </cl:ifGranted>
                <div class="pull-right">
                <span class="button"><cl:viewPublicLink uid="${instance?.uid}"/></span>
                <span class="button"><cl:jsonSummaryLink uid="${instance.uid}"/></span>
                <span class="button"><cl:jsonDataLink uid="${instance.uid}"/></span>
                </div>
              </g:form>
            </div>
        </div>

    <r:script>
        function checkIptInstance(){
            $('.iptCheck .spinner').removeClass('hide');
            $('.iptBtn').attr('disabled','disabled');
            var checkUrl = "${createLink(controller: "ipt", action: "scan", params:[format:"json", uid: instance?.uid, check:true])}";
            var jqxhr = $.get(checkUrl, function(data) {
              $('.iptStatus').html("Success! IPT instance has " + data.length + " resources available." );
              $('.iptStatus').removeClass('hide')
            })
              .fail(function() {
                alert( "There was a problem. Check the website URL and try again." );
              })
              .always(function() {
                $('.iptCheck .spinner').addClass('hide');
                $('.iptBtn').removeAttr('disabled');
              });
        }

        function updateResourcesFromIpt(){
            $('.iptUpdate .spinner').removeClass('hide');
            $('.iptBtn').attr('disabled','disabled');
            var updateUrl = "${createLink(controller: "ipt", action: "scan", params:[format:"json", uid: instance?.uid, create:true])}";
            var jqxhr = $.get(updateUrl, function(data) {
              $('.iptStatus').html("Success! + " + data.length + " resources has been added from this IPT instance." );
              $('.iptStatus').removeClass('hide')
            })
              .fail(function() {
                alert( "There was a problem. Check the website URL and try again." );
              })
              .always(function() {
                $('.iptUpdate .spinner').addClass('hide');
                $('.iptBtn').removeAttr('disabled');
              });
        }

        $('.iptCheck').click(checkIptInstance);
        $('.iptUpdate').click(updateResourcesFromIpt);

    </r:script>

    </body>
</html>
