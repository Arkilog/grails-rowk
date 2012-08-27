package grails.plugins.rowk

class RowkUser extends RowkAssignee {
	static mapping = {
		discriminator value:'1', type:'integer'
	}
    static constraints = {
        value(nullable:true)
        ref(nullable:true)
    }
}
