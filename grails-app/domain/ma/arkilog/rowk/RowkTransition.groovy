package ma.arkilog.rowk

class RowkTransition {

	RowkState nextState

	List actions

	static belongsTo = [state : RowkState]

	static hasMany = [actions:RowkAction]

    static constraints = {
    }

    String toString() {"> ${nextState?.name}"}
}
