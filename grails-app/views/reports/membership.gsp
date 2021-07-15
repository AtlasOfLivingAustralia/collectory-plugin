<%@ page import="au.org.ala.collectory.ReportsController.ReportCommand" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <title><g:message code="reports.title" /></title>
    </head>
    <body>
        <div class="btn-toolbar">
            <ul class="btn-group">
                <li class="btn btn-default"><cl:homeLink/></li>
                <li class="btn btn-default"><span class="glyphicon glyphicon-th-list"></span><g:link class="list" action="list"> <g:message code="reports.li.reports"/></g:link></li>
            </ul>
        </div>
        <div class="body">
            <h1><g:message code="reports.membership.title01" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <div class="dialog">
              <table class="table table-striped table-bordered">
                <colgroup><col width="40%"/><col width="50%"/><col width="10%"/></colgroup>

                <tr class="reportGroupTitle"><td><g:message code="reports.membership.tr01" /> (${reports.partners.size()})</td>
                  <!-- put first member on same line as title -->
                  <td colspan="2">
                    <g:if test="${reports.partners.size() > 0}"><cl:showOrEdit entity="${reports.partners[0]}"/></g:if><g:else>None</g:else>
                  </td></tr>
                <g:each var="p" in="${reports.partners}" status="i">
                  <!-- skip first member -->
                  <g:if test="${i > 0}"><tr><td></td><td colspan="2"><cl:showOrEdit entity="${p}"/></td></tr></g:if>
                </g:each>

                <tr><td colspan="3"><hr></td></tr>
                <tr class="reportGroupTitle">
                  <td>CHAH Members (${reports.chahMembers.size()})</td>
                  <td colspan="2"><b><g:message code="reports.membership.tr02" /></b></td>
                </tr>
                <g:each var="p" in="${reports.chahMembers}" status="i">
                  <tr><td></td><td colspan="2"><cl:showOrEdit entity="${p}"/></td></tr>
                </g:each>

                <tr><td colspan="3"><hr></td></tr>
                <tr class="reportGroupTitle">
                  <td><g:message code="reports.chafc" /> (${reports.chafcMembers.size()})</td>
                  <td colspan="2"><b><g:message code="reports.membership.tr0302" /></b></td>
                </tr>
                <g:each var="p" in="${reports.chafcMembers}" status="i">
                  <tr><td></td><td><cl:showOrEdit entity="${p}"/></td>
                    <td>
                      <g:if test="${p.ENTITY_TYPE == 'Collection' && p.getPrimaryContact()?.contact?.email}">
                        <a href='#' onclick="return contactCurator('${p.getPrimaryContact()?.contact?.email}','${p.getPrimaryContact()?.contact?.firstName}','${p.uid}','${p.institution?.uid}','${p.name}')">Contact</a>
                      </g:if>
                    </td>
                  </tr>
                </g:each>

                <tr><td colspan="3"><hr></td></tr>
                <tr class="reportGroupTitle">
                  <td><g:message code="reports.chaec" /> (${reports.chaecMembers.size()})</td>
                  <td colspan="2"><b><g:message code="reports.membership.tr0402" /></b></td>
                </tr>
                <g:each var="p" in="${reports.chaecMembers}" status="i">
                  <tr><td></td><td><cl:showOrEdit entity="${p}"/></td>
                    <td>
                      <g:if test="${p.ENTITY_TYPE == 'Collection' && p.getPrimaryContact()?.contact?.email}">
                        <a href='#' onclick="return contactCurator('${p.getPrimaryContact()?.contact?.email}','${p.getPrimaryContact()?.contact?.firstName}','${p.uid}','${p.institution?.uid}','${p.name}')">Contact</a>
                      </g:if>
                    </td>
                  </tr>
                </g:each>

                <tr><td colspan="3"><hr></td></tr>
                <tr class="reportGroupTitle">
                  <td><g:message code="reports.chacm" /> (${reports.amrrnMembers.size()})</td>
                  <td colspan="2"><b><g:message code="reports.membership.tr0502" /></b></td>
                </tr>
                <g:each var="p" in="${reports.amrrnMembers}" status="i">
                  <tr><td></td><td><cl:showOrEdit entity="${p}"/></td>
                    <td>
                      <g:if test="${p.ENTITY_TYPE == 'Collection' && p.getPrimaryContact()?.contact?.email}">
                        <a href='#' onclick="return contactCurator('${p.getPrimaryContact()?.contact?.email}','${p.getPrimaryContact()?.contact?.firstName}','${p.uid}','${p.institution?.uid}','${p.name}')">Contact</a>
                      </g:if>
                    </td>
                  </tr>
                </g:each>

                <tr><td colspan="3"><hr></td></tr>
                <tr class="reportGroupTitle">
                  <td><g:message code="reports.camd" /> (${reports.camdMembers.size()})</td>
                  <td colspan="2"><b><g:message code="reports.membership.tr0602" /></b></td>
                </tr>
                <g:each var="p" in="${reports.camdMembers}" status="i">
                  <tr><td></td><td colspan="2"><cl:showOrEdit entity="${p}"/></td></tr>
                </g:each>

              </table>
            </div>
        </div>
    </body>
</html>
