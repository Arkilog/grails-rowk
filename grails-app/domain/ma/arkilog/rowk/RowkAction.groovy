package ma.arkilog.rowk

class RowkAction {

	String service

	String function

	List parameters

	List results

	static hasMany = [parameters : RowkParameter, results : RowkResult]

    static constraints = {
    }

    String toString() {"$service.$function"}
}
