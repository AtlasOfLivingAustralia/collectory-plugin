<%@ page import="au.org.ala.collectory.Licence; groovy.json.JsonSlurper; au.org.ala.collectory.DataResource;au.org.ala.collectory.Institution;au.org.ala.collectory.Collection" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="layout" content="${grailsApplication.config.skin.layout}" />
    <title><g:message code="dataResource.base.label" default="Edit data resource metadata" /></title>
    <link rel="stylesheet" href="${resource(dir:'css/smoothness',file:'jquery-ui-1.8.14.custom.css')}" type="text/css" media="screen"/>
    <r:require modules="jquery, jquery_ui_custom"/>
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
    <div class="row-fluid">
        <div class="span6">

            <g:set var="imageMetadata" value="${new groovy.json.JsonSlurper().parseText(command.imageMetadata?:'{}')}"/>

            <g:form controller="dataResource" action="updateImageMetadata">
                <g:hiddenField name="id" value="${command?.id}" />
                <g:hiddenField name="version" value="${command.version}" />
                <fieldset>
                    <label>Creator</label>
                    <input type="text" name="creator" class="input-xxlarge" placeholder="Type something…" value="${imageMetadata?.creator}">

                    <label>Rights</label>
                    <textarea name="rights" rows="3" class="input-xxlarge" placeholder="Type something…">${imageMetadata?.rights}</textarea>

                    <label>Rights holder</label>
                    <textarea name="rightsHolder" rows="3" class="input-xxlarge" placeholder="Type something…">${imageMetadata?.rightsHolder}</textarea>

                    <label>Licence</label>
                    <g:select name="license" from="${au.org.ala.collectory.Licence.findAll().collect{it.name } }" id="license"
                              class="form-control input-xxlarge" value="${imageMetadata?.license}">
                    </g:select>
                    <br/>
                    <button type="submit" class="btn">Save</button>
                </fieldset>
            </g:form>
        </div>
        <div class="span6 well">
            <p>
                These values will be displayed on record pages where the images are displayed unless
                values have been provided that are specific to the image.
            </p>
        </div>
    </div>
</div>
</body>
</html>
