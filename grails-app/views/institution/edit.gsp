<%@ page import="au.org.ala.collectory.ProviderGroup; au.org.ala.collectory.Contact; au.org.ala.collectory.Institution" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <g:set var="entityName" value="${message(code: 'institution.label', default: 'Institution')}" />
        <title><g:message code="default.edit.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><cl:homeLink/></span>
            <span class="menuButton"><g:link class="list" action="list"><g:message code="default.list.label" args="[entityName]" /></g:link></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.edit.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:hasErrors bean="${providerGroupInstance}">
            <div class="errors">
                <g:renderErrors bean="${providerGroupInstance}" as="list" />
            </div>
            </g:hasErrors>
            <g:uploadForm method="post" url="[action:'editInstitution']">
                <g:hiddenField name="id" value="${providerGroupInstance?.id}" />
                <g:hiddenField name="version" value="${providerGroupInstance?.version}" />
                <!-- event field is used by submit buttons to pass the web flow event (rather than using the text of the button as the event name) -->
                <g:hiddenField id="event" name="_eventId" value="done" />
                <g:hiddenField id="logoFile" name="_logoFile" value="${providerGroupInstance?.logoRef?.file}" />
                <g:hiddenField id="imageFile" name="_imageFile" value="${providerGroupInstance?.imageRef?.file}" />
                <div class="dialog">
                    <table>
                        <tbody>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="guid"><g:message code="providerGroup.guid.label" default="Guid" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: providerGroupInstance, field: 'guid', 'errors')}">
                                    <g:textField name="guid" maxlength="45" value="${providerGroupInstance?.guid}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="name"><g:message code="providerGroup.name.label" default="Name" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: providerGroupInstance, field: 'name', 'errors')}">
                                    <g:textField name="name" maxlength="128" value="${providerGroupInstance?.name}" />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="acronym"><g:message code="providerGroup.acronym.label" default="Acronym" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: providerGroupInstance, field: 'acronym', 'errors')}">
                                    <g:textField name="acronym" maxlength="45" value="${providerGroupInstance?.acronym}" />
                                </td>
                            </tr>
                        
<!-- ALA partner -->        <g:ifAllGranted role="${ProviderGroup.ROLE_ADMIN}">
                              <tr class="prop">
                                  <td valign="top" class="name">
                                    <label for="isALAPartner"><g:message code="providerGroup.isALAPartner.label" default="Is ALA Partner" /></label>
                                  </td>
                                  <td valign="top" class="value ${hasErrors(bean: providerGroupInstance, field: 'isALAPartner', 'errors')}">
                                      <g:checkBox name="isALAPartner" value="${providerGroupInstance?.isALAPartner}" />
                                  </td>
                              </tr>
                            </g:ifAllGranted>

<!-- pub description -->    <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="pubDescription"><g:message code="providerGroup.pubDescription.label" default="Public Description" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: command, field: 'pubDescription', 'errors')}">
                                    <g:textArea name="pubDescription" cols="40" rows="5" value="${providerGroupInstance?.pubDescription}" />
                                    <cl:helpText code="institution.pubDescription"/>
                                  </td>
                                  <cl:helpTD/>
                            </tr>

<!-- tech description -->   <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="techDescription"><g:message code="providerGroup.techDescription.label" default="Technical Description" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: command, field: 'techDescription', 'errors')}">
                                    <g:textArea name="techDescription" cols="40" rows="5" value="${providerGroupInstance?.techDescription}" />
                                    <cl:helpText code="institution.techDescription"/>
                                  </td>
                                  <cl:helpTD/>
                            </tr>

<!-- focus -->              <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="focus"><g:message code="providerGroup.focus.label" default="Contribution to ALA" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: command, field: 'focus', 'errors')}">
                                    <g:textArea name="focus" cols="40" rows="5" value="${providerGroupInstance?.focus}" />
                                    <cl:helpText code="institution.focus"/>
                                </td>
                              <cl:helpTD/>
                            </tr>

<!-- network membership -->
                            <tr class="prop">
                              <td valign="top" class="name">
                                <label for="networkMembership"><g:message code="providerGroup.networkMembership.label" default="Belongs to" /></label>
                              </td>
                              <td valign="top" class="checkbox ${hasErrors(bean: providerGroupInstance, field: 'networkMembership', 'errors')}">
                                <cl:checkboxSelect name="networkMembership" from="${ProviderGroup.networkTypes}" value="${providerGroupInstance?.networkMembership}" multiple="yes" valueMessagePrefix="providerGroup.networkMembership" noSelection="['': '']" />
                                <cl:helpText code="providerGroup.networkMembership"/>
                              </td>
                              <td><img class="helpButton" alt="help" src="${resource(dir:'images/skin', file:'help.gif')}" onclick="toggleHelp(this);"/></td>
                            </tr>


<!-- address -->            <tr class="prop">
                                <td valign="top" class="name">
                                  <g:message code="providerGroup.address.label" default="Address" />
                                </td>
                                <td valign="top">
                                  <table class="shy">
                                    <tr class='prop'>
                                      <td valign="top" class="name">
                                        <label for="address.street"><g:message code="providerGroup.address.street.label" default="Street" /></label>
                                      </td>
                                      <td valign="top" class="value ${hasErrors(bean: providerGroupInstance, field: 'address.street', 'errors')}">
                                          <g:textField name="address.street" maxlength="128" value="${providerGroupInstance?.address?.street}" />
                                      </td>
                                    </tr>
                                    <tr class='prop'>
                                      <td valign="top" class="name">
                                        <label for="address.postBox"><g:message code="providerGroup.address.postBox.label" default="Post box" /></label>
                                      </td>
                                      <td valign="top" class="value ${hasErrors(bean: providerGroupInstance, field: 'address.postBox', 'errors')}">
                                          <g:textField name="address.postBox" maxlength="128" value="${providerGroupInstance?.address?.postBox}" />
                                      </td>
                                    </tr>
                                    <tr class='prop'>
                                      <td valign="top" class="name">
                                        <label for="address.city"><g:message code="providerGroup.address.city.label" default="City" /></label>
                                      </td>
                                      <td valign="top" class="value ${hasErrors(bean: providerGroupInstance, field: 'address.city', 'errors')}">
                                          <g:textField name="address.city" maxlength="128" value="${providerGroupInstance?.address?.city}" />
                                      </td>
                                    </tr>
                                    <tr class='prop'>
                                      <td valign="top" class="name">
                                        <label for="address.state"><g:message code="providerGroup.address.state.label" default="State or territory" /></label>
                                      </td>
                                      <td valign="top" class="value ${hasErrors(bean: providerGroupInstance, field: 'address.state', 'errors')}">
                                          <g:textField name="address.state" maxlength="128" value="${providerGroupInstance?.address?.state}" />
                                      </td>
                                    </tr>
                                    <tr class='prop'>
                                      <td valign="top" class="name">
                                        <label for="address.postcode"><g:message code="providerGroup.address.postcode.label" default="Postcode" /></label>
                                      </td>
                                      <td valign="top" class="value ${hasErrors(bean: providerGroupInstance, field: 'address.street', 'errors')}">
                                          <g:textField name="address.postcode" maxlength="128" value="${providerGroupInstance?.address?.postcode}" />
                                      </td>
                                    </tr>
                                    <tr class='prop'>
                                      <td valign="top" class="name">
                                        <label for="address.country"><g:message code="providerGroup.address.country.label" default="Country" /></label>
                                      </td>
                                      <td valign="top" class="value ${hasErrors(bean: providerGroupInstance, field: 'address.country', 'errors')}">
                                          <g:textField name="address.country" maxlength="128" value="${providerGroupInstance?.address?.country}" />
                                      </td>
                                    </tr>
                                  </table>
                                    
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="state"><g:message code="providerGroup.state.label" default="State" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: providerGroupInstance, field: 'state', 'errors')}">
                                    <g:textField name="state" maxlength="45" value="${providerGroupInstance?.state}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="websiteUrl"><g:message code="providerGroup.websiteUrl.label" default="Website Url" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: providerGroupInstance, field: 'websiteUrl', 'errors')}">
                                    <g:textField name="websiteUrl" maxLength="256" value="${providerGroupInstance?.websiteUrl}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="logoRef"><g:message code="providerGroup.logoRef.label" default="Logo Ref" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: providerGroupInstance, field: 'logoRef', 'errors')}">
                                    <g:render template="/shared/attributableLogo" model="[command: providerGroupInstance, directory: 'institution', action: 'editInstitution']"/>
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="imageRef"><g:message code="providerGroup.imageRef.label" default="Image Ref" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: providerGroupInstance, field: 'imageRef', 'errors')}">
                                  <g:render template="/shared/attributableImage" model="[command: providerGroupInstance, directory: 'institution', action: 'editInstitution']"/>
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="latitude"><g:message code="providerGroup.latitude.label" default="Latitude" />
                                    <br/><span class=hint>(decimal degrees)</span>
                                  </label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: providerGroupInstance, field: 'latitude', 'errors')}">
                                  <g:textField name="latitude" value="${cl.showDecimal(value:fieldValue(bean: providerGroupInstance, field: 'latitude'))}" />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="longitude">
                                    <g:message code="providerGroup.longitude.label" default="Longitude" />
                                    <br/><span class=hint>(decimal degrees)</span>
                                  </label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: providerGroupInstance, field: 'longitude', 'errors')}">
                                  <g:textField name="longitude" value="${cl.showDecimal(value:fieldValue(bean: providerGroupInstance, field: 'longitude'))}" />
                                </td>
                            </tr>

                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="email"><g:message code="providerGroup.email.label" default="Email" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: providerGroupInstance, field: 'email', 'errors')}">
                                    <g:textField name="email" value="${providerGroupInstance?.email}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="phone"><g:message code="providerGroup.phone.label" default="Phone" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: providerGroupInstance, field: 'phone', 'errors')}">
                                    <g:textField name="phone" maxlength="45" value="${providerGroupInstance?.phone}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="notes"><g:message code="providerGroup.notes.label" default="Notes" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: providerGroupInstance, field: 'notes', 'errors')}">
                                    <g:textArea name="notes" cols="40" rows="5" value="${providerGroupInstance?.notes}" />
                                </td>
                            </tr>
                        
                            <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="institutionType"><g:message code="providerGroup.institutionType.label" default="Institution Type" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: providerGroupInstance, field: 'institutionType', 'errors')}">
                                    <g:textField name="institutionType" maxlength="45" value="${providerGroupInstance?.institutionType}" />
                                </td>
                            </tr>
                        
<!-- contacts -->         <tr><td colspan="2">
                          <p class="wizardHeading"><g:message code="institution.edit.des01" /></p>

                          <span style="margin-left:50px;"><g:message code="providerGroup.existingContacts.label" default="Current contacts for this institution" /></span>
                          <table style="width:80%;border:1px solid #CCC;margin-left:auto;margin-right:auto;margin-bottom:20px;">
                              <tbody>

      <!-- current -->          <g:each in="${providerGroupInstance.getContacts()}" var="i" status="row">
                                  <tr class="prop">
                                    <td valign="top" class="name">${i?.contact?.buildName()}</td>
                                    <td valign="top" class="name">${i?.role}</td>
                                    <td valign="top" class="name">
                                      <g:if test="${i?.administrator}">
                                        <g:message code="institution.edit.des02" />
                                      </g:if>
                                    </td>
                                    <td style="width:130px;">
                                      <span class="bodyButton"><g:link id="${i?.contact?.id}" class="removeAction" action="editInstitution" event="remove"
                                              onclick="return confirm('Remove ${i?.contact?.buildName()} as a contact for this institution?');">Remove</g:link></span>
                                    </td>
                                  </tr>
                                </g:each>
                              </tbody>
                          </table>

      <!--add existing--> <span style="margin-left:50px;"><g:message code="providerGroup.addAContact.label" default="Add a known contact to this institution" /></span>

                          <table style="width:80%;border:1px solid #CCC;margin-left:auto;margin-right:auto;margin-bottom:20px;padding-left:20px;">

                            <tr class="prop">
                              <td valign="top" class="name"><g:message code="institution.edit.des03" /></td>
                              <td valign="top" class="value">
                                <g:select name="addContact" from="${Contact.list()}" optionKey="id" noSelection="${['null':'Select one to add']}" />
                              </td>
                            </tr>
      <!-- role -->         <tr class="prop">
                              <td valign="top" class="name">
                                <label for="role"><g:message code="institution.edit.des04" /><br/><span style="color:#777"><g:message code="institution.edit.des05" />, <br/><g:message code="institution.edit.des06" /></span></label>
                              </td>
                              <td valign="top" class="value">
                                  <g:textField name="role" maxlength="45"/>
                              </td>
                            </tr>

      <!-- is admin -->     <tr class="prop">
                            <td class="checkbox">
                              <label for="isAdmin"><g:message code="contactFor.administrator.label" default="Administrator" /></label>
                            </td>
                            <td class="checkbox">
                              <label>
                                <g:checkBox name="isAdmin" value="true" />
                                <span class="hint"><g:message code="institution.edit.des07" /></span>
                              </label>
                            </td>
                          </tr>

      <!-- add button -->   <tr>
                              <td>
                                <input type="submit" style="color:#222;" onclick="return anySelected('addContact','You must select a contact to add.');" class="addAction" value="Add contact"/>
                              </td>
                            </tr>

                            </table>

      <!-- add new -->      <span style="margin-left:50px;"><g:message code="institution.edit.des08" /></span>
                            <table style="width:80%;border:1px solid #CCC;margin-left:auto;margin-right:auto;margin-bottom:20px;padding-left:20px;">

      <!-- title-->           <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="title"><g:message code="contact.title.label" default="Title" /></label><br/>
                                  <span style="color:#777">e.g. Dr</span>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: command, field: 'title', 'errors')}">
                                    <g:textField name="title" maxlength="10"/>
                                </td>
                              </tr>

      <!-- firstName-->       <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="firstName"><g:message code="contact.firstName.label" default="First name" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: command, field: 'firstName', 'errors')}">
                                    <g:textField name="firstName" maxlength="255"/>
                                </td>
                              </tr>

      <!-- lastName-->        <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="lastName"><g:message code="contact.lastName.label" default="Last name" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: command, field: 'lastName', 'errors')}">
                                    <g:textField name="lastName" maxlength="255"/>
                                </td>
                              </tr>

      <!-- phone-->           <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="phone"><g:message code="contact.phone.label" default="Phone" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: command, field: 'phone', 'errors')}">
                                  <!-- NOTE change in field name as it clashes with the same field in the main model -->
                                  <g:textField name="c_phone" maxlength="45"/>
                                </td>
                              </tr>

      <!-- mobile-->          <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="mobile"><g:message code="contact.mobile.label" default="Mobile" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: command, field: 'mobile', 'errors')}">
                                    <g:textField name="mobile" maxlength="45"/>
                                </td>
                              </tr>

      <!-- email-->           <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="email"><g:message code="contact.email.label" default="Email" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: command, field: 'email', 'errors')}">
                                  <!-- NOTE change in field name as it clashes with the same field in the main model -->
                                  <g:textField name="c_email" maxlength="45"/>
                                </td>
                              </tr>

      <!-- fax-->             <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="fax"><g:message code="contact.fax.label" default="Fax" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: command, field: 'fax', 'errors')}">
                                    <g:textField name="fax" maxlength="45"/>
                                </td>
                              </tr>

      <!-- notes-->           <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="notes"><g:message code="contact.notes.label" default="Notes" /></label>
                                </td>
                                <td valign="top" class="value ${hasErrors(bean: command, field: 'notes', 'errors')}">
                                  <!-- NOTE change in field name as it clashes with the same field in the main model -->
                                    <g:textArea name="c_notes" cols="40" rows="5" maxlength="1024"/>
                                </td>
                              </tr>

      <!-- publish-->         <tr class="prop">
                                <td class="checkbox">
                                  <label for="publish"><g:message code="contact.publish.label" default="Make public?" /></label>
                                </td>
                                <td class="checkbox">
                                    <label>
                                      <g:checkBox name="publish" value="true"/>
                                      <span class="hint"><g:message code="institution.edit.des09" /></span>
                                    </label>
                                </td>
                              </tr>

        <!-- role -->         <tr class="prop">
                                <td valign="top" class="name">
                                  <label for="role"><g:message code="contactFor.role.label" default="Role" /><br/><span style="color:#777;">e.g. Manager, <br/>Curator, Editor</span></label>
                                </td>
                                <td valign="top" class="value">
                                    <g:textField name="role2" maxlength="45"/>
                                </td>
                              </tr>

        <!-- is admin -->     <tr class="prop">
                                <td class="checkbox">
                                  <label for="isAdmin2"><g:message code="contactFor.administrator.label" default="Administrator" /></label>
                                </td>
                                <td class="checkbox">
                                  <label>
                                    <g:checkBox name="isAdmin2" value="true" />
                                    <span class="hint"><g:message code="institution.edit.des10" /></span>
                                  </label>
                                </td>
                              </tr>
                              <tr><td>
                                <input type="submit" style="color:#222" onclick="return document.getElementById('event').value = 'create'" class="addAction" value="Add contact"/>
                              </td></tr>
                          </table>
                        </td></tr>

                        </tbody>
                    </table>
                </div>
                <cl:navButtons exclude='back next' />
            </g:uploadForm>
        </div>
    </body>
</html>
