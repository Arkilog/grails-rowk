package grails.plugins.rowk.types

import grails.plugins.rowk.RowkValue

class RowkFloatValue extends RowkValue{

	Float value

	def val(){value}

	static mapping = {
		discriminator value: "8", type: "integer"
	}
    static constraints = {
    	value(nullable:true)
    }
}