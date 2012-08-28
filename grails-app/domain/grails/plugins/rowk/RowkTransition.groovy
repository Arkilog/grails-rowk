package grails.plugins.rowk

class RowkTransition {

	RowkState nextState

    RowkFyiTarget fyi

	List actions

	static belongsTo = [state : RowkState]

	static hasMany = [actions:RowkAction]

    static mapping = {
        state lazy: false
        actions lazy: false
    }

    static constraints = {
        fyi(nullable:true)
    }

    String toString() {"${state?.name} >> ${nextState?.name}"}
}
