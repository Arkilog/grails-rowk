package ma.arkilog.rowk

class RowkWorkflow {

	List states

	List events
	
	List variables

	RowkState start

	String name

	static hasMany = [states: RowkState, events: RowkEvent, variables:RowkVariable]

    static constraints = {
    }

    String toString() {name}

}
