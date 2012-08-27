package grails.plugins.rowk

class RowkHumanState extends RowkState {

	RowkTarget assignment
	
	RowkFyiTarget fyi

    static constraints = {
    	assignment(nullable:false)
    	fyi(nullable:true)
    	transitions(minSize:2)
    }

}
