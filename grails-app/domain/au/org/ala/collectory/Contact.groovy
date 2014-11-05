package au.org.ala.collectory
/*  represents a person who acts as a contact for an ALA entity such as
 *  an institution, collection or dataset.
 *
 *  - based on collectory data model version 5
 */
class Contact implements Serializable {

    String title            // the person's honorific eg Dr
    String firstName
    String lastName
    String phone
    String mobile
    String email
    String fax
    String notes
    boolean publish = true    // controls whether the contact is listed on web site

    Date dateCreated
    Date lastUpdated
    String userLastModified

    static auditable = [ignore: ['version','dateCreated','lastUpdated','userLastModified']]

    static constraints = {
        title(nullable:true, maxSize: 20, inList: ["Dr", "Prof", "Mr", "Ms", "Mrs", "Assoc Prof", "Assist Prof"])
        firstName(nullable: true, maxSize: 255)
        lastName(nullable: true, maxSize: 255)
        phone(nullable: true, maxSize:45)
        mobile(nullable: true, maxSize:45)
        email(nullable: true, maxSize:128, email: true)
        fax(nullable: true, maxSize:45)
        notes(nullable: true, maxSize: 1024)
        publish()
        userLastModified(maxSize:256)
    }

    def print() {
        ["title: " + title,
         "firstName: " + firstName,
         "lastName: " + lastName,
         "phone: " + phone,
         "mobile: " + mobile,
         "email: " + email,
         "fax: " + fax,
         "notes: " + notes,
         "publish " + publish]
    }

    /**
     * Loads a name that comes as a single string. Only handles simple cases.
     *
     */
    void parseName(String name) {
        if (!name) return
        // remove any trailing parentheses  -  handles cases like "Mr Tom Weir (BSc (HONS))"

        if (name.indexOf('(') > 0) {
            name = name.substring(0, name.indexOf('('))
        }

        def parts = name.split()
        switch (parts.size()) {
            case 0: break // bad
            case 1:
                lastName = name // only one word so make it last name
                break
            case 2:              // assume first + last
                firstName = parts[0]
                lastName = parts[1]
                break
            default:
                // cater for Dr Lemmy Caution and Lemmy A Caution
                /* Algorithm is:
                    - make first part the title if it is recognised
                    - make the last part the last name
                    - dump all the remaining parts into first name
                 */
                if (parts[0] in ["Dr", "Dr.", "Prof", "Mr", "Ms", ""]) {
                    title = parts[0]
                    firstName = parts[1..parts.size() - 2].join(" ")
                } else {
                    title = ''
                    firstName = parts[0..parts.size() - 2].join(" ")
                }
                lastName = parts[parts.size() - 1]
                break
        }
    }

    String buildName() {
        if (lastName)
            return [(title ?: ''), (firstName ?: ''), lastName].join(" ").trim()
        else if (email)
            return email
        else if (phone)
            return phone
        else if (mobile)
            return mobile
        else if (fax)
            return fax
        else
            return ''
    }

    String toString() {
        return buildName()
    }

    /**
     * Quick test to see if an instance has any content.
     *
     * Note stupid name because Grails seems to object to isEmpty thinking there should be an empty property.
     */
    boolean hasContent() {
        lastName || phone || mobile || email || fax
    }

    /**
     * Returns the list of provider groups that this contact is a contact for.
     *
     * @return list of ProviderGroup or empty list
     */
    List<ProviderGroup> getContactsFor() {
        List<ProviderGroup> result = []
        ContactFor.findAllByContact(this).each {
            result << ProviderGroup._get(it.entityUid)
        }
        return result
    }
}
