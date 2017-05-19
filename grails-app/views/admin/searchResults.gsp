<html>
    <head>
        <title><g:message code="admin.sr.title" /></title>
	<meta name="layout" content="${grailsApplication.config.skin.layout}" />

    </head>

    <body>
      <div class="resultsPage">
        <div class="pageTitle">
          <h1><g:message code="admin.sr.title01" /></h1>
          <g:if test="${results.total == 0}">
            <span><g:message code="admin.sr.span01" />.</span>
          </g:if>
          <g:else>
            <p><g:message code="admin.sr.des01" /> <em>(<g:message code="admin.sr.des02" />)</em> <g:message code="admin.sr.des03" />.  <g:message code="admin.sr.des04" /> <em>(<g:message code="admin.sr.des05" />)</em> <g:message code="admin.sr.des01" />.</p>
          </g:else>
        </div>

        <g:if test="${results.collections}">
          <div class="resultsGroup">
            <h2><g:message code="admin.sr.title02" /></h2>
            <ul>
              <g:each var="c" in="${results.collections}">
                <li><span class="resultsName"><g:link controller="collection" action="show" id="${c.uid}">${c.name}</g:link></span>
                  <g:link controller="public" action="show" id="${c.uid}">(<g:message code="admin.sr.link.view" />)</g:link>
                  <g:link controller="collection" action="show" id="${c.uid}">(<g:message code="admin.sr.link.edit" />)</g:link>
                </li>
              </g:each>
            </ul>
          </div>
        </g:if>

        <g:if test="${results.institutions}">
          <div class="resultsGroup">
            <h2><g:message code="admin.sr.title03" /></h2>
            <ul>
              <g:each var="c" in="${results.institutions}">
                <li><span class="resultsName"><g:link controller="institution" action="show" id="${c.uid}">${c.name}</g:link></span>
                  <g:link controller="public" action="show" id="${c.uid}">(<g:message code="admin.sr.link.view" />)</g:link>
                  <g:link controller="institution" action="show" id="${c.uid}">(<g:message code="admin.sr.link.edit" />)</g:link>
                </li>
              </g:each>
            </ul>
          </div>
        </g:if>

        <g:if test="${results.dataProviders}">
          <div class="resultsGroup">
            <h2><g:message code="admin.sr.title04" /></h2>
            <ul>
              <g:each var="c" in="${results.dataProviders}">
                <li><span class="resultsName"><g:link controller="dataProvider" action="show" id="${c.uid}">${c.name}</g:link></span>
                  <g:link controller="public" action="show" id="${c.uid}">(<g:message code="admin.sr.link.view" />)</g:link>
                  <g:link controller="dataProvider" action="show" id="${c.uid}">(<g:message code="admin.sr.link.edit" />)</g:link>
                </li>
              </g:each>
            </ul>
          </div>
        </g:if>

        <g:if test="${results.dataResources}">
          <div class="resultsGroup">
            <h2><g:message code="admin.sr.title05" /></h2>
            <ul>
              <g:each var="c" in="${results.dataResources}">
                <li><span class="resultsName"><g:link controller="dataResource" action="show" id="${c.uid}">${c.name}</g:link></span>
                  <g:link controller="public" action="show" id="${c.uid}">(<g:message code="admin.sr.link.view" />)</g:link>
                  <g:link controller="dataResource" action="show" id="${c.uid}">(<g:message code="admin.sr.link.edit" />)</g:link>
                </li>
              </g:each>
            </ul>
          </div>
        </g:if>

      </div>

    </body>
</html>
