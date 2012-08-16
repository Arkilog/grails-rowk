package ma.arkilog.rowk

class RowkSerialTarget extends RowkTarget {
	static mapping = {
		discriminator value:'3', type:'integer'
	}
}
