<!-- location -->
<div class="show-section well">
  <h2><g:message code="shared.location.title01" /></h2>
  <table>
    <colgroup><col width="10%"/><col width="45%"/><col width="45%"/></colgroup>
    <!-- Address -->
    <tr class="prop">
      <td valign="top" class="name category"><g:message code="address.label" default="Address"/></td>

      <td valign="top" class="value">
        <address>
        ${fieldValue(bean: instance, field: "address.street")}<br/>
        ${fieldValue(bean: instance, field: "address.city")}<br/>
        ${fieldValue(bean: instance, field: "address.state")}
        ${fieldValue(bean: instance, field: "address.postcode")}
        <g:if test="${fieldValue(bean: instance, field: 'address.country') != 'Australia'}">
          <br/>${fieldValue(bean: instance, field: "address.country")}
        </g:if>
        </address>
      </td>

      <!-- map spans all rows -->
      <td rowspan="6">
        <div id="mapCanvas" style="width:100%;height:400px;"></div>
      </td>

    </tr>

    <!-- Postal -->
    <tr class="prop">
      <td valign="top" class="name category"><g:message code="providerGroup.address.postal.label" default="Postal"/></td>
      <td valign="top" class="value">${fieldValue(bean: instance, field: "address.postBox")}</td>
    </tr>

    <!-- Latitude -->
    <tr class="prop">
      <td valign="top" class="name category"><g:message code="providerGroup.latitude.label" default="Latitude"/></td>
      <td valign="top" class="value"><cl:showDecimal value='${instance.latitude}' degree='true'/></td>
    </tr>

    <!-- Longitude -->
    <tr class="prop">
      <td valign="top" class="name category"><g:message code="providerGroup.longitude.label" default="Longitude"/></td>
      <td valign="top" class="value"><cl:showDecimal value='${instance.longitude}' degree='true'/></td>
    </tr>

    <!-- State -->
    <tr class="prop">
      <td valign="top" class="name category"><g:message code="providerGroup.state.label" default="State"/></td>
      <td valign="top" class="value">${fieldValue(bean: instance, field: "state")}</td>
    </tr>

    <!-- Email -->
    <tr class="prop">
      <td valign="top" class="name category"><g:message code="providerGroup.email.label" default="Email"/></td>
      <td valign="top" class="value">${fieldValue(bean: instance, field: "email")}</td>
    </tr>

    <!-- Phone -->
    <tr class="prop">
      <td valign="top" class="name category"><g:message code="providerGroup.phone.label" default="Phone"/></td>
      <td valign="top" class="value">${fieldValue(bean: instance, field: "phone")}</td>
    </tr>
  </table>
  <div style="clear:both;"></div>
  <cl:editButton uid="${instance.uid}" page="/shared/location"/>
</div>

