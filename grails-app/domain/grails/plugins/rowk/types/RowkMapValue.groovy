package grails.plugins.rowk.types

import grails.plugins.rowk.RowkValue

class RowkMapValue extends RowkValue{

	List value

	static mappedBy = [value:'map']
	static hasMany = [value : RowkEntryValue]
	
	static transients = ['map']

	def setMap(map){
		value?.each{it.delete()}
		map?.each{
			addToValues(RowkValue.create(it))
		}
	}

	def getMap(){
		def res = [:]
		value.each{
			res[(it.key?.value)] = it.value?.value
		}
		return res
	}

	def val(){getMap()}

	static mapping = {
		discriminator value: "10", type: "integer"
		value  cascade: 'all-delete-orphan', lazy: false
    }

    static constraints = {
    	value(nullable:true)
    }
}