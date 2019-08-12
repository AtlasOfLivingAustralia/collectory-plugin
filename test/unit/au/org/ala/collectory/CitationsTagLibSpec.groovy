/*
 * Copyright (C) 2019 Atlas of Living Australia
 * All Rights Reserved.
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 */

package au.org.ala.collectory

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.GroovyPageUnitTestMixin} for usage instructions
 */
@TestFor(CitationsTagLib)
class CitationsTagLibSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    /**
     * Old GBIF DOI format with 'doi:' prefix
     */
    void "test GBIF DOI old format"() {
        expect:
        applyTemplate('<citations:doiLink gbifDoi="doi:10.15468/14jd9g"/>') == 'https://doi.org/10.15468/14jd9g'
        tagLib.doiLink(gbifDoi: 'doi:10.15468/14jd9g').toString() == 'https://doi.org/10.15468/14jd9g'
        applyTemplate('<citations:doiLink gbifDoi="doi:10.15468/dchsnk"/>') == 'https://doi.org/10.15468/dchsnk'
    }

    /**
     * New GBIF DOI format (path only)
     */
    void "test GBIF DOI new format"() {
        expect:
        applyTemplate('<citations:doiLink gbifDoi="10.15468/14jd9g"/>') == 'https://doi.org/10.15468/14jd9g'
        applyTemplate('<citations:doiLink gbifDoi="10.15468/dchsnk"/>') == 'https://doi.org/10.15468/dchsnk'
    }

    /**
     * Fully formatted DOI
     */
    void "test GBIF DOI full-url format"() {
        expect:
        applyTemplate('<citations:doiLink gbifDoi="https://doi.org/10.15468/14jd9g"/>') == 'https://doi.org/10.15468/14jd9g'
        applyTemplate('<citations:doiLink gbifDoi="https://doi.org/10.15468/dchsnk"/>') == 'https://doi.org/10.15468/dchsnk'
    }
}
