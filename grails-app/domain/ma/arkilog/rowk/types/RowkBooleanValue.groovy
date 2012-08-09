package ma.arkilog.rowk.types

import ma.arkilog.rowk.RowkValue

class RowkBooleanValue extends RowkValue{

	Boolean value

	def val(){value}

	static mapping = {
		discriminator value: "1", type: "integer"
	}

}