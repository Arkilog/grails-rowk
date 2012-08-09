package ma.arkilog.rowk.types

import ma.arkilog.rowk.RowkValue

class RowkDomainValue extends RowkValue{

	def grailsApplication

	Long value

	String clazz

	def val(){value}

	def setValue(obj){
		throw new UnsupportedOperationException("Not implemented yet")
	}
	def getValue(){
		throw new UnsupportedOperationException("Not implemented yet")
	}

	static mapping = {
		discriminator value: "11", type: "integer"
	}

	static transients = ['value']

}