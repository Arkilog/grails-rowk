package grails.plugins.rowk

class RowkFifoTarget extends RowkTarget {
	static mapping = {
		discriminator value:'1', type:'integer'
	}
}
