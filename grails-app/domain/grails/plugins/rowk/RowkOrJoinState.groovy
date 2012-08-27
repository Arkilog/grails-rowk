package grails.plugins.rowk

class RowkOrJoinState extends RowkSystemState {

	static mapping = {
		discriminator value: "3", type: "integer"
	}

	static constraints = {
		transitions size: 1..1
	}
}
