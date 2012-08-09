package ma.arkilog.rowk.types

import ma.arkilog.rowk.RowkValue

class RowkStringValue extends RowkValue{

	String value

	def val(){value}

	static mapping = {
		discriminator value: "4", type: "integer"
	}
}