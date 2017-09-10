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
                        <div class="form-group">
                            <label for="pubShortDescription"><g:message code="providerGroup.pubShortDescription.label" default="Public Short Description" /><cl:helpText code="providerGroup.pubShortDescription"/></label>
                            <g:textArea name="pubShortDescription" class="form-control"  rows="${cl.textAreaHeight(text:command.pubShortDescription)}" value="${command.pubShortDescription}" />
                        </div>

                        <!-- public description -->
                        <div class="form-group">
                            <label for="pubDescription"><g:message code="providerGroup.pubDescription.label" default="Public Description" /><cl:helpText code="providerGroup.pubDescription"/></label>
                            <g:textArea name="pubDescription" class="form-control"  rows="${cl.textAreaHeight(text:command.pubDescription)}" value="${command.pubDescription}" />
                        </div>

                        <!-- tech description -->
                        <div class="form-group">
                            <label for="techDescription"><g:message code="providerGroup.techDescription.label" default="Technical Description" /><cl:helpText code="providerGroup.techDescription"/></label>
                            <g:textArea name="techDescription" class="form-control"  rows="${cl.textAreaHeight(text:command.techDescription)}" value="${command?.techDescription}" />
                        </div>

                        <!-- purpose -->
                        <div class="form-group">
                            <label for="purpose"><g:message code="providerGroup.purpose.label" default="Purpose" /><cl:helpText code="providerGroup.purpose"/></label>
                            <g:textArea name="purpose" class="form-control"  rows="${cl.textAreaHeight(text:command.purpose)}" value="${command?.purpose}" />
                            <cl:helpTD/>
                        </div>

                        <!-- geographicDescription -->
                        <div class="form-group">
                            <label for="geographicDescription"><g:message code="providerGroup.geographicDescription.label" default="Geographic Description" /><cl:helpText code="providerGroup.geographicDescription"/></label>
                            <g:textArea name="geographicDescription" class="form-control"  rows="${cl.textAreaHeight(text:command.geographicDescription)}" value="${command?.geographicDescription}" />
                        </div>

                        <!-- bounding box -->
                        <div class="form-group">
                            <label><g:message code="providerGroup.boundingBox.label" default="Bounding Box" /></label>
                            <p class="help-block"><g:message code="providerGroup.boundingBox.help" default="Decimal degrees, WGS84"/></p>

                            <div class="input-group">
                                <label for="northBoundingCoordinate" class="input-group-addon"><g:message code="providerGroup.northBoundingCoordinate.label" default="North" /></label>
                                <g:field type="number" name="northBoundingCoordinate" class="form-control" min="-90.0" max="90.0" step="any" value="${command?.northBoundingCoordinate}" />

                                <label for="southBoundingCoordinate" class="input-group-addon"><g:message code="providerGroup.southBoundingCoordinate.label" default="South" /></label>
                                <g:field type="number" name="southBoundingCoordinate" class="form-control" min="-90.0" max="90.0" step="any" value="${command?.southBoundingCoordinate}" />

                                <label for="eastBoundingCoordinate" class="input-group-addon"><g:message code="providerGroup.eastBoundingCoordinate.label" default="East" /></label>
                                <g:field type="number" name="eastBoundingCoordinate" class="form-control" min="-180.0" max="180.0" step="any" value="${command?.eastBoundingCoordinate}" />

                                <label for="westBoundingCoordinate" class="input-group-addon"><g:message code="providerGroup.westBoundingCoordinate.label" default="West" /></label>
                                <g:field type="number" name="westBoundingCoordinate" class="form-control" min="-180.0" max="180.0" step="any" value="${command?.westBoundingCoordinate}" />
                            </div>

                        </div>

                        <!-- temporal range -->
                        <div class="form-group">
                            <label><g:message code="providerGroup.temporal.label" default="Temporal range" /></label>
                            <p class="help-block"><g:message code="providerGroup.temporal.help" default="Date format yyyy-mm-dd"/></p>
                            <div class="input-group">
                                <label for="beginDate" class="input-group-addon"><g:message code="providerGroup.beginDate.label" default="Start date" /></label>
                                <g:textField name="beginDate" class="form-control" pattern="\\d\\d\\d\\d-\\d\\d-\\d\\d" value="${command?.beginDate}" />
                                <label for="endDate" class="input-group-addon"><g:message code="providerGroup.endDate.label" default="End date" /></label>
                                <g:textField name="endDate" class="form-control" pattern="\\d\\d\\d\\d-\\d\\d-\\d\\d" value="${command?.endDate}" />
                            </div>
                            
                        </div>

                        <!-- qualityControlDescription -->
                        <div class="form-group">
                                <label for="qualityControlDescription"><g:message code="providerGroup.qualityControlDescription.label" default="Data Quality Description" /><cl:helpText code="providerGroup.qualityControlDescription"/></label>
                                <g:textArea name="qualityControlDescription" class="form-control"  rows="${cl.textAreaHeight(text:command.qualityControlDescription)}" value="${command?.qualityControlDescription}" />
                        </div>

                        <!-- methodStepDescription -->
                        <div class="form-group">
                                <label for="methodStepDescription"><g:message code="providerGroup.methodStepDescription.label" default="Methods" /><cl:helpText code="providerGroup.methodStepDescription"/></label>
                                <g:textArea name="methodStepDescription" class="form-control"  rows="${cl.textAreaHeight(text:command.methodStepDescription)}" value="${command?.methodStepDescription}" />
                        </div>

                        <!-- focus -->
                        <div class="form-group">
                              <label for="focus"><g:message code="providerGroup.focus.label" default="Focus" /><cl:helpText code="providerGroup.focus"/><cl:helpText code="dataResource.focus"/></label>
                            <g:textArea name="focus" class="form-control"  rows="${cl.textAreaHeight(text:command.focus)}" value="${command?.focus}" />
                        </div>

                        <!-- data generalisations -->
                        <div class="form-group">
                            <label for="dataGeneralizations"><g:message code="dataResource.dataGeneralizations.label" default="Data Generalisations" /><cl:helpText code="dataResource.dataGeneralizations"/></label>
                            <g:textArea name="dataGeneralizations" class="form-control" rows="${cl.textAreaHeight(text:command.dataGeneralizations)}" value="${command?.dataGeneralizations}" />
                        </div>

                        <!-- information withheld -->
                        <div class="form-group">
                              <label for="informationWithheld"><g:message code="dataResource.informationWithheld.label" default="Information withheld" /><cl:helpText code="dataResource.informationWithheld"/></label>
                            <g:textArea name="informationWithheld" class="form-control" rows="${cl.textAreaHeight(text:command.informationWithheld)}" value="${command?.informationWithheld}" />
                        </div>

                        <!-- content types -->
                        <div class="container">
                            
                              <g:message code="dataResource.contentTypes.label" default="Content types" />
                            
                            
                                <g:hiddenField name="contentTypes" value="${command.contentTypes}"/>
                                <p><g:message code="dataresource.description.des01" />.
                                <cl:helpText code="dataResource.informationWithheld"/>
                                </p>

                                <div class="row">
                                    <div class="source-box col-md-6">
                                        <h4><g:message code="dataresource.description.title01" /></h4>
                                        <ul>
                                            <g:each var="ct" in="${DataResource.contentTypesList}">
                                                <li class='free'>${ct}</li>
                                            </g:each>
                                        </ul>
                                    </div>
                                    <div class="sink-box col-md-6 well well-small">
                                        <h4><g:message code="dataresource.description.title02" /></h4>
                                        <ul>
                                            <li class="msg"><g:message code="dataresource.description.des02" />.</li>
                                        </ul>
                                    </div>
                                </div>
                        </div>

                      </tbody>
                    </table>
                </div>

                <div class="buttons">
                    <span class="button"><input type="submit" name="_action_updateDescription" value="${message(code:"collection.button.update")}" class="save btn btn-success"></span>
                    <span class="button"><input type="submit" name="_action_cancel" value="${message(code:"collection.button.cancel")}" class="cancel btn btn-default"></span>
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
