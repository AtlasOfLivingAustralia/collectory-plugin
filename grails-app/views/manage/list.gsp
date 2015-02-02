<%@ page import="au.org.ala.collectory.Contact; org.codehaus.groovy.grails.commons.ConfigurationHolder; au.org.ala.collectory.ProviderGroup; au.org.ala.collectory.Collection" %>
<html>
    <head>
        <title><g:message code="manage.show.title" /></title>
	    <meta name="layout" content="${grailsApplication.config.ala.skin}" />
        <r:require modules="smoothness, collectory, jquery_ui_custom" />
    </head>
    
    <body>
      <div class="content">

        <div class="pull-right">
            <g:link class="mainLink btn" controller="public" action="map"><g:message code="manage.list.link01" /></g:link>
        </div>

        <h1><g:message code="manage.list.title01" /></h1>

        <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
        </g:if>

        <div class="row-fluid">
            <div class="span3">
                <ul id="adminNavMenu" class="nav nav-list nav-stacked nav-tabs">
                    <li><a href="javascript:showSection('adminTools');"><i class="icon-chevron-right">&nbsp;</i> <g:message code="manage.list.li01" /></a></li>
                    <li><a href="javascript:showSection('yourMetadata');"><i class="icon-chevron-right">&nbsp;</i> <g:message code="manage.list.li02" /></a></li>
                    <li><a href="javascript:showSection('addCollection');"><i class="icon-chevron-right">&nbsp;</i> <g:message code="manage.list.li03" /></a></li>
                </ul>
            </div>

            <div class="span9">

                <div id="yourMetadata" class="infoSection hide">
                    <g:if test="${show == 'user'}">
                        <div>
                            <h2><g:message code="manage.list.title02" /></h2>
                            <p><g:message code="manage.list.des02" /> ${grailsApplication.config.security.cas.bypass ? 'bypassed' : 'active'}.</p>
                            <g:set var="username" value="${request.userPrincipal?.name}"/>
                            <g:if test="${username}">
                                <p><g:message code="manage.list.username.des01" /> ${username}.</p>
                                <p>User ${request.isUserInRole('ROLE_COLLECTION_ADMIN') ? 'has' : 'does not have'} ROLE_COLLECTION_ADMIN.</p>
                                <p>User ${request.isUserInRole('ROLE_COLLECTION_EDITOR') ? 'has' : 'does not have'} ROLE_COLLECTION_EDITOR.</p>
                            </g:if>
                            <g:else><p><g:message code="manage.list.des03" />.</p></g:else>
                            <p>
                                <g:set var="cookiename" value="${cookie(name: 'ALA-Auth')}"/>
                                <g:if test="${cookiename}"><g:message code="manage.list.cookiename01" /> ${cookiename}.</g:if>
                                <g:else><g:message code="manage.list.cookiename02" />.</g:else>
                            </p>
                        </div>
                    </g:if>

                    <h2><g:message code="manage.list.title03" /></h2>
                    <p><g:message code="manage.list.des04" />.</p>

                    <g:if test="${entities}">
                        <table class="shy" style="margin-left: 25px;">
                            <thead><tr><td style="text-align: center;width:40px;"><g:message code="manage.list.table01.cell0101" /></td><td style="text-align: center;width:40px;"><g:message code="manage.list.table01.cell0102" /></td><td></td></tr></thead>
                            <g:each in="${entities}" var="ent">
                                <tr>
                                    <td style="text-align: center;"><g:link controller="public" action="show" id="${ent.uid}">
                                        <i class="icon-eye-open"></i></g:link>
                                    </td>
                                    <td style="text-align: center;">
                                        <i class="icon-edit"></i>
                                    </td>
                                    <g:set var="name" value="${ent.uid[0..1] == 'in' ? ent.name + ' (Institution)' : ent.name}"/>
                                    <td style="padding-left: 5px;">${name}</td>
                                </tr>
                            </g:each>
                        </table>
                    </g:if>
                    <g:else>
                        <cl:ifGranted role="ROLE_COLLECTION_ADMIN">
                            <p><strong><em><g:message code="manage.list.des05" />.</em></strong></p>
                        </cl:ifGranted>
                        <cl:ifNotGranted role="ROLE_COLLECTION_ADMIN">
                            <p style="font-style: italic;margin: 10px;color: black;"><g:message code="manage.list.des06" />.</p>
                        </cl:ifNotGranted>

                        <cl:ifGranted role="ROLE_COLLECTION_EDITOR">
                            <p><g:message code="manage.list.des07" />.</p>
                        </cl:ifGranted>
                        <cl:ifNotGranted role="ROLE_COLLECTION_EDITOR">
                            <p><g:message code="manage.list.des08" />
                            <span class="link" onclick="return sendEmail('support(SPAM_MAIL@ALA.ORG.AU)ala.org.au')"><g:message code="manage.list.des09" /></span>
                            <g:message code="manage.list.des10" />.</p>
                        </cl:ifNotGranted>
                    </g:else>

                    <p><g:message code="manage.list.des11" />
                    <span id="instructions-link" class="link under"><g:message code="manage.list.des12" /></span>.</p>
                    <div id="instructions">
                        <div id="requirementsForEditing">
                            <h3><g:message code="manage.list.title04" /></h3>
                            <h4><g:message code="manage.list.title05" />?</h4>
                            <p><g:message code="manage.list.des13" />:</p>
                            <ol>
                                <li><g:message code="manage.list.li04" /></li>
                                <li><g:message code="manage.list.li05" /></li>
                                <li><g:message code="manage.list.li06" />.</li>
                            </ol>

                            <h4 class="ok"><g:message code="manage.list.title06" /></h4>
                            <p><g:message code="manage.list.des14" /> <em><cl:loggedInUsername/></em>.</p>

                            <cl:ifGranted role="ROLE_COLLECTION_EDITOR">
                                <h4 class="ok"><g:message code="manage.list.title07" /></h4>
                            </cl:ifGranted>
                            <cl:ifNotGranted role="ROLE_COLLECTION_EDITOR">
                                <h4 class="missing"><g:message code="manage.list.title08" />!</h4>
                                <p><g:message code="manage.list.des15" /> <span class="link" onclick="return sendEmail('support(SPAM_MAIL@ALA.ORG.AU)ala.org.au')"><g:message code="manage.list.des16" /></span>
                                <g:message code="manage.list.des17" /> ROLE_COLLECTION_EDITOR.</p>
                            </cl:ifNotGranted>

                            <cl:ifGranted role="ROLE_COLLECTION_EDITOR">
                                <g:if test="${!entities}">
                                    <h4 class="missing"><g:message code="manage.list.title09" />!</h4>
                                </g:if>
                                <g:else>
                                    <h4 class="ok"><g:message code="manage.list.title10" args="[entities.size()]" />.</h4>
                                </g:else>
                            </cl:ifGranted>
                            <cl:ifNotGranted role="ROLE_COLLECTION_EDITOR">
                                <h4><g:message code="manage.list.title11" />.</h4>
                            </cl:ifNotGranted>
                            <p><g:message code="manage.list.des18" />.</p>
                            <p><g:message code="manage.list.des19" />
                            <span class="link" onclick="return sendEmail('support(SPAM_MAIL@ALA.ORG.AU)ala.org.au')"><g:message code="manage.list.des16" /></span>
                            <g:message code="manage.list.des20" />.</p>

                            <h4><g:message code="manage.list.title12" />.</h4>
                            <p><g:message code="manage.list.des21" />
                            <span class="link" onclick="return sendEmail('support(SPAM_MAIL@ALA.ORG.AU)ala.org.au')"><g:message code="manage.list.des22" /></span>
                            <g:message code="manage.list.des23" />.</p>
                        </div>
                    </div>
                </div>

                <div id="addCollection" class="hide infoSection">
                    <cl:ifGranted role="ROLE_COLLECTION_EDITOR">

                        <h2><g:message code="manage.list.addcollection.title01" /></h2>
                        <p><g:message code="manage.list.addcollection.des01" />:</p>
                        <ul class="list">
                            <li><g:message code="manage.list.addcollection.li01" />.</li>
                            <li><g:message code="manage.list.addcollection.li02" />.</li>
                        </ul>

                        <g:link controller="dataResource" action="create" class="btn"><g:message code="manage.list.addcollection.link01" /></g:link>

                        <h2><g:message code="manage.list.addcollection.title02" /></h2>
                        <p><g:message code="manage.list.addcollection.des02" />:</p>
                        <ul class="list">
                            <li><g:message code="manage.list.addcollection.li03" />.</li>
                            <li><g:message code="manage.list.addcollection.li04" />.</li>
                            <li><g:message code="manage.list.addcollection.li05" />.</li>
                            <li><g:message code="manage.list.addcollection.li06" />.</li>
                            <li><g:message code="manage.list.addcollection.li07" />.</li>
                            <li><g:message code="manage.list.addcollection.li08" />.</li>
                        </ul>

                        <g:link controller="collection" action="create" class="btn"><g:message code="manage.list.addcollection.link02" /></g:link>
                    </cl:ifGranted>
                </div>

                <div id="adminTools" class="infoSection">
                <cl:ifGranted role="ROLE_COLLECTION_ADMIN">
                  <div>
                    <h2><g:message code="manage.list.addtools.title01" /></h2>
                    <p><g:message code="manage.list.addtools.des01" args="[ProviderGroup.ROLE_ADMIN]" />.</p>
                    <div class="homeCell">
                        <g:link class="mainLink" controller="collection" action="list"><g:message code="manage.list.addtools.vac" /></g:link>
                        <p class="mainText"><g:message code="manage.list.addtools.des02" />.</p>
                    </div>

                    <div class="homeCell">
                        <span class="mainLink"><g:message code="manage.list.addtools.span01" /></span>

                        <p class="mainText"><g:message code="manage.list.addtools.des03" /></p>
                        <g:form controller="collection" action="searchList" method="get">
                            <div class="input-append">
                                <g:textField class="mainText" name="term" placeholder="Search for collection"/>
                                <g:submitButton class="btn" name="search" value="Search"/>
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
                        <g:link class="mainLink" controller="manage" action="gbifLoadCountry"><g:message code="manage.list.addtools.addgbif" /></g:link>
                        <p class="mainText"r><g:message code="manage.list.addtools.des15" /></p>
                    </div>
                    <div class="homeCell">
                        <g:link class="mainLink" controller="dataResource" action="gbifUpload"><g:message code="manage.list.addtools.uploadgbif" /></g:link>
                        <p class="mainText"r><g:message code="manage.list.addtools.des16" /></p>
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
                document.location.href = "${ConfigurationHolder.config.grails.serverURL}/manage/show/" + uid;
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
                    url: "${ConfigurationHolder.config.grails.serverURL}/collection/nameExists?name=" + o.val(),
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
                               %{--"${ConfigurationHolder.config.grails.serverURL}/collection/create?name=" +--}%
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