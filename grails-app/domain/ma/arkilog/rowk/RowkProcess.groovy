package ma.arkilog.rowk

class RowkProcess {

	static belongsTo = [workflow : RowkWorkflow]

	static hasMany = [activities:RowkActivity]

    static constraints = {
    }
}
