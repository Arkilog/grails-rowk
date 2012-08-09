package ma.arkilog.rowk.types

import ma.arkilog.rowk.RowkValue

class RowkBigDecimalValue extends RowkValue{

	BigDecimal value

	def val(){value}

	static mapping = {
		discriminator value: "7", type: "integer"
	}
}