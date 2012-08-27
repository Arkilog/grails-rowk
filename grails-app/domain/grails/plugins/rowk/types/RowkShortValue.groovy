package grails.plugins.rowk.types

import grails.plugins.rowk.RowkValue

class RowkShortValue extends RowkValue{

	Short value

	def val(){value}

	static mapping = {
		discriminator value: "6", type: "integer"
	}
    static constraints = {
    	value(nullable:true)
    }
}