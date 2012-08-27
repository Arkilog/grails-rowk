package grails.plugins.rowk

class RowkFyiTarget extends RowkTarget {
	static mapping = {
		discriminator value:'4', type:'integer'
	}
}
