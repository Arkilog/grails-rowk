package grails.plugins.rowk

class RowkSerialTarget extends RowkTarget {
	static mapping = {
		discriminator value:'3', type:'integer'
	}
}
