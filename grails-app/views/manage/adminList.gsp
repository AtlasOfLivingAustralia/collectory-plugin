<%@ page import="au.org.ala.collectory.Contact; au.org.ala.collectory.ProviderGroup; au.org.ala.collectory.Collection" %>
<html>
    <head>
        <title><g:message code="manage.show.title" /></title>
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <r:require modules="smoothness, collectory, jquery_ui_custom" />
    </head>
    
    <body>
      <div class="content">

        <div class="pull-right">
            <g:link class="mainLink btn btn-default" controller="public" action="map"><g:message code="manage.list.link01" /></g:link>
        </div>

        <h1><g:message code="manage.list.title01" /></h1>

        <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
        </g:if>

        <div class="row">

            <div class="col-md-12">

                <div id="adminTools" class="infoSection">
                <cl:ifGranted role="ROLE_COLLECTION_ADMIN">
                  <div>
                    <h2><g:message code="manage.list.addtools.title01" /></h2>
                    <p><g:message code="manage.list.addtools.des01" args="[ProviderGroup.ROLE_ADMIN]" />.</p>
                    <div class="homeCell">
                        <g:link class="mainLink" controller="collection" action="list"><g:message code="manage.list.addtools.vac" /></g:link>
                        <p class="mainText"><g:message code="manage.list.addtools.des02" />.</p>
                    </div>

                    <div class="homeCell well">
                        <span class="mainLink"><g:message code="manage.list.addtools.span01" /></span>

                        <p class="mainText"><g:message code="manage.list.addtools.des03" /></p>
                        <g:form controller="collection" action="searchList" method="get">
                            <div class="input-group">
                                <g:textField class="mainText" name="term" placeholder="Search for collection"/>
                                <g:submitButton class="btn btn-default" name="search" value="Search"/>
                            </div>
                        </g:form>
                    </div>

                    <div class="homeCell">
                        <g:link class="mainLink" controller="collection" action="create"><g:message code="manage.list.addtools.aac" /></g:link>
                        <p class="mainText"><g:message code="manage.list.addtools.des04" />.</p>
                    </div>

                    <div class="homeCell">
                        <g:link class="mainLink" controller="institution" action="list"><g:message code="manage.list.addtools.vai" /></g:link>
                        <p class="mainText"><g:message code="manage.list.addtools.des05" />.</p>
                    </div>

                    <div class="homeCell">
                        <g:link class="mainLink" controller="dataProvider" action="list"><g:message code="manage.list.addtools.vadp" /></g:link>
                        <p class="mainText"><g:message code="manage.list.addtools.des06" />.</p>
                    </div>

                    <div class="homeCell">
                        <g:link class="mainLink" controller="dataResource" action="list"><g:message code="manage.list.addtools.vadr" /></g:link>
                        <p class="mainText"><g:message code="manage.list.addtools.des07" />.</p>
                    </div>

                    <div class="homeCell">
                        <g:link class="mainLink" controller="dataHub" action="list"><g:message code="manage.list.addtools.vadh" /></g:link>
                        <p class="mainText"><g:message code="manage.list.addtools.des08" />.</p>
                    </div>

                    <div class="homeCell">
                        <g:link class="mainLink" controller="licence" action="list"><g:message code="admin.index.licence"  default="View all licences" /></g:link>
                        <p class="mainText"><g:message code="admin.index.licence.desc" default="View all licences, and add new licences" />.</p>
                    </div>

                    <div class="homeCell">
                        <g:link class="mainLink" controller="reports" action="list"><g:message code="manage.list.addtools.vr" /></g:link>
                        <p class="mainText"><g:message code="manage.list.addtools.des09" />.</p>
                    </div>

                    <div class="homeCell">
                        <g:link class="mainLink" controller="contact" action="list"><g:message code="manage.list.addtools.mc" /></g:link>
                        <p class="mainText"><g:message code="manage.list.addtools.des10" />.</p>
                    </div>

                    <div class="homeCell">
                        <g:link class="mainLink" controller="providerCode" action="list"><g:message code="manage.list.addtools.mpc" /></g:link>
                        <p class="mainText"><g:message code="manage.list.addtools.des11" />.</p>
                    </div>

                    <div class="homeCell">
                        <g:link class="mainLink" controller="providerMap" action="list"><g:message code="manage.list.addtools.mpm" /></g:link>
                        <p class="mainText"><g:message code="manage.list.addtools.des12" />.</p>
                    </div>

                    <div class="homeCell">
                        <g:link class="mainLink" controller="admin" action="export"><g:message code="manage.list.addtools.eadaj" /></g:link>
                        <p class="mainText"><g:message code="manage.list.addtools.des13" />.</p>
                    </div>

                    <div class="homeCell">
                        <g:link class="mainLink" controller="auditLogEvent" action="list" params="[max:1000]"><g:message code="manage.list.addtools.vae" /></g:link>
                        <p class="mainText"><g:message code="manage.list.addtools.des14" /></p>
                    </div>
                    <div class="homeCell">
                        <g:link class="mainLink" controller="manage" action="loadExternalResources"><g:message code="manage.list.addtools.addexternal" /></g:link>
                        <p class="mainText"r><g:message code="manage.list.addtools.des15" /></p>
                    </div>
                    <div class="homeCell">
                        <g:link class="mainLink" controller="dataResource" action="gbifUpload"><g:message code="manage.list.addtools.uploadgbif" /></g:link>
                        <p class="mainText"r><g:message code="manage.list.addtools.des16" /></p>
                    </div>
                      <div class="homeCell">
                          <g:link class="mainLink" controller="gbif" action="healthCheck"><g:message code="manage.list.addtools.gbif.healthcheck" /></g:link>
                          <p class="mainText"r><g:message code="manage.list.addtools.gbif.healthcheck.desc" default="GBIF Healthcheck" /></p>
                      </div>
                  </div>
                </cl:ifGranted>
                </div>

            </div>
        </div>

        <script type="text/javascript">

            function showSection(sectionToShow){
                $('.infoSection').hide();
                $('#'+sectionToShow).show();
            }

            function edit(uid) {
                document.location.href = "${grailsApplication.config.grails.serverURL}/manage/show/" + uid;
            }
            $('#instructions-link').click(function() {
                var height = $('#instructions').css('height');
                $('#instructions').animate({height: height == '0px' ? 440 : 0}, 'slow');
                return false;
            });

            var hasContact = ${user != null};

            var $name = $("#name");
            var $acronym = $("#acronym");
            var $role = $("#role");
            var $title = $("#title");
            var $firstName = $("#firstName");
            var $lastName = $("#lastName");
            var $phone = $("#phone");
            var $publish = $("#publish");
            var $contactFields = $role;
            if (!hasContact) {
                $contactFields = $contactFields.add($title).add($firstName).add($lastName).add($phone).add($publish);
            }
            var $allFields = $contactFields.add($name);
            var $tips = $(".validateTips");

            function updateTips( t ) {
                $tips
                    .text( t )
                    .addClass( "ui-state-highlight" );
                setTimeout(function() {
                    $tips.removeClass( "ui-state-highlight", 1500 );
                }, 500 );
            }

            function checkLength( o, n, min, max ) {
                if ( o.val().length > max || o.val().length < min ) {
                    o.addClass( "ui-state-error" );
                    updateTips( "Length of " + n + " must be between " +
                        min + " and " + max + "." );
                    return false;
                } else {
                    return true;
                }
            }

            function checkRegexp( o, regexp, n ) {
                if ( !( regexp.test( o.val() ) ) ) {
                    o.addClass( "ui-state-error" );
                    updateTips( n );
                    return false;
                } else {
                    return true;
                }
            }

            function checkUnique(o) {
                var isUnique = true;
                // make a synchronous call to check existence of the name
                $.ajax({
                    url: "${grailsApplication.config.grails.serverURL}/collection/nameExists?name=" + o.val(),
                    dataType: 'json',
                    async: false,
                    success: function(data) {
                        if (data.found == 'true') {
                            o.addClass( "ui-state-error" );
                            updateTips("A collection with this name already exists (" + data.uid + ")");
                            isUnique = false;
                        }
                    }
                });
                return isUnique;
            }

            %{--$('#dialog-form').dialog({--}%
                %{--autoOpen: false,--}%
                %{--width: 350,--}%
                %{--modal: true,--}%
                %{--buttons: {--}%
                    %{--"Create collection": function() {--}%
                        %{--var bValid = true;--}%
                        %{--$allFields.removeClass( "ui-state-error" );--}%

                        %{--bValid = bValid && checkLength( $name, "name", 3, 1024 );--}%

                        %{--if ($('#addAsContact').is(':checked')) {--}%
                            %{--bValid = bValid && checkLength( $role, "role", 3, 45 );--}%
                        %{--}--}%
                        %{----}%
                        %{--bValid = bValid && checkRegexp( $name, /^[a-z]([0-9a-z_ ])+$/i, "Name may consist of a-z, 0-9, underscores, begin with a letter." );--}%

                        %{--bValid = bValid && checkUnique($name);--}%

                        %{--if ( bValid ) {--}%
                            %{--var fieldValues = "";--}%
                            %{--if ($('#addAsContact').is(':checked')) {--}%
                                %{--fieldValues = "&addUserAsContact=true";--}%
                                %{--fieldValues += "&role=" + ($role.val() ? $role.val() : 'editor');--}%
                                %{--if (!hasContact) {--}%
                                    %{--if ($title.val()) fieldValues += "&title=" + $title.val();--}%
                                    %{--if ($firstName.val()) fieldValues += "&firstName=" + $firstName.val();--}%
                                    %{--if ($lastName.val()) fieldValues += "&lastName=" + $lastName.val();--}%
                                    %{--if ($phone.val()) fieldValues += "&phone=" + $phone.val();--}%
                                    %{--if ($('#publish').is(':checked')) fieldValues += "&publish=true";--}%
                                %{--}--}%
                            %{--}--}%
                            %{--//alert(fieldValues);--}%
                             %{--// redirect to create collection--}%
                            %{--document.location.href =--}%
                               %{--"${grailsApplication.config.grails.serverURL}/collection/create?name=" +--}%
                                       %{--$name.val() + fieldValues;--}%
                        %{--}--}%
                    %{--},--}%
                    %{--Cancel: function() {--}%
                        %{--$( this ).dialog( "close" );--}%
                    %{--}--}%
                %{--},--}%
                %{--close: function() {--}%
                    %{--$allFields.val( "" ).removeClass( "ui-state-error" );--}%
                %{--}--}%
            %{--});--}%
            $('#create').click(function() {
                $( "#dialog-form" ).dialog( "open" );
            });
            $('#addAsContact').change(function() {
                if ($('#addAsContact').is(':checked')) {
                    $contactFields.removeAttr('disabled');
                    $contactFields.css('opacity',1);
                    $contactFields.prev('label').css('opacity',1);
                }
                else {
                    $contactFields.attr('disabled', 'disabled');
                    $contactFields.css('opacity',0.5);
                    $contactFields.prev('label').css('opacity',0.5);
                }
            });
        </script>
    </body>
</html>