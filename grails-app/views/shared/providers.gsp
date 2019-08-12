<%@ page import="au.org.ala.collectory.DataResource;au.org.ala.collectory.Collection" %>
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
        <h1><g:message code="shared.title.editing" />: ${command.name}</h1>
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
                <g:textField name="providers" value="${command.listProviders().join(',')}" />
                <div class="dialog">
                    <div id="not-selected" class="container">
                        <h1><g:message code="shared.providers.notselected.label" />:</h1>
                        <ul>
                            <g:each in="${DataResource.findAllByResourceType('records',[sort:'name'])}" var="r">
                                <g:if test="${!(r.uid in command.listProviders())}">
                                    <li id="${r.uid}" class="draggable">${r.name}</li>
                                </g:if>
                            </g:each>
                        </ul>
                    </div>
                    <div id="selected" class="container">
                        <h1><g:message code="shared.providers.selected.title" />.</h1><p><g:message code="shared.providers.selected.des" /></p>
                        <ul>
                            <g:each in="${DataResource.findAllByResourceType('records',[sort:'name'])}" var="r">
                                <g:if test="${r.uid in command.listProviders()}">
                                    <li id="${r.uid}" class="draggable">${r.name}</li>
                                </g:if>
                            </g:each>
                        </ul>
                    </div>
                </div>
                
                <div style="clear:both;"></div>
                <div class="buttons">
                    <span class="button"><input type="submit" name="_action_updateProviders" value="${message(code:"shared.button.update")}" class="save"></span>
                    <span class="button"><input type="submit" name="_action_cancel" value="${message(code:"shared.button.cancel")}" class="cancel"></span>
                </div>
            </g:form>
        </div>
        <script type="text/javascript">
            var $to = $('#selected');
            var $from = $('#not-selected');
            $(function() {
        		$( ".draggable" ).draggable({ revert: "invalid", scroll: false, helper: 'clone' });
		        $to.droppable({
                    accept: function(me) {
                        return $(me).parent().parent().attr('id') == 'not-selected';
                    },
                    hoverClass: "ui-state-active",
                    drop: function( event, ui ) {
                        $('#selected p').remove();
                        placeItem(ui.draggable, $to);
                    }
                });
		        $from.droppable({
                    accept: function(me) {
                        return $(me).parent().parent().attr('id') == 'selected';
                    },
                    hoverClass: "ui-state-active",
                    drop: function( event, ui ) {
                        placeItem(ui.draggable, $from);
                    }
                });
                $('.draggable').click(function(){
                    if ($(this).parent().parent().attr('id') == 'not-selected') {
                        placeItem($(this), $to);
                    } else {
                        placeItem($(this), $from);
                    }
                });
	        });
            function placeItem( $item , $target) {
                $item.fadeOut(function() {
                    var $list = $( "ul", $target ).appendTo( $target );
                    $item.css({'top':'','left':''});
                    $item.appendTo( $list ).fadeIn(function() {
                        var providers = new Array();
                        $.each($('#selected li'), function(index, value) {
                            providers.push($(value).attr('id'));
                        });
                        $("input[name='providers']").attr('value',providers);
                    });
                });
            }
        </script>
    </body>
</html>
