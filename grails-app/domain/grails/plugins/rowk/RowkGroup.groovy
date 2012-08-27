package grails.plugins.rowk

class RowkGroup extends RowkAssignee {
	static mapping = {
		discriminator value:'2', type:'integer'
	}
    static constraints = {
        value(nullable:true)
        ref(nullable:true)
    }
}
