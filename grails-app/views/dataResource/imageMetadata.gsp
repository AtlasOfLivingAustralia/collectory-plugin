<%@ page import="au.org.ala.collectory.Licence; groovy.json.JsonSlurper; au.org.ala.collectory.DataResource;au.org.ala.collectory.Institution;au.org.ala.collectory.Collection" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="layout" content="${grailsApplication.config.skin.layout}" />
    <title><g:message code="dataResource.base.label" default="Edit data resource metadata" /></title>
    <link rel="stylesheet" href="${resource(dir:'css/smoothness',file:'jquery-ui-1.12.1.min.css')}" type="text/css" media="screen"/>
    <r:require modules="jquery_ui_custom"/>
</head>
<body>
<div class="nav">
    <h1><g:message code="collection.title.editing" />: ${command.name} : Image metadata</h1>
</div>
<div id="baseForm" class="body">
    <g:if test="${message}">
        <div class="message">${message}</div>
    </g:if>
    <g:hasErrors bean="${command}">
        <div class="errors">
            <g:renderErrors bean="${command}" as="list" />
        </div>
    </g:hasErrors>
    <div class="row">
        <div class="col-md-8">
            <g:set var="imageMetadata" value="${new groovy.json.JsonSlurper().parseText(command.imageMetadata?:'{}')}"/>
            <g:form controller="dataResource" action="updateImageMetadata">
                <g:hiddenField name="id" value="${command?.id}" />
                <g:hiddenField name="version" value="${command.version}" />
                <fieldset>
                    <div class="form-group">
                        <label for="creator">Creator</label>
                        <g:textField name="creator" class="form-control" placeholder="Type something…" value="${imageMetadata?.creator}"/>
                    </div>
                    <div class="form-group">
                        <label for="rights">Rights</label>
                        <g:textArea name="rights" rows="3" class="form-control" placeholder="Type something…" value="${imageMetadata?.rights}"/>
                    </div>
                    <div class="form-group">
                        <label for="rightsHolder">Rights holder</label>
                        <g:textArea name="rightsHolder" rows="3" class="form-control" placeholder="Type something…" value="${imageMetadata?.rightsHolder}"/>
                    </div>
                    <div class="form-group">
                        <label for="license">Licence</label>
                        <g:select name="license" from="${au.org.ala.collectory.Licence.findAll().collect{it.name } }" id="license" class="form-control" value="${imageMetadata?.license}"/>
                    </div>
                    <div class="form-group">
                        <button type="submit" class="btn btn-success">Save</button>
                    </div>
                </fieldset>
            </g:form>
        </div>
        <div class="col-md-4 well">
            <p>
                These values will be displayed on record pages where the images are displayed unless
                values have been provided that are specific to the image.
            </p>
        </div>
    </div>
</div>
</body>
</html>
