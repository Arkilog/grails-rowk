package ma.arkilog.rowk.types

import ma.arkilog.rowk.RowkValue

class RowkMapValue extends RowkValue{

	List entries

	static hasMany = [entries : RowkEntryValue]
	
	static transients = ['value']

	def setValue(map){
		entries?.each{it.delete()}
		map?.each{
			addToValues(RowkValue.create(it))
		}
	}

	def getValue(){
		def res = [:]
		entries.each{
			res[(it.key?.value)] = it.value?.value
		}
		return res
	}

	def val(){getValue()}

	static mapping = {
		discriminator value: "10", type: "integer"
		entries  cascade: 'all-delete-orphan'
	}
}