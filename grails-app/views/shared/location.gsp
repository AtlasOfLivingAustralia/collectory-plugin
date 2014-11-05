<%@ page import="au.org.ala.collectory.ProviderGroup" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="${grailsApplication.config.ala.skin}"/>
  <g:set var="entityName" value="${command.ENTITY_TYPE}"/>
  <g:set var="entityNameLower" value="${command.ENTITY_TYPE.toLowerCase()}"/>
  <title><g:message code="collection.base.label" default="Edit ${entityNameLower} metadata" /></title>
  <script type="text/javascript" src="http://maps.google.com/maps/api/js?v=3.3&sensor=false"></script>
</head>
<body onload="load();">
  <style>
  #mapCanvas {
    width: 500px;
    height: 500px;
    float: right;
  }
  </style>
<div class="nav">
  <h1><g:message code="shared.location.main.title01" />: ${fieldValue(bean: command, field: "name")}</h1>
</div>
<div class="body">
  <g:if test="${message}">
    <div class="message">${message}</div>
  </g:if>
  <g:hasErrors bean="${command}">
    <div class="errors">
      <g:renderErrors bean="${command}" as="list"/>
    </div>
  </g:hasErrors>
  <g:form method="post" enctype="multipart/form-data" action="editCollection">
    <g:hiddenField name="id" value="${command?.id}"/>
    <g:hiddenField name="version" value="${command.version}"/>
    <div class="dialog">
      <table>
        <tbody>

        <!-- state -->
        <tr class="prop">
          <td valign="top" class="name">
            <label for="state"><g:message code="providerGroup.state.label" default="State"/>
              <br/><span class=hint>(where the ${entityNameLower}<br>resides)</span>
            </label>
          </td>
          <td valign="top" colspan="3" class="value ${hasErrors(bean: command, field: 'state', 'errors')}">
            <g:select id="state" name="state" from="${ProviderGroup.statesList}" value="${command?.state}" valueMessagePrefix="providerGroup.state" noSelection="['': '']"/>
            <cl:helpText code="${entityNameLower}.state"/>
          </td>
          <cl:helpTD/>
        </tr>

        <!-- email -->
        <tr class="prop">
          <td valign="top" class="name">
            <label for="email"><g:message code="providerGroup.email.label" default="Email"/></label>
          </td>
          <td valign="top" colspan="3" class="value ${hasErrors(bean: command, field: 'email', 'errors')}">
            <g:textField name="email" maxLength="256" value="${command?.email}"/>
            <cl:helpText code="collection.email"/>
          </td>
          <cl:helpTD/>
        </tr>

        <!-- phone -->
        <tr class="prop">
          <td valign="top" class="name">
            <label for="phone"><g:message code="providerGroup.phone.label" default="Phone"/></label>
          </td>
          <td valign="top" colspan="3" class="value ${hasErrors(bean: command, field: 'phone', 'errors')}">
            <g:textField name="phone" maxlength="45" value="${command?.phone}"/>
            <cl:helpText code="collection.phone"/>
          </td>
          <cl:helpTD/>
        </tr>

        <!-- address -->
          <tr class='prop'>
            <td valign="top" class="name">
              <label for="address.street"><g:message code="providerGroup.address.street.label" default="Street"/></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: command, field: 'address.street', 'errors')}">
              <g:textField id="street" name="address.street" maxlength="128" value="${command?.address?.street}"/>
            </td>
          </tr>
          <tr class='prop'>
            <td valign="top" class="name">
              <label for="address.city"><g:message code="providerGroup.address.city.label" default="City"/></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: command, field: 'address.city', 'errors')}">
              <g:textField id="city" name="address.city" maxlength="128" value="${command?.address?.city}"/>
            </td>
          </tr>
          <tr class='prop'>
            <td valign="top" class="name">
              <label for="address.state"><g:message code="providerGroup.address.state.label" default="State or territory"/></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: command, field: 'address.state', 'errors')}">
              <g:textField id="state" name="address.state" maxlength="128" value="${command?.address?.state}"/>
            </td>
          </tr>
          <tr class='prop'>
            <td valign="top" class="name">
              <label for="address.postcode"><g:message code="providerGroup.address.postcode.label" default="Postcode"/></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: command, field: 'address.street', 'errors')}">
              <g:textField name="address.postcode" maxlength="128" value="${command?.address?.postcode}"/>
            </td>
          </tr>
          <tr class='prop'>
            <td valign="top" class="name">
              <label for="address.country"><g:message code="providerGroup.address.country.label" default="Country"/></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: command, field: 'address.country', 'errors')}">
              <g:textField id="country" name="address.country" maxlength="128" value="${command?.address?.country}"/>
            </td>
          </tr>
          <tr class='prop'>
            <td valign="top" class="name">
              <label for="address.postBox"><g:message code="providerGroup.address.postBox.label" default="Postal address"/></label>
            </td>
            <td valign="top" class="value ${hasErrors(bean: command, field: 'address.postBox', 'errors')}">
              <g:textField name="address.postBox" maxlength="128" value="${command?.address?.postBox}"/>
            </td>
          </tr>

        <!-- latitude -->
        <div id="mapCanvas" class="pull-right"></div>
        <tr class="prop">
          <td valign="top" class="name">
            <label for="latitude"><g:message code="providerGroup.latitude.label" default="Latitude"/>
              <br/><span class=hint>(decimal degrees)</span>
            </label>
          </td>
          <td valign="top" class="value ${hasErrors(bean: command, field: 'latitude', 'errors')}">
            <g:textField id="latitude" name="latitude" value="${cl.numberIfKnown(number:command.latitude)}"/>
            <cl:helpText code="collection.latitude"/>
          </td>
          <cl:helpTD/>

          <!-- map spans 4-5 rows -->
          <td rowspan="5">
            </td>
        </tr>

        <!-- longitude -->
        <tr class="prop">
          <td valign="top" class="name">
            <label for="longitude">
              <g:message code="providerGroup.longitude.label" default="Longitude"/>
              <br/><span class=hint>(decimal degrees)</span>
            </label>
          </td>
          <td valign="top" class="value ${hasErrors(bean: command, field: 'longitude', 'errors')}">
            <g:textField id="longitude" name="longitude" value="${cl.numberIfKnown(number:command.longitude)}"/>
            <cl:helpText code="collection.longitude"/>
          </td>
          <cl:helpTD/>
        </tr>

        <!-- lookup lat/lng -->
        <tr>
          <td></td><td colspan="2">
            <input type="button" class="classicButton btn" onclick="return codeAddress();" value="Lookup"/> <g:message code="shared.location.lookup" />.<div style="width:100%;"></div>
          </td>
        </tr>

        <g:if test="${command.ENTITY_TYPE == 'Collection'}">
          <g:if test="${(command.latitude == -1 || command.longitude == -1) && command.inheritedLatLng()}">
            <tr>
              <td></td><td colspan="2">
                <input type="button" class="classicButton" onclick="return useInherited();" value="Use inherited"/> <g:message code="shared.location.button.useinherited" />.<div style="width:100%;"></div>
              </td>
            </tr>
          </g:if>
        </g:if>
        <tr>
          <td></td><td colspan="2"><g:message code="shared.location.main.des01" /> ${entityNameLower}.<br/>
                <g:message code="shared.location.main.des02" />.</td>
        </tr>

        </tbody>
      </table>
    </div>
    <div class="buttons">
      <span class="button"><input type="submit" name="_action_updateLocation" value="${message(code:"shared.location.button.update")}" class="save btn"></span>
      <span class="button"><input type="submit" name="_action_cancel" value="${message(code:"shared.location.button.cancel")}" class="cancel btn"></span>
    </div>
  </g:form>
</div>

<script type="text/javascript">
  function codeAddress() {
    var address = document.getElementById('street').value + "," + document.getElementById('city').value + "," + document.getElementById('state').value + "," + document.getElementById('country').value
    var geocoder = new google.maps.Geocoder();
    if (geocoder) {
      geocoder.geocode({ 'address': address}, function(results, status) {
        if (status == google.maps.GeocoderStatus.OK) {
          var lat = results[0].geometry.location.lat();
          var lng = results[0].geometry.location.lng();
          $('input#latitude').val(lat);
          $('input#longitude').val(lng);
          map.setCenter(results[0].geometry.location);
          marker.setPosition(results[0].geometry.location);
          return true;
        } else {
          return false;
        }
      });
    }
  }

</script>

<g:if test="${command.ENTITY_TYPE == 'Collection'}">
  <g:if test="${(command.latitude == -1 || command.longitude == -1) && command.inheritedLatLng()}">
    <script type="text/javascript">
      function useInherited() {
        var latLng = new google.maps.LatLng(${command.inheritedLatLng()?.lat}, ${command.inheritedLatLng()?.lng})
        $('input#latitude').val(latLng.lat());
        $('input#longitude').val(latLng.lng());
        map.setCenter(latLng);
        marker.setPosition(latLng);
      }
    </script>
  </g:if>
</g:if>

<script type="text/javascript">
var map;
var marker;

function load() {
    initialize();
}

function updateMarkerPosition(latLng) {
  $('input#latitude').val(latLng.lat());
  $('input#longitude').val(latLng.lng());
}

function initialize() {
  var lat = ${command.latitude}
  if (lat == undefined || lat == 0 || lat == -1 ) {lat = -35.294325779329654}
  var lng = ${command.longitude}
  if (lng == undefined || lng == 0 || lng == -1 ) {lng = 149.10602960586547}
  var latLng = new google.maps.LatLng(lat, lng);
  map = new google.maps.Map(document.getElementById('mapCanvas'), {
    zoom: 12,
    center: latLng,
    mapTypeId: google.maps.MapTypeId.ROADMAP,
    streetViewControl: false
  });
  marker = new google.maps.Marker({
    position: latLng,
    title: 'my collection',
    map: map,
    draggable: true
  });

  // Add dragging event listeners.
  google.maps.event.addListener(marker, 'drag', function() {
    updateMarkerPosition(marker.getPosition());
  });

  google.maps.event.addListener(marker, 'dragend', function() {
    updateMarkerPosition(marker.getPosition());
  });
}

</script>
</body>
</html>
