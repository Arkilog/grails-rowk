package grails.plugins.rowk

class RowkVoteTarget extends RowkTarget {
	static mapping = {
		discriminator value:'2', type:'integer'
	}
}
