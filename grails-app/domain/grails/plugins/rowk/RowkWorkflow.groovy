package grails.plugins.rowk

class RowkWorkflow {

	List states

	List events
	
	List variables

	RowkState start

	String name

	static hasMany = [states: RowkState, events: RowkEvent, variables:RowkVariable]

    static mapping = {
        states lazy: false
        events lazy: false
        variables lazy: false
    }

    static constraints = {
    }

    String toString() {name}

}
