package grails.plugins.rowk.types

import grails.plugins.rowk.RowkValue

class RowkIntegerValue extends RowkValue{

	Integer value

	def val(){value}

	static mapping = {
		discriminator value: "3", type: "integer"
	}
    static constraints = {
    	value(nullable:true)
    }
}