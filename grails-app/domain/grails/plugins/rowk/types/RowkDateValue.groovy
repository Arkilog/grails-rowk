package grails.plugins.rowk.types

import grails.plugins.rowk.RowkValue

class RowkDateValue extends RowkValue{

	Date value

	def val(){value}

	static mapping = {
		discriminator value: "2", type: "integer"
	}

    static constraints = {
    	value(nullable:true)
    }
}