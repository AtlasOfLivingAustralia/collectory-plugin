<%@ page import="au.org.ala.collectory.DataResource" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <title><g:message code="dataResource.base.label" default="Edit data resource metadata" /></title>
        <style type="text/css">
            li.free :hover { cursor: pointer; }
            li.free { cursor: pointer; }
        </style>
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
            <g:form method="post" name="baseForm" action="base">
                <g:hiddenField name="id" value="${command?.id}" />
                <g:hiddenField name="version" value="${command.version}" />
                <div class="dialog">
                    <table>
                        <tbody>

                        <!-- public short description -->
                        <tr class="prop">
                            <td valign="top" class="name">
                                <label for="pubShortDescription"><g:message code="providerGroup.pubShortDescription.label" default="Public Short Description" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'pubShortDescription', 'errors')}">
                                <g:textArea name="pubShortDescription" class="input-xlarge"  rows="${cl.textAreaHeight(text:command.pubShortDescription)}" value="${command.pubShortDescription}" />
                                <cl:helpText code="providerGroup.pubShortDescription"/>
                            </td>
                            <cl:helpTD/>
                        </tr>

                        <!-- public description -->
                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="pubDescription"><g:message code="providerGroup.pubDescription.label" default="Public Description" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'pubDescription', 'errors')}">
                                <g:textArea name="pubDescription" class="input-xxlarge"  rows="${cl.textAreaHeight(text:command.pubDescription)}" value="${command.pubDescription}" />
                                <cl:helpText code="providerGroup.pubDescription"/>
                              </td>
                              <cl:helpTD/>
                        </tr>

                        <!-- tech description -->
                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="techDescription"><g:message code="providerGroup.techDescription.label" default="Technical Description" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'techDescription', 'errors')}">
                                <g:textArea name="techDescription" class="input-xxlarge"  rows="${cl.textAreaHeight(text:command.techDescription)}" value="${command?.techDescription}" />
                                <cl:helpText code="providerGroup.techDescription"/>
                              </td>
                              <cl:helpTD/>
                        </tr>

                        <!-- purpose -->
                        <tr class="prop">
                            <td valign="top" class="name">
                                <label for="purpose"><g:message code="providerGroup.purpose.label" default="Purpose" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'purpose', 'errors')}">
                                <g:textArea name="purpose" class="input-xxlarge"  rows="${cl.textAreaHeight(text:command.purpose)}" value="${command?.purpose}" />
                                <cl:helpText code="providerGroup.purpose"/>
                            </td>
                            <cl:helpTD/>
                        </tr>

                        <!-- geographicDescription -->
                        <tr class="prop">
                            <td valign="top" class="name">
                                <label for="geographicDescription"><g:message code="providerGroup.geographicDescription.label" default="Geographic Description" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'geographicDescription', 'errors')}">
                                <g:textArea name="geographicDescription" class="input-xxlarge"  rows="${cl.textAreaHeight(text:command.geographicDescription)}" value="${command?.geographicDescription}" />
                                <cl:helpText code="providerGroup.geographicDescription"/>
                            </td>
                            <cl:helpTD/>
                        </tr>

                        <!-- bounding box -->
                        <tr class="prop">
                            <td valign="top" class="name">
                                <label for="boundingBox"><g:message code="providerGroup.boundingBox.label" default="Bounding Box" /></label>
                            </td>
                            <td valign="top" class="">
                                <label for="northBoundingCoordinate"><g:message code="providerGroup.northBoundingCoordinate.label" default="North (decimal degrees WGS84)" /></label>
                                <g:textField name="northBoundingCoordinate" class="input" value="${command?.northBoundingCoordinate}" />

                                <label for="southBoundingCoordinate"><g:message code="providerGroup.southBoundingCoordinate.label" default="South (decimal degrees WGS84)" /></label>
                                <g:textField name="southBoundingCoordinate" class="input" value="${command?.southBoundingCoordinate}" />

                                <label for="eastBoundingCoordinate"><g:message code="providerGroup.eastBoundingCoordinate.label" default="East (decimal degrees WGS84)" /></label>
                                <g:textField name="eastBoundingCoordinate" class="input" value="${command?.eastBoundingCoordinate}" />

                                <label for="westBoundingCoordinate"><g:message code="providerGroup.westBoundingCoordinate.label" default="West (decimal degrees WGS84)" /></label>
                                <g:textField name="westBoundingCoordinate" class="input" value="${command?.westBoundingCoordinate}" />
                            </td>
                        </tr>

                        <!-- temporal range -->
                        <tr class="prop">
                            <td valign="top" class="name">
                                <label for="temporalRange"><g:message code="providerGroup.temporal.label" default="Temporal range" /></label>
                            </td>
                            <td valign="top" class="">
                                <label for="beginDate"><g:message code="providerGroup.beginDate.label" default="Start date (yyyy-mm-dd)" /></label>
                                <g:textField name="beginDate" class="input" value="${command?.beginDate}" />

                                <label for="endDate"><g:message code="providerGroup.endDate.label" default="End date (yyyy-mm-dd)" /></label>
                                <g:textField name="endDate" class="input" value="${command?.endDate}" />
                            </td>
                        </tr>

                        <!-- qualityControlDescription -->
                        <tr class="prop">
                            <td valign="top" class="name">
                                <label for="qualityControlDescription"><g:message code="providerGroup.qualityControlDescription.label" default="Data Quality Description" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'qualityControlDescription', 'errors')}">
                                <g:textArea name="qualityControlDescription" class="input-xxlarge"  rows="${cl.textAreaHeight(text:command.qualityControlDescription)}" value="${command?.qualityControlDescription}" />
                                <cl:helpText code="providerGroup.qualityControlDescription"/>
                            </td>
                            <cl:helpTD/>
                        </tr>

                        <!-- methodStepDescription -->
                        <tr class="prop">
                            <td valign="top" class="name">
                                <label for="methodStepDescription"><g:message code="providerGroup.methodStepDescription.label" default="Methods" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'methodStepDescription', 'errors')}">
                                <g:textArea name="methodStepDescription" class="input-xxlarge"  rows="${cl.textAreaHeight(text:command.methodStepDescription)}" value="${command?.methodStepDescription}" />
                                <cl:helpText code="providerGroup.methodStepDescription"/>
                            </td>
                            <cl:helpTD/>
                        </tr>

                        <!-- focus -->
                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="focus"><g:message code="providerGroup.focus.label" default="Focus" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'focus', 'errors')}">
                                <g:textArea name="focus" class="input-xxlarge"  rows="${cl.textAreaHeight(text:command.focus)}" value="${command?.focus}" />
                                <cl:helpText code="providerGroup.focus"/>
                            </td>
                          <cl:helpTD/>
                        </tr>

                        <!-- data generalisations -->
                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="dataGeneralizations"><g:message code="dataResource.dataGeneralizations.label" default="Data Generalisations" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'dataGeneralizations', 'errors')}">
                                <g:textArea name="dataGeneralizations" class="input-xxlarge" rows="${cl.textAreaHeight(text:command.dataGeneralizations)}" value="${command?.dataGeneralizations}" />
                                <cl:helpText code="dataResource.dataGeneralizations"/>
                              </td>
                              <cl:helpTD/>
                        </tr>

                        <!-- information withheld -->
                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="informationWithheld"><g:message code="dataResource.informationWithheld.label" default="Information withheld" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'informationWithheld', 'errors')}">
                                <g:textArea name="informationWithheld" class="input-xxlarge" rows="${cl.textAreaHeight(text:command.informationWithheld)}" value="${command?.informationWithheld}" />
                                <cl:helpText code="dataResource.informationWithheld"/>
                              </td>
                              <cl:helpTD/>
                        </tr>

                        <!-- content types -->
                        <tr class="prop">
                            <td valign="top" class="name">
                              <g:message code="dataResource.contentTypes.label" default="Content types" />
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'contentTypes', 'errors')}">
                                <g:hiddenField name="contentTypes" value="${command.contentTypes}"/>
                                <p><g:message code="dataresource.description.des01" />.
                                <cl:helpText code="dataResource.informationWithheld"/>
                                </p>

                                <div class="row-fluid">
                                    <div class="source-box span6">
                                        <h4><g:message code="dataresource.description.title01" /></h4>
                                        <ul>
                                            <g:each var="ct" in="${DataResource.contentTypesList}">
                                                <li class='free'>${ct}</li>
                                            </g:each>
                                        </ul>
                                    </div>
                                    <div class="sink-box span6 well well-small">
                                        <h4><g:message code="dataresource.description.title02" /></h4>
                                        <ul>
                                            <li class="msg"><g:message code="dataresource.description.des02" />.</li>
                                        </ul>
                                    </div>
                                </div>
                              </td>
                              <cl:helpTD/>
                        </tr>

                      </tbody>
                    </table>
                </div>

                <div class="buttons">
                    <span class="button"><input type="submit" name="_action_updateDescription" value="${message(code:"collection.button.update")}" class="save btn"></span>
                    <span class="button"><input type="submit" name="_action_cancel" value="${message(code:"collection.button.cancel")}" class="cancel btn"></span>
                </div>
            </g:form>
        </div>

        <script type="text/javascript">
            $(function() {
                // bind click
                $('li.free').click(function() {
                    if ($(this).parent().parent().hasClass('source-box')) {
                        add(this);
                    }
                    else {
                        remove(this);
                    }
                });
                // move selected types to sink
                var selected = getSelectedList();
                $('li.free').each(function(index, element) {
                    $.each(selected, function(index, value) {
                        if ($(element).html() == value) {
                            add(element);
                        }
                    });
                })
            });
            function add(obj) {
                // clear instructions if present
                $('.sink-box li.msg').remove();
                $(obj).appendTo($('.sink-box ul'));
                addToList($(obj).html());
            }
            function remove(obj) {
                $(obj).appendTo($('.source-box ul'));
                removeFromList($(obj).html());
            }
            function getSelectedList() {
                var list = $.parseJSON($('input#contentTypes').val());
                return list == undefined ? [] : list
            }
            function addToList(ct) {
                var list = getSelectedList();
                if ($.inArray(ct, list) < 0) {
                    list.push(ct);
                }
                $('input#contentTypes').val(toJSON(list));
            }
            function removeFromList(ct) {
                var list = getSelectedList();
                var idx = $.inArray(ct, list);
                list.splice(idx,1);
                $('input#contentTypes').val(toJSON(list));
            }
            function toJSON(list) {
                if (typeof(JSON) == 'object' && JSON.stringify) {
                    return JSON.stringify(list);
                }
                else {
                    // assume list of string
                    if (list.length == 0) return "";
                    var str = "[";
                    $.each(list, function(index, value) {
                        str += '"' + value + '",';
                    })
                    str = (str.length > 1 ? str.substr(0,str.length-1) : str) + "]";
                    return str;
                }
            }
        </script>
    </body>
</html>
