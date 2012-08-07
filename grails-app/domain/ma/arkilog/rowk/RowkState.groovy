package ma.arkilog.rowk

class RowkState {

	String name

	List transitions
	
	static belongsTo = [workflow : RowkWorkflow]

	static hasMany = [transitions:RowkTransition]

	static mappedBy = [transitions:'state']

    static constraints = {
    }

    String toString() {name}
}
