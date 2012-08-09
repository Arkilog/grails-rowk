package ma.arkilog.rowk.types

import ma.arkilog.rowk.RowkValue

class RowkFloatValue extends RowkValue{

	Float value

	def val(){value}

	static mapping = {
		discriminator value: "8", type: "integer"
	}
}