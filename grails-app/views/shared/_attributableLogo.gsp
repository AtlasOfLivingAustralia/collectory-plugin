  <g:if test="${fieldValue(bean: command, field: 'logoRef.file')}">
    <div>
        <img id="logo" alt="${fieldValue(bean: command, field: "logoRef.file")}"
                src="${resource(absolute: 'true', dir:'data/'+directory, file:command.logoRef.file)}" />
      <td colspan="2" style="padding-bottom:14px;"><span style="padding-right:75px;">${command.logoRef.file}</span></td>
    </div>
  </g:if>
  <div class="form-group">
    <label for="logoFile">
      <g:if test="${fieldValue(bean: command, field: 'logoRef.file')}">
        <g:message code="shared.mes01" /> <br/><g:message code="shared.mes02" />
      </g:if>
      <g:else>
        <g:message code="shared.mes03" /> <br/><g:message code="shared.mes04" />
      </g:else>
    </label>
      <input id="logoFile" class="form-control" type="file" name="logoFile" value="${command?.logoRef?.file}"/>
  </div>
  <div class="form-group">
      <label for="logoRef.caption"><g:message code="providerGroup.logoRef.filename.label" default="Caption" /></label>
        <g:textField class="form-control" name="logoRef.caption" maxlength="128" value="${command?.logoRef?.caption}" />
  </div>
  <div class="form-group">
      <label for="logoRef.attribution"><g:message code="providerGroup.logoRef.attribution.label" default="Attribution" /></label>
        <g:textField class="form-control" name="logoRef.attribution" maxlength="128" value="${command?.logoRef?.attribution}" />
  </div>
  <div class="form-group">
      <label for="logoRef.copyright"><g:message code="providerGroup.logoRef.copyright.label" default="Copyright" /></label>
        <g:textField class="form-control" name="logoRef.copyright" maxlength="128" value="${command?.logoRef?.copyright}" />
  </div>
