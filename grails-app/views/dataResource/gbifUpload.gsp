<%@ page import="grails.converters.JSON; au.org.ala.collectory.ProviderGroup; au.org.ala.collectory.DataProvider" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="layout" content="${grailsApplication.config.skin.layout}" />

    <title><g:message code="upload.gbif.archive" /></title>
    <script type="text/javascript" src="https://maps.google.com/maps/api/js?v=3.3&sensor=false"></script>
    <r:require modules="fileupload"/>
</head>
<body>

<h1><g:message code="dataresource.gbifupload.title" /></h1>

<div class="row">

    <div class="well pull-right col-md-6">
        <p>
            <g:message code="dataresource.gbifupload.des01" />
            <a href="https://www.gbif.org/"><g:message code="dataresource.gbifupload.link.gbifportal" /></a>.
            <br/>
            <g:message code="dataresource.gbifupload.des02" />.
        </p>
        <p>
            <b><g:message code="dataresource.gbifupload.des03" /></b>: <g:message code="dataresource.gbifupload.des04" />.<br/>
            <g:message code="dataresource.gbifupload.des05" />.
        </p>
        <p>
            <g:message code="dataresource.gbifupload.info0"/> <br/>
            <strong>https://api.gbif.org/v1/occurrence/download/request/0001008-150512124619364.zip</strong>
            <br/>
            <g:message code="dataresource.gbifupload.info1"/> <br/>
        </p>
    </div>

    <div class="col-md-6" style="padding-left:0;margin-left:0;">
        <div class="form-group">
            <label for="url"><g:message code="dataresource.gbifupload.label" /></label>
            <g:field type="url" class="form-control" name="url" value=""/>
        </div>
        <div>
            <button class="btn btn-default" id="downloadArchiveBtn">
                <r:img class="spinner-progress hide" uri="/images/spinner.gif"></r:img>
                <g:message code="dataresource.gbifupload.btn"/>
            </button>
        </div>
        <div id="download-result" class="hide well" style="margin-top:30px;">
            <g:message code="dataresource.gbifupload.success0"/>:<br/>
            <strong><span id="new-resource-name"></span></strong>.<br/>
            <a href="#" id="new-resource-link"><g:message code="dataresource.gbifupload.success1"/></a>
        </div>
    </div>
</div>

<r:script>

    var dataResourceUrlBase = '${g.createLink([controller:"dataResource",action:"show"])}/';

    function downloadFile(){

        $('#download-result').addClass("hide");
        $('.spinner-progress').removeClass("hide")

        $('#downloadArchiveBtn').prop('disabled', true);
        $.ajax( "downloadGBIFFile?url=" +  $('#url').val() )
        .done(function(data) {
            //alert( "success - " + data.dataResourceUid );
            $('#download-result').removeClass("hide");
            $('#new-resource-name').html(data.dataResourceName );
            $('#new-resource-link').attr('href', dataResourceUrlBase + data.dataResourceUid );
        })
        .fail(function() {
            alert( "error" );
        })
        .always(function() {
            $('.spinner-progress').addClass("hide");
            $('#downloadArchiveBtn').prop('disabled', false);
        });
    }

    $('#downloadArchiveBtn').click(downloadFile);

</r:script>


</body>
</html>
