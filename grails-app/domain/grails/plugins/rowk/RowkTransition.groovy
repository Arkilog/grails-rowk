package grails.plugins.rowk

class RowkTransition {

	RowkState nextState

	List actions

	static belongsTo = [state : RowkState]

	static hasMany = [actions:RowkAction]

    static mapping = {
        state lazy: false
        actions lazy: false
    }

    static constraints = {
    }

    String toString() {"${state?.name} >> ${nextState?.name}"}
}
