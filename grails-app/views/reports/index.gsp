<%@ page import="au.org.ala.collectory.Contact; au.org.ala.collectory.DataResource; au.org.ala.collectory.DataProvider; au.org.ala.collectory.ProviderGroup; au.org.ala.collectory.Institution; au.org.ala.collectory.Collection" %>
<html>
    <head>
        <title><g:message code="reports.index.title" /></title>
	<meta name="layout" content="${grailsApplication.config.skin.layout}" />

    </head>
    <body>
    <div class="btn-toolbar">
        <ul class="btn-group">
            <li class="btn btn-default"><cl:homeLink/></li>
            <li class="btn btn-default"><span class="glyphicon glyphicon-th-list"></span><g:link class="list" action="list"> <g:message code="reports.li.reports"/></g:link></li>
        </ul>
    </div>
      <div id="welcome">
        <h1><g:message code="reports.index.title01" /></h1>
        <p><g:message code="reports.index.des01" />.</p>
      </div>

      <cl:isNotLoggedIn>
        <div class="homeCell">
          <h4 class="inline"><g:message code="reports.index.title02" /></h4>
            <span class="buttons" style="float: right;">
              <g:link controller="login">&nbsp;<g:message code="reports.index.link.login" />&nbsp;</g:link>
            </span>
          <p><g:message code="reports.index.des02" /></p>
        </div>
      </cl:isNotLoggedIn>

    <cl:ifGranted role="${ProviderGroup.ROLE_ADMIN}">
    <div class="dashboard">


    <div class="well pull-right">
      <h2><g:message code="reports.index.title03" /></h2>
      <div class="lead">
        <span class="total">${Collection.count()}</span> <g:message code="reports.index.total01" /><br/>
        <span class="total">${DataResource.count()}</span> <g:message code="reports.index.total02" /><br/>
        <span class="total">${Institution.count()}</span> <g:message code="reports.index.total03" /><br/>
        <span class="total">${DataProvider.count()}</span> <g:message code="reports.index.total04" />
      </div>
    </div>

     <h2><g:message code="reports.index.title04" /></h2>
    <div>
      <p class="pageLink"><g:link class="mainLink" controller="reports" action="data"><g:message code="reports.index.pagelink.data" /></g:link>
      <span class="linkText">- <g:message code="reports.index.linktext.data" /></span></p>
      <p class="pageLink"><g:link class="mainLink" controller="reports" action="changes"><g:message code="reports.index.pagelink.changes" /></g:link>
      <span class="linkText">- <g:message code="reports.index.linktext.changes" />.</span></p>
      <p class="pageLink"><g:link class="mainLink" controller="reports" action="membership"><g:message code="reports.index.pagelink.membership" /></g:link>
      <span class="linkText">- <g:message code="reports.index.linktext.membership" />.</span></p>
    </div>

      <div class="dashCell">
        <h2><g:message code="reports.index.title05" /></h2>
        <div>
          <p class="pageLink"><g:link class="mainLink" controller="reports" action="collections"><g:message code="reports.index.pagelink.collections" /></g:link>
          <span class="linkText">- <g:message code="reports.index.linktext.collections" /></span></p>
          <p class="pageLink"><g:link class="mainLink" controller="reports" action="codes"><g:message code="reports.index.pagelink.codes" /></g:link>
          <span class="linkText">- <g:message code="reports.index.linktext.codes" />.</span></p>
          <p class="pageLink"><g:link class="mainLink" controller="reports" action="attributions"><g:message code="reports.index.pagelink.attributions" /></g:link>
          <span class="linkText">- <g:message code="reports.index.linktext.attributions" />.</span></p>
          <p class="pageLink"><g:link class="mainLink" controller="reports" action="missingRecords"><g:message code="reports.index.pagelink.mr" /></g:link>
          <span class="linkText">- <g:message code="reports.index.linktext.mr" />.</span></p>
          <p class="pageLink"><g:link class="mainLink" controller="reports" action="classification"><g:message code="reports.index.pagelink.classification" /></g:link>
          <span class="linkText">- <g:message code="reports.index.linktext.classification" />.</span></p>
          <p class="pageLink"><g:link class="mainLink" controller="reports" action="collectionTypes"><g:message code="reports.index.pagelink.ct" /></g:link>
          <span class="linkText">- <g:message code="reports.index.linktext.ct" />.</span></p>
          <p class="pageLink"><g:link class="mainLink" controller="reports" action="taxonomicHints"><g:message code="reports.index.pagelink.th" /></g:link>
          <span class="linkText">- <g:message code="reports.index.linktext.th" />.</span></p>
        </div>
      </div>

      <div class="dashCell">
      <h2><g:message code="reports.index.title06" /></h2>
        <div style="clear:both;">
          <p class="pageLink"><g:link class="mainLink" controller="reports" action="institutions"><g:message code="reports.index.pagelink.institutions" /></g:link>
          <span class="linkText">- <g:message code="reports.index.linktext.institutions" /></span></p>
        </div>
      </div>

      <div class="dashCell">
        <h2><g:message code="reports.index.title07" /></h2>
        <div style="clear:both;">
          <p class="pageLink"><g:link class="mainLink" controller="reports" action="providers"><g:message code="reports.index.pagelink.providers" /></g:link>
          <span class="linkText">- <g:message code="reports.index.linktext.providers" /></span></p>
          <p class="pageLink"><g:link class="mainLink" controller="reports" action="rights"><g:message code="reports.index.pagelink.rights" /></g:link>
          <span class="linkText">- <g:message code="reports.index.linktext.rights" /></span></p>
          <p class="pageLink"><g:link class="mainLink" controller="reports" action="harvesters"><g:message code="reports.index.pagelink.harvesters" /></g:link>
          <span class="linkText">- <g:message code="reports.index.linktext.harvesters" /></span></p>
          <p class="pageLink"><g:link class="mainLink" controller="reports" action="resources"><g:message code="reports.index.pagelink.resources" /></g:link>
          <span class="linkText">- <g:message code="reports.index.linktext.resources" /></span></p>
          <p class="pageLink"><g:link class="mainLink" controller="reports" action="dataLinks"><g:message code="reports.index.pagelink.dl" /></g:link>
          <span class="linkText">- <g:message code="reports.index.linktext.dl" /></span></p>
        </div>
      </div>

      <div class="dashCell">
        <h2><g:message code="reports.index.title08" /></h2>
        <div style="clear:both;">
          <p class="pageLink"><g:link class="mainLink" controller="reports" action="duplicateContacts"><g:message code="reports.index.pagelink.dc" /></g:link></p>
          <p class="pageLink"><g:link class="mainLink" controller="reports" action="contactsForCouncilMembers"><g:message code="reports.index.pagelink.cfcm" /></g:link>
          <span class="linkText">- <g:message code="reports.index.linktext.cfcm" />.</span></p>
          <p class="pageLink"><g:link class="mainLink" controller="reports" action="contactsForCollections"><g:message code="reports.index.pagelink.cfc" /></g:link>
          <p class="pageLink"><g:link class="mainLink" controller="reports" action="contactsForInstitutions"><g:message code="reports.index.pagelink.cfi" /></g:link>
        </div>
      </div>

    </div>
    </cl:ifGranted>

    <cl:ifNotGranted role="${ProviderGroup.ROLE_ADMIN}">
      <p>Your must have the admin role (ROLE_COLLECTION_ADMIN) to view reports.</p>
    </cl:ifNotGranted>

    </body>
</html>