<%@ page import="au.org.ala.collectory.ProviderGroup" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="${grailsApplication.config.ala.skin}"/>
  <g:set var="entityName" value="${command.ENTITY_TYPE}"/>
  <g:set var="entityNameLower" value="${command.ENTITY_TYPE.toLowerCase()}"/>
  <title>Edit ${entityNameLower} metadata</title>
</head>
<body onload="load();">
  <style>
  #mapCanvas {
    width: 300px;
    height: 300px;
    float: right;
  }
  </style>
<div class="nav">
  <h1>Editing: ${fieldValue(bean: command, field: "name")}</h1>
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

        <!-- BCI -->
        <tr class="prop">
          <td valign="top" class="name">
            <label for="BCI"><g:message code="shared.attributes.label01" /></label>
          </td>
          <td valign="top" class="value">
            <g:checkBox name="BCI" value="${BCI}"/>
            <cl:helpText code="providerGroup.attribution.BCI"/>
          </td>
          <cl:helpTD/>
        </tr>

        <!-- CHAH -->
        <tr class="prop">
          <td valign="top" class="name">
            <label for="CHAH"><g:message code="shared.attributes.label02" /></label>
          </td>
          <td valign="top" class="value">
            <g:checkBox name="CHAH" value="${CHAH}"/>
            <cl:helpText code="providerGroup.attribution.CHAH"/>
          </td>
          <cl:helpTD/>
        </tr>

        <!-- CHACM -->
        <tr class="prop">
          <td valign="top" class="name">
            <label for="CHACM"><g:message code="shared.attributes.label03" /></label>
          </td>
          <td valign="top" class="value">
            <g:checkBox name="CHACM" value="${CHACM}"/>
            <cl:helpText code="providerGroup.attribution.CHACM"/>
          </td>
          <cl:helpTD/>
        </tr>

        <!-- institution -->
        <tr class="prop">
          <td valign="top" class="name">
            <label for="institution"><g:message code="shared.attributes.label04" /></label>
          </td>
          <td valign="top" class="value">
            <g:checkBox disabled="true" name="institution" value="${true}"/>
            <cl:helpText code="providerGroup.attribution.institution"/>
          </td>
          <cl:helpTD/>
        </tr>

        </tbody>
      </table>
    </div>
    <div class="buttons">
      <span class="button"><input type="submit" name="_action_updateAttributions" value="Update" class="save"></span>
      <span class="button"><input type="submit" name="_action_cancel" value="Cancel" class="cancel"></span>
    </div>
  </g:form>
</div>

</body>
</html>
