package grails.plugins.rowk

abstract class RowkTarget {

	List assignees

	static belongsTo = [state : RowkState]

	static hasMany = [assignees:RowkAssignee]

    static mapping = {
        assignees lazy: false
    }

    static constraints = {
    }

    String toString() {">> ${assignees}"}
    static mainTypes = [
		"fifo" : RowkFifoTarget,
		"vote" : RowkVoteTarget,
		"serial" : RowkSerialTarget,
		"fyi" : RowkFyiTarget
    ]
    static RowkTarget create(type){
        type = type ?: "fifo"
    	return mainTypes.get(type)?.newInstance()
    }
    def val(){
        assignees*.val()
    }

}
