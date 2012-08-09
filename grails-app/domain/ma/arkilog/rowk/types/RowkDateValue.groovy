package ma.arkilog.rowk.types

import ma.arkilog.rowk.RowkValue

class RowkDateValue extends RowkValue{

	Date value

	def val(){value}

	static mapping = {
		discriminator value: "2", type: "integer"
	}

}