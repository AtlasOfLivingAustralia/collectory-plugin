# collectory-plugin   [![Build Status](https://travis-ci.org/AtlasOfLivingAustralia/collectory-plugin.svg?branch=master)](http://travis-ci.org/AtlasOfLivingAustralia/collectory-plugin)

The collectory-plugin manages metadata that describe Australia's biodiversity collections and suppliers of biodiversity data.

 * [Database schema (PDF)](https://github.com/AtlasOfLivingAustralia/collectory/blob/master/Collectory_schema_20140916.pdf?raw=true)

## Release notes - version 1.5
 [See release notes on release page for 1.5](https://github.com/AtlasOfLivingAustralia/collectory-plugin/releases/tag/1.5)

## Release notes - version 1.2

* IPT integration UI (Beta)
* Changes to GBIF imports to avoid dataset duplication

## Release notes - version 1.1

* Extend EML rendering to include complete rights information
* removed getString usage - was causing exception
* Add user id to temp data resources
* Fix the issue of open an email dialog on Chrome
* Fix the issue of open an email dialog on Chrome
* removed reference to collections_editor role

## Release notes - version 1.0

 * Provide this Grails plugin for your collectory project. The initial version is set to 1.0 since it is from collectory project.
 * Copy data folder from the web-app folder of this project to your server folder /data/ala-collectory/data, which can be set in your collectory application config.groovy file (repository.location.images = '/data/ala-collectory/data').
