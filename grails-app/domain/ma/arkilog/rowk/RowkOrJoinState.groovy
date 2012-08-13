package ma.arkilog.rowk

class RowkOrJoinState extends RowkState {

	String name

	List transitions
	
	static belongsTo = [workflow : RowkWorkflow]

	static hasMany = [transitions:RowkTransition]

	static mappedBy = [transitions:'state']

    static constraints = {
    }
	static mapping = {
		discriminator value: "3", type: "integer"
	}

    String toString() {name}
}
