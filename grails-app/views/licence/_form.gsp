<%@ page import="au.org.ala.collectory.Licence" %>

<div class="fieldcontain ${hasErrors(bean: licenceInstance, field: 'name', 'error')} form-group">
    <label for="name"><cl:required><g:message code="licenceInstance.name.label" default="Name" /></cl:required></label>
    <g:textField name="name" class="form-control" maxlength="200" required="" value="${licenceInstance?.name}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenceInstance, field: 'acronym', 'error')} form-group">
    <label for="name"><cl:required><g:message code="licenceInstance.acronym.label" default="Acronym" /></cl:required></label>
    <g:textField name="acronym" class="form-control" maxlength="200" required="" value="${licenceInstance?.acronym}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenceInstance, field: 'acronym', 'error')} form-group">
    <label for="name"><cl:required><g:message code="licenceInstance.licenceVersion.label" default="Version" /></cl:required></label>
    <g:textField name="licenceVersion" class="form-control" maxlength="200" required="" value="${licenceInstance?.licenceVersion}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenceInstance, field: 'url', 'error')} form-group">
    <label for="name"><cl:required><g:message code="licenceInstance.url.label" default="URL" /></cl:required></label>
    <g:field type="url" name="url" class="form-control" maxlength="200" required="" value="${licenceInstance?.url}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: licenceInstance, field: 'imageUrl', 'error')} form-group">
    <label for="name"><cl:required><g:message code="licenceInstance.imageUrl.label" default="Image url" /></cl:required></label>
    <g:field type="url" name="imageUrl" class="form-control" maxlength="200" required="" value="${licenceInstance?.imageUrl}"/>
</div>


