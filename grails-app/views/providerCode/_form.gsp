<%@ page import="au.org.ala.collectory.ProviderCode" %>

<div class="form-group ${hasErrors(bean: providerCodeInstance, field: 'code', 'error')}">
	<label for="code"><cl:required><g:message code="providerCode.code.label" default="Code" /></cl:required></label>
	<g:textField name="code" class="form-control" maxlength="200" required="" value="${providerCodeInstance?.code}"/>
</div>

