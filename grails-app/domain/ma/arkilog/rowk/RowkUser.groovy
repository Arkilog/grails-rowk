package ma.arkilog.rowk

class RowkUser extends RowkAssignee {
	static mapping = {
		discriminator value:'1', type:'integer'
	}
}
