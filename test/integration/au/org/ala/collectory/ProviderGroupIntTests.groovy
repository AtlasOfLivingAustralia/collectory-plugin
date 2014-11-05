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

package au.org.ala.collectory

import grails.test.GrailsUnitTestCase

/**
 * User: markew
 * Date: 15/07/11
 */
class ProviderGroupIntTests extends GrailsUnitTestCase {

    protected void setUp() {
        super.setUp()
    }

    protected void tearDown() {
        super.tearDown()
    }

    void testGetContacts() {
        Collection cc = new Collection(uid: "co1",name: "test",userLastModified: 'test')
        cc.save()

        Contact c1 = new Contact(firstName: "contact1", userLastModified: 'test', publish: false)
        c1.save()
        Contact c2 = new Contact(firstName: "contact2", userLastModified: 'test', publish: true)
        c2.save()
        Contact c3 = new Contact(firstName: "contact3", userLastModified: 'test', publish: false)
        c3.save()
        Contact c4 = new Contact(firstName: "contact4", userLastModified: 'test', publish: true)
        c4.save()

        ContactFor cf1 = cc.addToContacts(c1, 'role', false, false, 'test') // not primary, not public
        ContactFor cf2 = cc.addToContacts(c2, 'role', false, false, 'test')  // not primary, public
        ContactFor cf3 = cc.addToContacts(c3, 'role', false, true, 'test')  // primary, not public
        ContactFor cf4 = cc.addToContacts(c4, 'role', false, true, 'test') // primary, public

        cc.save()

        assert cc.getContacts().size() == 4
        assert cc.getPrimaryContact() in [cf3,cf4] // don't know which is the first found
        assert cc.getPrimaryPublicContact() == cf4
        assert cc. getPublicContactsPrimaryFirst()[0] == cf4
    }
}
