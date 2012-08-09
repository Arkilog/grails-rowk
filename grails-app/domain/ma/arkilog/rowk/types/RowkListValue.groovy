package ma.arkilog.rowk.types

import ma.arkilog.rowk.RowkValue

class RowkListValue extends RowkValue{

	List values
	
	static transients = ['value']

	static hasMany = [values:RowkValue]

	def setValue(list){
		values?.each{it.delete()}
		list?.each{
			addToValues(RowkValue.create(it))
		}
	}

	def getValue(){
		values.value
	}

	def val(){getValue()}

	static mapping = {
		discriminator value: "9", type: "integer"
		values cascade: 'all-delete-orphan'
	}
}