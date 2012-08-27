package grails.plugins.rowk.types

import grails.plugins.rowk.RowkValue

class RowkBooleanValue extends RowkValue{

	Boolean value

	def val(){value}

	static mapping = {
		discriminator value: "1", type: "integer"
	}
    static constraints = {
    	value(nullable:true)
    }

}