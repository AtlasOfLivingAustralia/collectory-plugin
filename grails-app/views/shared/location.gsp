<%@ page import="au.org.ala.collectory.ProviderGroup" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="${grailsApplication.config.skin.layout}"/>
  <g:set var="entityName" value="${command.ENTITY_TYPE}"/>
  <g:set var="entityNameLower" value="${command.ENTITY_TYPE.toLowerCase()}"/>
  <title><g:message code="collection.base.label" default="Edit ${entityNameLower} metadata" args="[entityNameLower]" /></title>
  <script async defer
          src="https://maps.googleapis.com/maps/api/js?key=${grailsApplication.config.google?.apikey}"
          type="text/javascript"></script></head>
<body onload="load();">
  <style>
  #mapCanvas {
    width: 100%;
    min-height: 200px;
    min-width: 200px;
  }
  </style>
<div class="nav">
  <h1><g:message code="shared.location.main.title01" />: ${fieldValue(bean: command, field: "name")}</h1>
</div>
<div class="container">
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
    <div class="row">
      <div class="col-md-8">
        <!-- state -->
        <div class="form-group">
          <label for="state"><g:message code="providerGroup.state.label" default="State/Territory/County"/><cl:helpText code="${entityNameLower}.state"/>
            <br/><span class=hint>(where the ${entityNameLower}<br>resides)</span>
          </label>
          <g:textField class="form-control" name="sate" maxLength="256" value="${command?.state}"/>
        </div>

        <!-- email -->
        <div class="form-group">
          <label for="email"><g:message code="providerGroup.email.label" default="Email"/><cl:helpText code="collection.email"/></label>
          <g:field type="email" class="form-control" name="email" maxLength="256" value="${command?.email}"/>
        </div>

        <!-- phone -->
        <div class="form-group">
          <label for="phone"><g:message code="providerGroup.phone.label" default="Phone"/><cl:helpText code="collection.phone"/></label>
          <g:field type="tel" class="form-control" name="phone" maxlength="45" value="${command?.phone}"/>
        </div>

        <!-- address -->
        <div class="form-group">
          <label for="address.street"><g:message code="providerGroup.address.street.label" default="Street"/></label>
          <g:textField class="form-control" id="street" name="address.street" maxlength="128" value="${command?.address?.street}"/>
        </div>
        <div class="form-group">
          <label for="address.city"><g:message code="providerGroup.address.city.label" default="City"/></label>
          <g:textField class="form-control" id="city" name="address.city" maxlength="128" value="${command?.address?.city}"/>
        </div>
        <div class="form-group">
          <label for="address.state"><g:message code="providerGroup.address.state.label" default="State or territory"/></label>
          <g:textField class="form-control" id="state" name="address.state" maxlength="128" value="${command?.address?.state}"/>
        </div>
        <div class="form-group">
          <label for="address.postcode"><g:message code="providerGroup.address.postcode.label" default="Postcode"/></label>
          <g:textField class="form-control" name="address.postcode" maxlength="128" value="${command?.address?.postcode}"/>
        </div>
        <div class="form-group">
          <label for="address.country"><g:message code="providerGroup.address.country.label" default="Country"/></label>
          <g:textField class="form-control" id="country" name="address.country" maxlength="128" value="${command?.address?.country}"/>
        </div>
        <div class="form-group">
          <label for="address.postBox"><g:message code="providerGroup.address.postBox.label" default="Postal address"/></label>
          <g:textField class="form-control" name="address.postBox" maxlength="128" value="${command?.address?.postBox}"/>
        </div>

        <!-- latitude and longitude -->
        <div class="form-group">
          <label for="latitude"><g:message code="providerGroup.latitude.label" default="Latitude"/><cl:helpText code="collection.latitude"/>
            <br/><span class=hint>(decimal degrees)</span>
          </label>
          <g:textField type="number" class="form-control" id="latitude" name="latitude" step="any" value="${cl.numberIfKnown(number:command.latitude)}"/>
          <!-- map spans 4-5 rows -->
        </div>

        <!-- longitude -->
        <div class="form-group">
          <label for="longitude">
            <g:message code="providerGroup.longitude.label" default="Longitude"/><cl:helpText code="collection.longitude"/>
            <br/><span class=hint>(decimal degrees)</span>
          </label>
          <g:field type="number" class="form-control" id="longitude" name="longitude" min="-180.0" max="180.0" step="any" value="${cl.numberIfKnown(number:command.longitude)}"/>
        </div>

        <!-- lookup lat/lng -->
        <div class="form-group">
          <input type="button" class="classicButton btn btn-default" onclick="return codeAddress();" value="Lookup"/> <g:message code="shared.location.lookup" />.<div style="width:100%;"></div>
        </div>

        <g:if test="${command.ENTITY_TYPE == 'Collection'}">
          <g:if test="${(command.latitude == -1 || command.longitude == -1) && command.inheritedLatLng()}">
            <div class="form-group">
              <input type="button" class="classicButton" onclick="return useInherited();" value="Use inherited"/> <g:message code="shared.location.button.useinherited" />.<div style="width:100%;"></div>
            </div>
          </g:if>
        </g:if>
        <div class="form-group">
          <g:message code="shared.location.main.des01" /> ${entityNameLower}.<br/>
          <g:message code="shared.location.main.des02" />.
        </div>

      </div>
      <div class="col-md-4 pull-right">
        <div id="mapCanvas"></div>
      </div>
    </div>
    <div class="buttons">
      <span class="button"><input type="submit" name="_action_updateLocation" value="${message(code:"shared.location.button.update")}" class="save btn btn-success"></span>
      <span class="button"><input type="submit" name="_action_cancel" value="${message(code:"shared.location.button.cancel")}" class="cancel btn btn-default"></span>
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
          var lat = results[0].geometry.location.lat().toFixed(6);
          var lng = results[0].geometry.location.lng().toFixed(6);
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
        $('input#latitude').val(latLng.lat().toFixed(6));
        $('input#longitude').val(latLng.lng().toFixed(6));
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
