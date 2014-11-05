<%@ page import="grails.converters.JSON; au.org.ala.collectory.resources.DarwinCoreFields; au.org.ala.collectory.DataHub; au.org.ala.collectory.ProviderGroup; au.org.ala.collectory.DataResource; au.org.ala.collectory.resources.Profile" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.ala.skin}" />
        <title><g:message code="dataResource.base.label" default="Edit data resource metadata" /></title>
        <link rel="stylesheet" href="${resource(dir:'css/smoothness',file:'jquery-ui-1.8.14.custom.css')}" type="text/css" media="screen"/>
        <r:require modules="jquery, jquery_ui_custom"/>
    </head>
    <body>
        <div class="nav">
            <h1><g:message code="collection.title.editing" />: ${command.name}</h1>
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
            <g:form method="post" name="contributionForm" action="contribution">
                <g:hiddenField name="id" value="${command?.id}" />
                <g:hiddenField name="uid" value="${command?.uid}" />
                <g:hiddenField name="version" value="${command.version}" />
                <div class="span12">
                    <table>
                        <tbody>

                        <!-- status -->
                        <tr class="prop">
                            <td valign="top" class="name span4">
                              <label for="status"><g:message code="dataResource.status.label" default="Status" /></label>
                            </td>
                            <td valign="top" class="value span8 ${hasErrors(bean: command, field: 'status', 'errors')}">
                                <g:select name="status"
                                        from="${DataResource.statusList}"
                                        value="${command.status}"/>
                                <cl:helpText code="dataResource.status"/>
                              </td>
                              <cl:helpTD/>
                        </tr>

                        <!-- provenance -->
                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="provenance"><g:message code="dataResource.provenance.label" default="Provenance" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'provenance', 'errors')}">
                                <g:select name="provenance"
                                        from="${DataResource.provenanceTypesList}"
                                        value="${command.provenance}"
                                        noSelection="${['':'none']}"/>
                                <cl:helpText code="dataResource.provenance"/>
                              </td>
                              <cl:helpTD/>
                        </tr>

                        <!-- last checked -->
                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="lastChecked"><g:message code="dataResource.lastChecked.label" default="Last checked" /></label>
                            </td>
                            <td valign="top" class="value">
                                <g:textField name="lastChecked" value="${command.lastChecked}"/>
                                <cl:helpText code="dataResource.lastChecked"/>
                            </td>
                          <cl:helpTD/>
                        </tr>

                        <!-- data currency -->
                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="dataCurrency"><g:message code="dataResource.dataCurrency.label" default="Data currency" /></label>
                            </td>
                            <td valign="top" class="value">
                                <g:textField name="dataCurrency" value="${command.dataCurrency}"/>
                                <cl:helpText code="dataResource.dataCurrency"/>
                            </td>
                          <cl:helpTD/>
                        </tr>

                        <!-- harvest frequency -->
                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="harvestFrequency"><g:message code="dataResource.harvestFrequency.label" default="Harvest frequency" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'harvestFrequency', 'errors')}">
                                <g:textField name="harvestFrequency" value="${command.harvestFrequency}"/>
                                <cl:helpText code="dataResource.harvestFrequency"/>
                            </td>
                          <cl:helpTD/>
                        </tr>

                        <!-- mob notes -->
                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="mobilisationNotes"><g:message code="dataResource.mobilisationNotes.label" default="Mobilisation notes" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'harvestingNotes', 'errors')}">
                                <g:textArea name="mobilisationNotes" cols="40" rows="${cl.textAreaHeight(text:command.mobilisationNotes)}" value="${command?.mobilisationNotes}" />
                                <p><g:message code="dataresource.contribution.des01" />.</p>
                                <cl:helpText code="dataResource.mobilisationNotes"/>
                              </td>
                              <cl:helpTD/>
                        </tr>

                        <!-- harvest notes -->
                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="harvestingNotes"><g:message code="dataResource.harvestingNotes.label" default="Harvesting notes" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'harvestingNotes', 'errors')}">
                                <g:textArea name="harvestingNotes" cols="40" rows="${cl.textAreaHeight(text:command.harvestingNotes)}" value="${command?.harvestingNotes}" />
                                <cl:helpText code="dataResource.harvestingNotes"/>
                              </td>
                              <cl:helpTD/>
                        </tr>

                        <!-- public archive -->
                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="publicArchiveAvailable"><g:message code="dataResource.publicArchiveAvailable.label" default="Public archive available" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'publicArchiveAvailable', 'errors')}">
                                <g:checkBox name="publicArchiveAvailable" value="${command?.publicArchiveAvailable}" />
                                <cl:helpText code="dataResource.publicArchiveAvailable"/>
                              </td>
                              <cl:helpTD/>
                        </tr>

                        <!-- harvest parameters -->
                        <tr><td colspan="3"><h3><g:message code="dataresource.contribution.table0101" /></h3></td></tr>
                        <cl:connectionParameters bean="command" connectionParameters="${command.connectionParameters}"/>

                        <g:if test="${command.resourceType == 'records'}">
                            <!-- darwin core defaults -->
                            <tr><td colspan="3"><h3><g:message code="dataresource.contribution.table0201" /></h3></td></tr>
                            <tr><td colspan="3"><g:message code="dataresource.contribution.table0301" />.</td></tr>
                            <g:set var="dwc" value="${command.defaultDarwinCoreValues ? JSON.parse(command.defaultDarwinCoreValues) : [:]}"/>
                            <!-- add fields for each of the important terms -->
                            <g:each in="${DarwinCoreFields.getImportant()}" var="dwcf">
                                <tr class="prop">
                                    <td valign="top" class="name">
                                      <label for="${dwcf.name}"><g:message code="dataResource.DwC.${dwcf.name}.label" default="${dwcf.name}" /></label>
                                    </td>
                                    <td valign="top" class="value">
                                        <g:if test="${dwcf.values}">
                                            <!-- pick list -->
                                            <g:select name="${dwcf.name}" from="${dwcf.values}" value="${dwc[dwcf.name]}"/>
                                        </g:if>
                                        <g:else>
                                            <!-- text field -->
                                            <g:textField name="${dwcf.name}" value="${dwc[dwcf.name]}"/>
                                        </g:else>
                                        <cl:helpText code="dataResource.${dwcf.name}"/>
                                    </td>
                                  <!--cl:helpTD/-->
                                </tr>
                            </g:each>
                            <!-- add fields for any other terms that have values -->
                            <g:each var="dwcf" in="${dwc.entrySet()}">
                                <g:if test="${dwcf.key in DarwinCoreFields.getLessImportant().collect({it.name})}">
                                    <tr class="prop">
                                        <td valign="top" class="name">
                                          <label for="${dwcf.key}"><g:message code="dataResource.DwC.${dwcf.key}.label" default="${dwcf.key}" /></label>
                                        </td>
                                        <td valign="top" class="value">
                                            <g:textField name="${dwcf.key}" value="${dwcf.value}"/>
                                        </td>
                                      <!--cl:helpTD/-->
                                    </tr>
                                </g:if>
                            </g:each>

                            <!-- add a blank field so other DwC terms can be added -->
                            <tr id="add-another"><td colspan="3"><g:message code="dataresource.contribution.table0401" />.</td></tr>
                            <tr class="prop">
                                <td valign="top" class="name">
                                    <g:select name="otherKey" from="${DarwinCoreFields.getLessImportant().collect({it.name})}"/>
                                </td>
                                <td valign="top" class="value">
                                    <button id="more-terms" type="button" class="btn"><g:message code="dataresource.contribution.table.button" /></button>
                                </td>
                            </tr>

                        </g:if>
                        
                      </tbody>
                    </table>
                </div>

                <div class="buttons">
                    <span class="button"><input type="submit" name="_action_updateContribution" value="${message(code:"collection.button.update")}" class="save btn"></span>
                    <span class="button"><input type="submit" name="_action_cancel" value="${message(code:"collection.button.cancel")}" class="cancel btn"></span>
                </div>
            </g:form>
        </div>
        <script type="text/javascript">
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
                $('tr.labile').css('display','none');
                $('tr.labile input,textArea').attr('disabled','true');
                // show the selected
                $('tr#'+protocol).removeAttr('style');
                $('tr#'+protocol+' input,textArea').removeAttr('disabled');
                // re-enable the autocomplete functionality
                instrument();
            }
            instrument();
            //$('[name="start_date"]').datepicker({dateFormat: 'yy-mm-dd'});

            /* this expands lists of urls into an array of text inputs */

            // create a delete element that removes the element before it and itself
            %{--var deleteImageUrl = "${resource(dir:'/images/ala',file:'delete.png')}";--}%
            var $deleteLink = $('<span class="delete btn btn-danger"><i class="icon icon-remove icon-white"></i> Remove</span>')
                    .click(function() {
                        $(this).prev().remove();
                        $(this).remove();
                    });

            // handle all urls (including hidden ones)
            var urlInputs = $('input[name="url"]');

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
                                .insertAfter($(obj).parent().children('input,span').last())
                                .after($deleteLink.clone(true));
                        }
                    });
                }
            });

            /* this injects 'add another' functionality to urls */
            $.each(urlInputs, function(i, obj) {
                $('<span class="clearfix link under btn">Add another</span>')
                        .insertAfter($(obj).parent().children('input,span').last())
                        .click(function() {
                            // clone the original input
                            var $clone = $(obj).clone();
                            $clone.val('').css('width','95%');
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
                    var newField = "<tr class='prop'><td valign='top' class='name'><label for='" + term +
                            "'>" + term + "</label></td>" +
                            "<td valign='top' class='value'><input type='text' id='" + term + "' name='" + term + "'/></td></tr>";
                    $('#add-another').parent().append(newField);
                }
            });
        </script>
    </body>
</html>
