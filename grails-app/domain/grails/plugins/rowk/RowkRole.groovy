package grails.plugins.rowk

class RowkRole extends RowkAssignee {
	static mapping = {
		discriminator value:'3', type:'integer'
	}
    static constraints = {
        value(nullable:true)
        ref(nullable:true)
    }
}
