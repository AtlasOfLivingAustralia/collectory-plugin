<%@ page import="au.org.ala.collectory.ExternalResourceBean; grails.converters.JSON; au.org.ala.collectory.ProviderGroup; au.org.ala.collectory.DataProvider" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <meta name="layout" content="${grailsApplication.config.skin.layout}" />
    <title><g:message code="manage.extloadr.title" /></title>
    <r:require module="jquery_ui_custom"/>
    <r:require module="datatables"/>
</head>
<body>

<h1>
    <g:if test="${dataProvider?.name}">
        <g:message code="manage.extloadr.title01" args="${[ configuration.name, dataProvider?.name ?: 'none' ]}"/>
    </g:if>
    <g:else>
        <g:message code="manage.extloadr.title01.noprovider" />
    </g:else>
</h1>
<div class="btn-toolbar">
    <ul class="btn-group">
        <li class="btn"><cl:homeLink/></li>
    </ul>
</div>
<div id="baseForm">
    <g:form action="updateFromExternalSources" controller="manage">
        <g:hiddenField name="loadGuid" value="${loadGuid}"/>
        <g:hiddenField name="guid" value="${configuration.guid}"/>
        <g:hiddenField name="name" value="${configuration.name}"/>
        <g:hiddenField name="description" value="${configuration.description}"/>
        <g:hiddenField name="adaptorString" value="${configuration.adaptorString}"/>
        <g:hiddenField name="endpoint" value="${configuration.endpoint}"/>
        <g:hiddenField name="dataProviderUid" value="${configuration.dataProviderUid}"/>
        <g:hiddenField name="username" value="${configuration.username}"/>
        <g:hiddenField name="password" value="${configuration.password}"/>
            <div class="col-md-12">
                <table id="resource-table" class="resource-table table table-hover">
                    <thead>
                        <tr class="header">
                            <th><g:message code="manage.extloadr.label01"/></th>
                            %{--<th><g:message code="manage.extloadr.label02"/></th>--}%
                            <th><g:message code="manage.extloadr.label03"/></th>
                            <th><g:message code="manage.extloadr.label04"/></th>
                            <th><g:message code="manage.extloadr.label05"/></th>
                            <th><g:message code="manage.extloadr.label06"/> <button type="button" onclick="invertColumn('.addResource'); return false"><span class="glyphicon glyphicon-check"></span></button></th>
                            <th><g:message code="manage.extloadr.label07"/> <button type="button" onclick="invertColumn('.updateMetadata'); return false"><span class="glyphicon glyphicon-check"></span></button></th>
                            <th><g:message code="manage.extloadr.label08"/> <button type="button" onclick="invertColumn('.updateConnection'); return false"><span class="glyphicon glyphicon-check"></span></button></th>
                        </tr>
                    </thead>
                    <tbody>
                    <g:if test="${configuration.resources}">
                    <g:each in="${configuration.resources}" var="res" status="rs">
                    <tr class="resource-scan-${res.status}">
                        <td>
                            <g:hiddenField id="resources-${rs}-uid" name="resources[${rs}].uid" value="${res.uid}"/>
                            <g:hiddenField name="resources[${rs}].guid" value="${res.guid}"/>
                            <g:hiddenField name="resources[${rs}].source" value="${res.source}"/>
                            <g:textField class="resource-name col-xs-4" name="resources[${rs}].name" value="${res.name}" />
                            <br/>
                            <small><a href="${res.source}"><g:fieldValue field="guid" bean="${res}"/></a></small>
                        </td>
                        <td><span title="<g:message code="manage.extstatus.${res.status}.detail"/>"><g:message code="manage.extstatus.${res.status}"/></span></td>
                        <td class="resource-mapping"><span id="existing-${rs}"><g:if test="${res.uid}"><g:fieldValue field="uid" bean="${res}"/> - <g:fieldValue field="name" bean="${res}"/></g:if></span><span class="btn btn-default btn-xs" onclick="existingDialog('#existing-${rs}', '#resources-${rs}-uid'); return false"><g:message code="manage.extloadr.button01" default="..."/></span> </td>
                        <td><g:formatDate type="datetime" date="${res.sourceUpdated}"/><g:if test="${res.existingChecked}">&nbsp;(<g:formatDate type="datetime" date="${res.existingChecked}"/>)</g:if></td>
                        <td><g:checkBox name="resources[${rs}].addResource" value="${res.addResource}"/></td>
                        <td><g:checkBox name="resources[${rs}].updateMetadata" value="${res.updateMetadata}"/></td>
                        <td><g:checkBox name="resources[${rs}].updateConnection" value="${res.updateConnection}"/></td>
                    </tr>
                    </g:each>
                    </g:if>
                    <g:else>
                        <tr><td colspan="8"><g:message code="manage.extloadr.noresources"/></td></tr>
                    </g:else>
                    </tbody>
                </table>
                <div>
                <span class="button"><g:actionSubmit class="save btn btn-warning" controller="manage" action="updateFromExternalSources" value="${message(code: 'default.button.load.label', default: 'Load')}" onclick="return confirm('${message(code: 'default.button.load.confirm.message', default: 'Are you sure?')}');" /></span>
                </div>
         </div>
    </g:form>
</div>
<div id="existing-dialog" title="<g:message code="manage.extloadr.title02" default="Find Data Resource"/>">
    <table id="existing-dataresources" class="table table-striped">
    <thead>
    <tr><th></th><th><g:message code="manage.extloadr.label01"/></th></tr>
    </thead>
    <tbody>
<g:each in="${dataResources}" var="res" status="rs">
    <tr id="existing-row-${res.uid}"><td>${res.uid}</td><td><g:fieldValue field="name" bean="${res}"/></td></tr>
</g:each>
    </tbody>
    </table>
    <div class="buttons">
        <span id="existing-ok-button" class="btn btn-success"><g:message code="manage.extloadr.button.ok" default="Ok"/></span>
        <span id="existing-cancel-button" class="btn btn-default" onlcick="$('#existing-dialog').dialog('close')"><g:message code="manage.extloadr.button.cancel" default="Cancel"/></span>
    </div>
</div>
<script type="text/javascript">
    var existing_table, existing_dialog, resource_table;

    var width = $(window).width();
    width =  Math.ceil(Math.min(width * 0.9, Math.max(700, width * 0.4)));
    $(function () {
        existing_dialog = $('#existing-dialog').dialog({
            autoOpen: false,
            modal: true,
            width: width,
            zIndex: 2000
        });
        existing_table = $('#existing-dataresources').DataTable({
            select: "single"
        });
        resource_table = $('#resource-table').DataTable({
            "columns": [
                {"orderable": false},
                null,
                null,
                null,
                null,
                {"orderable": false},
                {"orderable": false},
                {"orderable": false}
            ]
        });
    } );

    function existingOk(existingId, uidId) {
        var selected = existing_table.rows({ selected: true});
        var uid = '';
        var name = '';
        if (selected.count() > 0) {
            var data = selected.data().toArray()[0];
            uid = data[0];
            name = data[0] + ' - ' + data[1];
        }
        $(uidId).val(uid);
        $(existingId).html(name);
    }

    function existingDialog(existingId, uidId) {
        var uid = $(uidId).val();
        var width = $(window).width();


        $('#existing-ok-button').off('click'); // jQuery click function *adds* handler
        $('#existing-ok-button').click( function() { existingOk(existingId, uidId); existing_dialog.dialog("close"); });
        existing_dialog.dialog("open");
        if (uid != null && uid != '') {
            var selected = existing_table.row('#existing-row-' + uid);
            selected.select();
        }
    }

    function invertColumn(suffix) {
        $('input:checkbox').each( function(index, element) {
            if (element.name.endsWith(suffix)) {
                element.checked = !element.checked;
            }
        });
    }

    /* Fix for curCSS bug */
    jQuery.curCSS = function(element, prop, val) {
        return jQuery(element).css(prop, val);
    };
</script>
</body>
</html>