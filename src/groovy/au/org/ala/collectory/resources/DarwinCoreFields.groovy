/*
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 *
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 */

package au.org.ala.collectory.resources

/**
 * User: markew
 * Date: 4/08/11
 */
class DarwinCoreFields {
    static List fields = [
        new DarwinCoreField(name: 'basisOfRecord', values: ["","FossilSpecimen","HumanObservation","LivingSpecimen","MachineObservation","NomenclaturalChecklist","PreservedSpecimen"], important: true),
        new DarwinCoreField(name: 'type', important: true, values: ["","Event","MovingImage","PhysicalObject","Sound","StillImage"]),
        new DarwinCoreField(name: 'recordedBy', important: true),
        new DarwinCoreField(name: 'occurrenceStatus', values: ["","present", "absent"], important: true),
        new DarwinCoreField(name: 'samplingProtocol', important: true),
        new DarwinCoreField(name: 'country', important: true),
        new DarwinCoreField(name: 'geodeticDatum', important: true),
        new DarwinCoreField(name: 'coordinateUncertaintyInMeters', important: true),
        new DarwinCoreField(name: 'coordinatePrecision', important: true),
        new DarwinCoreField(name: 'georeferencedBy', important: true),
        new DarwinCoreField(name: 'georeferenceProtocol', important: true),
        new DarwinCoreField(name: 'georeferenceSources', important: true),
        new DarwinCoreField(name: 'georeferenceVerificationStatus', important: true),
        new DarwinCoreField(name: 'identifiedBy', important: true),
        new DarwinCoreField(name: 'identificationReferences', important: true),
        new DarwinCoreField(name: 'identificationQualifier', important: true),
        new DarwinCoreField(name: 'kingdom', important: true),
        new DarwinCoreField(name: 'taxonRank', values: ["","kingdom","subkingdom","division","phylum","subdivision","subphylum","class","subclass","order","suborder","family","subfamily","tribe","subtribe","genus","subgenus","section","subsection","series","subseries","species","subspecies","variety","subvariety","form","subform"
], important: true),
            new DarwinCoreField(name: 'acceptedNameUsage'),
            new DarwinCoreField(name: 'acceptedNameUsageID'),
            new DarwinCoreField(name: 'associatedMedia'),
            new DarwinCoreField(name: 'associatedOccurrences'),
            new DarwinCoreField(name: 'associatedReferences'),
            new DarwinCoreField(name: 'associatedSequences'),
            new DarwinCoreField(name: 'associatedTaxa'),
            new DarwinCoreField(name: 'bed'),
            new DarwinCoreField(name: 'behavior'),
            new DarwinCoreField(name: 'catalogNumber'),
            new DarwinCoreField(name: 'class'),
            new DarwinCoreField(name: 'classs'),
            new DarwinCoreField(name: 'collectionCode'),
            new DarwinCoreField(name: 'collectionID'),
            new DarwinCoreField(name: 'continent'),
            new DarwinCoreField(name: 'countryCode'),
            new DarwinCoreField(name: 'dataGeneralizations'),
            new DarwinCoreField(name: 'datasetID'),
            new DarwinCoreField(name: 'datasetName'),
            new DarwinCoreField(name: 'dateIdentified'),
            new DarwinCoreField(name: 'day'),
            new DarwinCoreField(name: 'decimalLatitude'),
            new DarwinCoreField(name: 'decimalLongitude'),
            new DarwinCoreField(name: 'disposition'),
            new DarwinCoreField(name: 'dynamicProperties'),
            new DarwinCoreField(name: 'earliestAgeOrLowestStage'),
            new DarwinCoreField(name: 'earliestEonOrLowestEonothem'),
            new DarwinCoreField(name: 'earliestEpochOrLowestSeries'),
            new DarwinCoreField(name: 'earliestEraOrLowestErathem'),
            new DarwinCoreField(name: 'earliestPeriodOrLowestSystem'),
            new DarwinCoreField(name: 'endDayOfYear'),
            new DarwinCoreField(name: 'establishmentMeans'),
            new DarwinCoreField(name: 'eventDate'),
            new DarwinCoreField(name: 'eventID'),
            new DarwinCoreField(name: 'eventRemarks'),
            new DarwinCoreField(name: 'eventTime'),
            new DarwinCoreField(name: 'family'),
            new DarwinCoreField(name: 'fieldNotes'),
            new DarwinCoreField(name: 'fieldNumber'),
            new DarwinCoreField(name: 'footprintSpatialFit'),
            new DarwinCoreField(name: 'footprintSRS'),
            new DarwinCoreField(name: 'footprintWKT'),
            new DarwinCoreField(name: 'formation'),
            new DarwinCoreField(name: 'genus'),
            new DarwinCoreField(name: 'geologicalContextID'),
            new DarwinCoreField(name: 'georeferenceRemarks'),
            new DarwinCoreField(name: 'group'),
            new DarwinCoreField(name: 'habitat'),
            new DarwinCoreField(name: 'higherClassification'),
            new DarwinCoreField(name: 'higherGeography'),
            new DarwinCoreField(name: 'higherGeographyID'),
            new DarwinCoreField(name: 'highestBiostratigraphicZone'),
            new DarwinCoreField(name: 'identificationID'),
            new DarwinCoreField(name: 'identificationRemarks'),
            new DarwinCoreField(name: 'individualCount'),
            new DarwinCoreField(name: 'individualID'),
            new DarwinCoreField(name: 'informationWithheld'),
            new DarwinCoreField(name: 'infraspecificEpithet'),
            new DarwinCoreField(name: 'institutionCode'),
            new DarwinCoreField(name: 'institutionID'),
            new DarwinCoreField(name: 'island'),
            new DarwinCoreField(name: 'islandGroup'),
            new DarwinCoreField(name: 'latestAgeOrHighestStage'),
            new DarwinCoreField(name: 'latestEonOrHighestEonothem'),
            new DarwinCoreField(name: 'latestEpochOrHighestSeries'),
            new DarwinCoreField(name: 'latestEraOrHighestErathem'),
            new DarwinCoreField(name: 'latestPeriodOrHighestSystem'),
            new DarwinCoreField(name: 'lifeStage'),
            new DarwinCoreField(name: 'lithostratigraphicTerms'),
            new DarwinCoreField(name: 'locality'),
            new DarwinCoreField(name: 'locationAccordingTo'),
            new DarwinCoreField(name: 'locationID'),
            new DarwinCoreField(name: 'locationRemarks'),
            new DarwinCoreField(name: 'lowestBiostratigraphicZone'),
            new DarwinCoreField(name: 'maximumDepthInMeters'),
            new DarwinCoreField(name: 'maximumDistanceAboveSurfaceInMeters'),
            new DarwinCoreField(name: 'maximumElevationInMeters'),
            new DarwinCoreField(name: 'measurementAccuracy'),
            new DarwinCoreField(name: 'measurementDeterminedBy'),
            new DarwinCoreField(name: 'measurementDeterminedDate'),
            new DarwinCoreField(name: 'measurementID'),
            new DarwinCoreField(name: 'measurementMethod'),
            new DarwinCoreField(name: 'measurementRemarks'),
            new DarwinCoreField(name: 'measurementType'),
            new DarwinCoreField(name: 'measurementUnit'),
            new DarwinCoreField(name: 'measurementValue'),
            new DarwinCoreField(name: 'member'),
            new DarwinCoreField(name: 'minimumDepthInMeters'),
            new DarwinCoreField(name: 'minimumDistanceAboveSurfaceInMeters'),
            new DarwinCoreField(name: 'minimumElevationInMeters'),
            new DarwinCoreField(name: 'month'),
            new DarwinCoreField(name: 'municipality'),
            new DarwinCoreField(name: 'nameAccordingTo'),
            new DarwinCoreField(name: 'nameAccordingToID'),
            new DarwinCoreField(name: 'namePublishedIn'),
            new DarwinCoreField(name: 'namePublishedInID'),
            new DarwinCoreField(name: 'nomenclaturalCode'),
            new DarwinCoreField(name: 'nomenclaturalStatus'),
            new DarwinCoreField(name: 'occurrenceDetails'),
            new DarwinCoreField(name: 'occurrenceID'),
            new DarwinCoreField(name: 'occurrenceRemarks'),
            new DarwinCoreField(name: 'order'),
            new DarwinCoreField(name: 'originalNameUsage'),
            new DarwinCoreField(name: 'originalNameUsageID'),
            new DarwinCoreField(name: 'otherCatalogNumbers'),
            new DarwinCoreField(name: 'ownerInstitutionCode'),
            new DarwinCoreField(name: 'parentNameUsage'),
            new DarwinCoreField(name: 'parentNameUsageID'),
            new DarwinCoreField(name: 'phylum'),
            new DarwinCoreField(name: 'pointRadiusSpatialFit'),
            new DarwinCoreField(name: 'preparations'),
            new DarwinCoreField(name: 'previousIdentifications'),
            new DarwinCoreField(name: 'recordNumber'),
            new DarwinCoreField(name: 'relatedResourceID'),
            new DarwinCoreField(name: 'relationshipAccordingTo'),
            new DarwinCoreField(name: 'relationshipEstablishedDate'),
            new DarwinCoreField(name: 'relationshipOfResource'),
            new DarwinCoreField(name: 'relationshipRemarks'),
            new DarwinCoreField(name: 'reproductiveCondition'),
            new DarwinCoreField(name: 'resourceID'),
            new DarwinCoreField(name: 'resourceRelationshipID'),
            new DarwinCoreField(name: 'samplingEffort'),
            new DarwinCoreField(name: 'scientificName'),
            new DarwinCoreField(name: 'scientificNameAuthorship'),
            new DarwinCoreField(name: 'scientificNameID'),
            new DarwinCoreField(name: 'sex'),
            new DarwinCoreField(name: 'species'),
            new DarwinCoreField(name: 'specificEpithet'),
            new DarwinCoreField(name: 'startDayOfYear'),
            new DarwinCoreField(name: 'stateProvince'),
            new DarwinCoreField(name: 'subgenus'),
            new DarwinCoreField(name: 'taxonConceptID'),
            new DarwinCoreField(name: 'taxonID'),
            new DarwinCoreField(name: 'taxonomicStatus'),
            new DarwinCoreField(name: 'taxonRemarks'),
            new DarwinCoreField(name: 'typeStatus'),
            new DarwinCoreField(name: 'verbatimCoordinates'),
            new DarwinCoreField(name: 'verbatimCoordinateSystem'),
            new DarwinCoreField(name: 'verbatimDepth'),
            new DarwinCoreField(name: 'verbatimElevation'),
            new DarwinCoreField(name: 'verbatimEventDate'),
            new DarwinCoreField(name: 'verbatimLatitude'),
            new DarwinCoreField(name: 'verbatimLocality'),
            new DarwinCoreField(name: 'verbatimLongitude'),
            new DarwinCoreField(name: 'verbatimSRS'),
            new DarwinCoreField(name: 'verbatimTaxonRank'),
            new DarwinCoreField(name: 'vernacularName'),
            new DarwinCoreField(name: 'waterBody'),
            new DarwinCoreField(name: 'year')
    ]

    static List getImportant() {
        return fields.findAll {it.important}
    }

    static List getLessImportant() {
        return fields.findAll {!it.important}
    }

}

class DarwinCoreField {
    String name
    List values
    boolean important = false
}