package ma.arkilog.rowk.types

import ma.arkilog.rowk.RowkValue

class RowkEntryValue {

	RowkValue key
	RowkValue value

	static mapping = {
		key cascade: 'all-delete-orphan'
		value cascade: 'all-delete-orphan'
	}
	
}