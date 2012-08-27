package grails.plugins.rowk.types

import grails.plugins.rowk.RowkValue

class RowkDomainValue extends RowkValue{

	def grailsApplication

	Long value

	String clazz

	def val(){value}

	def setObject(obj){
		throw new UnsupportedOperationException("Not implemented yet")
	}
	def getObject(){
		throw new UnsupportedOperationException("Not implemented yet")
	}

	static mapping = {
		discriminator value: "11", type: "integer"
	}

	static transients = ['object']
    static constraints = {
    	value(nullable:true)
    }

}