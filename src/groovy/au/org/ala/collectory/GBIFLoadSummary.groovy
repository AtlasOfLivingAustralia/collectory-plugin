package au.org.ala.collectory

/*
 * Copyright (C) 2013 Atlas of Living Australia
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

/**
 * The status information for the GBIF country load
 * @author Natasha Quimby (natasha.quimby@csiro.au)
 */

import groovy.transform.ToString

@ToString(includeNames=true,ignoreNulls = true)
class GBIFLoadSummary {
    Date startTime
    Date finishTime
    String country
    //int total //stores the total to load this will either represent the "max" supplied by the user or the total number of resources in GBIF
    String status
    //int completed // stores the number of resource that have been loaded
    //List activeLoads =[] //stores the current GBIF downloads that are being processed for insertion into the collectory
    //List completedLoads=[]
    List loads = []
    def isLoadRunning(){
        //System.out.println("Testing to see if it is still running")
        GBIFActiveLoad firstItemNotFinished =  loads.find{!it.isComplete()}
        //System.out.println("First item : " + firstItemNotFinished)
        return firstItemNotFinished != null
    }
    def getPercentageComplete(){
        //get the number that are complete
        List complete = loads.findAll {it.isComplete()}
        return (complete.size()/loads.size()) *100
    }
}
