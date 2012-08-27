package grails.plugins.rowk.types

import grails.plugins.rowk.RowkValue

class RowkListValue extends RowkValue{

	List value
	
	static transients = ['list']

	static hasMany = [value:RowkValue]

	def setList(list){
		value?.each{it.delete()}
		list?.each{
			addToValue(RowkValue.create(it))
		}
	}

	def getList(){
		value?.value
	}

	def val(){getList()}

	static mapping = {
		discriminator value: "9", type: "integer"
		value cascade: 'all-delete-orphan', lazy: false
    }

    static constraints = {
    	value(nullable:true)
    }
}