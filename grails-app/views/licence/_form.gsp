<%@ page import="au.org.ala.collectory.Licence" %>

<div class="fieldcontain ${hasErrors(bean: licenceInstance, field: 'name', 'error')} required">
    <label for="name">
        <g:message code="licenceInstance.name.label" default="Name" />
        <span class="required-indicator">*</span>
    </label>
    <g:textField name="name" class="input-xlarge" maxlength="200" required="" value="${licenceInstance?.name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenceInstance, field: 'acronym', 'error')} required">
    <label for="name">
        <g:message code="licenceInstance.acronym.label" default="Acronym" />
        <span class="required-indicator">*</span>
    </label>
    <g:textField name="acronym" maxlength="200" required="" value="${licenceInstance?.acronym}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenceInstance, field: 'acronym', 'error')} required">
    <label for="name">
        <g:message code="licenceInstance.licenceVersion.label" default="Version" />
        <span class="required-indicator">*</span>
    </label>
    <g:textField name="licenceVersion" maxlength="200" required="" value="${licenceInstance?.licenceVersion}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenceInstance, field: 'url', 'error')} required">
    <label for="name">
        <g:message code="licenceInstance.url.label" default="URL" />
        <span class="required-indicator">*</span>
    </label>
    <g:textField name="url" class="input-xxlarge" maxlength="200" required="" value="${licenceInstance?.url}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenceInstance, field: 'imageUrl', 'error')} required">
    <label for="name">
        <g:message code="licenceInstance.imageUrl.label" default="Image url" />
        <span class="required-indicator">*</span>
    </label>
    <g:textField name="imageUrl" class="input-xxlarge" maxlength="200" required="" value="${licenceInstance?.imageUrl}"/>
</div>


