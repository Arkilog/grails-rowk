package grails.plugins.rowk.types

import grails.plugins.rowk.RowkValue

class RowkBigDecimalValue extends RowkValue{

	BigDecimal value

	def val(){value}

	static mapping = {
		discriminator value: "7", type: "integer"
	}
    static constraints = {
    	value(nullable:true)
    }
}