package grails.plugins.rowk

abstract class RowkAssignee {

    RowkVariable ref

	RowkValue value

	static belongsTo = [target : RowkTarget]

    static constraints = {
        ref(nullable:true)
        value(nullable:true)
    }

    String toString() {"${val()}"}
    static mainTypes = [
		"user" : RowkUser,
		"group" : RowkGroup,
		"role" : RowkRole
    ]
    static RowkAssignee create(type){
    	def assignee = null
    	def rowkClass = mainTypes.get(type)
    	if (rowkClass){
    		assignee = rowkClass.newInstance()
    	}
    	return assignee
    }
    def val(){
        value?.val() ?: ref?.val()
    }

}
