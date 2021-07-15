<%@ page import="au.org.ala.collectory.ProviderGroup" %>
<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="${grailsApplication.config.skin.layout}" />
		<g:set var="entityName" value="${message(code: 'licence.label', default: 'Licence')}" />
		<title><g:message code="default.show.label" args="[entityName]" /></title>
	</head>
	<body>
		<div class="btn-toolbar">
			<ul class="btn-group">
				<li class="btn btn-default"><cl:homeLink/></li>
				<li class="btn btn-default"><span class="glyphicon glyphicon-list"></span><g:link class="list" action="list"> <g:message code="default.list.label" args="[entityName]"/></g:link></li>
				<li class="btn btn-default"><span class="glyphicon glyphicon-plus"></span><g:link class="create" action="create"> <g:message code="default.new.label" args="[entityName]"/></g:link></li>
			</ul>
		</div>
		<div id="show-licence" class="content scaffold-show" role="main">
			<h1><g:fieldValue bean="${licenceInstance}" field="name"/></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<div class="property-list licence">
				<p>
				<label id="name-label" class="property-label category"><g:message code="licence.name.label" default="Name" /></label>
				<span class="property-value" aria-labelledby="code-label"><g:fieldValue bean="${licenceInstance}" field="name"/></span>
				</p>
				<p>
				<label id="acronymlabel" class="property-label category"><g:message code="licence.acronym.label" default="Acronym" /></label>
				<span class="property-value" aria-labelledby="code-label"><g:fieldValue bean="${licenceInstance}" field="acronym"/></span>
				</p>
				<p>
				<label id="versionlabel" class="property-label category"><g:message code="licence.acronym.label" default="Version" /></label>
				<span class="property-value" aria-labelledby="code-label"><g:fieldValue bean="${licenceInstance}" field="licenceVersion"/></span>
				</p>
				<p>
				<label id="url-label" class="property-label category"><g:message code="licence.url.label" default="URL" /></label>
				<span class="property-value" aria-labelledby="code-label"><g:fieldValue bean="${licenceInstance}" field="url"/></span>
				</p>
				<p>
				<label id="imageUrl-label" class="property-label category"><g:message code="licence.imageUrl.label" default="Image" /></label>
				<span class="property-value" aria-labelledby="code-label">
					<g:if test="${licenceInstance.imageUrl}">
						<img src="${licenceInstance.imageUrl}"/>
					</g:if>
					<g:else>
						Image URL not specified
					</g:else>
				</span>
				</p>
				<br/>
			</div>
			<div class="btn-toolbar">
				<g:form class="btn-group">
					<g:hiddenField name="id" value="${licenceInstance?.id}"/>
					<g:link class="edit btn btn-default" action="edit" id="${licenceInstance?.id}"><g:message code="default.button.edit.label" default="Edit" /></g:link>
					<cl:ifGranted role="${ProviderGroup.ROLE_ADMIN}">
						<g:actionSubmit class="delete btn btn-danger" action="delete" value="${message(code: 'default.button.delete.label', default: 'Delete')}" onclick="return confirm('${message(code: 'default.button.delete.confirm.message', default: 'Are you sure?')}');"/>
					</cl:ifGranted>
				</g:form>
			</div>
		</div>
	</body>
</html>
