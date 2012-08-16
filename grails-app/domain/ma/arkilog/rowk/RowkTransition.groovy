package ma.arkilog.rowk

class RowkTransition {

	RowkState nextState

	RowkTarget assignment
	
	RowkFyiTarget fyi

	List actions

	static belongsTo = [state : RowkState]

	static hasMany = [actions:RowkAction]

    static constraints = {
    	assignment(nullable:false)
    	fyi(nullable:false)
    }

    String toString() {"> ${nextState?.name}"}
}
