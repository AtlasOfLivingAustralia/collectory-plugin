<%@ page import="au.org.ala.collectory.JSONHelper" %>
<!-- taxonomy hints -->
<div class="show-section  well">
  <h2><g:message code="shared.th.title01" /></h2>
  <ul class='simple'>
    <g:each in="${JSONHelper.taxonomyHints(instance.taxonomyHints)}" var="hint">
      <g:set var="key" value="${hint.keySet().iterator().next()}"/>
      <li>${key} = ${hint[key]}</li>
    </g:each>
  </ul>
  <div style="clear:both;"></div>
  <cl:editButton uid="${instance.uid}" page="/shared/editTaxonomyHints"/>
</div>
