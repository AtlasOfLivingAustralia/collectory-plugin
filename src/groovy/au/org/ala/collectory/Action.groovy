package au.org.ala.collectory

/**
 * Created by markew
 * Date: Jun 17, 2010
 * Time: 2:38:06 PM
 */
enum Action {
    LOGIN ('logged in'),
    LOGOUT ('logged out'),
    VIEW ('viewed'),
    EDIT_CANCEL ('edited but cancelled'),
    EDIT_SAVE ('edited and saved'),
    PREVIEW ('previewed'),
    DELETE ('deleted'),
    SEARCH ('searched for '),
    LIST ('listed collections'),
    MYLIST ('listed own collections'),
    DATA_LOAD ('loaded data'),
    CREATE ('created a collection'),
    CREATE_CANCEL ('cancelled creation of collection'),
    CREATE_INSTITUTION ('created an institution'),
    CREATE_CONTACT ('created a contact'),
    UPLOAD_IMAGE ('uploaded file'),
    REPORT ('viewed reports'),
    NOTIFY ('notifiable event'),
    SCAN ('scanned for updates')

    String display

    private Action(display) {
        this.display = display
    }

    String toString() {
        return display
    }
}
