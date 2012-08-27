package grails.plugins.rowk

abstract class RowkState {

	String name

	List transitions
	
	static belongsTo = [workflow : RowkWorkflow]

	static hasMany = [transitions:RowkTransition]

	static mappedBy = [transitions:'state']

    static mapping = {
        transitions lazy: false
    }

    String toString() {name}
}
