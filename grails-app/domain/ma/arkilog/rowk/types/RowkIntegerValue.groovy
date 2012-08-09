package ma.arkilog.rowk.types

import ma.arkilog.rowk.RowkValue

class RowkIntegerValue extends RowkValue{

	Integer value

	def val(){value}

	static mapping = {
		discriminator value: "3", type: "integer"
	}
}