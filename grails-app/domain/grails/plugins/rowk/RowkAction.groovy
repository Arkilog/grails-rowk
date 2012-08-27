package grails.plugins.rowk

class RowkAction {

	String service

	String function

	List parameters

	List results

	static hasMany = [parameters : RowkParameter, results : RowkResult]

    static mapping = {
        parameters lazy: false
        results lazy: false
    }

    static constraints = {
    }

    String toString() {"$service.$function"}
}
