package ma.arkilog.rowk

class RowkVariable {

	String name

	RowkVariable ref
	
	RowkValue value
	
	static belongsTo = [workflow : RowkWorkflow]

	def val(){
		ref?.val() ?: value?.val()
	}

    static constraints = {
    	ref(nullable:true)
    	value(nullable:true)
    }

    String toString() {name}
}
