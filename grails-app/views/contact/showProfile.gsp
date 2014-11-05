<%@ page import="au.org.ala.collectory.ProviderGroup" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.ala.skin}" />
        <title><g:message code="profile.base.label" default="My profile" /></title>
    </head>
    <body>
        <div class="nav">
            <h1 style="display:inline;"><g:message code="contact.sp.title01" /> (${contact.email})</h1><span style="float:right;" class="menuButton"><cl:homeLink/></span>
        </div>
        <div id="baseForm" class="body">
            <g:if test="${message}">
            <div class="message">${message}</div>
            </g:if>
            <g:hasErrors bean="${contact}">
            <div class="errors">
                <g:renderErrors bean="${contact}" as="list" />
            </div>
            </g:hasErrors>
            <g:form method="post" action="updateProfile">
                <g:hiddenField name="id" value="${contact?.id}" />
                <g:hiddenField name="version" value="${contact.version}" />
                <div class="dialog custom-edit">
                  <h2 style="padding-top:10px;"><g:message code="contact.sp.title02" /></h2>

                  <div>
                    <table style="border: none;">
                      <tbody>
                          <tr class="prop">
                              <td>
                                <table class="shy">
                                  <tr>
                                    <td style="width:90px;" class="${hasErrors(bean: contact, field: 'title', 'errors')}">
                                        <g:select name="title" from="${contact.constraints.title.inList}" value="${contact?.title}" valueMessagePrefix="contact.title" noSelection="['': '']" />
                                    </td>
                                    <td class="${hasErrors(bean: contact, field: 'firstName', 'errors')}">
                                        <g:textField name="firstName" maxlength="45" value="${contact?.firstName}" />
                                    </td>
                                    <td class="${hasErrors(bean: contact, field: 'lastName', 'errors')}">
                                        <g:textField name="lastName" maxlength="45" value="${contact?.lastName}" />
                                    </td>
                                  </tr>
                                </table>
                              </td>
                          </tr>
                        
                          <tr class="prop">
                              <td>
                                <table class="shy">
                                  <tr>
                                    <td style="vertical-align:middle;color:gray;"><g:message code="contact.sp.title03" />:</td>
                                    <td class="${hasErrors(bean: contact, field: 'phone', 'errors')}">
                                        <g:textField name="phone" maxlength="45" value="${contact?.phone}" />
                                    </td>
                                    <td style="vertical-align:middle;color:gray;"><g:message code="contact.sp.title04" />:</td>
                                    <td class="${hasErrors(bean: contact, field: 'mobile', 'errors')}">
                                        <g:textField name="mobile" maxlength="45" value="${contact?.mobile}" />
                                    </td>
                                    <td style="vertical-align:middle;color:gray;"><g:message code="contact.sp.title05" />:</td>
                                    <td class="${hasErrors(bean: contact, field: 'fax', 'errors')}">
                                        <g:textField name="fax" maxlength="45" value="${contact?.fax}" />
                                    </td>
                                  </tr>
                                </table>
                              </td>
                          </tr>
                      </tbody>
                    </table>
                  </div>

                  <div class="repeats">
                    <g:each var="cr" in="${contactRels}">
                      <g:set var="uid" value="${cr.cf.entityUid}"/>
                      <g:set var="entity" value="${ProviderGroup.textFormOfEntityType(uid)}"/>
                      <h2><g:message code="contact.sp.title06" /> ${cr.entityName} (${uid})</h2>
                      <p>
                        <g:message code="contact.sp.des01" /> ${entity}?
                        <g:textField name="${uid}_role" maxlength="45" value="${cr.cf.role}" />
                      </p>
                      <p>
                        <g:message code="contact.sp.des02" /> ${entity}?
                        <g:checkBox name="${uid}_notify" value="${cr.cf.notify}" onchange="toggleFreq(this);"/>
                      </p>
                      <p id="${uid}_freq" style="display:${cr.cf.notify ? 'block':'none'}">
                        <g:message code="contact.sp.des03" />?
                        <g:radio name="${uid}_frequency" value="each"/> <g:message code="contact.sp.radio01" />
                        <g:radio name="${uid}_frequency" value="daily" checked="checked"/> <g:message code="contact.sp.radio02" />
                        <g:radio name="${uid}_frequency" value="weekly"/> <g:message code="contact.sp.radio03" />
                      </p>
                    </g:each>
                  </div>

                </div>

                <div class="buttons">
                    <span class="button"><input type="submit" name="_action_updateProfile" value="${message(code:"collection.button.update")}" class="save btn"></span>
                    <span class="button"><input type="submit" name="_action_cancelProfile" value="${message(code:"collection.button.cancel")}" class="cancel btn"></span>
                </div>
            </g:form>
        </div>
    <script type="text/javascript">
      function toggleFreq(obj) {
        var id = obj.id.substring(0, obj.id.indexOf('_')) + '_freq';
        var display = obj.checked ? 'block':'none';
        $('p#' + id).css('display',display);
      }
    </script>
    </body>
</html>
