<table class="shy">
  <g:if test="${fieldValue(bean: command, field: 'logoRef.file')}">
    <tr>
      <td colspan="2">
        <img id="logo" alt="${fieldValue(bean: command, field: "logoRef.file")}"
                src="${resource(absolute: 'true', dir:'data/'+directory, file:command.logoRef.file)}" />
      </td>
    </tr>
    <tr>
      <td colspan="2" style="padding-bottom:14px;"><span style="padding-right:75px;">${command.logoRef.file}</span></td>
    </tr>
  </g:if>
  <tr class='prop'>
    <td valign="top" class="name">
      <g:if test="${fieldValue(bean: command, field: 'logoRef.file')}">
        <g:message code="shared.mes01" /> <br/><g:message code="shared.mes02" />
      </g:if>
      <g:else>
        <g:message code="shared.mes03" /> <br/><g:message code="shared.mes04" />
      </g:else>
    </td>
    <td valign="top" class="value ${hasErrors(bean: command, field: 'logoRef.file', 'errors')}">
      <input id="logoFile" type="file" name="logoFile" value="${command?.logoRef?.file}"/>
    </td>
  </tr>
  <tr class='prop'>
    <td valign="top" class="name">
      <label for="logoRef.caption"><g:message code="providerGroup.logoRef.filename.label" default="Caption" /></label>
    </td>
    <td valign="top" class="value ${hasErrors(bean: command, field: 'logoRef.caption', 'errors')}">
        <g:textField name="logoRef.caption" maxlength="128" value="${command?.logoRef?.caption}" />
    </td>
  </tr>
  <tr class='prop'>
    <td valign="top" class="name">
      <label for="logoRef.attribution"><g:message code="providerGroup.logoRef.attribution.label" default="Attribution" /></label>
    </td>
    <td valign="top" class="value ${hasErrors(bean: command, field: 'logoRef.attribution', 'errors')}">
        <g:textField name="logoRef.attribution" maxlength="128" value="${command?.logoRef?.attribution}" />
    </td>
  </tr>
  <tr class='prop'>
    <td valign="top" class="name">
      <label for="logoRef.copyright"><g:message code="providerGroup.logoRef.copyright.label" default="Copyright" /></label>
    </td>
    <td valign="top" class="value ${hasErrors(bean: command, field: 'logoRef.copyright', 'errors')}">
        <g:textField name="logoRef.copyright" maxlength="128" value="${command?.logoRef?.copyright}" />
    </td>
  </tr>
</table>
