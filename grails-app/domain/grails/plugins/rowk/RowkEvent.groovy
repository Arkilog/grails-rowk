package grails.plugins.rowk

class RowkEvent {

	String name

	RowkTransition transition
	
	static belongsTo = [workflow : RowkWorkflow]

    static constraints = {
    }

    String toString() {name}
}
