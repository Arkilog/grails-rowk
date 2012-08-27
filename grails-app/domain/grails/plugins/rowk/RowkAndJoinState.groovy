package grails.plugins.rowk

class RowkAndJoinState extends RowkSystemState {

	static mapping = {
		discriminator value: "2", type: "integer"
	}
	static constraints = {
		transitions size: 1..1
	}
}
