<%@ page import="grails.converters.JSON; au.org.ala.collectory.ProviderGroup; au.org.ala.collectory.DataProvider" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <g:set var="entityName" value="${instance.ENTITY_TYPE}" />
        <g:set var="entityNameLower" value="${cl.controller(type: instance.ENTITY_TYPE)}"/>
        <title><g:message code="default.show.label" args="[entityName]" /></title>
        <script type="text/javascript" src="https://maps.google.com/maps/api/js?v=3.3&sensor=false"></script>
        <r:require modules="fileupload"/>
    </head>
    <body>
        <h1><g:message code="dataresource.upload.title" />:
            <g:link controller="dataResource" action="show" id="${instance.uid}">
            ${fieldValue(bean: instance, field: "name")}
            <cl:valueOrOtherwise value="${instance.acronym}"> (${fieldValue(bean: instance, field: "acronym")})</cl:valueOrOtherwise>
            </g:link>
        </h1>

        <div class="well pull-right col-md-4">
            <g:message code="dataresource.upload.des" />.
        </div>

    <g:uploadForm action="uploadDataFile" controller="dataResource">

        <g:hiddenField name="id" value="${instance.uid}"/>

        <!-- drag and drop file uploads -->
        <div class="form-group">
            <label for="protocol"><g:message code="dataresource.upload.label.protocol" />:</label>
            <g:select id="protocol" name="protocol" class="form-control" from="${connectionProfiles}" value="protocol" optionValue="display" optionKey="name"/>
        </div>

        <div class="form-group">
            <label for="fileToUpload"><g:message code="dataresource.upload.label.file" /></label>

            %{--<div class="input-group fileupload fileupload-new" data-provides="fileupload">--}%
                %{--<div class="uneditable-input input-group-addon">--}%
                    %{--<i class="glyphicon glyphicon-file fileupload-exists"></i>--}%
                    %{--<span class="fileupload-preview"></span>--}%
                %{--</div>--}%
                %{--<div class="input-group-addon">--}%
                    %{--<span class="fileupload-new"><g:message code="dataresource.upload.label.selectfile" /></span>--}%
                    %{--<span class="fileupload-exists"><g:message code="dataresource.upload.label.change" /></span>--}%
                %{--</div>--}%
                %{--<g:field type="file" name="myFile" />--}%
                %{--<div class="input-group-addon">--}%
                    %{--<a href="#" class="btn fileupload-exists" data-dismiss="fileupload">Remove</a>--}%
                %{--</div>--}%
            %{--</div>--}%

            %{--<label for="input-b1">Select File</label>--}%
            %{--<input id="input-b1" name="input-b1" type="file" class="file">--}%

            <div class="fileupload fileupload-new" data-provides="fileupload">
                <span class="btn btn-primary btn-file">
                    <span class="fileupload-new"><g:message code="dataresource.upload.label.selectfile" /></span>
                    <span class="fileupload-exists"><g:message code="dataresource.upload.label.change" /></span>
                    <g:field type="file" name="myFile" /></span>
                <span class="fileupload-preview"></span>
                <a href="#" class="close fileupload-exists" data-dismiss="fileupload" style="float: none">×</a>
            </div>


        </div>

        <div id="connectionParams">

        </div>

        <div style="clear:both">
            <input type="submit" id="fileToUpload" class="btn fileupload-exists btn-primary" value="Upload"/>
            <g:link action="show" controller="dataResource" id="${instance.id}" class="btn btn-default">
                <g:message code="dataresource.upload.label.cancel" />
            </g:link>
        </div>
        </g:uploadForm>

        <div id="connectionTemplates" class="hide">
            <g:each in="${connectionProfiles}" var="profile">
                <div id="profile-${profile.name}">
                    <g:each in="${profile.params.minus('LOCATION_URL')}" var="param">
                        <!-- get param -->
                        <g:set var="connectionParam" value="${connectionParams[param]}"/>
                        <div class="form-group">
                        <g:if test="${connectionParam.type == 'boolean'}">
                            <label class="checkbox ${profile.name}">
                                <g:checkBox id="${connectionParam.paramName}" name="${connectionParam.paramName}"/>
                                ${connectionParam.display}
                            </label>
                        </g:if>
                        <g:else>
                            <label for="${connectionParam.paramName}">${connectionParam.display}:</label>
                            <input type="text" class="form-control" id="${connectionParam.paramName}" name="${connectionParam.paramName}" value="${connectionParam.defaultValue}" />
                        </g:else>
                        </div>
                    </g:each>
                </div>
            </g:each>
        </div>

        <r:script>

            function loadConnParams(){
               $('#connectionParams').html('');
               var $protocol = $('#protocol');
               $('#connectionParams').html($('#profile-' + $protocol.val()).html());
            }

            $(function(){
               $('#protocol').change(function(){
                   loadConnParams();
               });
               loadConnParams();
            })
        </r:script>

    </body>
</html>
