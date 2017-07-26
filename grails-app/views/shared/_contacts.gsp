<!-- Contacts -->
 <section class="public-metadata show-section well">
 <h2><g:message code="shared.contacts.title01" /></h2>
 <ul class="fancy">
   <g:each in="${contacts}" var="c">
     <li><g:link controller="contact" action="show" id="${c?.contact?.id}">
       ${c?.contact?.buildName()}
       <cl:roleIfPresent role='${c?.role}'/>
       <cl:adminIfPresent admin='${c?.administrator}'/>
       ${c?.contact?.phone}
       <cl:valueOrOtherwise value ="${c?.primaryContact}"> (Primary contact)</cl:valueOrOtherwise>
     </g:link>
     </li>
   </g:each>
 </ul>
 <div style="clear:both;"></div>
 <cl:editButton uid="${instance.uid}" page="/shared/showContacts"/>
 </section>
