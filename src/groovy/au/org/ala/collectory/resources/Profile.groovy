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

class PP {
    final static PP LOCATION_URL = new PP(name:'url',display:'Location URL', type:'text')
    final static PP SERVICE_URL = new PP(name:'url',display:'Service URL', type:'text')
    final static PP WEBSITE_URL = new PP(name:'url',display:'Website URL', type:'text')
    final static PP BASE_URL = new PP(name:'url',display:'Base URL', type:'text')
    final static PP RESOURCE = new PP(name:'resource',display:'Resource', type:'text')
    final static PP TERMS = new PP(name:'termsForUniqueKey',
            display:'DwC terms that uniquely<br/> identify a record', type:'text')
    final static PP PARAMS = new PP(name:'params',display:'JSON map of parameters', type:'textArea')
    final static PP DOCUMENT_MAPPER = new PP(name:'documentMapper', display:'Document mapper', type:'text')
    final static PP SITE_MAP = new PP(name:'siteMap', display:'Site map', type:'text')
    final static PP HARVESTER = new PP(name:'harvester', display:'Harvester class', type:'text')
    final static PP MIME_TYPE = new PP(name:'mime_type', display:'MIME type', type:'text')
    final static PP GROUP_ID = new PP(name:'group_id',display:'Group ID', type:'text')
    final static PP API_KEY = new PP(name:'api_key',display:'API key', type:'text')
    final static PP START_DATE = new PP(name:'start_date',display:'Start date', type:'text')
    final static PP CONTENT_TYPE = new PP(name:'content_type',display:'Content type', type:'text')
    final static PP PRIVACY_FILTER = new PP(name:'privacy_filter',display:'Privacy filter', type:'text')
    final static PP PER_PAGE = new PP(name:'per_page',display:'# per page', type:'text')
    final static PP KEYWORDS = new PP(name:'keywords',display:'Keywords', type:'textArea')
    final static PP AUTO = new PP(name:'automation',display:'Automatically loaded', type:'boolean')
    final static PP CSV_DELIMITER = new PP(name:'csv_delimiter',display:'Value delimiter', type:'delimiter', defaultValue: ',')
    final static PP CSV_END_OF_LINE = new PP(name:'csv_eol',display:'Line delimiter', type:'delimiter', defaultValue: '\n')
    final static PP CSV_ESCAPE = new PP(name:'csv_escape_char',display:'Escape character', type:'text', defaultValue: '/')
    final static PP CSV_QUOTE = new PP(name:'csv_text_enclosure',display:'Text enclosure', type:'text', defaultValue: '"')
    final static PP ID_REGEX = new PP(name:'autofeed_id_regex',display:'Regex to match id files', type:'text')
    final static PP DATA_REGEX = new PP(name:'autofeed_data_regex',display:'Regex to match data files', type:'text')

/*
    final static String HT = "\u0009"
    final static String VT = "\u000b"
    final static String FF = "\u000C"
    final static String CR = "\u000D"
    final static String LF = "%0A".decodeURL() // using "\u000a" confuses the compiler
*/

    static String HT_CHAR = "\u0009"
    static String VT_CHAR = "\u000b"
    static String FF_CHAR = "\u000c"
    static String CR_CHAR = "%0D".decodeURL()
    static String LF_CHAR = "%0A".decodeURL()

    String name
    String display
    String type
    String defaultValue = ""
}

enum Profile {

    NONE('none',[]),
    DIGIR('DIGIR',
            [PP.SERVICE_URL,PP.RESOURCE,PP.TERMS]),
    TAPIR('TAPIR',
            [PP.SERVICE_URL,PP.TERMS]),
    BioCASe('BioCase',
            [PP.SERVICE_URL,PP.TERMS]),
    CustomWebservice('Custom web service',
            [PP.SERVICE_URL,PP.PARAMS,PP.TERMS]),
    DwC('DarwinCore csv file',
            [PP.LOCATION_URL,PP.AUTO,PP.CSV_DELIMITER,PP.CSV_END_OF_LINE,PP.CSV_ESCAPE,PP.CSV_QUOTE,PP.TERMS]),
    DwCA('DarwinCore archive',
            [PP.LOCATION_URL,PP.AUTO,PP.TERMS]),
    AutoFeed('Automated feed',
            [PP.LOCATION_URL,PP.AUTO,PP.CSV_DELIMITER,PP.CSV_END_OF_LINE,PP.CSV_ESCAPE,PP.CSV_QUOTE,PP.TERMS,PP.ID_REGEX,PP.DATA_REGEX]),
    WebsiteWithSitemap('Website with sitemap',
            [PP.WEBSITE_URL,PP.DOCUMENT_MAPPER,PP.SITE_MAP,PP.HARVESTER,PP.MIME_TYPE]),
    Flickr('Flickr API',
            [PP.BASE_URL,PP.GROUP_ID,PP.API_KEY,PP.START_DATE,PP.CONTENT_TYPE,PP.PRIVACY_FILTER,
             PP.PER_PAGE,PP.KEYWORDS,PP.TERMS])

    String name
    List<PP> parameters

    Profile(String name, List params) {
        this.name = name
        this.parameters = params
    }

    static List list() {
        Profile.values().collect {it.name}
    }

}
