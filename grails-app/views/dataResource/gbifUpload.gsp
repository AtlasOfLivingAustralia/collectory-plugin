<%@ page import="grails.converters.JSON; au.org.ala.collectory.ProviderGroup; au.org.ala.collectory.DataProvider" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="layout" content="${grailsApplication.config.ala.skin}" />

    <title><g:message code="upload.gbif.archive" /></title>
    <script type="text/javascript" src="http://maps.google.com/maps/api/js?v=3.3&sensor=false"></script>
    <r:require modules="fileupload"/>
</head>
<body>
<h1><g:message code="dataresource.gbifupload.title" />
</h1>

<g:uploadForm action="uploadGBIFFile" controller="dataResource">
    <label for="fileToUpload"><g:message code="dataresource.gbifupload.label" />:</label>
    <div class="fileupload fileupload-new" data-provides="fileupload">

        <div class="well pull-right span5">
            <p>
                <g:message code="dataresource.gbifupload.des01" /> <a href="http://www.gbif.org/"><g:message code="dataresource.gbifupload.link.gbifportal" /></a>.
                <br/>
                <g:message code="dataresource.gbifupload.des02" />.
            </p>
            <p>
                <b><g:message code="dataresource.gbifupload.des03" /></b>: <g:message code="dataresource.gbifupload.des04" />.<br/>
                <g:message code="dataresource.gbifupload.des05" />.
            </p>
        </div>

        <div class="input-append">
            <div class="uneditable-input span3">
                <i class="icon-file fileupload-exists"></i>
                <span class="fileupload-preview"></span>
            </div>
            <span class="btn btn-file">
                <span class="fileupload-new"><g:message code="dataresource.gbifupload.span01" /></span>
                <span class="fileupload-exists"><g:message code="dataresource.gbifupload.span02" /></span>
                <input type="file" name="myFile" />
            </span>
            <a href="#" class="btn fileupload-exists" data-dismiss="fileupload"><g:message code="dataresource.gbifupload.link.remove" /></a>
        </div>
        <div style="clear:both">
            <input type="submit" id="fileToUpload" class="btn fileupload-exists btn-primary" value="${message(code:"dataresource.gbifupload.btn.upload")}"/>
            <span class="btn cancel"><g:message code="dataresource.gbifupload.btn.cancel" /></span>
        </div>

    </div>
</g:uploadForm>



</body>
</html>