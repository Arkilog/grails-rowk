package ma.arkilog.rowk

class RowkWorkflow {

	List states

	List events

	RowkState start

	String name

	static hasMany = [states: RowkState, events: RowkEvent]

    static constraints = {
    }

    String toString() {name}

}
