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
                <cl:isEditor>
                  <div>
                    <h2><g:message code="manage.list.addtools.title01" /></h2>

                    <p>
                        <g:set var="hasRoles" value="${userRoles.join(',')}"/>
                        <g:message code="manage.list.addtools.des01" args="[hasRoles]" />.
                    </p>

                    <div class="col-md-3" style="padding-left:0px;">

                          <div class="panel panel-default">
                              <div class="panel-heading">Collections/Institutions</div>
                              <div class="panel-body">

                                  <div class="homeCell">
                                      <g:link class="mainLink" controller="collection" action="list"><g:message code="manage.list.addtools.vac" /></g:link>
                                      <p class="mainText"><g:message code="manage.list.addtools.des02" />.</p>
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
                            </div>
                        </div>
                    </div>

                    <div class="col-md-3">
                        <div class="panel panel-default">
                            <div class="panel-heading">Data Licences</div>
                            <div class="panel-body">

                                <div class="homeCell">
                                    <g:link class="mainLink" controller="licence" action="list"><g:message code="admin.index.licence"  default="View all licences" /></g:link>
                                    <p class="mainText"><g:message code="admin.index.licence.desc" default="View all licences, and add new licences" />.</p>
                                </div>

                            </div>
                        </div>
                    </div>

                    <div class="col-md-3">
                        <div class="panel panel-default">
                            <div class="panel-heading"> Reports</div>
                            <div class="panel-body">

                                <div class="homeCell">
                                    <g:link class="mainLink" controller="reports" action="list"><g:message code="manage.list.addtools.vr" /></g:link>
                                    <p class="mainText"><g:message code="manage.list.addtools.des09" />.</p>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="col-md-3">
                        <div class="panel panel-default">
                            <div class="panel-heading">Contacts</div>
                            <div class="panel-body">

                                <div class="homeCell">
                                    <g:link class="mainLink" controller="contact" action="list"><g:message code="manage.list.addtools.mc" /></g:link>
                                    <p class="mainText"><g:message code="manage.list.addtools.des10" />.</p>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="col-md-3">
                        <div class="panel panel-default">
                            <div class="panel-heading">Institution/Collection code mapping</div>
                            <div class="panel-body">
                                <div class="homeCell">
                                    <g:link class="mainLink" controller="providerCode" action="list"><g:message code="manage.list.addtools.mpc" /></g:link>
                                    <p class="mainText"><g:message code="manage.list.addtools.des11" />.</p>
                                </div>

                                <div class="homeCell">
                                    <g:link class="mainLink" controller="providerMap" action="list"><g:message code="manage.list.addtools.mpm" /></g:link>
                                    <p class="mainText"><g:message code="manage.list.addtools.des12" />.</p>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="col-md-3">
                        <div class="panel panel-default">
                            <div class="panel-heading">Data Export</div>
                            <div class="panel-body">
                                <div class="homeCell">
                                    <g:link class="mainLink" controller="admin" action="export"><g:message code="manage.list.addtools.eadaj" /></g:link>
                                    <p class="mainText"><g:message code="manage.list.addtools.des13" />.</p>
                                </div>
                                <div class="homeCell">
                                    <g:link class="mainLink" controller="gbif" action="downloadCSV"><g:message code="manage.list.download.resources.csv" /></g:link>
                                    <p class="mainText"><g:message code="manage.list.download.resources.csv.desc" />.</p>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="col-md-3">

                        <div class="panel panel-default">
                            <div class="panel-heading">Audit</div>
                            <div class="panel-body">
                                <div class="homeCell">
                                    <g:link class="mainLink" controller="auditLogEvent" action="list" params="[max:1000]"><g:message code="manage.list.addtools.vae" /></g:link>
                                    <p class="mainText"><g:message code="manage.list.addtools.des14" /></p>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="col-md-3">
                        <div class="panel panel-default">
                            <div class="panel-heading">GBIF tools</div>
                            <div class="panel-body">
                              <div class="homeCell">
                                <g:link class="mainLink" controller="dataResource" action="gbifUpload"><g:message code="manage.list.addtools.uploadgbif" /></g:link>
                                <p class="mainText"r><g:message code="manage.list.addtools.des16" /></p>
                              </div>
                              <div class="homeCell">
                                  <g:link class="mainLink" controller="gbif" action="healthCheck"><g:message code="manage.list.addtools.gbif.healthcheck" /></g:link>
                                  <p class="mainText"r><g:message code="manage.list.addtools.gbif.healthcheck.desc" default="GBIF Healthcheck" /></p>
                              </div>
                              <div class="homeCell">
                                <g:link class="mainLink" controller="manage" action="loadExternalResources"><g:message code="manage.list.addtools.addexternal" /></g:link>
                                <p class="mainText"r><g:message code="manage.list.addtools.des15" /></p>
                              </div>
                            </div>
                        </div>
                    </div>
                  </div>
                </cl:isEditor>
                </div>
            </div>
        </div>
      </div>
    </body>
</html>