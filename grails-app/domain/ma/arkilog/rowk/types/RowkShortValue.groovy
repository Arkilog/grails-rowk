package ma.arkilog.rowk.types

import ma.arkilog.rowk.RowkValue

class RowkShortValue extends RowkValue{

	Short value

	def val(){value}

	static mapping = {
		discriminator value: "6", type: "integer"
	}
}