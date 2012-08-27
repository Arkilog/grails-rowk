package grails.plugins.rowk.types

import grails.plugins.rowk.RowkValue

class RowkStringValue extends RowkValue{

	String value

	def val(){value}

	static mapping = {
		discriminator value: "4", type: "integer"
	}
    static constraints = {
    	value(nullable:true)
    }
}