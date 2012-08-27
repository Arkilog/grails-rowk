package grails.plugins.rowk

class RowkResult extends RowkVariable {

	static belongsTo = [action : RowkAction]

    static mapping = {
    	discriminator value:"r"
    }

    String toString() {name}
}
