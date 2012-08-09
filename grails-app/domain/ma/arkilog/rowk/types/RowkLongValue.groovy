package ma.arkilog.rowk.types

import ma.arkilog.rowk.RowkValue

class RowkLongValue extends RowkValue{

	Long value

	def val(){value}

	static mapping = {
		discriminator value: "5", type: "integer"
	}
}