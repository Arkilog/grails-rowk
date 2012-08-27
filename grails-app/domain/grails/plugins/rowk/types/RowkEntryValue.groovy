package grails.plugins.rowk.types

import grails.plugins.rowk.RowkValue

class RowkEntryValue {

	RowkValue key
	RowkValue value
	static belongsTo = [map:RowkMapValue]

	static mapping = {
		key cascade: 'all-delete-orphan'
		value cascade: 'all-delete-orphan'
	}
    static constraints = {
    	key(nullable:true)
    	value(nullable:true)
    }
	
}