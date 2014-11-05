<%@ page import="au.org.ala.collectory.ProviderGroup; org.codehaus.groovy.grails.commons.ConfigurationHolder; au.org.ala.collectory.Collection" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="${grailsApplication.config.ala.skin}"/>
  <g:set var="entityName" value="${message(code: 'collection.label', default: 'Collection')}"/>
  <title><g:message code="default.show.label" args="[entityName]"/></title>
  <script type="text/javascript" src="http://maps.google.com/maps/api/js?v=3.3&sensor=false"></script>
  <r:require module="collectory"/>
</head>
<body>
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

  <g:form url="mailto:${instance.getPrimaryContact()?.contact?.email}?subject=Request to review web pages presenting information about the ${instance.name}.&body=${contactEmailBody}">
  <ul>
  <li><span class="menuButton"><cl:homeLink/></span></li>
  <li><span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]"/></g:link></span></li>
  <li><span class="menuButton"><g:link class="list" action="myList"><g:message code="default.myList.label" args="[entityName]"/></g:link></span></li>
  <li><span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]"/></g:link></span></li>
  </ul>
  <!--span class="buttons"><input type="submit" class="special" value="Contact the curator"/></span-->
  </g:form>
</div>
<div class="body">
  <g:if test="${flash.message}">
    <div class="message">${flash.message}</div>
  </g:if>
  <div class="dialog emulate-public">
    <!-- base attributes -->
    <div class="show-section well titleBlock">

      <!-- Name --><!-- Acronym -->
      <h1>${fieldValue(bean: instance, field: "name")}<cl:valueOrOtherwise value="${instance.acronym}"> (${fieldValue(bean: instance, field: "acronym")})</cl:valueOrOtherwise></h1>

      <!-- Institution --><!-- ALA Partner -->
      <h2 style="display:inline"><g:link controller="institution" action="show" id="${instance.institution?.uid}">${instance.institution?.name}</g:link></h2>
      <cl:partner test="${instance.institution?.isALAPartner}"/><br/>

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
      <p><span class="category">Last change:</span> ${fieldValue(bean: instance, field: "userLastModified")} on ${fieldValue(bean: instance, field: "lastUpdated")}</p>

      <cl:editButton uid="${instance.uid}" page="/shared/base" notAuthorisedMessage="Not authorised to edit."/>
    </div>

    <!-- collection description -->
    <div class="show-section well">
      <!-- Pub Desc -->
      <h2><g:message code="collection.show.title.description" /></h2>

      <span class="category"><g:message code="collection.show.span04" /></span><br/>
      <cl:formattedText body="${instance.pubDescription}"/>

      <!-- Tech Desc -->
      <span class="category"><g:message code="collection.show.span05" /></span><br/>
      <cl:formattedText body="${instance.techDescription}"/>
      <span class="category"><g:message code="collection.show.span06" /></span>
      <g:if test="${instance.startDate || instance.endDate}">
          <p><cl:temporalSpanText start='${fieldValue(bean: instance, field: "startDate")}' end='${fieldValue(bean: instance, field: "endDate")}'/></p>
      </g:if>
      
      <!-- Collection types -->
      <p><span class="category"><g:message code="collection.show.span07" />:</span>
      <cl:JSONListAsStrings json='${instance.collectionType}'/>.</p>

      <!-- Active -->
      <p><span class="category"><g:message code="collection.show.span08" />:</span> <cl:valueOrOtherwise value="${instance.active}" otherwise="unknown"/>.</p>

      <!-- Keywords -->
      <p><span class="category"><g:message code="collection.show.span09" />:</span> <g:message code="collection.show.des01" />: <cl:valueOrOtherwise value="${instance.listKeywords().join(', ')}" otherwise="none"/>.
        <g:if test="${!instance.listKeywords().contains('fauna')}">
          (<g:link action="addKeyword" id="${instance.id}" params="[keyword:'fauna']"><g:message code="collection.show.link.addfauna" /></g:link>)
        </g:if>
        <g:if test="${!instance.listKeywords().contains('plants')}">
          (<g:link action="addKeyword" id="${instance.id}" params="[keyword:'plants']"><g:message code="collection.show.link.addplants" /></g:link>)
        </g:if>
        <g:if test="${!instance.listKeywords().contains('entomology')}">
          (<g:link action="addKeyword" id="${instance.id}" params="[keyword:'entomology']"><g:message code="collection.show.link.addentomology" /></g:link>)
        </g:if>
        <g:if test="${!instance.listKeywords().contains('microbes')}">
          (<g:link action="addKeyword" id="${instance.id}" params="[keyword:'microbes']"><g:message code="collection.show.link.addmicrobes" /></g:link>)
        </g:if>
      </p>

      <!-- sub collections -->
      <h2><g:message code="collection.show.title.subcollections" /></h2>
      <cl:subCollectionList list="${instance.subCollections}"/>

      <cl:editButton uid="${instance.uid}" page="description"/>
  </div>

    <!-- images -->
    <g:render template="/shared/images" model="[target: 'imageRef', image: instance.imageRef, title:'Representative image', instance: instance]"/>

    <!-- location -->
    <g:render template="/shared/location" model="[instance: instance]"/>

    <!-- collection scope -->
    <div class="show-section well">
      <h2><g:message code="collection.show.title.gr" /></h2>
      <table>
        <colgroup><col width="25%"/><col width="75%"/></colgroup>
        <!-- Geo descrip -->
        <tr class="prop">
          <td valign="top" class="category"><g:message code="geographicDescription.label" default="Geographic Description"/></td>
          <td valign="top" class="value"><cl:formattedText>${fieldValue(bean: instance, field: "geographicDescription")}</cl:formattedText></td>
        </tr>

        <!-- States -->
        <tr class="prop">
          <td valign="top" class="category"><g:message code="states.label" default="States covered"/></td>
          <td valign="top" class="value">${fieldValue(bean: instance, field: "states")}</td>
        </tr>

        <!-- Extent -->
        <tr class="prop">
          <td valign="top" class="category"><g:message code="collection.show.coordinate.des01" /><br/> <g:message code="collection.show.coordinate.des02" /></td>
          <td valign="top" class="value">
            <table class="table table-bordered">
              <colgroup><col width="30%"/><col width="40%"/><col width="30%"/></colgroup>
              <tr><td></td><td><g:message code="collection.show.coordinate.des03" />: <cl:showDecimal value='${instance.northCoordinate}' degree='true'/></td><td></td></tr>
              <tr><td><g:message code="collection.show.coordinate.des04" />: <cl:showDecimal value='${instance.westCoordinate}' degree='true'/></td>
              <td></td>
              <td><g:message code="collection.show.coordinate.des05" />: <cl:showDecimal value='${instance.eastCoordinate}' degree='true'/></td></tr>
              <tr><td></td><td><g:message code="collection.show.coordinate.des06" />: <cl:showDecimal value='${instance.southCoordinate}' degree='true'/></td><td></td></tr>
            </table>
          </td>
        </tr>
      </table>
      <h2><g:message code="collection.show.title.tr" /></h2>
      <table>
        <colgroup><col width="25%"/><col width="75%"/></colgroup>
        <!-- Focus   -->
        <tr class="prop">
          <td valign="top" class="category"><g:message code="focus.label" default="Collection focus"/></td>
          <td valign="top" class="value"><cl:formattedText>${fieldValue(bean: instance, field: "focus")}</cl:formattedText></td>
        </tr>

        <!-- Kingdom cover-->
        <tr class="prop">
          <td valign="top" class="category"><g:message code="kingdomCoverage.label" default="Kingdom Coverage"/></td>
          <td valign="top" class="checkbox"><cl:checkBoxList readonly="true" name="kingdomCoverage" from="${Collection.kingdoms}" value="${instance?.kingdomCoverage}" /></td>
        </tr>

        <!-- sci names -->
        <tr class="prop">
          <td valign="top" class="category"><g:message code="scientificNames.label" default="Scientific Names"/></td>
          <td valign="top" class="value"><cl:JSONListAsStrings json='${fieldValue(bean: instance, field: "scientificNames")}'/></td>
        </tr>
      </table>

      <!-- estimate of records -->
      <h2 style="padding-top:10px;"><g:message code="collection.show.title.numberof" /> <cl:nounForTypes types="${instance.listCollectionTypes()}"/> <g:message code="collection.show.title.inthecollection" /></h2>
      <g:if test="${fieldValue(bean: instance, field: 'numRecords') != '-1'}">
        <p><g:message code="collection.show.des07" /> <cl:nounForTypes types="${instance.listCollectionTypes()}"/> <g:message code="collection.show.des08" /> <cl:collectionName prefix="the " name="${instance.name}"/> <g:message code="collection.show.des09" /> ${fieldValue(bean: instance, field: "numRecords")}.</p>
      </g:if>

      <g:if test="${fieldValue(bean: instance, field: 'numRecordsDigitised') != '-1'}">
        <p><g:message code="collection.show.des10" args="[fieldValue(bean: instance, field: "numRecordsDigitised")]" />.
        <g:message code="collection.show.des11" /> <cl:percentIfKnown dividend='${instance.numRecordsDigitised}' divisor='${instance.numRecords}' /> <g:message code="collection.show.des12" />.</p>
      </g:if>

      %{--<!-- actual biocache records -->--}%
      %{--<p><span id="numBiocacheRecords">Looking up... the number of records that</span> can be accessed through the Atlas of Living Australia.--}%
      %{--<cl:recordsLink collection="${instance}">Click to view these records.</cl:recordsLink>--}%
      %{--</p>--}%

      %{--<div id="speedo">--}%
        %{--<div id="progress">--}%
          %{--<img id="progressBar" src="${resource(dir:'images', file:'percentImage.png')}" alt="0%"--}%
                  %{--class="no-radius percentImage1" style='background-position: -120px 0;margin-left:35px;'/>--}%
          %{--<!--cl:progressBar percent="0.0"/-->--}%
        %{--</div>--}%
        %{--<p class="caption"><span id="speedoCaption">No records are available for viewing in the Atlas.</span></p>--}%
      %{--</div>--}%

      <p style="margin-top:20px;"><g:message code="collection.show.des13" />.</p>
      <cl:warnIfInexactMapping collection="${instance}"/>

      <cl:editButton uid="${instance.uid}" page="range"/>
    </div>

    <!-- Contacts -->
    <g:render template="/shared/contacts" model="[contacts: contacts, instance: instance]"/>

    <!-- Provider codes -->
    <div class="show-section well">
      <h2><g:message code="collection.show.title.providercodes" /></h2>
      <p><g:message code="collection.show.des14" />.</p>
      <p><g:message code="collection.show.des15" />: ${instance.getListOfInstitutionCodesForLookup().join(", ")}</p>
      <p><g:message code="collection.show.des16" />: ${instance.getListOfCollectionCodesForLookup().join(", ")}</p>
      <g:if test="${instance.providerMap}">
        <g:if test="${instance.providerMap?.matchAnyCollectionCode || !instance.providerMap?.exact}">
          <p>
            <cl:valueOrOtherwise value="${instance.providerMap?.matchAnyCollectionCode}"><g:message code="collection.show.des17" />. </cl:valueOrOtherwise>
            <cl:valueOrOtherwise value="${!instance.providerMap?.exact}"><g:message code="collection.show.des18" />. </cl:valueOrOtherwise>
            <cl:valueOrOtherwise value="${instance.providerMap?.warning}"><g:message code="collection.show.des19" />: ${instance.providerMap?.warning}</cl:valueOrOtherwise>
          </p>
        </g:if>
        <cl:editButton uid="${instance.uid}" id="${instance.providerMap?.id}" controller="providerMap" action="show" returnTo="${instance.uid}"/>
      </g:if>
      <g:else>
        <cl:editButton controller="providerMap" action="create" returnTo="${instance.uid}" createFor="${instance.uid}"/>
      </g:else>
    </div>

    <!-- Record providers and resources -->
    <g:render template="/shared/providers" model="[instance: instance]"/>

    <!-- Attributions -->
    <g:render template="/shared/attributions" model="[instance: instance]"/>

    <!-- taxonomy hints -->
    <g:render template="/shared/taxonomyHints" model="[instance: instance]"/>

    <!-- change history -->
    <g:render template="/shared/changes" model="[changes: changes, instance: instance]"/>

  </div>
  <div class="buttons">
    <div class="buttons pull-right">
          <span class="button"><cl:viewPublicLink uid="${instance?.uid}"/></span>
          <span class="button"><cl:jsonSummaryLink uid="${instance.uid}"/></span>
          <span class="button"><cl:jsonDataLink uid="${instance.uid}"/></span>
    </div>
    <g:form>
      <g:hiddenField name="id" value="${instance?.id}"/>
      <cl:ifGranted role="${ProviderGroup.ROLE_ADMIN}">
        <span class="button"><g:actionSubmit class="delete btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/></span>
      </cl:ifGranted>
    </g:form>
  </div>

</div>
<script type="text/javascript">
  var initial = -120;
  var imageWidth=240;
  var eachPercent = (imageWidth/2)/100;
  
/************************************************************\
*
\************************************************************/
function onLoadCallback() {
  // summary biocache data
  var biocacheRecordsUrl = "${ConfigurationHolder.config.grails.context}/public/biocacheRecords.json?uid=${instance.uid}";
  $.get(biocacheRecordsUrl, {}, biocacheRecordsHandler);
}
/************************************************************\
*
\************************************************************/
function biocacheRecordsHandler(response) {
  setNumbers(response.totalRecords, ${instance.numRecords});
}
/************************************************************\
*
\************************************************************/
function setNumbers(totalBiocacheRecords, totalRecords) {
  var recordsClause = "";
  switch (totalBiocacheRecords) {
    case 0: recordsClause = "No records"; break;
    case 1: recordsClause = "1 record"; break;
    default: recordsClause = addCommas(totalBiocacheRecords) + " records";
  }
  $('#numBiocacheRecords').html(recordsClause);

  if (totalRecords > 0) {
    var percent = totalBiocacheRecords/totalRecords * 100;
    setProgress(percent);
  } else {
    // to update the speedo caption
    setProgress(0);
  }
}
/************************************************************\
*
\************************************************************/
function setProgress(percentage)
{
  var captionText = "";
  if (${instance.numRecords < 1}) {
    captionText = "There is no estimate of the total number<br/>of specimens in this collection.";
  } else if (percentage == 0) {
    captionText = "No records are available for viewing in the Atlas.";
  } else {
    var displayPercent = percentage.toFixed(1);
    if (percentage < 0.1) {displayPercent = percentage.toFixed(2)}
    if (percentage > 20) {displayPercent = percentage.toFixed(0)}
    if (percentage > 100) {displayPercent = "over 100"}
    captionText = "Records for " + displayPercent + "% of specimens are<br/>available for viewing in the Atlas.";
  }
  $('#speedoCaption').html(captionText);

  if (percentage > 100) {
    $('#progressBar').removeClass('percentImage1');
    $('#progressBar').addClass('percentImage4');
    percentage = 101;
  }
  var percentageWidth = eachPercent * percentage;
  var newProgress = eval(initial)+eval(percentageWidth)+'px';
  $('#progressBar').css('backgroundPosition',newProgress+' 0');
}
/************************************************************\
*
\************************************************************/
function addCommas(nStr)
{
    nStr += '';
    x = nStr.split('.');
    x1 = x[0];
    x2 = x.length > 1 ? '.' + x[1] : '';
    var rgx = /(\d+)(\d{3})/;
    while (rgx.test(x1)) {
        x1 = x1.replace(rgx, '$1' + ',' + '$2');
    }
    return x1 + x2;
}
/************************************************************\
*
\************************************************************/

$(document).ready(function() {
    onLoadCallback();
    initializeLocationMap('${instance.canBeMapped()}',${instance.latitude},${instance.longitude});
});

</script>
</body>
</html>
