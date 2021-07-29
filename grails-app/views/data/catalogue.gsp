<%@ page import="au.org.ala.collectory.CollectionLocation" %>
<g:set var="orgNameLong" value="${grailsApplication.config.skin.orgNameLong}"/>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content ="${grailsApplication.config.skin.layout}" />
        <title><g:message code="data.catalogue.title" args="[orgNameLong]" /></title>
        <style type="text/css">
            .entity { font-weight: bold; }
            .code { font-family: 'courier new'}
        </style>
    </head>
    <body class="two-column-right" onload="">
    <div id="content">
      <div id="header">
        <div class="section full-width">
          <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
          </g:if>
          <div class="hrgroup">
            <img src="${resource(dir:"images/ala",file:"webservices.png")}" style="float: right;padding-right:50px;"/>
            <h1><g:message code="data.catalogue.title01" /></h1>
            <p><g:message code="data.catalogue.des01" /> <a href="http://code.google.com/p/ala-collectory/w/list"><g:message code="data.catalogue.des02" /></a>,
            <g:message code="data.catalogue.des03" /> <a href="http://code.google.com/p/ala-collectory/wiki/CollectoryServices"><g:message code="data.catalogue.des04" /></a>.</p>
          </div><!--close hrgroup-->
        </div><!--close section-->
      </div><!--close header-->
      <div id="column-one">
        <div class="section infoPage">
          <h2 id="WS0024"><g:message code="data.catalogue.title02" /></h2>
          <p><g:message code="data.catalogue.des05" args="[orgNameLong]"/>:</p>
          <ul>
            <li><span class='entity'><g:message code="data.catalogue.ws0024.collections" /></span> - <g:message code="data.catalogue.ws0024.collections01" />;</li>
            <li><span class='entity'><g:message code="data.catalogue.ws0024.institutions" /></span> - <g:message code="data.catalogue.ws0024.institutions01" />;</li>
            <li><span class='entity'><g:message code="data.catalogue.ws0024.dps" /></span> - <g:message code="data.catalogue.ws0024.dps01" />;</li>
            <li><span class='entity'><g:message code="data.catalogue.ws0024.drs" /></span> - <g:message code="data.catalogue.ws0024.drs01" />;</li>
            <li><span class='entity'><g:message code="data.catalogue.ws0024.tdrs" /></span> - <g:message code="data.catalogue.ws0024.tdrs01" />;</li>
            <li><span class='entity'><g:message code="data.catalogue.ws0024.dhs" /></span> - <g:message code="data.catalogue.ws0024.dhs01" /></li>
            <li><span class='entity'><g:message code="data.catalogue.ws0024.contacts" /></span> - <g:message code="data.catalogue.ws0024.contacts01" />.</li>
          </ul>
          <p><g:message code="data.catalogue.des0601" /> <em><g:message code="data.catalogue.des0602" /></em> <g:message code="data.catalogue.des0603" /> <a href="http://en.wikipedia.org/wiki/Representational_State_Transfer">RESTful</a> <g:message code="data.catalogue.des0604" /> <a href="http://en.wikipedia.org/wiki/JSON">JSON</a> <g:message code="data.catalogue.des0605" />.</p>
          <p><g:message code="data.catalogue.des00701" /> <a href="http://code.google.com/p/ala-collectory/w/list"><g:message code="data.catalogue.des00702" /></a>, <g:message code="data.catalogue.des00703" /> <a href="http://code.google.com/p/ala-collectory/wiki/CollectoryServices"><g:message code="data.catalogue.des00704" /></a>.</p>
          <h3><g:message code="data.catalogue.title03" /></h3>
          <table class="table">
            <colgroup><col width="55%"><col width="45%"></colgroup>
            <tr>
              <td><g:message code="data.catalogue.table01.cell0101" />:</td>
              <td><a href="${grailsApplication.config.grails.serverURL}/ws">https://collections.ala.org.au/ws</a></td>
            </tr>
            <tr>
              <td><g:message code="data.catalogue.table01.cell0201" />:</td>
              <td><a href="${grailsApplication.config.grails.serverURL}/ws/institution.json">https://collections.ala.org.au/ws/institution</a></td>
            </tr>
            <tr>
              <td><g:message code="data.catalogue.table01.cell0301" />:</td>
              <td><a href="${grailsApplication.config.grails.serverURL}/ws/institution/in72.json">https://collections.ala.org.au/ws/institution/in72</a></td>
            </tr>
            <tr>
              <td><g:message code="data.catalogue.table01.cell0401" />:</td>
              <td><a href="${grailsApplication.config.grails.serverURL}/ws/institution/count">https://collections.ala.org.au/ws/institution/count</a></td>
            </tr>
            <tr>
              <td><g:message code="data.catalogue.table01.cell0501" />:</td>
              <td><a href="${grailsApplication.config.grails.serverURL}/ws/institution/count/state">https://collections.ala.org.au/ws/institution/count/state</a></td>
            </tr>
            <tr>
              <td><g:message code="data.catalogue.table01.cell0601" />:</td>
              <td><a href="${grailsApplication.config.grails.serverURL}/ws/institution/in72/contacts.json">https://collections.ala.org.au/ws/institution/in72/contacts</a></td>
            </tr>
          </table>
          <h3><g:message code="data.catalogue.title04" /></h3>
          <p><g:message code="data.catalogue.des08" />.</p>
          <p><span class='entity'>GET</span> <g:message code="data.catalogue.des09" />.</p>
          <p><span class='entity'>HEAD</span> <g:message code="data.catalogue.des10" />.</p>
          <p><span class='entity'>POST</span> <g:message code="data.catalogue.des11" />:</p>
          <ul>
            <li><g:message code="data.catalogue.li04" /></li>
            <li><g:message code="data.catalogue.li05" />.</li>
          </ul>
          <p><g:message code="data.catalogue.des12" />:</p>
          <ul>
            <li><g:message code="data.catalogue.li06" /></li>
          </ul>
          <p><g:message code="data.catalogue.des13" />.</p>
          <p><span class='entity'>PUT</span> <g:message code="data.catalogue.des14" />.</p>
          <p><span class='entity'>OPTIONS</span> <g:message code="data.catalogue.des15" />.</p>
          <p><span class='entity'>DELETE</span> <g:message code="data.catalogue.des16" />:</p>
            <ul>
                <li><g:message code="data.catalogue.li07" /></li>
                <li><g:message code="data.catalogue.li08" />.</li>
            </ul>

        <h3 id="WS0025"><g:message code="data.catalogue.ws0025.title" /></h3>
          <p><g:message code="data.catalogue.ws0025.des" />.</p>
          <table class="table">
            <colgroup><col width="55%"><col width="45%"></colgroup>
            <tr>
              <td><g:message code="data.catalogue.ws0025.cell0101" />:</td>
              <td><a href="${grailsApplication.config.grails.serverURL}/ws/contacts.json">https://collections.ala.org.au/ws/contacts</a></td>
            </tr>
            <tr>
              <td><g:message code="data.catalogue.ws0025.cell0201" /></td>
              <td><a href="${grailsApplication.config.grails.serverURL}/ws/contacts/31.json">https://collections.ala.org.au/ws/contacts/31</a></td>
            </tr>
            <tr>
              <td><g:message code="data.catalogue.ws0025.cell0301" /></td>
              <td><a href="${grailsApplication.config.grails.serverURL}/ws/contacts/email/dave.martin@csiro.au">https://collections.ala.org.au/ws/contacts/email/dave.martin@csiro.au</a></td>
            </tr>
          </table>
          <p><g:message code="data.catalogue.ws0025.des0101" />. <span class='entity'>PUT</span> <g:message code="data.catalogue.ws0025.des0102" /> <span class='entity'>POST</span> <g:message code="data.catalogue.ws0025.des0103" />. <span class='entity'>DELETE</span> <g:message code="data.catalogue.ws0025.des0104" />.</p>
            <p><g:message code="data.catalogue.ws0025.des02" />:</p>
            <ul>
                <li><g:message code="data.catalogue.ws0025.user" /></li>
                <li><g:message code="data.catalogue.ws0025.apikey" />.</li>
            </ul>
            <p><g:message code="data.catalogue.ws0025.des03" /></p>
            <ul>
                <li><g:message code="data.catalogue.ws0025.firstname" /></li>
                <li><g:message code="data.catalogue.ws0025.lastname" /></li>
                <li><g:message code="data.catalogue.ws0025.fax" /></li>
                <li><g:message code="data.catalogue.ws0025.phone" /></li>
                <li><g:message code="data.catalogue.ws0025.mobile" /></li>
                <li><g:message code="data.catalogue.ws0025.notes" /></li>
                <li><g:message code="data.catalogue.ws0025.public" /></li>
            </ul>

          <p><g:message code="data.catalogue.ws0025.des04" />.</p>
          <p><g:message code="data.catalogue.ws0025.des05" />.</p>
          <h4><g:message code="data.catalogue.ws0025.title01" /></h4>
          <p><g:message code="data.catalogue.ws0025.des06" />.</p>
          <table class="table">
            <colgroup><col width="55%"><col width="45%"></colgroup>
            <tr>
              <td colspan="2"><g:message code="data.catalogue.ws0025.table0101" /> <span class="code"><g:message code="data.catalogue.ws0025.table0102" /></span> <g:message code="data.catalogue.ws0025.table0103" />:</td>
            </tr>
            <tr>
              <td colspan="2"><span class="code"><span class='entity'>GET</span> https://collections.ala.org.au/ws/{resource type}/{resource uid}/contacts</span></td>
            </tr>
            <tr>
              <td><g:message code="data.catalogue.ws0025.forexample" />:</td>
              <td><a href="${grailsApplication.config.grails.serverURL}/ws/collection/co13/contacts.json">https://collections.ala.org.au/ws/collection/co13/contacts</a></td>
            </tr>
            <tr>
              <td colspan="2"><g:message code="data.catalogue.ws0025.table0301" />:</td>
            </tr>
            <tr>
              <td colspan="2"><span class="code"><span class='entity'>GET</span> https://collections.ala.org.au/ws/{resource type}/{resource uid}/contacts/{id}</span></td>
            </tr>
            <tr>
              <td><g:message code="data.catalogue.ws0025.forexample" />:</td>
              <td><a href="${grailsApplication.config.grails.serverURL}/ws/collection/co13/contacts/20.json">https://collections.ala.org.au/ws/collection/co13/contacts/20</a></td>
            </tr>
            <tr>
              <td colspan="2"><g:message code="data.catalogue.ws0025.table0501" />:</td>
            </tr>
            <tr>
              <td colspan="2"><span class="code"><span class='entity'>GET</span> https://collections.ala.org.au/ws/{resource type}/contacts</span></td>
            </tr>
            <tr>
              <td><g:message code="data.catalogue.ws0025.forexample" />:</td>
              <td><a href="${grailsApplication.config.grails.serverURL}/ws/collection/contacts.json">https://collections.ala.org.au/ws/collection/contacts</a></td>
            </tr>
            <tr>
              <td colspan="2"><g:message code="data.catalogue.ws0025.table0701" />:</td>
            </tr>
            <tr>
              <td colspan="2"><span class="code"><span class='entity'>GET</span> https://collections.ala.org.au/ws/{resource type}/{resource uid}/contacts/notifiable</span></td>
            </tr>
            <tr>
              <td><g:message code="data.catalogue.ws0025.forexample" />:</td>
              <td><a href="${grailsApplication.config.grails.serverURL}/ws/collection/co13/contacts/notifiable.json">https://collections.ala.org.au/ws/collection/co13/contacts/notifiable</a></td>
            </tr>
            <tr>
              <td colspan="2"><g:message code="data.catalogue.ws0025.table0901" />:</td>
            </tr>
            <tr>
              <td colspan="2"><span class="code"><span class='entity'>GET</span> https://collections.ala.org.au/ws/contacts/{contact id}/authorised</span></td>
            </tr>
            <tr>
              <td><g:message code="data.catalogue.ws0025.forexample" />:</td>
              <td><a href="${grailsApplication.config.grails.serverURL}/ws/contacts/132/authorised.json">https://collections.ala.org.au/ws/contacts/132/authorised</a></td>
            </tr>
          </table>
          <p><g:message code="data.catalogue.ws0025.des07" />.<br/>
            <a href="http://code.google.com/p/ala-collectory/wiki/CollectoryServices#Data_services"><g:message code="data.catalogue.ws0025.link01" />.</a></p>

          <p><g:message code="data.catalogue.ws0025.des08" />.</p>
            <p><span class='entity'>POST</span> <g:message code="data.catalogue.ws0025.des09" />.</p>
            <p><g:message code="data.catalogue.ws0025.des10" />:</p>
            <ul>
                <li><g:message code="data.catalogue.ws0025.user" /></li>
                <li><g:message code="data.catalogue.ws0025.apikey" />.</li>
            </ul>
            <p><g:message code="data.catalogue.ws0025.des11" /></p>
            <ul>
                <li><g:message code="data.catalogue.ws0025.li12" /></li>
                <li><g:message code="data.catalogue.ws0025.li13" /></li>
                <li><g:message code="data.catalogue.ws0025.li14" /></li>
                <li><g:message code="data.catalogue.ws0025.li15" /></li>
            </ul>
            <p><g:message code="data.catalogue.ws0025.des12" /> https://collections.ala.org.au/ws/{resource type}/{resource uid}/contacts/{contact id}</p>
            <p><span class='entity'>PUT</span> <g:message code="data.catalogue.ws0025.des13" />.</p>
            <p><span class='entity'>DELETE</span> <g:message code="data.catalogue.ws0025.des14" />.</p>
            <p><g:message code="data.catalogue.ws0025.des15" />:</p>
            <ul>
                <li><g:message code="data.catalogue.ws0025.user" /></li>
                <li><g:message code="data.catalogue.ws0025.apikey" />.</li>
            </ul>
          <p><g:message code="data.catalogue.ws0025.des16" />.</p>


          <h3 id="WS0026"><g:message code="data.catalogue.ws0026.title" /></h3>
          <p><g:message code="data.catalogue.ws0026.des01" /> <a href="http://community.gbif.org/pg/pages/view/10913/the-gbif-eml-metadata-profile"><g:message code="data.catalogue.ws0026.link01" /></a>.
          <g:message code="data.catalogue.ws0026.des02" />:</p>
          <table class="table">
            <colgroup><col width="55%"><col width="45%"></colgroup>
            <tr>
              <td colspan="2"><span class="code"><span class='entity'>GET</span> https://collections.ala.org.au/ws/eml/<strong>{uid}</strong></span></td>
            </tr>
            <tr>
              <td><g:message code="data.catalogue.ws0026.table0101" />:</td>
              <td><a href="${grailsApplication.config.grails.serverURL}/ws/eml/dr368">https://collections.ala.org.au/ws/eml/dr368</a></td>
            </tr>
          </table>

          <h2 id="WS0032"><g:message code="data.catalogue.ws0032.title" /></h2>
          <p><g:message code="data.catalogue.ws0032.des01" />.</p>

          <h3><g:message code="data.catalogue.ws0032.title01" /></h3>
          <p><g:message code="data.catalogue.ws0032.des02" />:</p>
          <table class="table">
            <colgroup><col width="55%"><col width="45%"></colgroup>
            <tr>
              <td colspan="2"><span class="code"><span class='entity'>GET</span> https://collections.ala.org.au/ws/citations/<strong>{listOfUIDs}</strong></span></td>
            </tr>
            <tr>
              <td colspan="2"><g:message code="data.catalogue.ws0032.td0101" /> <span class="code"><g:message code="data.catalogue.ws0032.td0102" /></span> <g:message code="data.catalogue.ws0032.td0103" />.</td>
            </tr>
            <tr>
              <td><g:message code="data.catalogue.ws0032.td0201" />:</td>
              <td><a href="${grailsApplication.config.grails.serverURL}/ws/citations/dr368,dr105,dr357">https://collections.ala.org.au/ws/citations/dr368,dr105,dr357</a></td>
            </tr>
          </table>

          <p><g:message code="data.catalogue.ws0032.des03" />.
          <br/>
          <a href="http://code.google.com/p/ala-collectory/wiki/CollectoryServices#Lookup_citation_text_for_a_list_of_UIDs" class="btn btn-default"><g:message code="data.catalogue.ws0032.link02" />.</a></p>

          <h2><g:message code="data.catalogue.ws0032.title02" /></h2>
          <p><g:message code="data.catalogue.ws0032.des04" />.</p>
          <h3 id="WS0027"><g:message code="data.catalogue.ws0027.title" /></h3>
          <p><g:message code="data.catalogue.ws0027.des01" />:</p>
          <table class="table">
            <colgroup><col width="55%"><col width="45%"></colgroup>
            <tr>
              <td colspan="2"><span class="code"><span class='entity'>GET</span> https://collections.ala.org.au/lookup/inst/<strong>{institution-code}</strong>/coll/<strong>{collection-code}</strong></span></td>
            </tr>
            <tr>
              <td><g:message code="data.catalogue.ws0027.table0101" />:</td>
              <td><a href="${grailsApplication.config.grails.serverURL}/lookup/inst/ANIC/coll/Insects.json">https://collections.ala.org.au/lookup/inst/ANIC/coll/Insects</a></td>
            </tr>
            <tr>
              <td colspan="2"><a href="http://code.google.com/p/ala-collectory/wiki/CollectoryServices#Lookup_collection_from_institution_and_collection_codes" class="btn btn-default">More information.</a></td>
            </tr>
          </table>

          <h3 id="WS0028"><g:message code="data.catalogue.ws0028.title" /></h3>
          <p><g:message code="data.catalogue.ws0028.des01" />:</p>
          <table class="table">
            <colgroup><col width="55%"><col width="45%"></colgroup>
            <tr>
              <td colspan="2"><span class="code"><span class='entity'>GET</span> https://collections.ala.org.au/lookup/summary/<strong>{uid}</strong></span></td>
            </tr>
            <tr>
              <td><g:message code="data.catalogue.ws0028.table0101" />:</td>
              <td><a href="${grailsApplication.config.grails.serverURL}/lookup/summary/dp28.json">https://collections.ala.org.au/lookup/summary/dp28</a></td>
            </tr>
          </table>
          <p><g:message code="data.catalogue.ws0028.des02" />. <a href="http://code.google.com/p/ala-collectory/wiki/CollectoryServices#Lookup_summary_from_UID"><g:message code="data.catalogue.ws0028.link01" />.</a></p>

          <h3 id="WS0029"><g:message code="data.catalogue.ws0029.title" /></h3>
          <p><g:message code="data.catalogue.ws0029.des01" />: </p>
          <p><span class="code"><span class='entity'>GET</span> https://collections.ala.org.au/lookup/name/<strong>{uid}</strong></span></p>
          <p><a href="http://code.google.com/p/ala-collectory/wiki/CollectoryServices#Lookup_name_from_UID" class="btn btn-default"><g:message code="data.catalogue.ws0029.link01" />.</a></p>

          <h3 id="WS0030"><g:message code="data.catalogue.ws0030.title" /></h3>
          <p><g:message code="data.catalogue.ws0030.des01" />:</p>
          <table class="clean no-left-pad">
            <colgroup><col width="55%"><col width="45%"></colgroup>
            <tr>
              <td colspan="2"><span class="code"><span class='entity'>GET</span> https://collections.ala.org.au/lookup/taxonomicCoverageHints/<strong>{uid}</strong></span></td>
            </tr>
            <tr>
              <td><g:message code="data.catalogue.ws0030.table0101" />:</td>
              <td><a href="${grailsApplication.config.grails.serverURL}/lookup/taxonomyCoverageHints/co12.json">https://collections.ala.org.au/lookup/taxonomyCoverageHints/co12</a></td>
            </tr>
          </table>

          <h3><g:message code="data.catalogue.ws0030.title01" /></h3>
          <p><g:message code="data.catalogue.ws0030.des02" />.
          If <span class="code">[ ]</span> <g:message code="data.catalogue.ws0030.des03" />:</p>
          <p><span class="code"><span class='entity'>GET</span> <a href="https://collections.ala.org.au/lookup/downloadLimits">https://collections.ala.org.au/lookup/downloadLimits</a></span></p>

          <h3><g:message code="data.catalogue.ws0030.title02" /></h3>
          <p><g:message code="data.catalogue.ws0030.des04" />:</p>
          <p><span class="code">GET https://collections.ala.org.au/lookup/generateDataResourceUid</span></p>
          <p><a href="http://code.google.com/p/ala-collectory/wiki/CollectoryServices#Generate_UID_for_a_new_entity" class="btn btn-default"><g:message code="data.catalogue.ws0030.link01" />.</a></p>

          <h2 id="WS0031"><g:message code="data.catalogue.ws0031.title01" /></h2>
          <p><g:message code="data.catalogue.ws0031.des01" />:</p>

          <h4>IPT</h4>
          <p><g:message code="data.catalogue.ws0031.des02" /></p>
          <table class="clean no-left-pad">
            <colgroup><col width="55%"><col width="45%"></colgroup>
            <tr>
              <td colspan="2"><span class="code"><span class='entity'>GET</span> https://collections.ala.org.au/ipt/scan?create=true&isShareableWithGBIF=false&uid=<strong>{uid}</strong>&apiKey=XXXXXX</span></td>
            </tr>
          </table>
          <h4>GBIF</h4>
          <p><g:message code="data.catalogue.ws0031.des03" /></p>
          <table class="clean no-left-pad">
            <colgroup><col width="55%"><col width="45%"></colgroup>
            <tr>
              <td colspan="2"><span class="code"><span class='entity'>GET</span> https://collections.ala.org.au/gbif/scan?uid=<strong>{uid}</strong>&apiKey=XXXXXX</span></td>
            </tr>
          </table>

        </div><!--close section-->
      </div><!--close column-one-->

      <div id="column-two">
        <div class="section infoColumn">
          <h1><g:message code="data.catalogue.ct.title01" /></h1>
          <p>
            <a href="https://collections.ala.org.au/ws/collection.json"><g:message code="data.catalogue.ct.lacs" /></a><br/>
            <a href="https://collections.ala.org.au/ws/institution.json"><g:message code="data.catalogue.ct.lais" /></a><br/>
            <a href="https://collections.ala.org.au/ws/dataProvider.json"><g:message code="data.catalogue.ct.ladps" /></a><br/>
            <a href="https://collections.ala.org.au/ws/dataResource.json"><g:message code="data.catalogue.ct.ladrs" /></a><br/>
            <a href="https://collections.ala.org.au/ws/dataHub.json"><g:message code="data.catalogue.ct.lahs" /></a><br/>
          </p>
          <p>
            <a href="https://collections.ala.org.au/ws/collection/contacts.json"><g:message code="data.catalogue.ct.lmcfec" /></a><br/>
          </p>

        </div><!--close section-->
      </div><!--close column-two-->

    </div><!--close content-->
  </body>
</html>
