package grails.plugins.rowk

class RowkAndForkState extends RowkSystemState {

	static mapping = {
		discriminator value: "1", type: "integer"
	}
	static constraints = {
		transitions minSize: 2
 	}
}
