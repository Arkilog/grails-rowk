package grails.plugins.rowk

class RowkProcess {

	static belongsTo = [workflow : RowkWorkflow]

	static hasMany = [activities:RowkActivity]

    static mapping = {
        activities lazy: false
    }

    static constraints = {
    }
}
