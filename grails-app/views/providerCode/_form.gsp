<%@ page import="au.org.ala.collectory.ProviderCode" %>



<div class="fieldcontain ${hasErrors(bean: providerCodeInstance, field: 'code', 'error')} required">
	<label for="code">
		<g:message code="providerCode.code.label" default="Code" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="code" maxlength="200" required="" value="${providerCodeInstance?.code}"/>
</div>

