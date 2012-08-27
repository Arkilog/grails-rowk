package grails.plugins.rowk

class RowkOrForkState extends RowkSystemState {

	static mapping = {
		discriminator value: "4", type: "integer"
	}
	static constraints = {
		transitions minSize: 2
	}
}
