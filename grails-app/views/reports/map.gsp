<%@ page import="au.org.ala.collectory.ReportsController.ReportCommand" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <!--meta name="viewport" content="initial-scale=1.0, user-scalable=no" /-->
        <title><g:message code="reports.title" /></title>
        <!--script type="text/javascript" src="https://maps.google.com/maps/api/js?v=3.3&sensor=false"></script-->
    </head>
    <body onload="initialize()">
        <div class="btn-toolbar">
            <ul class="btn-group">
                <li class="btn btn-default"><cl:homeLink/></li>
                <li class="btn btn-default"><span class="glyphicon glyphicon-th-list"></span><g:link class="list" action="list"> <g:message code="reports.li.reports"/></g:link></li>
            </ul>
        </div>
        <div class="body">
            <h1>Collection locations</h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div id="map_canvas" style="width:732px; height:600px"></div>
        </div>
      <script type="text/javascript">
        var geocoder;
        var map;
        function initialize() {
          geocoder = new google.maps.Geocoder();
          var centre = new google.maps.LatLng(-28.0, 133.0);
          var myOptions = {
            zoom: 4,
            center: centre,
            mapTypeId: google.maps.MapTypeId.ROADMAP
          };
          map = new google.maps.Map(document.getElementById("map_canvas"), myOptions);

          <g:each var="loc" in="${locations}">
            <g:if test="${loc.latitude != -1 && loc.longitude != -1}">
              var latLng${loc.link} = new google.maps.LatLng(${loc.latitude}, ${loc.longitude});
            </g:if>
            <g:else>
              var address = "${loc.streetAddress}";
              var latLng${loc.link};
              geocoder.geocode( { 'address': address }, function(results, status) {
                if (status == google.maps.GeocoderStatus.OK) {
                  latLng${loc.link} = results[0].geometry.location;
                  var marker${loc.link} = new google.maps.Marker({
                    position: latLng${loc.link},
                    map: map,
                    title: "${loc.name}"
                  });
                }
              });
            </g:else>
              var marker${loc.link} = new google.maps.Marker({
                position: latLng${loc.link},
                map: map,
                title: "${loc.name}"
              });
              var infoWindow${loc.link} = new google.maps.InfoWindow({
                content: "<a href='/Collectory/collection/preview/${loc.link}'>${loc.name}</a>'"
              });
              google.maps.event.addListener(marker${loc.link}, 'click', function() {
                infoWindow${loc.link}.open(map, marker${loc.link});
              });
              
          </g:each>

        }

        function codeAddress(address) {
          if (geocoder) {
            geocoder.geocode( { 'address': address}, function(results, status) {
              if (status == google.maps.GeocoderStatus.OK) {
                var marker = new google.maps.Marker({
                    map: map,
                    position: results[0].geometry.location,
                    title: "Mark's house"
                });
                var infoWindow = new google.maps.InfoWindow({
                  content: "Mark lives here"
                });

                google.maps.event.addListener(marker, 'click', function() {
                  infoWindow.open(map, marker);
                });
              } else {
                alert("Geocode was not successful for the following reason: " + status);
              }
            });
          }
        }

      </script>
    </body>
</html>
