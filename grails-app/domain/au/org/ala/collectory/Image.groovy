package au.org.ala.collectory

class Image {
    static final long serialVersionUID = 1L;

    String file
    String caption
    String attribution
    String copyright

    static constraints = {
        file(blank:false)
        caption(nullable:true)
        attribution(nullable:true)
        copyright(nullable:true)
    }

    def String toString() {
        return ([file,caption,attribution,copyright].findAll {it}).join(", ")
    }

    def boolean equals(Object obj) {
        return obj instanceof Image && file == obj.file && caption == obj.caption &&
                attribution == obj.attribution && copyright == obj.copyright
    }
}
