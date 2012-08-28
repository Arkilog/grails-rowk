package grails.plugins.rowk

class RowkHumanState extends RowkState {

	RowkTarget assignment
	
    static constraints = {
    	assignment(nullable:false)
    	transitions(minSize:2)
    }

}
