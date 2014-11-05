<%@ page import="grails.converters.JSON; au.org.ala.collectory.ProviderGroup; au.org.ala.collectory.Institution" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.ala.skin}" />
        <title><g:message code="collection.base.label" default="Edit taxonomy hints" /></title>
    </head>
    <body>
        <div class="nav">
            <h1><g:message code="shared.eth.title01" />: ${command.name}</h1>
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

                        <!-- taxonomy hints -->
                        <tr class="prop">
                            <td valign="top" class="name">
                              <label for="taxonomyHints"><g:message code="taxonomyHints.label" default="Taxonomy hints" /></label>
                            </td>
                            <td valign="top" class="value ${hasErrors(bean: command, field: 'taxonomyHints', 'errors')}">
                              <p><g:message code="shared.eth.des01" /> ${command.urlForm()}.</p>
                              <table class="shy"><colgroup><col width="50%"/><col width="50%"/></colgroup>
                                <tr><td><g:message code="shared.eth.th01" /></td><td><g:message code="shared.eth.th02" /></td></tr>
                                <g:set var="hints" value="${command.listTaxonomyHints()}"/>
                                <g:each var="hint" in="${hints}" status="i">
                                  <tr>
                                    <td valign="top"><g:textField name="rank_${i}" value="${hint.rank.encodeAsHTML()}" /></td>
                                    <td valign="top"><g:textField name="name_${i}" value="${hint.name.encodeAsHTML()}" /></td>
                                  </tr>
                                </g:each>
                                <g:set var="j" value="${hints.size()}"/>
                                <g:each var="i" in="${[j, j+1, j+2]}">
                                  <tr>
                                    <td valign="top"><g:textField name="rank_${i}" value="" /></td>
                                    <td valign="top"><g:textField name="name_${i}" value="" /></td>
                                  </tr>
                                </g:each>
                              </table>
                              <cl:helpText code="taxonomyHints"/>
                              </td>
                              <cl:helpTD/>
                        </tr>

                        </tbody>
                    </table>
                </div>

                <div class="buttons">
                    <span class="button"><input type="submit" name="_action_updateTaxonomyHints" value="${message(code:"shared.eth.button.update")}" class="save"></span>
                    <span class="button"><input type="submit" name="_action_cancel" value="${message(code:"shared.eth.button.cancel")}" class="cancel"></span>
                </div>
            </g:form>
        </div>
    </body>
</html>
