<%@ page import="au.org.ala.collectory.Collection" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="${grailsApplication.config.skin.layout}" />
        <g:set var="entityName" value="${message(code: 'collection.label', default: 'Collection')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="btn-toolbar">
            <ul class="btn-group">
                <li class="btn btn-default"><cl:homeLink/></li>
                <li class="btn btn-default"><span class="glyphicon glyphicon-list"></span><g:link class="list" action="list"> <g:message code="default.list.label" args="[entityName]"/></g:link></li>
                <li class="btn btn-default"><span class="glyphicon glyphicon-plus"></span><g:link class="create" action="create"> <g:message code="default.new.label" args="[entityName]"/></g:link></li>
            </ul>
        </div>
        <div class="body">
            <h1 class="inline"><g:message code="default.list.label" args="[entityName]" />
            <div id="index">
            </div></h1>
            <g:if test="${flash.message}"><div class="message">${flash.message}</div></g:if>
            <g:if test="${message}"><div class="message">${message}</div></g:if>
            <div class="list">
                <table class="table table-striped table-bordered">
                  <colgroup><col width="45%"/><col width="10%"/><col width="35%"/><col width="10%"/></colgroup>
                    <thead>
                        <tr>
                            <g:sortableColumn property="name" title="${message(code: 'collection.name.label', default: 'Name')}" params="${params}"/>
                            <g:sortableColumn property="acronym" title="${message(code: 'collection.acronym.label', default: 'Acronym')}" params="${params}"/>
                            <g:sortableColumn property="institution" title="${message(code: 'collection.institution.label', default: 'Institution')}" />
                            <g:sortableColumn property="keywords" title="Class" />
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${collInstanceList}" status="i" var="instance">
                      <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">

                        <td>
                          <a name="${instance.name[0]}"></a>
                          <g:link controller="collection" action="show" id="${instance.uid}">${fieldValue(bean: instance, field: "name")}</g:link>
                        </td>

                        <td>${fieldValue(bean: instance, field: "acronym")}</td>

                        <td><g:link controller="institution" action="show" id="${instance.institution?.uid}">${instance.institution?.name}</g:link></td>

                        <td><cl:reportClassification keywords="${instance.keywords}"/></td>

                      </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
        </div>
    <script type="text/javascript">
      var sort = "${params.sort}";
      if (sort == undefined || sort == "name") {
        var letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        var html = "";
        for (var i = 0; i < 26; i++) {
          var letter = letters[i];
          var selector = "a[name='" + letter + "']";
          if ($(selector).length == 0) {
            html += "<span>" + letter + "</span> ";
          } else {
            html += "<a href='#" + letter + "'>" + letter + "</a> ";
          }
        }
        $('div#index').html(html);
      }
    </script>
    </body>
</html>
