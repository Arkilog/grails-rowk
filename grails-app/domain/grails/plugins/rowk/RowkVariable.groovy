package grails.plugins.rowk

class RowkVariable {

	String name

	RowkVariable ref
	
	RowkValue value

	String source
	
	static belongsTo = [workflow : RowkWorkflow]

	def val(){
		ref?.val() ?: value?.val()
	}

    static constraints = {
    	ref(nullable:true)
    	value(nullable:true)
    	source(nullable:true)
    }

    String toString() {name}
}
