<r:script type="text/javascript">
  var CHARTS_CONFIG = {
      biocacheServicesUrl: "${grailsApplication.config.biocacheServicesUrl}",
      biocacheWebappUrl: "${grailsApplication.config.biocacheUiURL}",
      bieWebappUrl: "${grailsApplication.config.bieUiURL}",
      collectionsUrl: "${grailsApplication.config.grails.serverURL}"
  };
  var taxonomyTreeOptions = {
      /* base url of the collectory */
      collectionsUrl: CHARTS_CONFIG.collectionsUrl,
      /* base url of the biocache ws*/
      biocacheServicesUrl: CHARTS_CONFIG.biocacheServicesUrl,
      /* base url of the biocache webapp*/
      biocacheWebappUrl: CHARTS_CONFIG.biocacheWebappUrl,
      /* the id of the div to create the charts in - defaults is 'charts' */
      targetDivId: "tree",
      /* a uid or list of uids to chart - either this or query must be present */
      instanceUid: "${instance.uid}"
  }

  // records
  if (${!instance.hasProperty("resourceType") || instance.resourceType == 'records'}) {
      // tree
      initTaxonTree(taxonomyTreeOptions);
  }
</r:script>