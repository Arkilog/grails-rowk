package grails.plugins.rowk

class RowkParameter extends RowkVariable {

	static belongsTo = [action : RowkAction]

    static mapping = {
    	discriminator value:"p"
    }

    String toString() {name}
}
