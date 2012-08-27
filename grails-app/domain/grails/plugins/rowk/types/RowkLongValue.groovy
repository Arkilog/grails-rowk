package grails.plugins.rowk.types

import grails.plugins.rowk.RowkValue

class RowkLongValue extends RowkValue{

	Long value

	def val(){value}

	static mapping = {
		discriminator value: "5", type: "integer"
	}
    static constraints = {
    	value(nullable:true)
    }
}