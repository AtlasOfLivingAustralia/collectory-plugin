<%@ page import="grails.converters.JSON; au.org.ala.collectory.resources.DarwinCoreFields; au.org.ala.collectory.DataHub; au.org.ala.collectory.ProviderGroup; au.org.ala.collectory.DataResource; au.org.ala.collectory.resources.Profile" %>
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
    <h1><g:message code="collection.title.editing" />: ${command.name}</h1>
</div>
<div id="baseForm" class="row">
    <g:if test="${message}">
        <div class="message">${message}</div>
    </g:if>
    <g:hasErrors bean="${command}">
        <div class="errors">
            <g:renderErrors bean="${command}" as="list" />
        </div>
    </g:hasErrors>
    <g:form method="post" name="contributionForm" action="contribution">
        <g:hiddenField name="id" value="${command?.id}" />
        <g:hiddenField name="uid" value="${command?.uid}" />
        <g:hiddenField name="version" value="${command.version}" />
    %{--<div class="col-md-12">--}%
        <table  style="margin-left:0; padding-left:0;">
            <tbody>

            <!-- status -->
            <div class="form-group">
                    <label for="status"><g:message code="dataResource.status.label" default="Status" /><cl:helpText code="dataResource.status"/></label>
                    <g:select name="status" class="form-control" from="${DataResource.statusList}" value="${command.status}"/>
            </div>

            <!-- provenance -->
            <div class="form-group">
                    <label for="provenance"><g:message code="dataResource.provenance.label" default="Provenance" /><cl:helpText code="dataResource.provenance"/></label>
                     <g:select name="provenance" class="form-control" from="${DataResource.provenanceTypesList}" value="${command.provenance}" noSelection="${['':'none']}"/>
            </div>

            <!-- last checked -->
            <div class="form-group">
                    <label for="lastChecked"><g:message code="dataResource.lastChecked.label" default="Last checked" /><cl:helpText code="dataResource.lastChecked"/></label>
                    <g:textField name="lastChecked" class="form-control" pattern="\\d\\d\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d\\.\\d" value="${command.lastChecked}"/>
            </div>

            <!-- data currency -->
            <div class="form-group">
                <label for="dataCurrency"><g:message code="dataResource.dataCurrency.label" default="Data currency" /><cl:helpText code="dataResource.dataCurrency"/></label>
                <g:textField name="dataCurrency" class="form-control" pattern="\\d\\d\\d\\d-\\d\\d-\\d\\d \\d\\d:\\d\\d:\\d\\d\\.\\d" value="${command.dataCurrency}"/>
            </div>

            <!-- harvest frequency -->
            <div class="form-group">
                <label for="harvestFrequency"><g:message code="dataResource.harvestFrequency.label" default="Harvest frequency" /><cl:helpText code="dataResource.harvestFrequency"/></label>
                <g:textField name="harvestFrequency" class="form-control" value="${command.harvestFrequency}"/>
            </div>

            <!-- mob notes -->
            <div class="form-group">
                <label for="mobilisationNotes"><g:message code="dataResource.mobilisationNotes.label" default="Mobilisation notes" /><cl:helpText code="dataResource.mobilisationNotes"/></label>
                <g:textArea name="mobilisationNotes" class="form-control" rows="${cl.textAreaHeight(text:command.mobilisationNotes)}" value="${command?.mobilisationNotes}" />
                    <p class="help-block"><g:message code="dataresource.contribution.des01" />.</p>
            </div>

            <!-- harvest notes -->
            <div class="form-group">
                <label for="harvestingNotes"><g:message code="dataResource.harvestingNotes.label" default="Harvesting notes" /> <cl:helpText code="dataResource.harvestingNotes"/></label>
                <g:textArea name="harvestingNotes" class="form-control" rows="${cl.textAreaHeight(text:command.harvestingNotes)}" value="${command?.harvestingNotes}" />
            </div>

            <!-- public archive -->
            <div class="form-group">
                <label for="publicArchiveAvailable">
                    <g:checkBox name="publicArchiveAvailable" value="${command?.publicArchiveAvailable}" />
                    <g:message code="dataResource.publicArchiveAvailable.label" default="Public archive available" /><cl:helpText code="dataResource.publicArchiveAvailable"/>
                </label>
            </div>

            <!-- harvest parameters -->
            <tr><h3><g:message code="dataresource.contribution.table0101" /></h3></div>
            <cl:connectionParameters bean="command" connectionParameters="${command.connectionParameters}"/>

            <g:if test="${command.resourceType == 'records'}">
                <!-- darwin core defaults -->
                <tr><h3><g:message code="dataresource.contribution.table0201" /></h3></div>
                <tr><g:message code="dataresource.contribution.table0301" />.</div>
                <g:set var="dwc" value="${command.defaultDarwinCoreValues ? JSON.parse(command.defaultDarwinCoreValues) : [:]}"/>
                <!-- add fields for each of the important terms -->
                <g:each in="${DarwinCoreFields.getImportant()}" var="dwcf">
                    <div class="form-group">
                            <label for="${dwcf.name}"><g:message code="dataResource.DwC.${dwcf.name}.label" default="${dwcf.name}" /> <cl:helpText code="dataResource.${dwcf.name}"/></label>
                            <g:if test="${dwcf.values}">
                                <!-- pick list -->
                                <g:select name="${dwcf.name}" class="form-control" from="${dwcf.values}" value="${dwc[dwcf.name]}"/>
                            </g:if>
                            <g:else>
                                <!-- text field -->
                                <g:textField name="${dwcf.name}" class="form-control" value="${dwc[dwcf.name]}"/>
                            </g:else>
                    </div>
                </g:each>
                <!-- add fields for any other terms that have values -->
                <g:each var="dwcf" in="${dwc.entrySet()}">
                    <g:if test="${dwcf.key in DarwinCoreFields.getLessImportant().collect({it.name})}">
                        <div class="form-group">
                            <label for="${dwcf.key}"><g:message code="dataResource.DwC.${dwcf.key}.label" default="${dwcf.key}" /></label>
                            <g:textField name="${dwcf.key}" class="form-control" value="${dwcf.value}"/>
                        </div>
                    </g:if>
                </g:each>

                <!-- add a blank field so other DwC terms can be added -->
                <tr id="add-another"><g:message code="dataresource.contribution.table0401" />.</div>
                <div class="form-group">
                        <g:select name="otherKey" class="form-control" from="${DarwinCoreFields.getLessImportant().collect({it.name})}"/>
                        <button id="more-terms" type="button" class="btn btn-default"><g:message code="dataresource.contribution.table.button" /></button>
                </div>

            </g:if>

            </tbody>
        </table>
    %{--</div>--}%

        <div class="buttons">
            <span class="button"><input type="submit" name="_action_updateContribution" value="${message(code:"collection.button.update")}" class="save btn btn-success"></span>
            <span class="button"><input type="submit" name="_action_cancel" value="${message(code:"collection.button.cancel")}" class="cancel btn btn-default"></span>
        </div>
    </g:form>
</div>
<r:script>
    function instrument() {
        var availableTags = [
            "institutionCode",
            "collectionCode",
            "catalogNumber",
            "occurrenceID",
            "recordNumber"
        ];
        function split( val ) {
            return val.split( /,\s*/ );
        }
        function extractLast( term ) {
            return split( term ).pop();
        }
        $( "input#termsForUniqueKey:enabled" )
            // don't navigate away from the field on tab when selecting an item
                .bind( "keydown", function( event ) {
                    if ( event.keyCode === $.ui.keyCode.TAB &&
                            $( this ).data( "autocomplete" ).menu.active ) {
                        event.preventDefault();
                    }
                })
                .autocomplete({
                    minLength: 0,
                    source: function( request, response ) {
                        // delegate back to autocomplete, but extract the last term
                        response( $.ui.autocomplete.filter(
                                availableTags, extractLast( request.term ) ) );
                    },
                    focus: function() {
                        // prevent value inserted on focus
                        return false;
                    },
                    select: function( event, ui ) {
                        var terms = split( this.value );
                        // remove the current input
                        terms.pop();
                        // add the selected item
                        terms.push( ui.item.value );
                        // add placeholder to get the comma-and-space at the end
                        terms.push( "" );
                        this.value = terms.join( ", " );
                        return false;
                    }
                });
    }
    function changeProtocol() {
        var protocol = $('#protocolSelector').attr('value');
        // remove autocomplete binding
        $('input#termsForUniqueKey:enabled').autocomplete('destroy');
        $('input#termsForUniqueKey:enabled').unbind('keydown');
        // clear all
        $('div.labile').css('display','none');
        $('div.labile input,textArea').attr('disabled','true');
        // show the selected
        $('div#'+protocol).removeAttr('style');
        $('div#'+protocol+' input,textArea').removeAttr('disabled');
        // re-enable the autocomplete functionality
        instrument();
    }
    instrument();
    //$('[name="start_date"]').datepicker({dateFormat: 'yy-mm-dd'});
    /* this expands lists of urls into an array of text inputs */
    // create a delete element that removes the element before it and itself
    %{--var deleteImageUrl = "${resource(dir:'/images/ala',file:'delete.png')}";--}%
    var $deleteLink = $('<span class="delete btn btn-mini btn-danger"><i class="glyphicon glyphicon-remove glyphicon-white"></i> </span>')
            .click(function() {
                $(this).prev().remove();
                $(this).remove();
            });
    // handle all urls (including hidden ones)
    var urlInputs = $('input[name="url"]');
    $('input[name="url"]').addClass('input-xxlarge');
    $.each(urlInputs, function(i, obj) {
        var urls = $(obj).val().split(',');
        if (urls.length > 1) {
            // more than one url so create an input for each extra one
            $.each(urls,function(i,url) {
                if (i == 0) {
                    // existing input gets the first url
                    $(obj).val(url);
                }
                else {
                    // clone the existing field and inject the next value - adding a delete link
                    $(obj).clone()
                            .val(url.trim())
                            .css('width','93%')
                            .addClass('form-control')
                            .insertAfter($(obj).parent().children('input,span').last())
                            .after($deleteLink.clone(true));
                }
            });
        }
    });
    /* this injects 'add another' functionality to urls */
    $.each(urlInputs, function(i, obj) {
        $('<span class="pull-right btn btn-default">Add another</span>')
                .insertAfter($(obj).parent().children('input,span').last())
                .click(function() {
                    // clone the original input
                    var $clone = $(obj).clone();
                    $clone.val('');
                    $clone.insertBefore(this);
                    $clone.after($deleteLink.clone(true)); // add delete link
                });
    });
    /* this binds the code to add a new term to the list */
    $('#more-terms').click(function() {
        var term = $('#otherKey').val();
        // check that term doesn't already exist
        if ($('#'+term).length > 0) {
            alert(term + " is already present");
        }
        else {
            var newField = "<div class=\"form-group\"><label for='" + term +"'>" + term + "</label>" +
                    "<input type='text' class='form-control' id='" + term + "' name='" + term + "'/></div>";
            $('#add-another').parent().append(newField);
        }
    });
</r:script>
</body>
</html>