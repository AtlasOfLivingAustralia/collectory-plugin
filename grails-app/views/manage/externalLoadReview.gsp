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
<h1><g:message code="manage.extloadr.title01" args="${[ dataProvider?.name ?: 'none' ]}"/></h1>
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
            <div class="span12">
                <table class="resource-table">
                    <tr class="header">
                        <th><g:message code="manage.extloadr.label01"/></th>
                        <th><g:message code="manage.extloadr.label02"/></th>
                        <th><g:message code="manage.extloadr.label03"/></th>
                        <th><g:message code="manage.extloadr.label04"/></th>
                        <th><g:message code="manage.extloadr.label05"/></th>
                        <th><g:message code="manage.extloadr.label06"/></th>
                        <th><g:message code="manage.extloadr.label07"/></th>
                        <th><g:message code="manage.extloadr.label08"/></th>
                    </tr>
                    <g:each in="${configuration.resources}" var="res" status="rs">
                    <tr class="resource-scan-${res.status}">
                        <td>
                            <g:hiddenField id="resources-${rs}-uid" name="resources[${rs}].uid" value="${res.uid}"/>
                            <g:hiddenField name="resources[${rs}].guid" value="${res.guid}"/>
                            <g:hiddenField name="resources[${rs}].source" value="${res.source}"/>
                            <g:textField name="resources[${rs}].name" value="${res.name}"/></td>
                        <td><a href="${res.source}"><g:fieldValue field="guid" bean="${res}"/></a></td>
                        <td><span title="<g:message code="manage.extstatus.${res.status}.detail"/>"><g:message code="manage.extstatus.${res.status}"/></span></td>
                        <td><span id="existing-${rs}"><g:if test="${res.uid}"><g:fieldValue field="uid" bean="${res}"/> - <g:fieldValue field="name" bean="${res}"/></g:if></span><span class="btn" onclick="existingDialog('#existing-${rs}', '#resources-${rs}-uid'); return false"><g:message code="manage.extloadr.button01" default="..."/></span> </td>
                        <td><g:formatDate type="datetime" date="${res.sourceUpdated}"/><g:if test="${res.existingChecked}">&nbsp;(<g:formatDate type="datetime" date="${res.existingChecked}"/>)</g:if></td>
                        <td><g:checkBox name="resources[${rs}].addResource" value="${res.addResource}"/></td>
                        <td><g:checkBox name="resources[${rs}].updateMetadata" value="${res.updateMetadata}"/></td>
                        <td><g:checkBox name="resources[${rs}].updateConnection" value="${res.updateConnection}"/></td>
                    </tr>
                    </g:each>
                </table>
                <span class="button"><input type="submit" name="performLoad" value="Load" class="save btn"></span>
         </div>
    </g:form>
</div>
<div id="existing-dialog" title="<g:message code="manage.extloadr.title02" default="Find Data Resource"/>" class="hide">
    <table id="existing-dataresources">
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
        <span id="existing-ok-button" class="btn"><g:message code="manage.extloadr.button.ok" default="Ok"/></span>
        <span id="existing-cancel-button" class="btn" onlcick="$('#existing-dialog').dialog('close')"><g:message code="manage.extloadr.button.cancel" default="Cancel"/></span>
    </div>
</div>
<script type="text/javascript">
    var existing_table;

    $(document).ready( function () {
        existing_table = $('#existing-dataresources').DataTable({
            select: "single"
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

        width =  Math.ceil(Math.min(width * 0.9, Math.max(700, width * 0.4)));
        $('#existing-ok-button').off('click'); // jQuery click function *adds* handler
        $('#existing-ok-button').click( function() { existingOk(existingId, uidId); $('#existing-dialog').dialog("close"); });
        $('#existing-dialog').dialog({
            modal: true,
            width: width,
            zIndex: 2000
        });
        if (uid != null && uid != '') {
            var selected = existing_table.row('#existing-row-' + uid);
            selected.select();
        }
    }

    /* Fix for curCSS bug */
    jQuery.curCSS = function(element, prop, val) {
        return jQuery(element).css(prop, val);
    };
</script>
</body>
</html>