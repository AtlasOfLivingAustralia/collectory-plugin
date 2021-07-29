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
 * Stores the information about a current load
 * @author Natasha Quimby (natasha.quimby@csiro.au)
 */
import groovy.transform.ToString

@ToString(includeNames=true, ignoreNulls = true)
class GBIFActiveLoad {
    String downloadId
    String gbifResourceUid
    String name
    String phase = "<NOT STARTED>"
    String dataResourceUid
    String repatriationCountry

    private boolean completed = false
    public void setCompleted(){
        completed = true
    }
    def isComplete(){
        return completed
    }

}
