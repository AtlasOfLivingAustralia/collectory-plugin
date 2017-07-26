<%@ page import="au.org.ala.collectory.ProviderGroup" %>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <meta name="layout" content="${grailsApplication.config.skin.layout}"/>
  <g:set var="entityName" value="${command.ENTITY_TYPE}"/>
  <g:set var="entityNameLower" value="${command.ENTITY_TYPE.toLowerCase()}"/>
  <title>Edit ${entityNameLower} metadata</title>
</head>
<body onload="load();">
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
    <!-- BCI -->
    <div class="form-group">
      <label for="BCI">
        <g:checkBox name="BCI" value="${BCI}"/>
        <g:message code="shared.attributes.label01" /><cl:helpText code="providerGroup.attribution.BCI"/>
      </label>
    </div>

    <!-- CHAH -->
    <div class="form-group">
      <label for="CHAH">
        <g:checkBox name="CHAH" value="${CHAH}"/>
        <g:message code="shared.attributes.label02" /><cl:helpText code="providerGroup.attribution.CHAH"/>
      </label>
    </div>

    <!-- CHACM -->
    <div class="form-group">
      <label for="CHACM">
        <g:checkBox name="CHACM" value="${CHACM}"/>
        <g:message code="shared.attributes.label03" /><cl:helpText code="providerGroup.attribution.CHACM"/>
      </label>
    </div>

    <!-- institution -->
    <div class="form-group">

      <label for="institution">
        <g:checkBox disabled="true" name="institution" value="${true}"/>
        <g:message code="shared.attributes.label04" /><cl:helpText code="providerGroup.attribution.institution"/>
      </label>
    </div>

    <div class="buttons">
      <span class="button"><input type="submit" name="_action_updateAttributions" value="Update" class="save btn btn-success"></span>
      <span class="button"><input type="submit" name="_action_cancel" value="Cancel" class="cancel btn btn-default"></span>
    </div>
  </g:form>
</div>

</body>
</html>
